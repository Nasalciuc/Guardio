'use client';
import React, { useState, useEffect, useRef } from 'react';

export default function TestUploadPage() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [analysis, setAnalysis] = useState<any>(null);
  const pollTimer = useRef<any>(null);
  const pollCount = useRef<number>(0);

  useEffect(() => {
    if (result?.vt_id && result?.vt_status === 'submitted') {
      // Start polling
      if (pollTimer.current) clearTimeout(pollTimer.current);
      const poll = async () => {
        try {
          const r = await fetch(`/api/scan/file/${result.vt_id}`);
          const j = await r.json();
            setAnalysis(j);
          if (j.status && j.status !== 'completed' && !j.error) {
            pollCount.current += 1;
            if (pollCount.current >= 6) {
              // Fallback: try hash lookup and then stop.
              if (result.sha256) {
                try {
                  const fr = await fetch(`/api/scan/file/hash/${result.sha256}`);
                  const fj = await fr.json();
                  if (fj.verdict) {
                    setResult((prev: any) => ({ ...prev, final_verdict: fj.verdict, analysis_stats: fj.stats, via: 'hash_lookup' }));
                  }
                } catch (e) { /* ignore */ }
              }
              return; // stop polling
            }
            pollTimer.current = setTimeout(poll, 5000);
          } else if (j.status === 'completed' && j.verdict) {
            // merge final verdict into initial result
            setResult((prev: any) => ({ ...prev, final_verdict: j.verdict, analysis_stats: j.stats }));
          }
        } catch (e) { /* ignore transient errors */ }
      };
      poll();
    }
    return () => { if (pollTimer.current) clearTimeout(pollTimer.current); };
  }, [result?.vt_id, result?.vt_status]);
  const [error, setError] = useState<string | null>(null);

  const handleUpload = async () => {
    if (!file) { setError('Select a file first'); return; }
    setLoading(true); setError(null); setResult(null);
    try {
      const form = new FormData();
      form.append('file', file);
      const resp = await fetch('/api/scan/file', { method: 'POST', body: form });
      const json = await resp.json();
      if (!resp.ok) {
        setError(json.error || 'Upload failed');
      } else {
        setResult(json);
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Test File Upload Scan</h1>
  <p>Select a file and submit to the /api/scan/file endpoint. Requires VIRUSTOTAL_API_KEY (or VT_API_KEY) env var placed in backend/.env.local for real submission; otherwise returns mock verdict.</p>
      <input type="file" onChange={e => setFile(e.target.files?.[0] || null)} />
      <button disabled={!file || loading} onClick={handleUpload} style={{ marginLeft: '0.5rem' }}>
        {loading ? 'Uploading...' : 'Upload & Scan'}
      </button>
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
      {result && (
        <>
          <h3 style={{ marginTop: '1.5rem' }}>Initial Submission Response</h3>
          <pre style={{ background: '#111', color: '#0f0', padding: '1rem', overflowX: 'auto' }}>
{JSON.stringify(result, null, 2)}
          </pre>
        </>
      )}
      {analysis && !result?.final_verdict && analysis.status !== 'completed' && (
        <p style={{ marginTop: '0.75rem' }}>Analysis status: {analysis.status} (polling... attempt {pollCount.current + 1}/6)</p>
      )}
      {!result?.final_verdict && pollCount.current >= 6 && (
        <p style={{ marginTop: '0.75rem', color: '#fa0' }}>Polling limit reached. Displaying fallback hash lookup result if available.</p>
      )}
      {result?.final_verdict && (
        <>
          <h3>Final Verdict</h3>
          <pre style={{ background: '#111', color: '#0ff', padding: '1rem', overflowX: 'auto' }}>
{JSON.stringify({ vt_id: result.vt_id, final_verdict: result.final_verdict, stats: result.analysis_stats }, null, 2)}
          </pre>
        </>
      )}
      {(result?.final_verdict || result?.verdict) && ['suspicious','malicious','vulnerable','not_safe'].includes(result.final_verdict || result.verdict) && (
        <div style={{ marginTop: '1rem', padding: '1rem', background: '#301', color: '#f99', border: '1px solid #700' }}>
          <p style={{ margin: 0 }}>The file scan indicates a potential threat (<strong>{result.final_verdict || result.verdict}</strong>). You can file a report.</p>
          <button style={{ marginTop: '0.5rem' }} onClick={() => {
            const params = new URLSearchParams({
              type: '6', // default to vulnerability category for files
              description: `File scan verdict: ${(result.final_verdict || result.verdict)} for ${result.sha256}`,
              source_item: result.sha256 || '',
              detection_time: new Date().toISOString().slice(0,16)
            });
            window.location.href = `/report?${params.toString()}`;
          }}>Report this file</button>
        </div>
      )}
      {(!result?.final_verdict && result?.sha256) && (
        <button style={{ marginTop: '0.75rem' }} onClick={async () => {
          try {
            const fr = await fetch(`/api/scan/file/hash/${result.sha256}`);
            const fj = await fr.json();
            if (fj.verdict) {
              setResult((prev: any) => ({ ...prev, final_verdict: fj.verdict, analysis_stats: fj.stats, via: 'manual_hash_lookup' }));
            } else {
              setError(fj.error || 'Hash lookup returned no verdict yet');
            }
          } catch (e: any) {
            setError(e.message);
          }
        }}>Force Hash Lookup</button>
      )}
    </div>
  );
}
