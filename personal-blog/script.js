const $ = (s, root = document) => root.querySelector(s);
const $$ = (s, root = document) => [...root.querySelectorAll(s)];

$('#year').textContent = new Date().getFullYear();

const toggle = $('#themeToggle');
const savedTheme = localStorage.getItem('blog-theme');
if (savedTheme === 'dark') document.body.classList.add('dark');
function updateThemeIcon(){ toggle.textContent = document.body.classList.contains('dark') ? '☀' : '☾'; }
updateThemeIcon();
toggle.addEventListener('click', () => {
  document.body.classList.toggle('dark');
  localStorage.setItem('blog-theme', document.body.classList.contains('dark') ? 'dark' : 'light');
  updateThemeIcon();
});

const posts = $$('.post');
let activeTag = 'all';
function filterPosts(){
  const q = $('#searchInput').value.trim().toLowerCase();
  let visible = 0;
  posts.forEach(post => {
    const tags = post.dataset.tags || '';
    const title = post.dataset.title || '';
    const matchTag = activeTag === 'all' || tags.includes(activeTag);
    const matchQuery = !q || title.toLowerCase().includes(q) || tags.toLowerCase().includes(q);
    const show = matchTag && matchQuery;
    post.hidden = !show;
    if(show) visible++;
  });
  $('#emptyState').hidden = visible !== 0;
}

$$('.tag').forEach(btn => btn.addEventListener('click', () => {
  $$('.tag').forEach(x => x.classList.remove('active'));
  btn.classList.add('active');
  activeTag = btn.dataset.tag;
  filterPosts();
}));
$('#searchInput').addEventListener('input', filterPosts);

const toast = $('#toast');
let toastTimer;
$$('.read-more').forEach(link => link.addEventListener('click', e => {
  e.preventDefault();
  toast.textContent = link.dataset.demo;
  toast.classList.add('show');
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove('show'), 3200);
}));
