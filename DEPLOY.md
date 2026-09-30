# EcoGiro: cadastro, login e publicação gratuita

Java 17 + Spring Boot + Gradle. Backend Render, telas Cloudflare Pages, PostgreSQL Neon.
Nenhuma conta ou hospedagem foi criada automaticamente.

## 1. Teste local no VS Code
Abra a pasta ecogiro. No Windows: `gradlew.bat bootRun`. No Linux: `bash gradlew bootRun`.
Abra http://localhost:8080/cadastro.html, cadastre e entre pelo login.html.
O banco H2 local é temporário e perde dados quando o aplicativo encerra.

## 2. Neon
Crie um projeto gratuito e copie os dados da conexão PostgreSQL.
DATABASE_URL deve ser JDBC: `jdbc:postgresql://HOST/DB?sslmode=require`.
Configure DATABASE_USERNAME e DATABASE_PASSWORD separadamente.
Não publique senha ou URL com credenciais no GitHub.

## 3. GitHub
Aplique os arquivos deste pacote à branch feature/mudança_nos_botões_principais.
Não copie .gradle ou build. Faça commit e push usando seu VS Code.

## 4. Render
New > Web Service > conecte o repositório EcoGiro.
Selecione a branch feature/mudança_nos_botões_principais.
Runtime Docker; Dockerfile na raiz; plano Free.
Defina SPRING_PROFILES_ACTIVE=prod e as três variáveis DATABASE_* acima.
Copie a URL HTTPS criada. Confira /api/health.
O render.yaml também permite configurar via Blueprint; confira o plano Free antes de criar.

## 5. Cloudflare Pages
Importe o repositório via integração Git. Escolha a mesma branch.
Framework: None. Raiz do projeto: raiz do repositório (não ecogiro).
Build command: `exit 0`.
Build output directory: `ecogiro/src/main/resources/static`.
Defina BACKEND_URL para a URL HTTPS do Render, sem caminho, nas variáveis das Functions.
Faça um novo deploy depois de definir a variável. Use integração Git, para compilar a pasta functions.
A pasta functions/api encaminha /api/* ao Spring Boot. O navegador usa o mesmo domínio para as telas e a API, inclusive o cookie de sessão; não é necessário liberar CORS.

## 6. Verificação publicada
Abra /cadastro.html no endereço Pages, cadastre, entre, veja seu nome em /conta.html e saia.
Teste senha errada, cadastro duplicado e /api/auth/me após sair (deve retornar 401).
Nunca envie credenciais pelo chat.

## Comportamento e limites
Senhas usam BCrypt; sessão usa cookie HttpOnly; CSRF permanece habilitado.
Cookie Secure é habilitado no perfil prod. Banco Neon guarda cadastros; sessões ficam na memória do backend e encerram após reinício ou suspensão.
Render gratuito suspende após inatividade; o primeiro acesso pode demorar. Use dentro das cotas gratuitas.
Recuperação de senha, verificação de e-mail e limite de tentativas não foram implementados; são próximos passos antes de abrir ao público em escala.
A tabela é criada com Hibernate ddl-auto=update para este projeto acadêmico; migrações versionadas são recomendadas para evolução em produção.

## Validação deste pacote
Sintaxe dos arquivos JavaScript verificada. Teste de integração incluído em AuthIntegrationTests.java. Compilação e testes Java não executados: o ambiente de preparo bloqueou o download do Gradle. Antes de publicar, execute gradlew.bat test e gradlew.bat bootRun no seu computador.
