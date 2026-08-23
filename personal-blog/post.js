const params = new URLSearchParams(location.search);
const slug = params.get('slug');
const articleEl = document.getElementById('article');
const yearEl = document.getElementById('year');
yearEl.textContent = new Date().getFullYear();

fetch('./posts/index.json')
  .then(r => r.json())
  .then(posts => {
    const post = posts.find(p => p.slug === slug) || posts[0];
    if (!post) throw new Error('No post');
    document.title = `${post.title} · Darkmatter`;
    document.querySelector('meta[name="description"]').content = post.excerpt;
    articleEl.innerHTML = `
      <header class="article-header">
        <div class="post-meta"><span>${post.tags.join(' · ')}</span><time>${post.date}</time></div>
        <h1>${escapeHtml(post.title)}</h1>
        <p class="article-excerpt">${escapeHtml(post.excerpt)}</p>
      </header>
      <div class="article-body">${marked.parse(post.content)}</div>
    `;
  })
  .catch(() => { articleEl.innerHTML = '<p class="empty">文章加载失败，请稍后重试。</p>'; });

function escapeHtml(s) { return s.replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }

const saved = localStorage.getItem('theme');
if (saved === 'light') document.body.classList.add('light');
document.getElementById('themeToggle').addEventListener('click', () => {
  document.body.classList.toggle('light');
  localStorage.setItem('theme', document.body.classList.contains('light') ? 'light' : 'dark');
});
