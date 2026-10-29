# Revisão preventiva de segurança — 29/10/2026

## Alterações nesta branch

Branch: `fix/security-defaults-20261029`
Base: `branch11---UI/UX-atualizados`

- `application.properties`: console H2 desabilitado por padrão e código de cadastro administrativo sem valor padrão.
- `AuthController.java`: cadastro administrativo falha de forma fechada quando o segredo de ambiente não está definido, está vazio, ou quando o ID enviado está vazio/incorreto.
- `SecurityConfig.java`: `/h2-console/**` é negado pelo Spring Security, mesmo se o console for habilitado acidentalmente por variável de ambiente.
- Nenhuma credencial, conta ou dado do Neon foi alterado.
- Render, Cloudflare Pages e ambiente hospedado não foram modificados nem implantados.

## Validação pendente

Estas alterações foram feitas por revisão estática no GitHub. O build e os testes automatizados não foram executados neste ambiente. Antes de integrar à branch de produção:

1. Executar com Java 21: `./gradlew test bootJar`.
2. Confirmar no Render que `ADMIN_REGISTRATION_CODE` está definido como variável privada e trocar qualquer valor antigo conhecido.
3. Confirmar `SPRING_H2_CONSOLE_ENABLED=false` no Render e verificar que `/h2-console` responde com acesso negado.
4. Testar cadastro/login USER, login ADMIN, cadastro administrativo com segredo válido, segredo incorreto e segredo ausente/vazio.
5. Verificar o estado dos checks da PR antes de integrar e testar o site público após o deploy.

## Pendências de auditoria que ainda exigem validação

- CSRF: a configuração atual aplica proteção apenas a determinados POSTs para manter compatibilidade com formulários antigos. É preciso inventariar os endpoints de escrita e adicionar tokens aos formulários/solicitações antes de ampliar a proteção sem quebrar o frontend.
- Autorização: revisar todos os controllers/endpoints, pois a regra final atual é `anyRequest().permitAll()`; cada rota sensível deve estar coberta por uma regra explícita ou anotação de autorização.
- Sessões/cookies, CORS, proxy Cloudflare Pages/Worker, dependências e logs de produção ainda precisam de revisão.
- O motivo de `/admin-login.html?error=1` não pode ser determinado apenas pelo código: esse redirecionamento ocorre quando a autenticação lança `AuthenticationException`. É necessário consultar os logs do Render e testar a conta/ambiente sem expor senhas ou hashes.

## Limite

Não considerar esta revisão uma homologação completa. Nenhuma integração ou implantação deve ocorrer antes dos testes e da confirmação das variáveis de ambiente.
