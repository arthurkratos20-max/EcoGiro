export async function onRequest({ request, env }) {
 let upstream;
 try {upstream = new URL(env.BACKEND_URL); if(upstream.protocol !== 'https:') throw new Error();}
 catch (_) {return Response.json({message:'Backend não configurado.'},{status:503});}
 const url=new URL(request.url); upstream.pathname=url.pathname; upstream.search=url.search;
 const headers=new Headers(request.headers);
 headers.delete('host'); headers.delete('forwarded'); headers.delete('x-forwarded-host');
 headers.set('x-forwarded-proto','https');
 try {
  const response=await fetch(upstream,{method:request.method,headers,body:['GET','HEAD'].includes(request.method)?undefined:request.body,redirect:'manual'});
  const result=new Response(response.body,response);result.headers.set('Cache-Control','no-store');return result;
 } catch (_) {return Response.json({message:'Servidor indisponível. Aguarde e tente novamente.'},{status:502});}
}
