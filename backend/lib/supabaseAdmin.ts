import { createClient } from '@supabase/supabase-js';

const url = process.env.NEXT_PUBLIC_SUPABASE_URL;
const serviceKey = process.env.SUPABASE_SERVICE_ROLE_KEY;

let supabaseAdmin: any;
if (process.env.SUPABASE_DISABLED === 'true' || !url || !serviceKey) {
  // Stub client for local development without a Supabase instance
  supabaseAdmin = {
    from: (_table: string) => ({
      insert: (val: any) => {
        console.warn('[SupabaseStub] insert skipped (no credentials)', _table, val);
        // mimic postgrest-js chain
        return {
          select: () => ({ single: async () => ({ data: { id: 'stub-id' }, error: null }) })
        };
      },
      upsert: async (val: any) => { console.warn('[SupabaseStub] upsert skipped', _table, val); return { error: null }; }
    })
  };
} else {
  supabaseAdmin = createClient(url, serviceKey, {
    auth: { persistSession: false },
    db: { schema: 'public' }
  });
}

export { supabaseAdmin };
