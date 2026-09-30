(() => {
    const password = document.querySelector('#password');
    const toggle = document.querySelector('.password-toggle');

    if (!password || !toggle) return;

    toggle.addEventListener('click', () => {
        const showPassword = password.type === 'password';
        password.type = showPassword ? 'text' : 'password';
        toggle.textContent = showPassword ? 'Ocultar' : 'Mostrar';
        toggle.setAttribute('aria-pressed', String(showPassword));
    });
})();
document.querySelector('#login-form').addEventListener('submit', async event => {
 event.preventDefault(); const form = event.currentTarget;
 const button = form.querySelector('[type=submit]'); const message = document.querySelector('#auth-message');
 button.disabled = true; message.textContent = 'Conectando… O primeiro acesso pode demorar.';
 try {await ecogiroRequest('/api/auth/login', {method:'POST', body:new URLSearchParams(new FormData(form))}); location.href='conta.html';}
 catch(error) {message.textContent=error.message;}
 finally {button.disabled=false;}
});
