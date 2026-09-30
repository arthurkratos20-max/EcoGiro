document.querySelector('#register-form').addEventListener('submit', async event => {
 event.preventDefault(); const form=event.currentTarget; const message=document.querySelector('#auth-message');
 if(form.password.value !== document.querySelector('#confirm').value){message.textContent='As senhas não coincidem.';return;}
 const button=form.querySelector('[type=submit]');button.disabled=true;message.textContent='Cadastrando… O primeiro acesso pode demorar.';
 try {const data=await ecogiroRequest('/api/auth/register',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(Object.fromEntries(new FormData(form)))});message.textContent=data.message;form.reset();}
 catch(error){message.textContent=error.message;} finally{button.disabled=false;}
});
