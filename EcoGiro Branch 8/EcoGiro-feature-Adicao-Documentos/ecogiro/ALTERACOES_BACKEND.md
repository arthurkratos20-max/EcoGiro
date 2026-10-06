# EcoGiro - Registro de Alteracoes do Backend

Este documento registra as implementacoes, correcoes, configuracoes de infraestrutura e validacoes realizadas no backend do projeto EcoGiro.

---

## 1. Backend e arquitetura

O projeto utiliza:

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- Gradle
- PostgreSQL
- Neon como banco PostgreSQL em nuvem
- Render para execucao da aplicacao
- Docker para build e execucao em producao

O backend foi preparado para trabalhar com o perfil de producao e banco PostgreSQL externo.

---

## 2. Banco de dados PostgreSQL / Neon

Foi realizada a integracao da aplicacao com um banco PostgreSQL hospedado no Neon.

Principais entidades/tabelas utilizadas pela aplicacao:

- `tb_usuario`
- `tb_plano_aluguel`
- `tb_solicitacao_plano`
- `tb_assinatura_plano`
- `tb_veiculo`
- `tb_recomendacao`

A conexao em producao utiliza variaveis de ambiente, evitando armazenar credenciais diretamente no codigo-fonte.

Foram configuradas variaveis para:

- URL do banco
- usuario do banco
- senha do banco
- datasource do Spring
- perfil de producao

A conexao entre Render e Neon foi validada com sucesso.

---

## 3. Cadastro e autenticacao

Foi implementado o sistema de autenticacao utilizando Spring Security e BCrypt.

Foram criados dois perfis de acesso:

- `USER`
- `ADMIN`

O usuario comum possui fluxo proprio de cadastro e autenticacao.

O administrador possui fluxo administrativo separado.

As senhas sao armazenadas utilizando hash BCrypt.

---

## 4. Separacao entre usuario e administrador

Foi corrigida a autenticacao para impedir que contas administrativas utilizem o login destinado aos usuarios comuns.

Tambem foi mantida a verificacao do fluxo administrativo para impedir que contas comuns obtenham acesso ao painel administrativo.

Comportamentos validados em producao:

- USER -> login de usuario: permitido
- USER -> login de administrador: bloqueado
- ADMIN -> login de administrador: permitido
- ADMIN -> login de usuario: bloqueado

Quando uma conta administrativa tenta utilizar o login comum, a sessao de autenticacao e invalidada e o usuario e direcionado novamente para a pagina de login.

Quando uma conta comum tenta utilizar o acesso administrativo, o sistema informa que a conta nao possui permissao de administrador.

---

## 5. Painel do usuario

Foi implementado painel destinado ao usuario autenticado.

O painel permite visualizar informacoes relacionadas a:

- plano atual
- solicitacoes de planos
- status das solicitacoes
- assinatura ativa
- recomendacoes de mobilidade

---

## 6. Painel administrativo

Foi implementada uma area administrativa restrita.

O painel administrativo permite acompanhar e gerenciar:

- usuarios
- solicitacoes de planos
- planos de aluguel
- assinaturas
- veiculos

O acesso aos endpoints administrativos e protegido pelo papel `ADMIN`.

---

## 7. Planos de aluguel

Foi implementado o fluxo de planos de aluguel.

O usuario pode solicitar um plano.

O administrador pode analisar a solicitacao e:

- aprovar
- rejeitar

Quando uma solicitacao e aprovada, o sistema pode gerar a assinatura correspondente para o usuario.

O status da solicitacao fica disponivel para consulta no painel.

---

## 8. Recomendacao de mobilidade

Foi implementado o registro das respostas do questionario de recomendacao.

As informacoes sao persistidas no banco e utilizadas para apresentar uma recomendacao de meio de transporte ao usuario.

O funcionamento da persistencia das recomendacoes foi validado no PostgreSQL.

---

## 9. Veiculos e mapa

Foi implementada persistencia de veiculos e endpoints utilizados pela funcionalidade de mapa.

A aplicacao possui integracao entre os dados de veiculos armazenados no backend e a interface de mapa.

---

## 10. Deploy no Render

O backend foi publicado como Web Service no Render.

A aplicacao utiliza Docker para realizar o build e iniciar o Spring Boot.

O Render foi configurado para executar o projeto a partir da branch:

`branch10---erros-corrigidos`

O servico foi validado utilizando:

- Spring Boot em producao
- PostgreSQL/Neon
- Hibernate/JPA
- HikariCP
- Tomcat

A aplicacao foi iniciada corretamente no ambiente de producao.

---

## 11. Atualizacao para Java 21

O projeto foi atualizado para utilizar Java 21.

O `build.gradle` passou a utilizar:

`JavaLanguageVersion.of(21)`

A compilacao local foi validada com sucesso utilizando Gradle.

---

## 12. Correcao do Docker para Java 21

Apos a atualizacao do Gradle para Java 21, o primeiro deploy falhou porque o Dockerfile ainda utilizava Java 17.

O ambiente de build utilizava:

`eclipse-temurin:17-jdk`

e o ambiente de execucao utilizava:

`eclipse-temurin:17-jre`

O Dockerfile foi corrigido para:

`eclipse-temurin:21-jdk`

e:

`eclipse-temurin:21-jre`

Apos a correcao, o build foi executado novamente no Render e o deploy foi concluido com sucesso.

Commit relacionado:

`1f40107 - Atualiza Docker para Java 21`

---

## 13. Correcao da mensagem de login

