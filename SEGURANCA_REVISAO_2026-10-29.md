# Revisão preventiva de segurança — 29/10/2026

## Alteração aplicada nesta branch

Branch: `fix/security-defaults-20261029`
Base: `branch11---UI/UX-atualizados`
Commit da configuração: `f37dfbc155355b43a9580e1932b6e81283f06060`

Arquivo alterado:
`EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/src/main/resources/application.properties`

- O console H2 agora fica desabilitado por padrão (`SPRING_H2_CONSOLE_ENABLED=false`). Para uso local intencional, pode ser habilitado explicitamente com `SPRING_H2_CONSOLE_ENABLED=true`.
- O código de cadastro administrativo não tem mais um valor padrão conhecido. O ambiente deve definir `ADMIN_REGISTRATION_CODE` para habilitar o fluxo que depende desse código.
- Nenhuma credencial, conta ou dado do Neon foi alterado.
- A branch de produção, o Render e o Cloudflare Pages não foram modificados nem implantados por esta revisão.

## Validação pendente

Esta é uma correção preventiva de configuração, ainda não homologada em execução. Antes de integrar à branch de produção:
1. Executar os testes Java com Java 21 e `./gradlew test bootJar`.
2. Confirmar no Render que `ADMIN_REGISTRATION_CODE` está definido e não usar o antigo valor padrão.
3. Confirmar que `SPRING_H2_CONSOLE_ENABLED=false` no Render e verificar que `/h2-console` não é acessível.
4. Testar cadastro/login USER, login ADMIN e fluxo de cadastro administrativo, incluindo o caso em que o código não está configurado.
5. Depois da revisão e aprovação, integrar e implantar a branch; testar o site público após o deploy.

## Limite desta revisão

A mudança remove dois padrões arriscados do arquivo de configuração, mas não é uma auditoria completa. Ainda é necessário inspecionar as regras de autorização, o proxy Cloudflare Pages, CSRF, cookies de sessão, CORS, dependências e logs de produção. O status público dos provedores não confirma o estado interno do serviço EcoGiro.
