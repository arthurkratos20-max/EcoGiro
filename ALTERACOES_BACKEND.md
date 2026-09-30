# EcoGiro — Registro de implementação do backend e autenticação

Data: 30/09/2026  
Responsável pela área: Arthur — desenvolvimento backend  
Branch de referência: `feature/mudança_nos_botões_principais`  
Commit de origem: `9bbfc20f1922091e14349dff0e2fe51140e44f02`

## 1. Objetivo da entrega

Preparar cadastro e autenticação de usuários no projeto EcoGiro, integrar as telas ao backend Java/Spring Boot e configurar uma arquitetura de publicação que utilize planos gratuitos. Este registro descreve o código preparado no pacote; não representa comprovação de publicação ou aprovação dos testes Java.

## 2. Situação encontrada

O pacote de origem continha a inicialização do Spring Boot e páginas estáticas. A tela de login enviava dados para `/login`, mas não havia implementação de autenticação, entidade de usuário ou tela de cadastro. O README apresentava funcionalidades planejadas que não estavam implementadas no código recebido. O projeto utiliza Gradle e Java 17.

## 3. Alterações realizadas

| Área | Implementação no pacote | Finalidade |
|---|---|---|
| Dependências | Inclusão de Spring Security, driver PostgreSQL e suporte de testes de segurança | Autenticação, conexão com banco externo e testes |
| Persistência | Entidade `AppUser` e repositório `UserRepository` | Armazenar ID, nome, e-mail e hash da senha |
| Cadastro | Endpoint com validação de nome, e-mail e senha; normalização do e-mail; unicidade no banco | Criar contas e impedir duplicação de e-mails |
| Proteção de senhas | BCrypt | Evitar armazenamento de senha em texto puro |
| Login | Autenticação por e-mail e senha com Spring Security | Validar credenciais e estabelecer sessão |
| Sessão | Cookie HttpOnly, SameSite=Lax e Secure no perfil de produção | Manter o usuário autenticado |
| CSRF | Proteção mantida, com obtenção de token pelos formulários | Proteger requisições que modificam estado |
| Controle de acesso | Rotas da API exigem autenticação, exceto cadastro, token CSRF, login e saúde | Restringir dados da conta |
| Tela de cadastro | Nome, e-mail, senha e confirmação | Permitir criação de conta pelo navegador |
| Tela de login | Envio real à API; mensagens de erro e estado de envio | Substituir formulário sem backend |
| Página da conta | Exibição do nome e e-mail; botão de sair | Confirmar autenticação e encerrar sessão |
| Página inicial | Cópia `index.html` da página `index.htm` | Permitir entrada pela raiz do site |
| Configuração local | H2 em memória | Desenvolvimento sem banco externo |
| Configuração de produção | PostgreSQL via variáveis de ambiente | Persistência externa sem credenciais no código |
| Render | Dockerfile e arquivo `render.yaml` | Preparar execução do Spring Boot na nuvem |
| Cloudflare | Pages Function para encaminhar `/api/*` ao backend | Permitir telas e API sob o mesmo domínio do navegador |
| Documentação | `DEPLOY.md` | Orientar testes locais e publicação |

## 4. Endpoints

| Método | Rota | Comportamento |
|---|---|---|
| GET | `/api/health` | Retorna estado básico da aplicação; não comprova funcionamento do banco |
| GET | `/api/auth/csrf` | Retorna token e nome do cabeçalho para proteção CSRF |
| POST | `/api/auth/register` | Cadastra usuário; retorna 201, 400 para dados inválidos ou 409 para conflito de persistência |
| POST | `/api/auth/login` | Recebe formulário com `username` (e-mail) e `password`; retorna 200 ou 401 |
| GET | `/api/auth/me` | Retorna ID, nome e e-mail do usuário autenticado; sem sessão, retorna 401 |
| POST | `/api/auth/logout` | Encerra sessão e remove cookie; retorna 204 |

Requisições POST devem apresentar token CSRF válido. A API não devolve hash ou senha no endpoint da conta. Dados de cadastro são enviados em JSON; dados de login são enviados como formulário URL-encoded.

## 5. Arquivos adicionados e modificados

Caminhos relativos à raiz do repositório.

