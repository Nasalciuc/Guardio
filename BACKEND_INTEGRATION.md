# Backend Integration Guide

This document explains how to connect the Android frontend with the Node.js backend for file scanning, URL scanning, and problem reporting.

## Backend Setup

1. **Navigate to the backend directory:**
   ```bash
   cd backend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Set up environment variables (optional):**
   Create a `.env.local` file in the backend directory:
   ```env
   VIRUSTOTAL_API_KEY=your_virustotal_api_key_here
   NEXT_PUBLIC_SUPABASE_URL=your_supabase_url
   SUPABASE_SERVICE_ROLE_KEY=your_supabase_service_key
   ```

4. **Start the backend server:**
   ```bash
   npm run dev
   ```
   The backend will be available at `http://localhost:3000`

## Frontend Configuration

The Android app is configured to connect to the backend at `http://10.0.2.2:3000` (for Android emulator).

### Network Configuration

Edit `app/src/main/java/com/example/gigahack_2025/network/NetworkConfig.kt` to change the backend URL:

```kotlin
object NetworkConfig {
    // For Android emulator
    const val BASE_URL = "http://10.0.2.2:3000/"
    
    // For physical device, use your computer's IP address
    // const val BASE_URL = "http://192.168.1.100:3000/"
}
```

### For Physical Device Testing

1. Find your computer's IP address:
   - Windows: `ipconfig`
   - Mac/Linux: `ifconfig` or `ip addr`

2. Update `NetworkConfig.kt` with your IP address:
   ```kotlin
   const val BASE_URL = "http://YOUR_IP_ADDRESS:3000/"
   ```

3. Make sure your computer and phone are on the same network

## Features

### 1. URL Scanning
- **Endpoint:** `POST /api/scan`
- **Input:** JSON with `url` field
- **Output:** Verdict (safe/malicious/suspicious) and details
- **APIs Used:** VirusTotal, Google Safe Browsing, URLScan.io

### 2. File Scanning
- **Endpoint:** `POST /api/scan/file`
- **Input:** Multipart form data with file
- **Output:** File analysis results with verdict
- **APIs Used:** VirusTotal

### 3. Problem Reporting
- **Endpoint:** `POST /api/report`
- **Input:** JSON with problem details
- **Output:** Report confirmation
- **Storage:** Supabase database

## API Response Examples

### URL Scan Response
```json
{
  "verdict": "safe",
  "details": "Scan complete."
}
```

### File Scan Response
```json
{
  "filename": "document.pdf",
  "size": 1024000,
  "sha256": "abc123...",
  "verdict": "safe",
  "vt_status": "submitted",
  "vt_id": "analysis_id",
  "note": "File submitted to VirusTotal"
}
```

### Report Response
```json
{
  "status": "Report received",
  "id": "report_id",
  "stub_mode": false,
  "downgraded_no_meta": false
}
```

## Error Handling

The frontend includes fallback mechanisms:

1. **Network Errors:** Falls back to mock results
2. **Backend Unavailable:** Shows mock data with error message
3. **Invalid Responses:** Uses default safe verdict

## Testing

1. **Start the backend server**
2. **Run the Android app**
3. **Test URL scanning:** Enter a URL and tap the search button
4. **Test file scanning:** Attach a file and tap the search button
5. **Test problem reporting:** Fill out the report form and submit

## Troubleshooting

### Common Issues

1. **Connection Refused:**
   - Ensure backend is running on port 3000
   - Check firewall settings
   - Verify IP address configuration

2. **Timeout Errors:**
   - Increase timeout values in `NetworkConfig.kt`
   - Check network connectivity

3. **CORS Issues:**
   - Backend should handle CORS for API endpoints
   - Check browser console for CORS errors

### Debug Mode

Enable HTTP logging by checking the Android logs:
```bash
adb logcat | grep "OkHttp"
```

## Security Notes

- API keys should be stored securely on the backend
- Use HTTPS in production
- Implement proper authentication for production use
- Validate all inputs on both frontend and backend
