import { SimpleVerdict } from './verdict';

interface ScannerResult {
  verdict: SimpleVerdict;
  raw: any;
}

const delay = (ms: number) => new Promise(r => setTimeout(r, ms));

export async function fetchVirusTotal(url: string): Promise<ScannerResult> {
  // Placeholder minimal logic; real integration would call VT API with hash or URL
  return { verdict: 'safe', raw: { mock: true, source: 'virustotal', url } };
}

export async function fetchGoogleSafeBrowsing(url: string): Promise<ScannerResult> {
  if (process.env.MOCK_GOOGLE_SAFE_BROWSING === 'true') {
    await delay(50);
    return { verdict: 'safe', raw: { mock: true, source: 'gsb', url } };
  }
  // Real call would go here
  return { verdict: 'safe', raw: { note: 'real call not implemented', url } };
}

export type UrlScanProgress = (event: { phase: string; detail?: any }) => Promise<void> | void;

export async function fetchUrlScan(url: string, onProgress?: UrlScanProgress): Promise<ScannerResult> {
  const key = process.env.URLSCAN_API_KEY;
  if (!key) {
  try { await onProgress?.({ phase: 'mock_no_key' }); } catch {}
    return { verdict: 'safe', raw: { mock: true, source: 'urlscan', url } };
  }
  try {
    const submitResp = await fetch('https://urlscan.io/api/v1/scan/', {
      method: 'POST',
      headers: {
        'API-Key': key,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({ url, visibility: 'private' })
    });
    if (!submitResp.ok) {
      const errTxt = await submitResp.text();
      try { await onProgress?.({ phase: 'error_submit_failed', detail: { status: submitResp.status } }); } catch {}
      return { verdict: 'suspicious', raw: { error: 'submit_failed', detail: errTxt, status: submitResp.status }, };
    }
    const submitJson: any = await submitResp.json();
    try { await onProgress?.({ phase: 'submitted', detail: { uuid: submitJson?.uuid } }); } catch {}
    const uuid = submitJson?.uuid;
    let resultJson: any = null;
    if (uuid) {
      for (let i = 0; i < 6; i++) { // ~18s total
        await delay(3000);
        const resResp = await fetch(`https://urlscan.io/api/v1/result/${uuid}/`, { headers: { 'Accept': 'application/json' } });
        if (resResp.ok) {
          resultJson = await resResp.json();
          // Only break if page data present
          if (resultJson?.page) break;
        } else if (resResp.status === 404) {
          // Not ready yet
        } else {
          break;
        }
        try { await onProgress?.({ phase: 'poll_attempt', detail: { attempt: i + 1, hasPage: !!resultJson?.page } }); } catch {}
      }
    }
    let verdict: SimpleVerdict = 'safe';
    const maliciousCount = resultJson?.verdicts?.overall?.malicious || 0;
    const suspiciousCount = resultJson?.verdicts?.overall?.suspicious || 0;
    if (maliciousCount > 0) verdict = 'malicious';
    else if (suspiciousCount > 0) verdict = 'suspicious';
    try { await onProgress?.({ phase: 'completed', detail: { verdict } }); } catch {}
    return { verdict, raw: { submit: submitJson, result: resultJson } };
  } catch (e: any) {
    try { await onProgress?.({ phase: 'error_exception', detail: { message: e?.message } }); } catch {}
    return { verdict: 'suspicious', raw: { error: 'exception', message: e?.message, url } };
  }
}

export async function fetchHIBP(email: string): Promise<{ is_pwned: boolean; breaches: string[]; raw: any; }> {
  if (process.env.MOCK_HIBP === 'true') {
    return { is_pwned: false, breaches: [], raw: { mock: true, source: 'hibp', email } };
  }
  return { is_pwned: false, breaches: [], raw: { note: 'real call not implemented', email } };
}
