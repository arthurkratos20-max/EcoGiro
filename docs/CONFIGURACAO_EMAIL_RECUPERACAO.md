# Configuração do envio de e-mails de recuperação de senha

A recuperação de senha do EcoGiro utiliza SMTP. O código não contém credenciais de e-mail: elas devem ser cadastradas como variáveis de ambiente no serviço de hospedagem.

## Variáveis necessárias

Configure no ambiente do backend (Render):

| Variável | Descrição | Exemplo |
| --- | --- | --- |
| `MAIL_HOST` | Servidor SMTP do provedor | fornecido pelo provedor |
| `MAIL_PORT` | Porta SMTP com STARTTLS | `587` |
| `MAIL_USERNAME` | Usuário SMTP | conta fornecida pelo provedor |
| `MAIL_PASSWORD` | Senha SMTP ou senha de aplicativo | segredo do provedor |
| `MAIL_FROM` | Remetente autorizado | endereço verificado no provedor |
| `APP_PUBLIC_URL` | URL pública do backend usada no link | `https://ecogiro.onrender.com` |

Não coloque esses valores em arquivos versionados, commits, prints ou no frontend.

## Exemplo de configuração

Para provedores SMTP com STARTTLS, use normalmente a porta 587. Os valores exatos de host, usuário e senha devem ser obtidos no painel do serviço escolhido. Alguns provedores exigem verificar o domínio ou o endereço remetente antes de permitir o envio.

## Fluxo de segurança

- O token é gerado com fonte criptograficamente segura e possui 256 bits.
- Somente o hash SHA-256 do token é armazenado no banco.
- O link expira em 30 minutos e só pode ser utilizado uma vez.
- Uma nova solicitação invalida o token anterior do mesmo usuário.
- A resposta da solicitação não confirma se o e-mail existe no cadastro.
- A senha nova precisa ter pelo menos 8 caracteres e é armazenada com BCrypt.
- O recurso é destinado a contas de usuário comum; o acesso administrativo não é redefinido por esse formulário.

## Ativação

A aplicação pode iniciar sem as variáveis SMTP, mas o envio não funcionará até que o provedor esteja configurado. Depois de cadastrar as variáveis no Render, faça um novo deploy e teste com uma conta de e-mail que você controla. Verifique também a pasta de spam.

O teste local exige as mesmas variáveis SMTP e uma URL pública ou túnel acessível ao navegador para que o link enviado por e-mail possa ser aberto.
