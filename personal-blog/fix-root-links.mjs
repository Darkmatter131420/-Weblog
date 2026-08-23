import fs from 'node:fs';
import path from 'node:path';

const site = path.join(new URL('.', import.meta.url).pathname, 'site');
const rootNav = `<a class="brand" href="./"><span>DM</span> Darkmatter</a><div class="nav-links"><a href="./#articles">文章</a><a href="./archive.html">归档</a><a href="./tags.html">标签</a><a href="./search.html">搜索</a><a href="./#about">关于</a><a href="https://github.com/Darkmatter131420" target="_blank" rel="noreferrer">GitHub ↗</a></div>`;
const postNav = `<a class="brand" href="../"><span>DM</span> Darkmatter</a><div class="nav-links"><a href="../#articles">文章</a><a href="../archive.html">归档</a><a href="../tags.html">标签</a><a href="../search.html">搜索</a><a href="../#about">关于</a><a href="https://github.com/Darkmatter131420" target="_blank" rel="noreferrer">GitHub ↗</a></div>`;

function fix(file, isPost) {
  const p = path.join(site, file);
  if (!fs.existsSync(p)) return;
  let html = fs.readFileSync(p, 'utf8');
  const nav = isPost ? postNav : rootNav;
  html = html.replace(/<nav class="nav container">[\s\S]*?<\/nav>/, `<nav class="nav container">${nav}</nav>`);
  if (!isPost) {
    html = html.replaceAll('href="../style.css"', 'href="style.css"')
      .replaceAll('href="../feed.xml"', 'href="feed.xml"')
      .replaceAll('src="../reading.js"', 'src="reading.js"')
      .replaceAll('src="../share.js"', 'src="share.js"')
      .replaceAll('src="../images.js"', 'src="images.js"');
  }
  fs.writeFileSync(p, html);
}

fix('index.html', false);
for (const file of ['archive.html', 'tags.html', 'search.html']) fix(file, false);
if (fs.existsSync(path.join(site, 'posts'))) {
  for (const file of fs.readdirSync(path.join(site, 'posts')).filter(f => f.endsWith('.html'))) fix(path.join('posts', file), true);
}

console.log('Fixed navigation paths for home, root pages, and article pages.');
