import { NextRequest, NextResponse } from 'next/server';
import { getVirusTotalApiKey } from '../../../../../../lib/config';
import { supabaseAdmin } from '../../../../../../lib/supabaseAdmin';

export const runtime = 'nodejs';

function deriveVerdict(stats: any): 'safe' | 'suspicious' | 'malicious' {
  if (!stats) return 'safe';
  if ((stats.malicious || 0) > 0) return 'malicious';
  if ((stats.suspicious || 0) > 0) return 'suspicious';
  return 'safe';
}

export async function GET(_req: NextRequest, { params }: { params: { hash: string } }) {
  const key = getVirusTotalApiKey();
  if (!key) {
    try { await supabaseAdmin.from('scan_logs').insert({ scan_type: 'file_hash_lookup', target: params.hash, status: 'error_key_missing', provider: 'virustotal', raw: { error: 'key missing' } }); } catch {}
    return NextResponse.json({ error: 'VirusTotal key missing' }, { status: 400 });
  }
  try {
    const resp = await fetch(`https://www.virustotal.com/api/v3/files/${params.hash}`, {
      headers: { 'x-apikey': key, 'accept': 'application/json' }
    });
    if (!resp.ok) {
      const text = await resp.text();
      try { await supabaseAdmin.from('scan_logs').insert({ scan_type: 'file_hash_lookup', target: params.hash, status: 'error_lookup_failed', provider: 'virustotal', raw: { detail: text, status: resp.status } }); } catch {}
      return NextResponse.json({ error: 'VirusTotal file lookup failed', detail: text, hash: params.hash }, { status: resp.status });
    }
    const data: any = await resp.json();
    const stats = data?.data?.attributes?.last_analysis_stats;
    const verdict = deriveVerdict(stats);
    try {
      await supabaseAdmin.from('scan_logs').insert({
        scan_type: 'file_hash_lookup',
        target: params.hash,
        verdict: verdict || null,
        status: 'completed',
        provider: 'virustotal',
        raw: { stats, full: data }
      });
    } catch {}
    // Upsert/update primary scans row if one exists (identified by hash)
    try {
      await supabaseAdmin.from('scans')
        .update({ verdict, api_responses: { virustotal_hash: data } })
        .eq('item_type', 'file')
        .eq('item_identifier', params.hash);
    } catch {}
    return NextResponse.json({ hash: params.hash, stats, verdict, source: 'hash_lookup' });
  } catch (e: any) {
    console.error(e);
    try { await supabaseAdmin.from('scan_logs').insert({ scan_type: 'file_hash_lookup', target: params.hash, status: 'error_exception', provider: 'virustotal', raw: { message: e?.message } }); } catch {}
    return NextResponse.json({ error: 'Internal error', hash: params.hash }, { status: 500 });
  }
}
