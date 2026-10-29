# Histórico de implementação — Branch 12 e validação de produção

**Projeto:** EcoGiro  
**Repositório:** https://github.com/arthurkratos20-max/EcoGiro  
**PR de implementação:** https://github.com/arthurkratos20-max/EcoGiro/pull/3  
**Branch de destino da implementação:** `branch11---UI/UX-atualizados`  
**Data da validação de produção:** 29/10/2026

## 1. Resumo

A Branch 12 acrescentou a consulta pública de solicitações por protocolo e CPF, reuniu configurações de publicação para Render, Neon e Cloudflare Pages e levou junto ajustes de segurança do fluxo de cadastro administrativo e do console H2. Após a integração, o serviço do Render foi implantado com sucesso e o responsável pelo projeto confirmou que a página inicial, a consulta de solicitações, o cadastro e o login funcionam no ambiente publicado.

Este documento registra o que foi alterado e o que foi efetivamente verificado. Ele não representa uma auditoria completa de segurança nem garante que todos os cenários possíveis do sistema tenham sido testados.

## 2. Funcionalidades e alterações de código

### 2.1 Consulta pública de solicitação

- Criada a página `consulta-solicitacao.html` para o usuário informar o protocolo e o CPF associados a uma solicitação.
- Adicionado o controlador `PublicRequestLookupController.java), responsável pelo endpoint público de consulta.
- Atualizado `PlanRequestRepository.java` para localizar a solicitação usando os dados necessários à consulta.
- Adicionado um link de acesso à consulta na página inicial `index.html`.
- A resposta é genérica para protocolo inexistente ou CPF divergente, evitando distinguir esses casos na resposta pública.
- O fluxo autenticado de criação de solicitações foi mantido; o protocolo é gerado pelo servidor e retornado no fluxo existente.

### 2.2 Segurança de configuração

- O console H2 fica desabilitado por padrão em `application.properties`.
- O acesso a `/h2-console/**` é negado pelo Spring Security, mesmo se a configuração do console for ativada por engano.
- O código padrão conhecido para cadastro administrativo foi removido. O cadastro administrativo falha de forma fechada quando `ADMIN_REGISTRATION_CODE` está ausente, vazio ou incorreto.
- As variáveis e credenciais de produção devem ser configuradas nos painéis dos provedores, não versionadas no Git.
- A revisão preventiva de segurança está registrada em [SEGURANCA_REVISAO_2026-10-29.md](SEGURANCA_REVISAO_2026-10-29.md).

### 2.3 Configuração de publicação

- Adicionado `render.yaml` para descrever a configuração do serviço de backend no Render.
- Adicionado `wrangler.toml` para a configuração do frontend no Cloudflare Pages.
- Adicionado `DEPLOY_BRANCH12.md` com orientações para Render, Neon e Cloudflare Pages.
- O backend utiliza Java 21 e Spring Boot; em produção, a persistência deve apontar para PostgreSQL no Neon através das variáveis `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.
- `ADMIN_REGISTRATION_CODE` deve ser configurada como variável privada no ambiente de produção e `SPRING_H2_CONSOLE_ENABLED` deve permanecer `false`.
- O Worker do Cloudflare está configurado para encaminhar chamadas `/api/*` ao backend usando a variável `BACKEND_URL`. O valor é configurado no painel do Cloudflare, sem credenciais no repositório.

## 3. Arquivos principais afetados pela Branch 12

- `.../config/SecurityConfig.java`
- `.../controller/AuthController.java`
- `.../controller/PublicRequestLookupController.java` — novo
- `.../repository/PlanRequestRepository.java`
- `.../resources/application.properties`
- `.../resources/static/consulta-solicitacao.html` — novo
- `.../resources/static/index.html`
- `DEPLOY_BRANCH12.md` — novo
- `render.yaml` — novo
- `wrangler.toml` — novo
- `SEGURANCA_REVISAO_2026-10-29.md` — revisão de segurança

Os arquivos Java e estáticos do aplicativo ficam no diretório `EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro/`.

## 4. Validação realizada

### Testes locais

- Executado `.\gradlew.bat test` em uma cópia local limpa do repositório.
- Resultado observado: **BUILD SUCCESSFUL**, com 4 tarefas executadas.
- Esse resultado confirma a execução bem-sucedida da tarefa Gradle de testes nesse ambiente; não substitui testes de todos os fluxos nem uma auditoria de segurança.

### Render e Neon

Em 29/10/2026, os registros do Render mostraram:

- implantação com estado **Live / Deploy succeeded**;
- aplicação iniciada com Java 21 e Spring Boot;
- servidor Tomcat escutando na porta atribuída pelo Render;
- pool Hikari iniciado e conexão PostgreSQL com o Neon estabelecida;
- aplicação publicada em https://ecogiro.onrender.com.

O primeiro aviso de detecção de porta durante a inicialização foi seguido pela inicialização do servidor na porta configurada; a implantação terminou como bem-sucedida.

### Testes manuais reportados pelo responsável

- página inicial no Render: abriu normalmente;
- página inicial no Cloudflare Pages (`.pages.dev`): disponível;
- consulta de solicitação: confirmada como funcional;
- cadastro e login: confirmados como funcionais.

Os resultados acima refletem os testes reportados durante a validação. Não foram registrados neste documento dados pessoais, protocolos reais, senhas ou valores secretos.

## 5. Operação e manutenção

1. Manter as credenciais exclusivamente nas variáveis privadas dos provedores.
2. Após mudanças, acompanhar o estado do deploy no Render e confirmar a conexão ao Neon pelos logs.
3. Testar a página inicial, cadastro/login e consulta de solicitação depois de cada publicação.
4. Em contas gratuitas, considerar que o serviço do Render pode entrar em suspensão por inatividade e demorar a responder à primeira requisição após acordar.
5. Não usar dados pessoais de terceiros em testes. Preferir uma conta e uma solicitação de teste controladas pela equipe.

## 6. Limitações e verificações futuras

- A validação manual concluída não equivale a um teste de carga, teste de penetração ou auditoria de segurança completa.
- A política CSRF, a autorização de todos os endpoints, as sessões/cookies, o CORS, a configuração do proxy e as dependências ainda precisam de revisão específica conforme registrado no documento de segurança.
- A causa de erros administrativos anteriores não deve ser presumida sem analisar os logs relevantes; não compartilhar senhas, hashes ou credenciais durante a investigação.
- O repositório contém a configuração e a documentação de deploy, mas não consegue criar ou preencher variáveis secretas nas contas Render, Neon ou Cloudflare.

## 7. Documentos relacionados

- [Guia de publicação da Branch 12](DEPLOY_BRANCH12.md)
- [Revisão preventiva de segurança](SEGURANCA_REVISAO_2026-10-29.md)
- [Histórico de integração da Branch 11](INTEGRACAO_BRANCH11.md)
- [Histórico do backend da Branch 10](HISTORICO_BACKEND_BRANCH10.md)
