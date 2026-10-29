# Auditoria e correção de segurança — EcoGiro

Data: 29/10/2026  
Branch de correção: `fix/seguranca-codigo-admin`  
Base: `branch11---UI/UX-atualizados`

## Correção aplicada no código

### 1. Código padrão de cadastro administrativo

**Problema:** `application.properties` definia `ADMIN_REGISTRATION_CODE` com o valor público conhecido `ECOGIRO-ADMIN-2026`. Se a variável de ambiente não estivesse configurada no Render, o valor padrão poderia autorizar cadastro indevido de contas `ADMIN_SETORIAL`.

**Correção:**
- Removido o valor padrão conhecido.
- A propriedade agora usa valor vazio se `ADMIN_REGISTRATION_CODE` não estiver configurada.
- O endpoint `POST /admin/register` falha de forma fechada quando o segredo está ausente, vazio ou não corresponde ao código informado.
- Nenhum usuário, senha, papel, atribuição ou registro do Neon foi alterado.

**Ação operacional obrigatória antes de publicar:** confirmar no Render que a variável privada `ADMIN_REGISTRATION_CODE` está configurada com um segredo forte. Não registrar o valor do segredo em documentação, código, commits ou mensagens.

## Pontos detectados que precisam de validação

### 2. Login administrativo retorna `error=1`

O endpoint redireciona para `/admin-login.html?error=1` quando o Spring Security lança uma `AuthenticationException`. Isso pode indicar credenciais incorretas ou falha ao localizar/autenticar a conta, mas o parâmetro sozinho não identifica a causa.

**Próximo diagnóstico:** verificar os logs do Render no horário de uma tentativa e confirmar a configuração da conexão PostgreSQL. Não redefinir credenciais nem executar SQL de produção sem evidência e autorização específica.

### 3. Testes no Docker

O `Dockerfile` usa `./gradlew clean bootJar -x test`; portanto, os testes Java não rodam durante a construção da imagem. O projeto registra validações prévias por GitHub Actions, mas é necessário conferir o estado do pipeline para o commit que será publicado.

**Decisão:** não alterar a estratégia de deploy automaticamente nesta correção, para evitar modificar o comportamento de produção sem verificar o pipeline existente.

### 4. Infraestrutura e produção

Esta auditoria não comprova a configuração atual do Render, a versão publicada no Cloudflare nem a conectividade do banco Neon. O código corrigido está em uma branch separada e não foi mesclado à branch de produção nem implantado.

## Testes e limitações

- Revisão estática dos arquivos alterados realizada.
- Testes Java e build ainda precisam ser executados no pipeline com Java 21.
- Não houve alteração direta no banco Neon.
- Não houve deploy no Render ou Cloudflare.
- A validação manual do cadastro administrativo deve confirmar que o endpoint rejeita o cadastro quando o segredo não está configurado e aceita apenas o segredo definido no ambiente.
