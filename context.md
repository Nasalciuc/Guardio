MScan Project Summary & Goals
1. Core Mission & Name
Project Name: MScan
Mission: To develop a native Android cybersecurity module that integrates seamlessly into the OS. It will provide users with robust tools for scanning files/links, staying informed about cyber threats, and reporting incidents, aligning with the "M" brand of Moldovan government applications (MPass, MDelivery).
2. Core Features
Security Scanner:
Scans files, URLs, and text content.
Triple-Check System: Utilizes three separate APIs for comprehensive analysis (e.g., VirusTotal, Google Safe Browsing, URLScan.io).
Email Breach Check: Includes an option to check an email address against the 'Have I Been Pwned' database (or a similar service with a public API) to see if it has been compromised in a known data breach.
Logging: All activities on the app and results are logged in the PocketBase backend.
Cybersecurity News Feed:
Aggregates the latest news and alerts using RSS feeds from official sources (stisc.gov.md, cyberevent.gov.md).
Articles open within an in-app browser to keep the user experience contained.
Notification System: Includes a user-configurable toggle (On/Off) to receive important security alerts via in-app push notifications.
Incident Reporting:
A streamlined process for users to report suspicious files, links, emails, or messages.
Allows for attaching screenshots to provide context.
All reports are logged in the PocketBase backend.
3. Access Methods & User Flows
Default Browser Integration: MScan can be set as the default link handler. It will intercept link clicks, perform a quick scan, and then either block (but allow force override for experienced users with a hidden toggle) the link or open it in the user's preferred browser.
Native Android Share Menu: The app will have two share intents:
Share to MScan (Scan): Immediately starts a security scan on the shared item.
Share to MScan (Report): Opens the reporting form with the shared item pre-attached.
Lock Screen Shortcut: A user can configure an action shortcut on their lock screen to instantly open a camera view for scanning QR codes or text containing links.
Post-Scan Reporting: After a scan identifies an item as malicious, a toggle/button will appear, allowing the user to report the threat with a single tap.
Direct App Access: The main app interface will provide three clear entry points: Scan, News, and Report.
4. Localization
The application must be fully translated and functional in Romanian, English, and Russian.

MScan Technical Architecture (Updated: Migrated to Supabase + Next.js)
1. Technology Stack
Mobile App (Android): Kotlin
Backend: Next.js (Node 18+; API Route Handlers / Edge Functions for server logic)
Database & Auth: Supabase (PostgreSQL + Auth + Storage). All prior PocketBase responsibilities are now handled by Supabase.
Logging & Data Persistence: Supabase tables (scans, email_checks, reports) with Row Level Security (RLS) and service‑role mediated writes.
2. Backend Architecture
API Endpoints:
POST /scan: Accepts a file (multipart) or URL. Proxies the request to the three external scanning services (VirusTotal, etc.). Aggregates results and returns a final verdict. Logs the transaction to PocketBase.
GET /news: Fetches and parses RSS feeds from government sources. Caches results to avoid excessive scraping. Returns a list of recent articles.
POST /report: Accepts a JSON payload containing the report details (text, attached screenshot as Base64 string). Saves the report to the PocketBase reports collection.
Notifications:
In-App: (Planned) FCM or native Android notifications triggered by polling or push channel (future enhancement).
Email: (Future scope) Could be implemented via Supabase Functions + third‑party email provider (e.g., Resend, Postmark) – removed PocketBase dependency.
3. API & Data Handling
External APIs:
Scanning: VirusTotal, Google Safe Browsing, URLScan.io. API keys are required.
News: RSS feed parsing from stisc.gov.md and cyberevent.gov.md.
Supabase Data Schema (PostgreSQL):
Table: scans
  id uuid PRIMARY KEY DEFAULT gen_random_uuid()
  created_at timestamptz DEFAULT now()
  item_type text CHECK (item_type IN ('file','url'))
  item_identifier text NOT NULL -- hash for file or URL string
  verdict text CHECK (verdict IN ('safe','suspicious','malicious')) NOT NULL
  api_responses jsonb NOT NULL DEFAULT '{}'::jsonb
  user_id uuid NULL REFERENCES auth.users(id) ON DELETE SET NULL
  INDEX scans_item_identifier_idx (item_identifier)

