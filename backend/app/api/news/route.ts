import { NextResponse } from 'next/server';
import Parser from 'rss-parser';

export const runtime = 'nodejs';

const parser = new Parser();
const FEEDS = [
  'https://stisc.gov.md/feed',
  'https://cyberevent.gov.md/feed'
];

let cache: { ts: number; articles: any[] } | null = null;
const ttl = parseInt(process.env.NEWS_CACHE_TTL_SECONDS || '600', 10) * 1000;

export async function GET() {
  try {
    const now = Date.now();
    if (cache && (now - cache.ts) < ttl) {
      return NextResponse.json({ articles: cache.articles });
    }

    const articles: any[] = [];
    for (const feedUrl of FEEDS) {
      try {
        const feed = await parser.parseURL(feedUrl);
  feed.items.slice(0, 10).forEach((item: any) => {
          articles.push({
            title: item.title,
            link: item.link,
            image_url: item.enclosure?.url || null
          });
        });
      } catch (e) {
        console.error('Feed error', feedUrl, e);
      }
    }

    cache = { ts: now, articles };
    return NextResponse.json({ articles });
  } catch (e: any) {
    console.error(e);
    return NextResponse.json({ error: 'Internal error' }, { status: 500 });
  }
}