Foi identificada uma incompatibilidade de codificacao ao enviar diretamente um caractere acentuado no parametro da URL de redirecionamento do login.

A mensagem foi ajustada para evitar a exibicao do caractere invalido na interface.

A compilacao foi novamente validada com:

`gradlew compileJava`

Resultado:

`BUILD SUCCESSFUL`

Commit relacionado:

`c088421 - Corrige mensagem do login administrativo`

---

## 14. Testes realizados em producao

Foram realizados testes diretamente na aplicacao publicada no Render.

Validacoes concluidas:

- conexao com PostgreSQL/Neon
- inicializacao do Spring Boot
- persistencia de usuario
- login de usuario
- login administrativo
- bloqueio de ADMIN no login de USER
- bloqueio de USER no login de ADMIN
- acesso ao painel do usuario
- acesso ao painel administrativo
- solicitacao de plano
- aprovacao de solicitacao
- geracao/visualizacao de assinatura
- persistencia de recomendacao
- consulta dos dados administrativos
- carregamento dos veiculos

---

## 15. Seguranca

Medidas aplicadas:

- senhas protegidas com BCrypt
- separacao entre papeis USER e ADMIN
- endpoints administrativos protegidos por role
- credenciais do banco configuradas por variaveis de ambiente
- sessao invalidada quando uma conta administrativa tenta autenticar pelo fluxo comum
- banco PostgreSQL externo utilizado no ambiente de producao

---

## 16. Historico recente de commits

### `4831fb4`
Implementa autenticacao, planos, painel admin, banco e mapa.

### `6a534fc`
Corrige separacao de login usuario e admin e atualiza Java 21.

### `1f40107`
Atualiza Docker para Java 21.

### `c088421`
Corrige mensagem do login administrativo.

---

## Estado atual

O backend EcoGiro encontra-se integrado ao PostgreSQL/Neon e publicado no Render.

Os fluxos de usuario e administrador foram separados e testados em producao.

O projeto utiliza Java 21 tanto na compilacao Gradle quanto no ambiente Docker.

Este documento deve ser atualizado juntamente com novas implementacoes, correcoes relevantes, alteracoes de infraestrutura e testes realizados.

---

## 17. Restricao das rotas por perfil

Durante os testes de autorizacao em producao, foi identificado que uma conta ADMIN autenticada conseguia acessar diretamente a pagina `/usuario.html`.

A causa era a configuracao das rotas de usuario utilizando apenas:

`authenticated()`

Essa regra permitia o acesso de qualquer conta autenticada, independentemente do papel USER ou ADMIN.

A configuracao de seguranca foi alterada para separar as permissoes:

- `/admin.html` e `/api/admin/**`: somente `ADMIN`
- `/usuario.html`, `/planos-aluguel.html` e `/api/user/**`: somente `USER`
- `/mapa.html` e `/api/map/**`: acessiveis por USER e ADMIN autenticados

O mapa permaneceu compartilhado porque faz parte tanto da experiencia do usuario quanto das funcionalidades disponiveis para a administracao.

Testes de seguranca realizados:

- usuario deslogado tentando acessar `/admin.html`: bloqueado
- usuario deslogado tentando acessar `/usuario.html`: bloqueado
- USER autenticado tentando acessar `/admin.html`: bloqueado com HTTP 403
- ADMIN acessando `/usuario.html`: falha identificada antes da correcao

A nova configuracao foi compilada localmente com sucesso utilizando:

`gradlew compileJava`

Resultado:

`BUILD SUCCESSFUL`

Apos o deploy, devera ser validado que ADMIN tambem nao consegue mais acessar diretamente as rotas exclusivas de USER.


### Validacao apos deploy

A correcao de autorizacao foi publicada e validada em producao.

Teste realizado:

- ADMIN autenticado tentando acessar diretamente `/usuario.html`: bloqueado com HTTP 403 (Forbidden)

Com isso, foi confirmado que as rotas exclusivas de USER nao podem mais ser acessadas por contas ADMIN.

A separacao de autorizacao entre USER e ADMIN esta funcionando conforme esperado.

Observacao: atualmente o bloqueio HTTP 403 utiliza a pagina Whitelabel padrao do Spring Boot. A substituicao por uma pagina de acesso negado personalizada fica registrada como melhoria de interface.


---

## 18. Pagina personalizada de acesso negado

Foi criada uma pagina personalizada para respostas HTTP 403 (Forbidden), substituindo a pagina Whitelabel padrao do Spring Boot quando um usuario autenticado tenta acessar uma area para a qual nao possui permissao.

Arquivo criado:

`src/main/resources/static/403.html`

A pagina informa ao usuario que sua conta nao possui permissao para acessar a area solicitada e disponibiliza opcoes para retornar a pagina anterior ou voltar para a pagina inicial do EcoGiro.

O Spring Security foi configurado utilizando:

`exceptionHandling(exception -> exception.accessDeniedPage("/403.html"))`

A rota `/403.html` foi adicionada as rotas publicas para permitir que a pagina de acesso negado seja exibida independentemente do perfil autenticado.

A alteracao nao modifica as regras de autorizacao existentes:

- rotas administrativas continuam exclusivas de `ADMIN`
- rotas de usuario continuam exclusivas de `USER`
- mapa continua disponivel para usuarios autenticados
- acessos sem permissao continuam sendo bloqueados com HTTP 403

A nova configuracao foi compilada localmente com:

`gradlew compileJava`

Resultado:

`BUILD SUCCESSFUL`

Validacao em producao pendente apos deploy.

