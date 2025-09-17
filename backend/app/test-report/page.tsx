"use client";
import React, { useState } from 'react';

export default function TestReportApi() {
  const [payload, setPayload] = useState(`{\n  "type": "7",\n  "description": "Test phishing incident",\n  "source_item": "https://evil.example/phish",\n  "email": "tester@example.com",\n  "meta": {\n    "detection_time": "2025-09-14T10:00",\n    "affected_resources": "https://evil.example/phish",\n    "reporter": "QA",\n    "consent_public": true\n  }\n}`);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const invoke = async () => {
    setLoading(true); setError(null); setResult(null);
    try {
      const resp = await fetch('/api/report', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: payload });
      const json = await resp.json();
      if (!resp.ok) setError(json.error || 'Request failed'); else setResult(json);
    } catch (e:any) { setError(e.message); }
    finally { setLoading(false); }
  };

  return (
    <div style={{ maxWidth: 800, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Test Report API</h1>
      <p>Compose JSON and send to <code>/api/report</code>.</p>
      <textarea value={payload} onChange={e=>setPayload(e.target.value)} rows={16} style={{ width: '100%', fontFamily: 'monospace', padding: '0.75rem' }} />
      <button disabled={loading} onClick={invoke} style={{ marginTop: '0.75rem' }}>{loading ? 'Sending...' : 'Send Report'}</button>
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {result && <pre style={{ background: '#111', color: '#0f0', padding: '1rem', marginTop: '1rem', overflowX: 'auto' }}>{JSON.stringify(result, null, 2)}</pre>}
    </div>
  );
}
