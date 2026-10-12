// Cloudflare Pages: encaminha sessão e operações ao Spring Boot.
const backendPaths = new Set([
  '/login', '/logout', '/register', '/admin/login', '/admin/register',
  '/admin.html', '/admin-setorial.html', '/mapa-admin.html',
  '/usuario.html', '/planos-aluguel.html', '/mapa.html'
]);
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (!url.pathname.startsWith('/api/') && !backendPaths.has(url.pathname)) {
      return env.ASSETS.fetch(request);
    }
    const backend = new URL(env.BACKEND_URL || 'https://ecogiro.onrender.com');
    if (backend.protocol !== 'https:') return new Response('Backend inválido.', { status: 503 });
    const target = new URL(url.pathname + url.search, backend.origin);
    const headers = new Headers(request.headers);
    headers.delete('host');
    headers.set('X-Forwarded-Host', url.host);
    headers.set('X-Forwarded-Proto', 'https');
    try {
      const response = await fetch(new Request(target, {
        method: request.method, headers,
        body: ['GET', 'HEAD'].includes(request.method) ? undefined : await request.arrayBuffer(),
        redirect: 'manual'
      }));
      const resultHeaders = new Headers(response.headers);
      const location = resultHeaders.get('Location');
      if (location) {
        const redirect = new URL(location, target);
        if (redirect.origin === backend.origin) {
          resultHeaders.set('Location', url.origin + redirect.pathname + redirect.search + redirect.hash);
        }
      }
      resultHeaders.set('Cache-Control', 'no-store');
      return new Response(response.body, { status: response.status, headers: resultHeaders });
    } catch {
      return new Response('Não foi possível conectar ao servidor EcoGiro. Tente novamente.', { status: 502 });
    }
  }
};
