# Operação territorial — branch 11

## Funcionalidades adicionadas em 13/10/2026

- Tela protegida `operacao.html`, acessível pelos painéis e mapa administrativos.
- Cadastro de setores e lojas pelo ADMIN_GERAL. A loja informa setor, coordenadas e capacidade.
- Veículo ligado opcionalmente a uma loja. Os veículos existentes são preservados sem atribuição.
- ADMIN_SETORIAL vê e opera apenas lojas ativas dos setores ativos atribuídos à sua conta. O mapa também aplica esse filtro no servidor.
- ADMIN_GERAL e ADMIN legado mantêm acesso global à frota; somente ADMIN_GERAL cadastra território e aprova redistribuição. Não existe promoção automática de contas.
- Transferências validam acesso na origem e destino, estado de aluguel, loja/setor ativos, bloqueio e capacidade. A loja de destino é bloqueada na transação para serializar transferências e alterações de capacidade concorrentes.
- A edição do veículo no painel global também bloqueia a linha em transação para não sobrescrever uma transferência concorrente.
- Transferência atualiza a loja, coordenadas e nome de localização do veículo.
- Capacidade não pode ser reduzida abaixo da quantidade atual de veículos. Veículos alugados não podem ser alterados ou transferidos por esta operação.
- Tela de propostas: simulação, criação manual ou a partir da sugestão, fila de pendentes, aprovação e rejeição. A aprovação continua sendo explícita e restrita ao administrador geral.
- Novas escritas exigem token CSRF. A tela busca o token na sessão e o envia no cabeçalho indicado pelo servidor.
- Proxy do Cloudflare protege também `/operacao` e `/operacao.html` e encaminha operações ao Render.
- Pipeline GitHub Actions com Java 21, testes Java, geração do JAR, testes Node e relatório de testes. O banco de testes é H2 em memória, sem usar o Neon de produção.

## Uso e dados existentes

1. Uma conta já provisionada como ADMIN_GERAL abre Operação territorial.
2. Cadastra setores e lojas. Os códigos são únicos; os formulários têm limites e validação no servidor.
3. Cadastra-se um administrador setorial pelo fluxo já existente. O cadastro público não concede ADMIN_GERAL.
4. O administrador geral cria/aprova a proposta de atribuição do setor ao ID do administrador setorial.
5. O administrador geral ou legado transfere os veículos sem loja para as lojas cadastradas. Somente então esses veículos ficam visíveis ao administrador setorial responsável.
6. O administrador setorial opera a frota entre suas próprias lojas. Um usuário comum não acessa essa tela ou suas APIs.

O código não altera senhas, papéis ou atribuições automaticamente. O primeiro ADMIN_GERAL foi provisionado manualmente pelo responsável no Neon, conforme registro de produção abaixo. Outras contas ADMIN legado não recebem essa promoção automaticamente.

## Alterações no banco

JPA adiciona `store_id` opcional em `TB_VEICULO`, `capacity` e `blocked` opcionais em `tb_loja_ecogiro`. Não há exclusão ou recriação de tabelas. Valores antigos nulos de capacidade/bloqueio são lidos como 20/false. O projeto já usa `spring.jpa.hibernate.ddl-auto=update`; backup e migrações versionadas continuam recomendados para evolução de produção.

## Verificação e limites

- 16 testes Node passaram localmente: perfis/headers/mapas, falhas e expiração, proxy, cookie, redirecionamento, logout e nova rota protegida.
- Testes Java adicionados para capacidade, bloqueio, alugados, origem não autorizada, atualização de localização, escopo setorial, logout/invalidação de sessão, bloqueios por papel e CSRF.
- Execução local do Gradle bloqueada por rede; execução com Java 21 no GitHub Actions aprovada. `test bootJar` passou, com 16 testes Java e 16 testes Node. Validação do código final: [execução 38329148732](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38329148732), commit `8e1cb57617c6abd8e094b1d47779e181383eb679`. A execução anterior também passou.
- Testes de logout do proxy usam respostas simuladas; o teste Java verifica a sessão no Spring. Logout no site publicado ainda requer confirmação em produção.
- Bloqueio manual não é trânsito real. Integração com trânsito, tempo de viagem, rotas e otimização por distância/carga necessita fornecedor e dados externos.
- Simulação de setores ainda usa quantidade de setores, não quantidade de lojas/veículos, distância ou tráfego.
- A transferência registra a posição administrativa na loja; não representa GPS ao vivo ou execução física do transporte.
- CRUD completo de lojas/setores, auditoria das transferências, atribuição automática de rotas e validação física de localização continuam fora desta entrega.
- Novos commits da equipe `bb1f5fe` e `f53ae8e` foram comparados à base `ad378bb`: não apresentaram diferença líquida de arquivos. Não houve substituição da interface da equipe.

## Deploy

Publicar a mesma branch 11 no Render e Cloudflare. A página nova vem do backend para exigir sessão; assets continuam no Pages. O Neon não possui branch de código da aplicação. Revalidar login, Operação territorial, mapa por perfil e logout após os dois deploys.

## Verificação em produção e correções — 12/10/2026 (Brasília)

As capturas enviadas confirmaram deploy bem-sucedido de `93e497c` no Render e no Cloudflare Pages de produção. A tela de operação carregou os veículos do Neon. A restrição antiga `tb_usuario_role_check` rejeitou ADMIN_GERAL com SQLSTATE 23514: `ddl-auto=update` não atualizou essa regra antiga. A migração manual [20261013_admin_roles.sql](docs/migrations/20261013_admin_roles.sql) permite os quatro papéis válidos mantendo o CHECK. Não concede privilégios automaticamente. O usuário executou a migração e alterou somente sua própria conta para ADMIN_GERAL; a tela de cadastro geral passou a aparecer após novo login.

O log de 01:09:31 UTC confirmou duplicidade de `DF-CENTRO` em `uk_setor_codigo`, SQLSTATE 23505. O setor já existia. A correção retorna HTTP 409 com mensagem de código duplicado para setores e lojas, sem mostrar SQL ou detalhes internos. A tela exibe a mensagem com texto seguro, impede reenvio simultâneo dos formulários e distingue cadastro salvo de falha na atualização da tela. Teste de integração reproduz o cadastro repetido.

Cadastro de loja, transferência, capacidade/bloqueio e aprovação com um administrador setorial ainda precisam de teste manual em produção. A captura da tela geral demonstra novo login, mas não confirma tentativa de reutilizar a sessão encerrada.

Validação da correção: 17 testes Java e compilação `test bootJar` aprovados no [Actions 38331010701](https://github.com/arthurkratos20-max/EcoGiro/actions/runs/38331010701); 18 testes Node aprovados localmente, incluindo os dois novos testes de formulário. O código foi validado antes da atualização da branch de deploy.
