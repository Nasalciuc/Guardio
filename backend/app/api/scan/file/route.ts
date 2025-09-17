import { NextRequest, NextResponse } from 'next/server';
import { supabaseAdmin } from '../../../../lib/supabaseAdmin';
import { getVirusTotalApiKey } from '../../../../lib/config';
import crypto from 'crypto';

export const runtime = 'nodejs';

interface VTFileVerdict {
  verdict: 'safe' | 'suspicious' | 'malicious';
  raw: any;
  status: string;
  id?: string;
}

async function scanWithVirusTotal(buffer: Buffer, filename: string, apiKey: string): Promise<VTFileVerdict> {
  const form = new FormData();
  // Convert buffer to a Uint8Array for the File constructor in the Edge runtime / Node 18+ fetch implementation.
  const uint8 = new Uint8Array(buffer);
  const fileObj = new File([uint8], filename);
  form.append('file', fileObj);

  const resp = await fetch('https://www.virustotal.com/api/v3/files', {
    method: 'POST',
    headers: { 'x-apikey': apiKey },
    body: form
  });

  if (!resp.ok) {
    const text = await resp.text();
    return { verdict: 'suspicious', raw: { error: text, status: resp.status }, status: 'error_upload' };
  }

  const data: any = await resp.json();
  // analysis link id provided in response data.id. Further polling for completed stats omitted for MVP.
  // Mark verdict as 'safe' initially; a production version would poll /analyses/{id} to refine.
  return { verdict: 'safe', raw: data, status: 'submitted', id: data?.data?.id };
}

function mockScan(filename: string): VTFileVerdict {
  return { verdict: 'safe', raw: { mock: true, source: 'virustotal', filename }, status: 'mock' };
}

export async function POST(req: NextRequest) {
  try {
    const contentType = req.headers.get('content-type') || '';
    if (!contentType.includes('multipart/form-data')) {
      return NextResponse.json({ error: 'multipart/form-data required' }, { status: 400 });
    }

    const form = await req.formData();
    const file = form.get('file');
    if (!file || !(file instanceof File)) {
      return NextResponse.json({ error: 'file field is required' }, { status: 400 });
    }

    if (file.size > 32_000_000) {
      return NextResponse.json({ error: 'file too large (>32MB limit for free VT)' }, { status: 413 });
    }

    const arrayBuffer = await file.arrayBuffer();
    const buffer = Buffer.from(arrayBuffer);
    const hash = crypto.createHash('sha256').update(buffer).digest('hex');

  let vt: VTFileVerdict;
  const apiKey = getVirusTotalApiKey() || '';
    if (apiKey) {
      vt = await scanWithVirusTotal(buffer, file.name, apiKey);
    } else {
      vt = mockScan(file.name);
    }

    // Persist minimal record (non-blocking if table missing)
    try {
      const { error: scanErr } = await supabaseAdmin.from('scans').insert({
        item_type: 'file',
        item_identifier: hash,
        verdict: vt.verdict,
        api_responses: { virustotal: vt.raw },
      });
      if (scanErr) console.error('[Supabase] scans insert error', scanErr);
      const { error: logErr } = await supabaseAdmin.from('scan_logs').insert({
        scan_type: 'file_upload',
        target: hash,
        verdict: vt.verdict,
        status: vt.status,
        provider: 'virustotal',
        raw: vt.raw
      });
      if (logErr) console.error('[Supabase] scan_logs insert error', logErr);
    } catch (e) {
      console.error('DB insert/log exception', e);
    }

    return NextResponse.json({
      filename: file.name,
      size: file.size,
      sha256: hash,
      verdict: vt.verdict,
      vt_status: vt.status,
      vt_id: vt.id,
  note: apiKey ? 'File submitted to VirusTotal (preliminary submission response).' : 'VirusTotal API key not detected (set VIRUSTOTAL_API_KEY or VT_API_KEY). Returned mock verdict.'
    });
  } catch (e: any) {
    console.error(e);
    return NextResponse.json({ error: 'Internal error' }, { status: 500 });
  }
}

export async function GET() {
  return NextResponse.json({
    endpoint: '/api/scan/file',
    usage: 'POST multipart/form-data with field "file"',
    example_curl: 'curl -F "file=@sample.pdf" http://localhost:3000/api/scan/file',
    note: 'Returns mock verdict unless VT_API_KEY is configured.'
  });
}
