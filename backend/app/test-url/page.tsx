'use client';
import React, { useState } from 'react';

export default function TestUrlScan() {
  const [url, setUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const handleScan = async () => {
    if (!url) { setError('Enter a URL'); return; }
    setLoading(true); setResult(null); setError(null);
    try {
      const resp = await fetch('/api/scan/url', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ url })
      });
      const json = await resp.json();
      if (!resp.ok) setError(json.error || 'Scan failed'); else setResult(json);
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 700, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Test URL Scan</h1>
      <p>Enter a URL and submit. This calls /api/scan/url which uses urlscan.io if URLSCAN_API_KEY is set, else returns a mock safe result.</p>
      <div style={{ display: 'flex', gap: '0.5rem' }}>
        <input style={{ flex: 1 }} placeholder="https://example.com" value={url} onChange={e => setUrl(e.target.value)} />
        <button disabled={!url || loading} onClick={handleScan}>{loading ? 'Scanning...' : 'Scan URL'}</button>
      </div>
      {error && <p style={{ color: 'red', marginTop: '0.75rem' }}>{error}</p>}
      {result && (
        <pre style={{ background: '#111', color: '#0f0', padding: '1rem', marginTop: '1rem', overflowX: 'auto' }}>
{JSON.stringify(result, null, 2)}
        </pre>
      )}
      {result?.verdict && ['suspicious','malicious','vulnerable','not_safe'].includes(result.verdict) && (
        <div style={{ marginTop: '1rem', padding: '1rem', background: '#330', color: '#fc0', border: '1px solid #640' }}>
          <p style={{ margin: 0 }}>Verdict: <strong>{result.verdict}</strong>. Consider submitting an incident report.</p>
          <button style={{ marginTop: '0.5rem' }} onClick={() => {
            const params = new URLSearchParams({
              type: '7', // default to phishing category if URL related
              description: `URL scan flagged verdict: ${result.verdict} for ${result.url}`,
              source_item: result.url || url,
              detection_time: new Date().toISOString().slice(0,16)
            });
            window.location.href = `/report?${params.toString()}`;
          }}>Report this incident</button>
        </div>
      )}
    </div>
  );
}
