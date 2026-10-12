# Correção da navegação do mapa — 12/10/2026

Branch: branch10---erros-corrigidos.

O mapa compartilhado identificava o perfil por respostas das APIs administrativas. Agora o administrador abre mapa-admin.html, com navegação administrativa própria; mapa.html mantém a navegação do usuário. Os dois reutilizam mapa.js e a mesma API de veículos.

GET /api/session/profile identifica apenas o papel da sessão autenticada, sem expor dados pessoais. Administradores que abrem o endereço antigo são encaminhados ao mapa administrativo. ADMIN_SETORIAL recebe o link Meus setores; ADMIN_GERAL e ADMIN legado recebem Administração. Falhas da API não ativam a navegação de usuário. O Spring Security restringe mapa-admin.html aos administradores.

Validação: node --test tests/map-navigation.test.cjs (11 cenários); node --check para mapa.js; git diff --check. Compilação Java e teste completo no servidor não executados neste ambiente, que tem JDK 17; o projeto requer JDK 21.

Após publicar backend e frontend da mesma branch, validar com contas USER, ADMIN legado, ADMIN_GERAL e ADMIN_SETORIAL: painel → mapa → painel, acesso ao endereço antigo, logout e acesso negado ao mapa administrativo com USER. A publicação do commit no GitHub não confirma, por si só, o deploy no Render/Cloudflare.
