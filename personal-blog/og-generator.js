#!/usr/bin/env node
import fs from 'node:fs';
import path from 'node:path';

const root = new URL('.', import.meta.url).pathname;
const posts = path.join(root, 'posts');
const out = path.join(root, 'site', 'og');
fs.rmSync(out, { recursive: true, force: true });
fs.mkdirSync(out, { recursive: true });

const esc = s => String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');

for (const file of fs.readdirSync(posts).filter(f => f.endsWith('.md'))) {
  const raw = fs.readFileSync(path.join(posts, file), 'utf8');
  const m = raw.match(/^---\s*([\s\S]*?)\s*---/);
  const meta = {};
  (m?.[1] || '').split('\n').forEach(line => {
    const i = line.indexOf(':');
    if (i > 0) meta[line.slice(0, i).trim()] = line.slice(i + 1).trim();
  });
  if (meta.draft === 'true') continue;
  const title = esc(meta.title || file.replace(/\.md$/, ''));
  const tags = esc(meta.tags || '');
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="1200" height="630"><rect width="1200" height="630" fill="#101010"/><circle cx="1040" cy="90" r="220" fill="#2d2d2d"/><text x="80" y="100" fill="#999" font-family="Arial" font-size="24" letter-spacing="6">DARKMATTER · BLOG</text><text x="80" y="290" fill="#fff" font-family="Arial" font-size="58" font-weight="700">${title.slice(0,60)}</text><text x="80" y="365" fill="#aaa" font-family="Arial" font-size="26">${tags.slice(0,70)}</text><text x="80" y="550" fill="#777" font-family="Arial" font-size="21">darkmatter131420.github.io</text></svg>`;
  fs.writeFileSync(path.join(out, file.replace(/\.md$/, '.svg')), svg);
}
console.log('Generated OG SVG cards in site/og.');
