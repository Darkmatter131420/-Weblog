import fs from 'node:fs';
import path from 'node:path';

const root = new URL('.', import.meta.url).pathname;
const postsDir = path.join(root, 'posts');
const siteDir = path.join(root, 'site');
const posts = fs.readdirSync(postsDir).filter(f => f.endsWith('.md')).map(file => {
  const raw = fs.readFileSync(path.join(postsDir, file), 'utf8');
  const [, fm = '', body = raw] = raw.split(/^---\s*$/m);
  const meta = {};
  fm.split('\n').forEach(line => { const i = line.indexOf(':'); if (i > 0) meta[line.slice(0, i).trim()] = line.slice(i + 1).trim(); });
  return { file, slug: file.replace(/\.md$/, ''), title: meta.title || file, date: meta.date || '', tags: (meta.tags || '').split(',').map(s => s.trim()).filter(Boolean), excerpt: meta.excerpt || '', content: body.trim() };
}).sort((a,b) => b.date.localeCompare(a.date));

fs.rmSync(siteDir, { recursive: true, force: true });
fs.mkdirSync(path.join(siteDir, 'posts'), { recursive: true });

const esc = s => s.replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
function markdown(md) {
  let out = esc(md).replace(/^###### (.*)$/gm,'<h6>$1</h6>').replace(/^##### (.*)$/gm,'<h5>$1</h5>').replace(/^#### (.*)$/gm,'<h4>$1</h4>').replace(/^### (.*)$/gm,'<h3>$1</h3>').replace(/^## (.*)$/gm,'<h2>$1</h2>').replace(/^# (.*)$/gm,'<h1>$1</h1>');
  out = out.replace(/^&gt; (.*)$/gm,'<blockquote>$1</blockquote>').replace(/\*\*(.*?)\*\*/g,'<strong>$1</strong>').replace(/`([^`]+)`/g,'<code>$1</code>');
  out = out.split(/\n\n+/).map(p => /^<h\d|^<blockquote/.test(p.trim()) ? p : `<p>${p.replace(/\n/g,'<br>')}</p>`).join('\n');
  return out;
}
const baseCss = fs.readFileSync(path.join(root, 'style.css'), 'utf8');
const shell = (title, body) => `<!doctype html><html lang="zh-CN"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="description" content="${esc(title)}"><title>${esc(title)} · Darkmatter</title><link rel="stylesheet" href="/ -Weblog/personal-blog/style.css" onerror="this.href='../style.css'"><style>${baseCss}.article{max-width:780px;margin:100px auto;padding-bottom:100px}.article h1{font-size:clamp(38px,6vw,64px);line-height:1.12;letter-spacing:-.05em}.article .lead{font-size:19px;color:var(--muted)}.article-body{font-size:17px}.article-body h2{margin-top:48px}.article-body blockquote{border-left:3px solid var(--accent);padding:10px 20px;color:var(--muted)}.article-body code{background:var(--accent-2);padding:2px 6px;border-radius:5px}</style></head><body><header class="site-header"><nav class="nav container"><a class="brand" href="/ -Weblog/personal-blog/"> <span>DM</span> Darkmatter</a><div class="nav-links"><a href="/ -Weblog/personal-blog/#articles">文章</a><a href="/ -Weblog/personal-blog/#about">关于</a><a href="https://github.com/Darkmatter131420">GitHub ↗</a></div></nav></header>${body}<footer class="footer container"><span>© ${new Date().getFullYear()} Darkmatter</span><span>Markdown · GitHub Pages</span></footer></body></html>`.replaceAll('/ -Weblog','/-Weblog');

const cards = posts.map((p,i)=>`<article class="post ${i===0?'featured':''}"><div class="post-meta"><span>${p.tags.join(' · ')}</span><time>${p.date}</time></div><h3>${esc(p.title)}</h3><p>${esc(p.excerpt)}</p><a class="read-more" href="posts/${p.slug}.html">阅读全文 <span>→</span></a></article>`).join('\n');
const index = fs.readFileSync(path.join(root,'index.html'),'utf8').replace(/<div class="post-grid" id="postGrid">[\s\S]*?<\/div>\s*<p id="emptyState"/, `<div class="post-grid" id="postGrid">${cards}</div><p id="emptyState"`);
fs.writeFileSync(path.join(siteDir,'index.html'), index.replace('href="./style.css"','href="./style.css"').replace('src="./script.js"','src="./script.js"'));
fs.copyFileSync(path.join(root,'style.css'), path.join(siteDir,'style.css'));
fs.copyFileSync(path.join(root,'script.js'), path.join(siteDir,'script.js'));
posts.forEach(p => fs.writeFileSync(path.join(siteDir,'posts',`${p.slug}.html`), shell(p.title, `<main class="article container"><a class="read-more" href="../">← 返回首页</a><p class="eyebrow">${p.tags.join(' · ')} · ${p.date}</p><h1>${esc(p.title)}</h1><p class="lead">${esc(p.excerpt)}</p><div class="article-body">${markdown(p.content)}</div></main>`)));
const feed = `<?xml version="1.0" encoding="UTF-8"?><rss version="2.0"><channel><title>Darkmatter · 个人博客</title><link>https://darkmatter131420.github.io/-Weblog/personal-blog/</link><description>Darkmatter 的个人博客</description>${posts.map(p=>`<item><title>${esc(p.title)}</title><link>https://darkmatter131420.github.io/-Weblog/personal-blog/posts/${p.slug}.html</link><pubDate>${new Date(p.date).toUTCString()}</pubDate><description>${esc(p.excerpt)}</description></item>`).join('')}</channel></rss>`;
fs.writeFileSync(path.join(siteDir,'feed.xml'), feed);
fs.writeFileSync(path.join(siteDir,'posts.json'), JSON.stringify(posts.map(({content,...p})=>p),null,2));
console.log(`Built ${posts.length} posts.`);
