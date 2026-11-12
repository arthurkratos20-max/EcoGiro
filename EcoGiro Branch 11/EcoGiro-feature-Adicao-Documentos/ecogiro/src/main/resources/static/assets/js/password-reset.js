(() => {
    const params = new URLSearchParams(window.location.search);
    const feedback = document.getElementById('feedback');
    const showMessage = (message, type) => {
        if (!feedback) return;
        feedback.hidden = false;
        feedback.className = 'auth-feedback ' + type;
        feedback.textContent = message;
    };

    if (params.get('sent') === '1') {
        showMessage('Se o e-mail estiver cadastrado, você receberá um link de recuperação em instantes. Verifique também a caixa de spam.', 'success');
    } else if (params.get('error') === 'mail') {
        showMessage('O envio de e-mails está temporariamente indisponível. Tente novamente mais tarde.', 'error');
    } else if (params.has('error')) {
        showMessage(params.get('error'), 'error');
    }

    const tokenInput = document.getElementById('token');
    if (tokenInput) {
        const token = params.get('token') || '';
        tokenInput.value = token;
        if (!/^[a-fA-F0-9]{64}$/.test(token)) {
            showMessage('Este link está incompleto ou inválido. Solicite uma nova recuperação de senha.', 'error');
            const form = document.getElementById('reset-form');
            if (form) form.querySelector('button[type="submit"]').disabled = true;
        }
    }

    const form = document.getElementById('reset-form');
    if (form) {
        form.addEventListener('submit', (event) => {
            const password = document.getElementById('password').value;
            const confirmation = document.getElementById('confirmPassword').value;
            if (password !== confirmation) {
                event.preventDefault();
                showMessage('As senhas não coincidem.', 'error');
            }
        });
    }
})();