| Arquivo ou grupo | Tipo | Conteúdo |
|---|---|---|
| `ecogiro/build.gradle` | Modificado | Dependências de segurança, PostgreSQL e testes |
| `ecogiro/src/main/java/com/example/ecogiro/auth/AppUser.java` | Adicionado | Entidade de usuário |
| `ecogiro/src/main/java/com/example/ecogiro/auth/UserRepository.java` | Adicionado | Consulta e persistência de usuários |
| `ecogiro/src/main/java/com/example/ecogiro/auth/SecurityConfig.java` | Adicionado | Login, logout, hash e autorização |
| `ecogiro/src/main/java/com/example/ecogiro/auth/AuthController.java` | Adicionado | Cadastro, conta, CSRF e saúde |
| `ecogiro/src/main/resources/application.properties` | Modificado | Banco local e sessão |
| `ecogiro/src/main/resources/application-prod.properties` | Adicionado | Banco externo e configuração de produção |
| `ecogiro/src/main/resources/static/login.html` | Modificado | Integração e link para cadastro |
| `ecogiro/src/main/resources/static/assets/js/login.js` | Modificado | Envio do login para API |
| `ecogiro/src/main/resources/static/assets/js/auth.js` | Adicionado | Requisições compartilhadas e CSRF |
| `ecogiro/src/main/resources/static/cadastro.html` e `assets/js/cadastro.js` | Adicionados | Formulário de cadastro |
| `ecogiro/src/main/resources/static/conta.html` e `assets/js/conta.js` | Adicionados | Conta e logout |
| `ecogiro/src/main/resources/static/index.html` | Adicionado | Entrada pela raiz |
| `ecogiro/src/main/resources/static/_routes.json` | Adicionado | Roteamento das Functions apenas para API |
| `functions/api/[[path]].js` | Adicionado | Encaminhamento para o Render |
| `Dockerfile`, `.dockerignore` e `render.yaml` | Adicionados | Empacotamento e configuração de hospedagem |
| `ecogiro/src/test/java/com/example/ecogiro/AuthIntegrationTests.java` | Adicionado | Teste de integração do fluxo de autenticação |
| `DEPLOY.md`, `ALTERACOES_BACKEND.md` e `RESUMO_PARA_RELATORIO.txt` | Adicionados | Instruções e registro da entrega |

## 6. Arquitetura preparada

As páginas HTML/CSS/JavaScript serão publicadas no Cloudflare Pages. Uma Pages Function encaminhará as chamadas da API para o Spring Boot no Render. O backend utilizará PostgreSQL no Neon. A linguagem Java e o framework Spring Boot foram mantidos conforme o requisito acadêmico.

No navegador, a API é acessada por caminhos relativos `/api/...`. O encaminhamento evita a necessidade de login por cookies entre domínios diferentes. A URL do backend fica em `BACKEND_URL`, no Cloudflare. A conexão do banco fica em `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD`, no Render. Nenhuma credencial real foi incluída.

## 7. Verificações e evidências

| Verificação | Resultado nesta entrega |
|---|---|
| Correspondência do ZIP com a branch | Confirmada pelo commit de origem |
| Sintaxe de todos os JavaScript de autenticação e da Pages Function | Aprovada com Node.js |
| Encaminhamento da API e preservação do cookie | Aprovado em teste isolado com resposta simulada |
| Resposta quando `BACKEND_URL` não está configurado | Aprovada em teste isolado; retorna 503 |
| Verificação de espaços e conflitos no diff | Aprovada com `git diff --check` |
| Teste de integração Java | Escrito; execução bloqueada antes dos testes pelo download do Gradle |
| Compilação e inicialização do Spring Boot | Ainda não confirmadas |
| Fluxo completo no navegador com backend real | Pendente |
| Conexão real com Neon | Pendente |
| Publicação em Render e Cloudflare | Pendente |

O teste Java incluído prevê: acesso sem autenticação, bloqueio de POST sem CSRF, cadastro válido, cadastro duplicado, senha incorreta, login válido, consulta da conta sem exposição de senha e logout. A existência do teste não significa que ele passou. Rodar `gradlew.bat test` e `gradlew.bat bootRun` no computador do responsável é necessário antes de publicar.

## 8. Pendências e limites

- Fazer commit e push das alterações. Nenhuma alteração foi enviada ao GitHub por esta entrega.
- Criar e configurar os serviços, variáveis de ambiente e URL pública conforme `DEPLOY.md`.
- Confirmar com o professor a arquitetura dividida: telas no Cloudflare e backend no Render.
- Executar os testes Java e validar cadastro, login e logout no navegador, localmente e após publicação.
- O H2 local é temporário; o PostgreSQL externo será o banco persistente.
- Sessões ficam na memória do backend e são perdidas quando ele reinicia ou é suspenso. Os cadastros ficam no PostgreSQL após configuração.
- Recuperação de senha, verificação de e-mail, limitação de tentativas, perfis administrativos e funcionalidades do quiz não foram implementados nesta entrega.
- Nenhum acesso especial às administradoras foi criado; este documento serve para registro e acompanhamento.
- O serviço gratuito do Render pode suspender por inatividade, aumentando o tempo do primeiro acesso.
- A criação e atualização de tabelas utilizam Hibernate `ddl-auto=update`; migrações versionadas ficam como evolução futura.

## 9. Orientação para o relatório acadêmico

Usar `RESUMO_PARA_RELATORIO.txt` como texto inicial e registrar separadamente as evidências obtidas pela equipe. Não descrever o sistema como publicado, validado integralmente ou operacional em produção até concluir essas etapas. Após a publicação, acrescentar data, URLs, responsável pela validação e capturas de cadastro, login e logout, sem expor senhas ou credenciais do banco.
