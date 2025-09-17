import { NextRequest, NextResponse } from 'next/server';
import { supabaseAdmin } from '../../../lib/supabaseAdmin';
import { aggregateVerdict } from '../../../lib/verdict';
import { fetchVirusTotal, fetchGoogleSafeBrowsing, fetchUrlScan } from '../../../lib/scanners';

export const runtime = 'nodejs';

export async function POST(req: NextRequest) {
  try {
    const contentType = req.headers.get('content-type') || '';
    let url: string | undefined;

    if (contentType.includes('application/json')) {
      const body = await req.json();
      url = body.url;
    } else if (contentType.includes('multipart/form-data')) {
      const formData = await req.formData();
      url = formData.get('url') as string | undefined;
      // File handling (future): we would upload to storage first and compute hash
    }

    // Sanitize to avoid Postgres 22P05 ("unsupported Unicode escape sequence" / NUL bytes) if client sends stray \u0000 chars
    if (url) {
      const original = url;
      url = url.replace(/\u0000/g, '').trim();
      if (original !== url) {
        console.warn('[ScanAPI] Removed NUL characters from incoming url');
      }
    }

  if (!url) {
      return NextResponse.json({ error: 'url is required for MVP' }, { status: 400 });
    }

    // Parallel external lookups
    const [vt, gsb, us] = await Promise.all([
      fetchVirusTotal(url),
      fetchGoogleSafeBrowsing(url),
      fetchUrlScan(url)
    ]);

    const verdict = aggregateVerdict([vt.verdict, gsb.verdict, us.verdict]);

    const { error } = await supabaseAdmin.from('scans').insert({
      item_type: 'url',
      item_identifier: url,
      verdict,
      api_responses: { virustotal: vt.raw, gsb: gsb.raw, urlscan: us.raw }
    });

    if (error) {
      console.error('DB insert error', error);
    }

    return NextResponse.json({ verdict, details: 'Scan complete.' });
  } catch (e: any) {
    console.error(e);
    return NextResponse.json({ error: 'Internal error' }, { status: 500 });
  }
}

// Allow simple GET requests (e.g. typing in browser) to avoid 405 errors.
export async function GET() {
  return NextResponse.json({
    endpoint: '/api/scan',
    usage: 'POST { "url": "https://example.com" }',
    methods: ['POST'],
    note: 'No scan executed via GET.'
  });
}