Table: email_checks
  id uuid PRIMARY KEY DEFAULT gen_random_uuid()
  created_at timestamptz DEFAULT now()
  email text NOT NULL
  is_pwned boolean NOT NULL DEFAULT false
  breaches jsonb NOT NULL DEFAULT '[]'::jsonb
  user_id uuid NULL REFERENCES auth.users(id) ON DELETE SET NULL
  INDEX email_checks_email_idx (email)

Table: reports
  id uuid PRIMARY KEY DEFAULT gen_random_uuid()
  created_at timestamptz DEFAULT now()
  report_type text NOT NULL
  description text
  attachment_path text NULL -- Supabase Storage object path
  source_item text NULL -- file hash or URL
  user_email text NULL -- optional reporter email (sanitized / hashed if needed)
  user_id uuid NULL REFERENCES auth.users(id) ON DELETE SET NULL

Storage Bucket: report_screenshots (private). Accessed via service-role through Next.js backend; signed URLs returned to the Android app when needed.

RLS Strategy:
  - Enable RLS on all tables.
  - Public (anon) clients NEVER write directly; Android app calls Next.js API routes; server uses service key.
  - Optional future policy: allow authenticated users to read only their own rows (matching user_id).
4. Security & DevOps
Security by Design:
Minimal PII: Only optional user_email in reports; consider hashing before storage if not required in cleartext.
Secrets: External API keys and Supabase keys stored in environment variables (see .env.example) – never commit real values.
RLS: Protects data at the database layer; service-role key ONLY on server (Next.js), never in mobile app.
File Integrity: File hashes used instead of raw file contents (scans table) to avoid storing sensitive binaries.
DevOps:
Local Dev: Use Supabase CLI (supabase start) or hosted project. Next.js dev server runs alongside Android emulator.
Migrations: Maintain SQL in /supabase/schema.sql and apply via Supabase dashboard or CLI (supabase db push).

