import fs from 'node:fs';
import path from 'node:path';

const site = path.join(new URL('.', import.meta.url).pathname, 'site');
const rootPages = ['archive.html', 'tags.html', 'search.html'];

for (const file of rootPages) {
  const p = path.join(site, file);
  if (!fs.existsSync(p)) continue;
  let html = fs.readFileSync(p, 'utf8');
  // These pages live directly under site/, while article pages live under site/posts/.
  // The shared shell defaults to ../ for article pages, so normalize root-page links/assets.
  html = html
    .replaceAll('href="../#', 'href="#')
    .replaceAll('href="../archive.html"', 'href="archive.html"')
    .replaceAll('href="../tags.html"', 'href="tags.html"')
    .replaceAll('href="../search.html"', 'href="search.html"')
    .replaceAll('href="../style.css"', 'href="style.css"')
    .replaceAll('href="../feed.xml"', 'href="feed.xml"')
    .replaceAll('src="../reading.js"', 'src="reading.js"')
    .replaceAll('src="../share.js"', 'src="share.js"')
    .replaceAll('src="../images.js"', 'src="images.js"');
  // The shared brand points to ../ on root pages; it should stay at the site root.
  html = html.replaceAll('class="brand" href="../"', 'class="brand" href="./"');
  fs.writeFileSync(p, html);
}

console.log('Fixed root-page navigation and asset paths.');
