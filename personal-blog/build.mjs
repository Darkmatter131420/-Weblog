import fs from 'node:fs';
import path from 'node:path';

const root = new URL('.', import.meta.url).pathname;
const postsDir = path.join(root, 'posts');
const siteDir = path.join(root, 'site');
const posts = fs.readdirSync(postsDir).filter(f => f.endsWith('.md')).map(file => {
  const raw = fs.readFileSync(path.join(postsDir, file), 'utf8');
  const parts = raw.split(/^---\s*$/m);
  const fm = parts.length > 2 ? parts[1] : '';
  const body = parts.length > 2 ? parts.slice(2).join('---') : raw;
  const meta = {};
  fm.split('\n').forEach(line => { const i = line.indexOf(':'); if (i > 0) meta[line.slice(0, i).trim()] = line.slice(i + 1).trim(); });
  return { file, slug: file.replace(/\.md$/, ''), title: meta.title || file, date: meta.date || '', tags: (meta.tags || '').split(',').map(s => s.trim()).filter(Boolean), excerpt: meta.excerpt || '', content: body.trim() };
}).sort((a,b) => b.date.localeCompare(a.date));

fs.rmSync(siteDir, { recursive: true, force: true });
fs.mkdirSync(path.join(siteDir, 'posts'), { recursive: true });
const esc = s => String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
const attr = s => esc(s).replaceAll("'",'&#39;');
function readingTime(md) { return Math.max(1, Math.ceil(md.replace(/[#>*`\-]/g,' ').trim().length / 350)); }
function slugify(s) { return s.toLowerCase().replace(/[^a-z0-9\u4e00-\u9fff]+/g,'-').replace(/^-|-$/g,''); }
function markdown(md) {
  const lines = esc(md).split('\n'); let html = [], toc = [], inCode = false, code = [];
  for (const line of lines) {
    if (line.startsWith('```')) { if (inCode) { html.push(`<pre><code>${code.join('\n')}</code></pre>`); code=[]; inCode=false; } else inCode=true; continue; }
    if (inCode) { code.push(line); continue; }
    const m = line.match(/^(#{1,3})\s+(.+)$/);
    if (m) { const level=m[1].length, text=m[2], id=slugify(text); if(level>=2) toc.push({level,text,id}); html.push(`<h${level} id="${id}">${text}</h${level}>`); continue; }
    if (line.startsWith('> ')) { html.push(`<blockquote>${line.slice(2)}</blockquote>`); continue; }
    if (line.startsWith('- ')) { const last=html[html.length-1]; if(!last || !last.startsWith('<ul>')) html.push('<ul>'); html.push(`<li>${line.slice(2)}</li>`); continue; }
    if (!line.trim()) { if(html[html.length-1]?.startsWith('<ul>')) html.push('</ul>'); continue; }
    let p=line.replace(/\*\*(.*?)\*\*/g,'<strong>$1</strong>').replace(/`([^`]+)`/g,'<code>$1</code>').replace(/\[(.*?)\]\((.*?)\)/g,'<a href="$2">$1</a>');
    html.push(`<p>${p}</p>`);
  }
  if(inCode) html.push(`<pre><code>${code.join('\n')}</code></pre>`); if(html[html.length-1]==='<ul>') html.push('</ul>');
  return {html:html.join('\n'),toc};
}
const baseCss = fs.readFileSync(path.join(root, 'style.css'), 'utf8');
const shell = (title, desc, body, canonical) => `<!doctype html><html lang="zh-CN"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="description" content="${attr(desc)}"><meta property="og:title" content="${attr(title)}"><meta property="og:description" content="${attr(desc)}"><meta property="og:type" content="article"><link rel="canonical" href="${canonical}"><title>${attr(title)} · Darkmatter</title><link rel="alternate" type="application/rss+xml" title="Darkmatter RSS" href="../feed.xml"><link rel="stylesheet" href="../style.css"><style>${baseCss}.article{max-width:900px;margin:90px auto;padding-bottom:100px}.article h1{font-size:clamp(38px,6vw,64px);line-height:1.12;letter-spacing:-.05em}.article .lead{font-size:19px;color:var(--muted)}.article-layout{display:grid;grid-template-columns:minmax(0,1fr) 210px;gap:56px;margin-top:40px}.article-body{font-size:18px;line-height:1.9}.article-body h2{margin-top:48px}.article-body blockquote{border-left:3px solid var(--accent);padding:10px 20px;color:var(--muted);background:var(--accent-2)}.article-body pre{overflow:auto;padding:18px;border-radius:12px;background:#151515;color:#eee}.article-body code{background:var(--accent-2);padding:2px 6px;border-radius:5px}.toc{position:sticky;top:90px;align-self:start;font-size:14px}.toc a{display:block;padding:5px 0;color:var(--muted)}.post-nav{display:flex;justify-content:space-between;gap:20px;border-top:1px solid var(--border);margin-top:70px;padding-top:25px}.post-nav a{max-width:45%}@media(max-width:800px){.article-layout{display:block}.toc{position:static;margin-bottom:35px}.post-nav{display:block}.post-nav a{display:block;max-width:none;margin:12px 0}}</style></head><body><header class="site-header"><nav class="nav container"><a class="brand" href="../"><span>DM</span> Darkmatter</a><div class="nav-links"><a href="../#articles">文章</a><a href="../#about">关于</a><a href="https://github.com/Darkmatter131420">GitHub ↗</a></div></nav></header>${body}<footer class="footer container"><span>© ${new Date().getFullYear()} Darkmatter</span><span>Markdown · GitHub Pages</span></footer></body></html>`;

const cards = posts.map((p,i)=>`<article class="post ${i===0?'featured':''}" data-tags="${attr(p.tags.join(' '))}" data-title="${attr(p.title)}" data-excerpt="${attr(p.excerpt)}"><div class="post-meta"><span>${p.tags.map(esc).join(' · ')}</span><time>${p.date}</time></div><h3>${esc(p.title)}</h3><p>${esc(p.excerpt)}</p><a class="read-more" href="posts/${p.slug}.html">阅读全文 <span>→</span></a></article>`).join('\n');
let index = fs.readFileSync(path.join(root,'index.html'),'utf8').replace(/<div class="post-grid" id="postGrid">[\s\S]*?<\/div>\s*<p id="emptyState"/, `<div class="post-grid" id="postGrid">${cards}</div><p id="emptyState"`);
index = index.replace('<a href="#articles">文章</a>','<a href="#articles">文章</a><a href="archive.html">归档</a>');
fs.writeFileSync(path.join(siteDir,'index.html'), index); fs.copyFileSync(path.join(root,'style.css'), path.join(siteDir,'style.css')); fs.copyFileSync(path.join(root,'script.js'), path.join(siteDir,'script.js'));

posts.forEach((p,i) => { const parsed=markdown(p.content), prev=posts[i+1], next=posts[i-1]; const toc=parsed.toc.length?`<aside class="toc"><strong>目录</strong>${parsed.toc.map(x=>`<a href="#${x.id}">${x.text}</a>`).join('')}</aside>`:''; const body=`<main class="article container"><a class="read-more" href="../">← 返回首页</a><p class="eyebrow">${p.tags.map(esc).join(' · ')} · ${p.date} · ${readingTime(p.content)} 分钟阅读</p><h1>${esc(p.title)}</h1><p class="lead">${esc(p.excerpt)}</p><div class="article-layout"><div><div class="article-body">${parsed.html}</div><nav class="post-nav">${prev?`<a href="${prev.slug}.html">← ${esc(prev.title)}</a>`:'<span>'}${next?`<a href="${next.slug}.html">${esc(next.title)} →</a>`:'<span>'}</nav></div>${toc}</div></main>`; fs.writeFileSync(path.join(siteDir,'posts',`${p.slug}.html`), shell(p.title,p.excerpt,body,`https://darkmatter131420.github.io/-Weblog/personal-blog/posts/${p.slug}.html`)); });

const archiveGroups={}; posts.forEach(p=>(archiveGroups[p.date.slice(0,4)]??=[]).push(p)); const archiveBody=Object.entries(archiveGroups).map(([year,items])=>`<section><p class="eyebrow">${year}</p>${items.map(p=>`<article class="post"><div class="post-meta"><span>${p.tags.join(' · ')}</span><time>${p.date}</time></div><h3><a href="posts/${p.slug}.html">${esc(p.title)}</a></h3><p>${esc(p.excerpt)}</p></article>`).join('')}</section>`).join('');
fs.writeFileSync(path.join(siteDir,'archive.html'),shell('文章归档','按年份浏览 Darkmatter 的全部文章',`<main class="article container"><p class="eyebrow">ARCHIVE</p><h1>文章归档</h1>${archiveBody}</main>`,'https://darkmatter131420.github.io/-Weblog/personal-blog/archive.html'));

const feed=`<?xml version="1.0" encoding="UTF-8"?><rss version="2.0"><channel><title>Darkmatter · 个人博客</title><link>https://darkmatter131420.github.io/-Weblog/personal-blog/</link><description>Darkmatter 的个人博客</description>${posts.map(p=>`<item><title>${esc(p.title)}</title><link>https://darkmatter131420.github.io/-Weblog/personal-blog/posts/${p.slug}.html</link><pubDate>${new Date(p.date).toUTCString()}</pubDate><description>${esc(p.excerpt)}</description></item>`).join('')}</channel></rss>`;
fs.writeFileSync(path.join(siteDir,'feed.xml'),feed); fs.writeFileSync(path.join(siteDir,'posts.json'),JSON.stringify(posts.map(({content,...p})=>({...p,readingTime:readingTime(content)})),null,2));
fs.writeFileSync(path.join(siteDir,'sitemap.xml'),`<?xml version="1.0" encoding="UTF-8"?><urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"><url><loc>https://darkmatter131420.github.io/-Weblog/personal-blog/</loc></url><url><loc>https://darkmatter131420.github.io/-Weblog/personal-blog/archive.html</loc></url>${posts.map(p=>`<url><loc>https://darkmatter131420.github.io/-Weblog/personal-blog/posts/${p.slug}.html</loc><lastmod>${p.date}</lastmod></url>`).join('')}</urlset>`);
console.log(`Built ${posts.length} posts.`);
