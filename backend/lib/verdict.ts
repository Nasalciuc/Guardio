export type SimpleVerdict = 'safe' | 'suspicious' | 'malicious';

export function aggregateVerdict(verdicts: SimpleVerdict[]): SimpleVerdict {
  if (verdicts.includes('malicious')) return 'malicious';
  if (verdicts.includes('suspicious')) return 'suspicious';
  return 'safe';
}