MScan: System Specification & Implementation Plan
This document outlines the technical specifications, architecture, and implementation details for the MScan project. It is intended to be the single source of truth for all development.
1. Frontend (Android - Kotlin)
The frontend will be a native Android application built with Kotlin. The UI must be a faithful implementation of the provided Figma designs, replicating the EVO app's aesthetic.
1.1. Core UI Components & Screens
Main Activity (MainActivity.kt):
Will host the bottom navigation bar seen in the designs (Acasa, Documente, Plăți, Card, Cont).
A new "Securitate" icon will be added to this navigation bar, which will launch the SecurityFragment.
Security Fragment (SecurityFragment.kt):
This is the main screen for the MScan module.
Layout (fragment_security.xml):
Search/Scan Bar:
An EditText for URL/text input.
An ImageButton with a + icon on the left for file uploads. This will open the Android file picker.
An ImageButton with an arrow/enter icon on the right to trigger the scan.
Email Breach Check:
A Button or TextView  pop-up for the first time entering, it asks the user "Doriți să efectuați o verificare de securitate a e-mailului dvs.?" and offers default “autocheck once a week?” checkbox, user has pre-entered email and just presses accept or decline, then notification gets sent to notifications with the result after backend processes request and runs the check and returns response. This happens for every check, every time it happens the notification gets sent.
Tapping this will show a dialog to enter an email and trigger the scan/email API call.
News Feed (RecyclerView):
Displays cybersecurity news articles in a grid layout (e.g., GridLayoutManager with 2 columns).
Each item (article_card.xml) will show a small image (ImageView) and a title (TextView).
The initial load will show 4 articles. The 4th item will be a special card with a + icon, which, when clicked, will load more articles into the feed.
Tapping any article card opens the link in a WebView within the app.
Report Button:
A button in the top right from EVO system design, a three dots icon, when pressed several options appear, one of which is the report button, another way to get to report fragment is through share button which also gets it here.
Tapping it will navigate to the ReportFragment.
Report Fragment (ReportFragment.kt):
Implements the "Report a Problem" screen from the Figma design.
Layout (fragment_report.xml): Spinner for problem type, EditText for description, Button for screenshot upload, EditText for email, and a "Send" Button.
1.2. Android OS Integration
Share Intent Handler:
Modify AndroidManifest.xml to declare an Activity that can handle ACTION_SEND intents for text (text/plain) and files (*/*).
Create two intent filters with different labels: "Scan with MScan" and "Report with MScan".
The Activity will check the intent's label to decide whether to launch the scanning process or the reporting fragment.
Default Browser Handler:
Declare an Activity in the AndroidManifest.xml with an intent filter for ACTION_VIEW and CATEGORY_BROWSABLE for http and https schemes.
When a link is opened, this activity will grab the URL, call the backend's /scan endpoint, and, based on the result, either show a warning or pass the URL via a new Intent to the user's actual default browser.
Lock Screen Camera Shortcut:
This is an advanced feature. It involves creating a TileService for the Quick Settings panel or investigating lock screen widget APIs available in the target Android version. The service will launch a camera activity dedicated to QR code/text scanning using a library like ML Kit.
2. Backend (Next.js & Supabase)
The backend is a Next.js application exposing API Route Handlers (/api/*) that orchestrate external scanning services and persist results to Supabase.
2.1. Setup
Framework: Next.js (App Router recommended). Route handlers under app/api/* for scan, report, news, and email checks.
Supabase Client: Use @supabase/supabase-js with service-role key ONLY on the server. Public anon key is NEVER bundled into the Android app (mobile talks only to Next.js backend).
External Services: VirusTotal, Google Safe Browsing, URLScan.io, HaveIBeenPwned.
Security: RLS enforced; all inserts done with service-role through server code; input validation & rate limiting (middleware) to mitigate abuse.
2.2. API Logic
POST /api/scan (multipart/form-data or JSON):
  Accepts file (future; via pre-signed upload) or url.
  Issues parallel (Promise.all) requests to VirusTotal, Google Safe Browsing, URLScan.io.
  Aggregates results → verdict heuristic (any malicious => malicious; else if any suspicious => suspicious; else safe).
  Persists row in scans.
  Returns verdict + summary.
POST /api/scan/email (JSON):
  Accepts { email }.
  Calls HaveIBeenPwned (range-based k-anonymity if using password endpoints; for breaches uses API key header).
  Stores result in email_checks.
  Returns breach list.
GET /api/news:
  Fetches & parses RSS feeds (stisc.gov.md, cyberevent.gov.md) with server-side 10m in-memory cache.
  Returns normalized article list.
POST /api/report (JSON):
  Accepts report payload + optional base64 screenshot.
  If screenshot provided → uploads to Supabase Storage (report_screenshots) and stores path.
  Inserts row in reports.
  Returns status.
3. API Specification (The Contract)
This defines the exact communication structure between the Kotlin frontend and the Next.js backend.
POST /scan
Request (multipart/form-data): file (optional file), url (optional string)
Response (200 OK):
{
  "verdict": "safe" | "suspicious" | "malicious",
  "details": "Scan complete. Found 2 issues."
}


POST /scan/email
Request (JSON): {"email": "user@example.com"}
Response (200 OK):
{
  "is_pwned": true,
  "breaches": ["Adobe", "LinkedIn"]
}


GET /news
Request: (None)
Response (200 OK):
{
  "articles": [
    {
      "title": "New Phishing Scam Alert",
      "link": "[https://stisc.gov.md/](https://stisc.gov.md/)...",
      "image_url": "https://..."
    }
  ]
}


POST /report
Request (JSON):
{
  "type": "Phishing Link",
  "description": "I received a suspicious SMS...",
  "screenshot_base64": "data:image/png;base64,iVBORw..."
}


Response (201 Created): {"status": "Report received"}

