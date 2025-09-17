import { NextRequest, NextResponse } from 'next/server';
import { getVirusTotalApiKey } from '../../../../../lib/config';
import { supabaseAdmin } from '../../../../../lib/supabaseAdmin';

export const runtime = 'nodejs';

interface VTStats {
  harmless?: number; malicious?: number; suspicious?: number; undetected?: number; timeout?: number; failure?: number; type_unsupported?: number; confirmed_timeout?: number; unrated?: number; timeout_in_progress?: number; // loose typing
}

function deriveVerdict(stats: VTStats): 'safe' | 'suspicious' | 'malicious' {
  if ((stats.malicious || 0) > 0) return 'malicious';
  if ((stats.suspicious || 0) > 0) return 'suspicious';
  return 'safe';
}

export async function GET(_req: NextRequest, { params }: { params: { id: string } }) {
  const id = params.id;
  const key = getVirusTotalApiKey();
  if (!key) {
    // Log missing key attempt
    try {
      await supabaseAdmin.from('scan_logs').insert({
        scan_type: 'file_analysis',
        target: id,
        status: 'error_key_missing',
        provider: 'virustotal',
        raw: { error: 'key missing' }
      });
    } catch {}
    return NextResponse.json({ error: 'VirusTotal key missing', vt_id: id }, { status: 400 });
  }
  try {
    const resp = await fetch(`https://www.virustotal.com/api/v3/analyses/${id}`, {
      headers: { 'x-apikey': key, 'accept': 'application/json' }
    });
    if (!resp.ok) {
      const text = await resp.text();
      try {
        await supabaseAdmin.from('scan_logs').insert({
          scan_type: 'file_analysis',
          target: id,
          status: 'error_query_failed',
          provider: 'virustotal',
          raw: { detail: text, status: resp.status }
        });
      } catch {}
      return NextResponse.json({ error: 'VirusTotal query failed', vt_id: id, detail: text }, { status: resp.status });
    }
    const data: any = await resp.json();
    const status: string = data?.data?.attributes?.status || 'unknown';
    const stats: VTStats = data?.data?.attributes?.stats || {};
    const verdict = status === 'completed' ? deriveVerdict(stats) : undefined;
    // Attempt to capture sha256 of file if present
    const fileHash: string | undefined = data?.meta?.file_info?.sha256;

    // Persist a log entry for every poll (could be throttled later). Include stats & verdict when available.
    try {
      await supabaseAdmin.from('scan_logs').insert({
        scan_type: 'file_analysis',
        target: verdict && fileHash ? fileHash : id, // prefer hash when final
        verdict: verdict || null,
        status,
        provider: 'virustotal',
        raw: { analysis_id: id, stats, full: status === 'completed' ? data : undefined }
      });
    } catch (e) { /* swallow */ }

    // If completed & have hash + verdict, update existing scans row (created at upload) with final verdict/stats
    if (status === 'completed' && verdict && fileHash) {
      try {
        const api_responses = { virustotal_analysis: data };
        await supabaseAdmin.from('scans')
          .update({ verdict, api_responses })
          .eq('item_type', 'file')
          .eq('item_identifier', fileHash);
      } catch (e) { /* ignore */ }
    }
    return NextResponse.json({ vt_id: id, status, stats, verdict });
  } catch (e: any) {
    console.error(e);
    try {
      await supabaseAdmin.from('scan_logs').insert({
        scan_type: 'file_analysis',
        target: id,
        status: 'error_exception',
        provider: 'virustotal',
        raw: { message: e?.message }
      });
    } catch {}
    return NextResponse.json({ error: 'Internal error', vt_id: id }, { status: 500 });
  }
}
