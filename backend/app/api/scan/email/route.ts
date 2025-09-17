import { NextRequest, NextResponse } from 'next/server';
import { supabaseAdmin } from '../../../../lib/supabaseAdmin';
import { fetchHIBP } from '../../../../lib/scanners';

export const runtime = 'nodejs';

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const email: string | undefined = body.email;
    if (!email) {
      return NextResponse.json({ error: 'email is required' }, { status: 400 });
    }

    const hibp = await fetchHIBP(email);

    const { error } = await supabaseAdmin.from('email_checks').insert({
      email,
      is_pwned: hibp.is_pwned,
      breaches: hibp.breaches
    });

    if (error) console.error('DB insert error', error);

    return NextResponse.json({ is_pwned: hibp.is_pwned, breaches: hibp.breaches });
  } catch (e: any) {
    console.error(e);
    return NextResponse.json({ error: 'Internal error' }, { status: 500 });
  }
}

// Provide GET to prevent 405 when accessed directly.
export async function GET() {
  return NextResponse.json({
    endpoint: '/api/scan/email',
    usage: 'POST { "email": "user@example.com" }',
    methods: ['POST'],
    note: 'GET is informational only.'
  });
}
