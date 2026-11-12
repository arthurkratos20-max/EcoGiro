# EcoGiro — Mobilidade Sustentável Urbana

> **Nota sobre a branch principal:** este README está na branch padrão `Main`, que ainda conserva uma versão anterior do código. A documentação técnica abaixo descreve a implementação mais recente registrada na branch [feature/branch12-rf04-rf05-rf08-rf09](https://github.com/arthurkratos20-max/EcoGiro/tree/feature/branch12-rf04-rf05-rf08-rf09). Esta atualização altera somente a documentação da branch `Main`: não mescla branches, não modifica o código-fonte nem altera as configurações de publicação. A branch de produção do frontend permanece separada.

Plataforma web acadêmica para apoiar a mobilidade urbana sustentável, reunindo recomendações de meios de transporte, consulta de planos, solicitações de contratação e ferramentas administrativas para gestão de usuários, veículos e unidades.

## Identificação acadêmica

- **Instituição:** Centro Universitário do Planalto Central Apparecido dos Santos (UniCEPLAC)
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Disciplina:** Projeto Integrado WEB
- **Orientador:** Prof. Hudson Neves

### Equipe

- Alessandro Falcão Zovico
- Arthur Cauã Machado De Souza
- Cauã Alves Fernandes
- Edna Júlia Menezes Marques
- Lucas Ibiapina De Morais
- Pedro Henrique Ferreira Gomes
- Sophia Fonseca Mandovano Moreira de Azevedo
- Yudi Fidelis De Morais

---

## Aplicação e repositório

- **Aplicação publicada:** [https://ecogiro.pages.dev/](https://ecogiro.pages.dev/)
- **Backend / serviço Render:** [https://ecogiro.onrender.com/](https://ecogiro.onrender.com/)
- **Login administrativo:** [https://ecogiro.onrender.com/admin-login.html](https://ecogiro.onrender.com/admin-login.html)
- **Repositório:** [arthurkratos20-max/EcoGiro](https://github.com/arthurkratos20-max/EcoGiro)
- **Branch de desenvolvimento documentada:** `feature/branch12-rf04-rf05-rf08-rf09`
- **Módulo Java:** `EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro`

> A branch de desenvolvimento não deve ser confundida com a branch de produção do Pages. Alterações em branches conectadas a deploy automático podem disparar novas publicações conforme as integrações configuradas no provedor.

## Visão geral da arquitetura

O EcoGiro é dividido em interface web, backend e persistência de dados:

| Camada | Tecnologias / responsabilidade |
| --- | --- |
| Frontend | HTML, CSS e JavaScript; interface do usuário, formulários, painéis e integração com a API |
| Backend | Java 21 e Spring Boot; endpoints HTTP, autenticação, autorização e regras de negócio |
| Persistência | Spring Data JPA e Hibernate; mapeamento objeto-relacional e acesso aos dados |
| Banco de dados | PostgreSQL, com configuração para Neon na nuvem; H2 como fallback local e para testes |
| Build e dependências | Gradle Wrapper |
| Hospedagem da API | Render |
| Publicação da interface | Cloudflare Pages |
| Mapas | Leaflet e OpenStreetMap |
| Monitoramento | UptimeRobot para verificações periódicas de disponibilidade |

### Organização do backend

O código Java segue uma separação de responsabilidades por pacotes:

- **`controller`:** recebe requisições HTTP, valida entradas de transporte e encaminha operações para os serviços.
- **`service`:** concentra regras de negócio, validações e coordenação dos fluxos.
- **`model`:** contém as entidades e enumerações que representam os dados do domínio.
- **`repository`:** abstrai operações de persistência com Spring Data JPA.
- **`dto`:** define objetos de transferência de dados entre a API e as demais camadas.
- **`config`:** reúne configurações, incluindo segurança e comportamento da aplicação.

Essa divisão reduz o acoplamento entre entrada HTTP, regras de negócio e banco de dados, facilitando testes, manutenção e evolução.

### Principais funcionalidades do backend

- Cadastro e autenticação de usuários e administradores.
- Validação de dados e prevenção de cadastros duplicados por CPF e e-mail.
- Hash de senhas com BCrypt e controle de acesso com Spring Security.
- Consulta e gerenciamento de planos de aluguel.
- Criação, consulta, cancelamento, aprovação e rejeição de solicitações.
- Seleção e validação, no servidor, de veículos compatíveis com o plano.
- Geração de protocolo UUID no servidor para novas solicitações.
- Registro de assinatura quando uma solicitação é aprovada.
- Gerenciamento administrativo de usuários, planos e frota.
- Operações territoriais com lojas, setores, localização e movimentação de veículos.
- Permissões distintas para administrador geral e administrador setorial, respeitando os setores atribuídos.

### Recomendação de transporte

O questionário de recomendação utiliza uma matriz determinística de preferências no JavaScript para classificar os perfis de transporte. O backend recebe e persiste o histórico da recomendação; ele não recalcula o ranking do questionário. As regras de compatibilidade de planos e veículos são verificadas separadamente no servidor, sem confiar no resultado do quiz como autorização para contratação.

## Gestão do projeto — Scrum, GitHub Projects e Git

A equipe utilizou uma abordagem ágil baseada em Scrum para organizar o desenvolvimento de forma incremental, dividir o escopo em atividades menores e acompanhar a evolução das entregas.

O **GitHub Projects** foi utilizado como quadro de acompanhamento do trabalho. Ele permite organizar as atividades, visualizar o que precisa ser feito, acompanhar tarefas em andamento e identificar o que já foi concluído. O quadro funciona como apoio à priorização e à transparência do progresso entre os integrantes.

O **GitHub** foi utilizado para hospedar o repositório e controlar a evolução do código-fonte:

- **Branches:** isolam conjuntos de alterações e permitem desenvolver funcionalidades sem editar diretamente a linha principal.
- **Commits:** registram alterações com histórico e mensagens descritivas.
- **Pull Requests:** permitem propor, revisar e integrar alterações entre branches.
- **Histórico do repositório:** ajuda a rastrear mudanças e identificar em qual etapa uma implementação foi introduzida.

No backend, esse fluxo foi aplicado junto à divisão em camadas: as funcionalidades e correções eram implementadas nos componentes responsáveis, verificadas por testes e registradas no controle de versão antes da integração. O Gradle Wrapper padroniza a execução do build e dos testes, enquanto o GitHub Actions pode automatizar as verificações configuradas no repositório.

> A documentação descreve a utilização do Scrum como abordagem de organização e do GitHub Projects como quadro de tarefas. Não presume duração fixa de sprints ou realização de cerimônias específicas que não estejam registradas pelo time.

## Infraestrutura e publicação

### Neon — PostgreSQL

O Neon fornece o serviço PostgreSQL gerenciado na nuvem. A aplicação Spring Boot aceita a conexão por variáveis de ambiente:

- `SPRING_DATASOURCE_URL` — URL JDBC do banco.
- `SPRING_DATASOURCE_USERNAME` — usuário do banco.
- `SPRING_DATASOURCE_PASSWORD` — senha do banco.

Em ambiente local, o projeto mantém H2 como fallback. Para produção, as variáveis do PostgreSQL precisam estar configuradas no serviço de hospedagem. Nunca inclua credenciais reais em arquivos versionados, commits, issues ou capturas de tela.

### Render — backend

O Render hospeda e executa o serviço Java/Spring Boot. A aplicação utiliza a variável `PORT` disponibilizada pelo ambiente e as variáveis de conexão do banco. O serviço pode entrar em suspensão no plano gratuito após períodos de inatividade; o monitoramento externo não representa garantia de disponibilidade contínua.

### Cloudflare Pages — frontend

O Cloudflare Pages publica a interface web. A configuração de integração encaminha chamadas da API para o backend hospedado no Render, conforme os arquivos de configuração do projeto e a variável de ambiente `BACKEND_URL`.

### UptimeRobot — monitoramento

O UptimeRobot foi configurado para realizar verificações periódicas do endereço público do serviço e enviar notificações de disponibilidade. Ele executa as verificações em infraestrutura externa, não no computador do desenvolvedor. O monitoramento ajuda a identificar indisponibilidade, mas não impede reinicializações ou limitações do plano de hospedagem.

## Modelo de dados e documentação técnica

A documentação complementar está organizada nos seguintes arquivos:

| Documento | Conteúdo |
| --- | --- |
| [Modelo de dados](https://github.com/arthurkratos20-max/EcoGiro/blob/feature/branch12-rf04-rf05-rf08-rf09/docs/MODELO_DE_DADOS.md) | Diagrama entidade-relacionamento, entidades, chaves, relacionamentos e dicionário de dados |
| [Regras de recomendação](https://github.com/arthurkratos20-max/EcoGiro/blob/feature/branch12-rf04-rf05-rf08-rf09/docs/REGRAS_RECOMENDACAO.md) | Matriz de preferências, critérios de pontuação, desempate e cenários de teste |
| [Auditoria do backlog](https://github.com/arthurkratos20-max/EcoGiro/blob/feature/branch12-rf04-rf05-rf08-rf09/docs/AUDITORIA_BACKLOG_20261015.md) | Rastreabilidade de tarefas, decisões de implementação, validações e pendências |
| [Publicação da Branch 12](https://github.com/arthurkratos20-max/EcoGiro/blob/feature/branch12-rf04-rf05-rf08-rf09/DEPLOY_BRANCH12.md) | Configuração de Render, Neon e Cloudflare Pages |

## Execução local

### Requisitos

- JDK 21
- Node.js 22 para os testes do frontend
- Git (para clonar o repositório)

### Backend

Entre no diretório do módulo Java:

```bash
cd "EcoGiro Branch 11/EcoGiro-feature-Adicao-Documentos/ecogiro"
```

No Linux/macOS:

```bash
bash gradlew test bootJar --no-daemon
bash gradlew bootRun
```

No Windows PowerShell:

```powershell
.\gradlew.bat test bootJar
.\gradlew.bat bootRun
```

Por padrão, a aplicação fica disponível em `http://localhost:8080/`, respeitando a porta definida pela variável `PORT`.

### Testes do frontend e regras JavaScript

Na raiz do repositório, com Node.js 22:

```bash
node --test tests/*.test.cjs
```

Os testes automatizados cobrem regras do questionário e comportamentos da interface. Os testes Java verificam serviços, regras de negócio, segurança e persistência conforme os cenários implementados. A execução local com H2 não substitui a homologação de integração com PostgreSQL/Neon.

## API de contratação

Principais endpoints do fluxo de planos e solicitações:

| Método | Endpoint | Finalidade |
| --- | --- | --- |
| `GET` | `/api/user/plans` | Consultar planos ativos |
| `GET` | `/api/user/vehicles?planId=ID` | Listar veículos disponíveis e compatíveis |
| `POST` | `/api/user/requests` | Criar solicitação com plano, veículo e mensagem opcional |
| `GET` | `/api/user/requests` | Consultar histórico de solicitações |
| `POST` | `/api/user/requests/{id}/cancel` | Cancelar solicitação própria pendente |
| `PATCH` | `/api/admin/requests/{id}/respond` | Aprovar ou rejeitar solicitação conforme autorização |

O protocolo de novas solicitações é gerado no servidor. Registros legados podem utilizar identificadores de apresentação próprios. A seleção de uma unidade na solicitação valida compatibilidade, mas não representa reserva física ou confirmação de aluguel do veículo.

## Segurança e boas práticas

- Senhas são armazenadas com hash BCrypt, não em texto puro.
- A autorização é verificada no backend, além de eventuais controles de interface.
- Regras de compatibilidade e permissões são revalidadas no servidor.
- Segredos e credenciais devem ser configurados como variáveis de ambiente nos provedores.
- Não utilize dados pessoais reais em exemplos, documentação pública ou evidências de teste.

## Limitações e validação

A existência de configuração de deploy não garante, por si só, que todos os fluxos foram homologados em produção. A integração real com Neon depende das variáveis de ambiente corretas e deve ser validada no ambiente publicado. A suíte com H2 valida o comportamento automatizado, mas não substitui testes de integração no PostgreSQL de produção.

O questionário oferece uma recomendação baseada em preferências informadas pelo usuário; não calcula custo real por quilômetro, autonomia garantida ou elegibilidade legal de cada modelo. A seleção de veículo em uma solicitação não implementa reserva física de frota. Funcionalidades futuras devem ser identificadas como planejadas até que sejam implementadas e testadas.

---

**EcoGiro — tecnologia, organização e mobilidade sustentável.**
