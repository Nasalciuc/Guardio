import { NextRequest, NextResponse } from 'next/server';
import { supabaseAdmin } from '../../../lib/supabaseAdmin';

export const runtime = 'nodejs';

// Placeholder: in a real version you'd upload to Supabase storage or S3 and return the path.
async function storeScreenshot(base64: string): Promise<string | null> {
  if (!base64) return null;
  // Basic size guard (avoid huge JSON bodies crashing the function)
  if (base64.length > 2_000_000) return null; // ~1.5MB raw
  return 'report_screenshots/mock-placeholder.png';
}

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const {
      type, // category code or textual type
      description,
      screenshot_base64,
      email,
      source_item,
      meta // optional extra structured data from UI
    } = body;

    if (!type) return NextResponse.json({ error: 'type is required' }, { status: 400 });

    const attachment_path = screenshot_base64 ? await storeScreenshot(screenshot_base64) : null;

    // Merge additional known top-level fields into meta for future extensibility
    const metaPayload = {
      ...(meta || {}),
      source_ver: 1,
      user_agent: req.headers.get('user-agent') || null,
    };

  const insertPayload: any = {
      report_type: type,
      description,
      attachment_path,
      source_item,
      user_email: email || null,
      // If the DB has a 'meta' column we send it; if not it will be ignored by stub or error silently.
      meta: metaPayload
    };

  const stubMode = process.env.SUPABASE_DISABLED === 'true' || !process.env.NEXT_PUBLIC_SUPABASE_URL || !process.env.SUPABASE_SERVICE_ROLE_KEY;
  console.log('[ReportAPI] incoming body', { body });
  console.log('[ReportAPI] insert payload', insertPayload, 'stubMode=', stubMode);

    let data: any = null; let error: any = null;
    let fallbackNoMeta = false;
    try {
      const builder = supabaseAdmin.from('reports').insert(insertPayload);
      if (builder && typeof builder.select === 'function') {
        const res = await builder.select('id').single();
        data = res.data; error = res.error;
      } else {
        const res = await builder; // stub path
        data = res.data; error = res.error;
      }
    } catch (e:any) {
      console.error('Insert exception', e);
    }
    // Fallback: if meta column not present (older DB) retry without meta
    if (error && (error.message || '').includes("'meta'")) {
      console.warn('[ReportAPI] retrying insert without meta column');
      fallbackNoMeta = true;
      try {
        const legacyPayload = { ...insertPayload };
        delete (legacyPayload as any).meta;
        const builder2 = supabaseAdmin.from('reports').insert(legacyPayload);
        if (builder2 && typeof builder2.select === 'function') {
          const res2 = await builder2.select('id').single();
          data = res2.data; error = res2.error;
        } else {
          const res2 = await builder2;
          data = res2.data; error = res2.error;
        }
      } catch (e:any) {
        console.error('Insert fallback exception', e);
      }
    }
    if (error) console.error('DB insert error (final)', error);

    return NextResponse.json({ status: 'Report received', id: data?.id || null, stub_mode: stubMode, downgraded_no_meta: fallbackNoMeta }, { status: 201 });
  } catch (e: any) {
    console.error(e);
    return NextResponse.json({ error: 'Internal error' }, { status: 500 });
  }
}

// Lightweight GET so opening the URL in a browser doesn't yield a 405.
// Returns usage guidance instead of performing an insert.
export async function GET() {
  return NextResponse.json({
    endpoint: '/api/report',
    usage: 'POST { "type": "PHI-12", "description": "..." }',
    accepted_meta_keys: ['detection_time','affected_resources','reporter','consent_public','category_label'],
    methods: ['POST'],
    note: 'This GET is informational only.'
  });
}
