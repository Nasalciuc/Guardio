# MScan Backend (Next.js + Supabase)

MVP backend replacing original PocketBase/Python design with Next.js Route Handlers and Supabase.

## API Routes
- POST /api/scan  { url } → parallel mocked scanners, stores scan result
- POST /api/scan/email  { email } → mocked HIBP lookup
- GET /api/news  returns cached RSS articles
- POST /api/report { type, description, screenshot_base64?, email?, source_item? }

## Mocking
Google Safe Browsing & HIBP are mocked when environment flags are true:
```
MOCK_GOOGLE_SAFE_BROWSING=true
MOCK_HIBP=true
```

## Environment
Copy `.env.example` to `.env.local` and fill real values when ready.

## Dev
Install deps and run:
```
npm install
npm run dev
```

## Supabase
Apply schema from `../supabase/schema.sql` via dashboard or CLI. Service role key must be present server-side only.

## Notes
- File uploads are placeholder only.
- All verdicts currently default to `safe` until real integrations added.
