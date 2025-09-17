# Guardio - Aplicație de Securitate Cibernetică

Guardio este o aplicație nativă Android de securitate cibernetică care se integrează perfect în sistemul de operare. Aceasta oferă utilizatorilor instrumente robuste pentru scanarea fișierelor/link-urilor, informații despre amenințările cibernetice și raportarea incidentelor.

## Caracteristici principale

### Scanner de securitate
- Scanare fișiere, URL-uri și conținut text
- Sistem Triple-Check: Utilizează trei API-uri separate pentru o analiză completă (VirusTotal, Google Safe Browsing, URLScan.io)
- Verificare breșe de email: Include o opțiune pentru verificarea adreselor de email împotriva bazei de date 'Have I Been Pwned'
- Istoric: Toate activitățile și rezultatele sunt înregistrate în backend-ul Supabase

### Feed de știri de securitate cibernetică
- Agregă cele mai recente știri și alerte folosind fluxuri RSS din surse oficiale (stisc.gov.md, cyberevent.gov.md)
- Articolele se deschid în browserul integrat în aplicație pentru o experiență utilizator continuă
- Sistem de notificări: Include un buton de activare/dezactivare configurabil pentru a primi alerte importante prin notificări push în aplicație

### Raportare incidente
- Proces simplificat pentru utilizatorii care raportează fișiere, linkuri, emailuri sau mesaje suspecte
- Permite atașarea capturilor de ecran pentru a oferi context
- Toate rapoartele sunt înregistrate în backend-ul Supabase

## Metode de acces și fluxuri de utilizare

- Integrare cu browser-ul implicit: Guardio poate fi setat ca handler implicit pentru link-uri
- Meniu nativ de partajare Android: Aplicația va avea două intenții de partajare:
  - Partajare către Guardio (Scanare): Începe imediat o scanare de securitate
  - Partajare către Guardio (Raportare): Deschide formularul de raportare
- Scurtătură pe ecranul de blocare: Un utilizator poate configura o scurtătură pentru a deschide instantaneu o vedere de cameră pentru scanarea codurilor QR
- Raportare post-scanare: După ce o scanare identifică un element ca fiind rău intenționat, apare un buton care permite utilizatorului să raporteze amenințarea
- Acces direct în aplicație: Interfața principală oferă trei puncte de intrare clare: Scanare, Știri și Raportare

## Arhitectura tehnică

### Stack Tehnologic
- Aplicație mobilă (Android): Kotlin + Jetpack Compose
- Backend: Next.js (Node 18+; API Route Handlers / Edge Functions pentru logica serverului)
- Bază de date și Autentificare: Supabase (PostgreSQL + Auth + Storage)
- Logging și Persistența datelor: Tabele Supabase cu Row Level Security (RLS)

### Backend
- API Endpoints:
  - POST /scan: Acceptă un fișier (multipart) sau URL
  - GET /news: Obține și analizează fluxuri RSS din sursele guvernamentale
  - POST /report: Acceptă un payload JSON cu detaliile raportului

## Instalare și configurare

### Backend (Next.js)
1. Navigați în directorul backend:
   ```bash
   cd backend
   ```

2. Instalați dependențele:
   ```bash
   npm install
   ```

3. Configurați variabilele de mediu (creați un fișier `.env.local`):
   ```
   VIRUSTOTAL_API_KEY=your_virustotal_api_key_here
   NEXT_PUBLIC_SUPABASE_URL=your_supabase_url
   SUPABASE_SERVICE_ROLE_KEY=your_supabase_service_key
   ```

4. Porniți serverul backend:
   ```bash
   npm run dev
   ```

### Frontend (Android)
1. Deschideți proiectul în Android Studio
2. Editați `app/src/main/java/com/example/gigahack_2025/network/NetworkConfig.kt` pentru a configura URL-ul backend-ului:
   
   Pentru emulatorul Android:
   ```kotlin
   const val BASE_URL = "http://10.0.2.2:3000/"
   ```
   
   Pentru dispozitive fizice:
   ```kotlin
   const val BASE_URL = "http://YOUR_IP_ADDRESS:3000/"
   ```

3. Executați aplicația pe un emulator sau dispozitiv fizic

## Supabase (Baza de date)

Schema bazei de date este disponibilă în directorul `supabase/schema.sql` și poate fi aplicată prin dashboard-ul Supabase sau CLI.

## Localizare

Aplicația este complet tradusă și funcțională în română, engleză și rusă.

## Licență

[License details to be added]

## Contribuții

Contribuțiile sunt binevenite! Vă rugăm să vedeți fișierul `CONTRIBUTING.md` pentru detalii despre cum puteți contribui la acest proiect.

## Contact

[Contact details to be added]