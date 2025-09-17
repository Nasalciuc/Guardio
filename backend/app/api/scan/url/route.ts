import { NextRequest, NextResponse } from 'next/server';
import { fetchUrlScan } from '../../../../lib/scanners';
import { supabaseAdmin } from '../../../../lib/supabaseAdmin';

export const runtime = 'nodejs';

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const url: string | undefined = body.url;
    if (!url) return NextResponse.json({ error: 'url is required' }, { status: 400 });
    const progressEvents: any[] = [];
    const scan = await fetchUrlScan(url, async (evt) => {
      progressEvents.push(evt);
      try {
        await supabaseAdmin.from('scan_logs').insert({
          scan_type: 'url_scan',
          target: url,
          status: evt.phase,
          provider: 'urlscan',
          raw: evt.detail || {}
        });
      } catch {}
    });
    try {
      const { error: scanErr } = await supabaseAdmin.from('scans').insert({
        item_type: 'url',
        item_identifier: url,
        verdict: scan.verdict,
        api_responses: { urlscan: scan.raw }
      });
      if (scanErr) console.error('[Supabase] scans insert error', scanErr);
      const { error: logErr } = await supabaseAdmin.from('scan_logs').insert({
        scan_type: 'url_scan',
        target: url,
        verdict: scan.verdict,
        status: 'final',
        provider: 'urlscan',
        raw: { final: scan.raw, timeline: progressEvents }
      });
      if (logErr) console.error('[Supabase] scan_logs insert error', logErr);
    } catch (e) { console.error('DB insert/log exception', e); }
    return NextResponse.json({ verdict: scan.verdict, url, urlscan: scan.raw });
  } catch (e: any) {
    console.error(e);
    return NextResponse.json({ error: 'Internal error' }, { status: 500 });
  }
}

export async function GET() {
  return NextResponse.json({
    endpoint: '/api/scan/url',
    usage: 'POST { "url": "https://example.com" }',
    note: 'Uses urlscan.io. Returns mock if URLSCAN_API_KEY not set.'
  });
}
