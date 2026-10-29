# Publicação do EcoGiro — Branch 12

## Render (API Spring Boot)

Crie/atualize o serviço a partir do Blueprint `render.yaml`. Configure os valores secretos no painel do Render, nunca no Git:

- `SPRING_DATASOURCE_URL`: JDBC do Neon no formato `jdbc:postgresql://HOST/DB?sslmode=require`.
- `SPRING_DATASOURCE_USERNAME`: usuário do banco Neon.
- `SPRING_DATASOURCE_PASSWORD`: senha do banco Neon.
- `ADMIN_REGISTRATION_CODE`: código forte e exclusivo para cadastrar administrador.
- `SPRING_H2_CONSOLE_ENABLED`: `false`.

O `application.properties` usa H2 apenas como fallback local. Em produção, confirme que as três variáveis `SPRING_DATASOURCE_*` estão preenchidas para não iniciar com H2.

## Neon

Crie um banco PostgreSQL e copie a connection string do painel Neon. Use o hostname, banco, usuário e senha do próprio projeto. Para o Spring, converta o começo `postgresql://` para `jdbc:postgresql://` e mantenha `?sslmode=require`. Não publique essa string em issues, commits ou prints.

## Cloudflare Pages

O arquivo `wrangler.toml` aponta a saída estática para a pasta do frontend e o `_worker.js` encaminha chamadas `/api/*` ao backend.

1. Importe o repositório no Cloudflare Pages.
2. Use a configuração de `wrangler.toml` e defina a variável de ambiente `BACKEND_URL` com a URL HTTPS pública do serviço Render, sem caminho final.
3. Publique em ambiente Production.
4. Após a publicação, teste a landing page, cadastro/login, catálogo, envio de solicitação, consulta com protocolo + CPF e o painel administrativo.

## Importante

O código configura a aplicação, mas não pode criar ou preencher secrets nos painéis Render/Neon/Cloudflare sem acesso autorizado às respectivas contas. O deploy só estará operacional depois de inserir as variáveis acima e confirmar o primeiro deploy. Não compartilhe credenciais no chat.
