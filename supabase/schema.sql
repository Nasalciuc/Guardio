-- Supabase / PostgreSQL schema for MScan
-- Run manually in Supabase SQL editor or via `supabase db push` (after integrating migrations)

-- Enable required extensions (idempotent)
create extension if not exists pgcrypto; -- for gen_random_uuid
create extension if not exists "uuid-ossp"; -- optional alternative

-- TABLE: scans
create table if not exists public.scans (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  item_type text check (item_type in ('file','url')) not null,
  item_identifier text not null,
  verdict text check (verdict in ('safe','suspicious','malicious')) not null,
  api_responses jsonb not null default '{}'::jsonb,
  user_id uuid null references auth.users(id) on delete set null
);
create index if not exists scans_item_identifier_idx on public.scans (item_identifier);

-- TABLE: scan_logs (append-only detailed logs for scans & lookups)
create table if not exists public.scan_logs (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  scan_type text check (scan_type in ('file_upload','file_analysis','file_hash_lookup','url_scan')) not null,
  target text null,
  verdict text null check (verdict in ('safe','suspicious','malicious')),
  status text null,
  provider text null,
  raw jsonb not null default '{}'::jsonb
);
create index if not exists scan_logs_scan_type_idx on public.scan_logs (scan_type);
create index if not exists scan_logs_target_idx on public.scan_logs (target);

-- TABLE: email_checks
create table if not exists public.email_checks (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  email text not null,
  is_pwned boolean not null default false,
  breaches jsonb not null default '[]'::jsonb,
  user_id uuid null references auth.users(id) on delete set null
);
create index if not exists email_checks_email_idx on public.email_checks (email);

-- TABLE: reports
create table if not exists public.reports (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now(),
  report_type text not null,
  description text null,
  attachment_path text null,
  source_item text null,
  user_email text null,
  meta jsonb not null default '{}'::jsonb,
  user_id uuid null references auth.users(id) on delete set null
);

-- RLS Enable
alter table public.scans enable row level security;
alter table public.email_checks enable row level security;
alter table public.reports enable row level security;
alter table public.scan_logs enable row level security;

-- Basic RLS policies (locked down: only service-role can write). Adjust if you introduce authenticated users later.
-- NOTE: service-role bypasses RLS automatically; anon/key clients cannot modify data.
create policy "scans_select_none" on public.scans for select using (false);
create policy "email_checks_select_none" on public.email_checks for select using (false);
create policy "reports_select_none" on public.reports for select using (false);
create policy "scan_logs_select_none" on public.scan_logs for select using (false);

-- OPTIONAL (if allowing authenticated users to read their own rows later):
-- create policy "scans_read_own" on public.scans for select using (auth.uid() = user_id);
-- create policy "email_checks_read_own" on public.email_checks for select using (auth.uid() = user_id);
-- create policy "reports_read_own" on public.reports for select using (auth.uid() = user_id);

-- Helper view to summarize scan verdict counts (example analytics)
create or replace view public.scan_verdict_stats as
select verdict, count(*) as total
from public.scans
group by verdict;

-- Grant usage (public schema usually already accessible). Avoid broad grants; rely on RLS.
-- Supabase automatically grants to authenticated/anon roles; with RLS disabled by default nothing is exposed.

-- Finished.
