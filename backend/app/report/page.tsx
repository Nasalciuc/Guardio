"use client";
import React, { useState, useEffect } from 'react';

// Category list derived from provided GOV form
const CATEGORIES = [
  { value: '1', label: 'MAL-13 (Malware general)' },
  { value: '2', label: 'THR-01 (Amenințari externe)' },
  { value: '3', label: 'SOC-10 (Suspiciuni de compromitere)' },
  { value: '4', label: 'COM-07 (Cont compromis)' },
  { value: '5', label: 'ZRO-18 (Zero-Day)' },
  { value: '6', label: 'VUL-02 (Vulnerabilități)' },
  { value: '7', label: 'PHI-12 (Phishing)' },
  { value: '8', label: 'EXP-03 (Exploits)' },
  { value: '9', label: 'INF-11 (Informare/Alarme de securitate)' },
  { value: '11', label: 'OPS-20 (Probleme operaționale)' },
  { value: '13', label: 'MRA-16 (Ransomware)' },
  { value: '14', label: 'MBN-15 (Botnet)' },
  { value: '15', label: 'MWR-14 (Worm)' },
  { value: '16', label: 'DOS-08 (DoS)' },
  { value: '17', label: 'MIS-06 (Misconfigurations)' },
  { value: '18', label: 'RPT-21 (False Positive)' },
  { value: '19', label: 'OTH-99 (Altele)' },
  { value: '21', label: 'INT-17 (Amenințări interne)' }
];

export default function ReportPage() {
  const [category, setCategory] = useState('');
  const [description, setDescription] = useState('');
  const [detectionTime, setDetectionTime] = useState('');
  const [affected, setAffected] = useState('');
  const [reporter, setReporter] = useState('');
  const [consent, setConsent] = useState(false);
  const [email, setEmail] = useState('');
  const [sourceItem, setSourceItem] = useState('');
  const [screenshot, setScreenshot] = useState<string | null>(null);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Hydration-safe query param parsing (runs only client-side)
    try {
      const usp = new URLSearchParams(window.location.search);
      const get = (k:string) => usp.get(k) || '';
      const cat = get('type'); if (cat) setCategory(cat);
      const desc = get('description'); if (desc) setDescription(desc);
      const det = get('detection_time'); if (det) setDetectionTime(det);
      const aff = get('affected_resources') || get('source_item'); if (aff) setAffected(aff);
      const rep = get('reporter'); if (rep) setReporter(rep);
      const em = get('email'); if (em) setEmail(em);
      const src = get('source_item'); if (src) setSourceItem(src);
      const cons = get('consent_public'); if (cons) setConsent(cons === 'true');
    } catch {}
  }, []);

  useEffect(() => {
    if (uploadFile) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = (reader.result as string).split(',')[1];
        setScreenshot(base64);
      };
      reader.readAsDataURL(uploadFile);
    } else {
      setScreenshot(null);
    }
  }, [uploadFile]);

  const handleSubmit = async () => {
    setSubmitting(true); setError(null); setResult(null);
    try {
      const resp = await fetch('/api/report', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          type: category,
          description,
          screenshot_base64: screenshot,
          email,
          source_item: sourceItem || affected,
          meta: {
            detection_time: detectionTime,
            affected_resources: affected,
            reporter,
            consent_public: consent,
            category_label: CATEGORIES.find(c=>c.value===category)?.label
          }
        })
      });
      const json = await resp.json();
      if (!resp.ok) setError(json.error || 'Report submission failed'); else setResult(json);
    } catch (e:any) {
      setError(e.message);
    } finally { setSubmitting(false); }
  };

  return (
    <div style={{ maxWidth: 760, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Cyber Incident Report</h1>
      <p style={{ fontSize: '0.9rem', lineHeight: 1.4 }}>Trimiteți un raport pentru un eveniment suspect sau malițios. Câmpurile marcate * sunt obligatorii.</p>

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Categorie *</label>
      <select value={category} onChange={e=>setCategory(e.target.value)} style={{ width: '100%', padding: '0.5rem' }}>
        <option value="">Selectați...</option>
        {CATEGORIES.map(c=> <option key={c.value} value={c.value}>{c.label}</option>)}
      </select>

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Data detectării *</label>
      <input type="datetime-local" value={detectionTime} onChange={e=>setDetectionTime(e.target.value)} style={{ width: '100%', padding: '0.5rem' }} />

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Resurse/Sisteme afectate</label>
      <input value={affected} onChange={e=>setAffected(e.target.value)} placeholder="Ex: IP, URL, hash fișier" style={{ width: '100%', padding: '0.5rem' }} />

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Descriere eveniment *</label>
      <textarea value={description} onChange={e=>setDescription(e.target.value)} rows={6} style={{ width: '100%', padding: '0.75rem', fontFamily: 'monospace' }} placeholder="Detaliați evenimentul și indicatorii observați..." />

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Raportor</label>
      <input value={reporter} onChange={e=>setReporter(e.target.value)} placeholder="Instituție / Nume" style={{ width: '100%', padding: '0.5rem' }} />

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Email contact</label>
      <input type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com" style={{ width: '100%', padding: '0.5rem' }} />

      <label style={{ display: 'block', marginTop: '1rem', fontWeight: 600 }}>Atașament / Captură ecran</label>
      <input type="file" onChange={e=>setUploadFile((e.target.files && e.target.files[0]) || null)} />
      {uploadFile && <p style={{ fontSize: '0.75rem' }}>Fișier selectat: {uploadFile.name}</p>}

      <label style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
        <input type="checkbox" checked={consent} onChange={e=>setConsent(e.target.checked)} />
        <span style={{ fontSize: '0.85rem' }}>Sunt de acord ca informațiile tehnice să fie publice pentru învățare (consimțământ).</span>
      </label>

      <button disabled={!category || !description || !detectionTime || submitting} onClick={handleSubmit} style={{ marginTop: '1.25rem', padding: '0.75rem 1.5rem', background: '#0b5', color: '#fff', border: 'none', cursor: 'pointer', borderRadius: 4 }}>
        {submitting ? 'Se trimite...' : 'Trimite raportul'}
      </button>

      {error && <p style={{ color: 'red', marginTop: '1rem' }}>Eroare: {error}</p>}
      {result && (
        <div style={{ marginTop: '1.25rem' }}>
          <h3>Status</h3>
          <pre style={{ background: '#111', color: '#0f0', padding: '1rem', overflowX: 'auto' }}>{JSON.stringify(result, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}
