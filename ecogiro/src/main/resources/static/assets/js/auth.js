window.ecogiroRequest = async function(path, options = {}) {
 const headers = new Headers(options.headers || {});
 if (options.method && options.method !== 'GET') {
  const response = await fetch('/api/auth/csrf', {credentials:'same-origin', cache:'no-store'});
  if (!response.ok) throw new Error('Não foi possível conectar ao servidor. Tente novamente em instantes.');
  const csrf = await response.json(); headers.set(csrf.headerName, csrf.token);
 }
 const response = await fetch(path, {...options, headers, credentials:'same-origin', cache:'no-store'});
 let data = {}; try {data = await response.json();} catch (_) {}
 if (!response.ok) throw new Error(data.message || (response.status === 403 ? 'Sessão expirada. Tente novamente.' : 'Não foi possível concluir a solicitação.'));
 return data;
};
