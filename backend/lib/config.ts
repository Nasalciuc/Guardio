// Centralized configuration access with light caching.

let cachedVTKey: string | null | undefined;

export function getVirusTotalApiKey(): string | undefined {
  if (cachedVTKey !== undefined) return cachedVTKey || undefined;
  cachedVTKey = (process.env.VIRUSTOTAL_API_KEY || process.env.VT_API_KEY || process.env.NEXT_PUBLIC_VIRUSTOTAL_API_KEY || '') || null;
  return cachedVTKey || undefined;
}

export function isMockMode(): boolean {
  // Future: inspect flags to decide whether to bypass real calls.
  return !getVirusTotalApiKey();
}
