# Padrao de erros HTTP da API

As respostas de erro das APIs seguem o mesmo envelope JSON:

```json
{
  "timestamp": "2026-11-12T10:00:00Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Os dados enviados sao invalidos.",
  "path": "/api/recurso"
}
```

## Codigos padronizados

| Status | Codigo | Quando utilizar |
|---|---|---|
| 400 | BAD_REQUEST | Validacao, parametro ausente, JSON malformado ou argumento invalido |
| 401 | UNAUTHORIZED | Requisicao a API sem autenticacao |
| 403 | FORBIDDEN | Usuario autenticado sem permissao para a operacao |
| 404 | NOT_FOUND | Recurso nao encontrado; use ResourceNotFoundException ou NoSuchElementException |
| 409 | CONFLICT | Duplicidade, integridade de dados ou conflito com o estado atual; use ConflictException ou IllegalStateException |

## Como usar nas regras de negocio

- Lance `ResourceNotFoundException("Veiculo nao encontrado.")` quando uma consulta nao localizar o recurso.
- Lance `ConflictException("Ja existe uma solicitacao pendente para este plano.")` para conflitos conhecidos.
- Excecoes de validacao `IllegalArgumentException` resultam em 400.
- Restricoes de integridade do banco resultam em 409, sem expor detalhes SQL.
- O Spring Security retorna 401 e 403 no mesmo formato para rotas `/api/**`.

As paginas HTML continuam usando os fluxos existentes de login e a pagina `/403.html`. Os controladores de formularios que ja tratam seus erros com redirecionamentos permanecem inalterados.

## Observacao

O handler central cobre validacao e excecoes comuns. Cada endpoint deve lançar as excecoes de dominio indicadas para distinguir corretamente 404 e 409. Execute a suite Gradle e testes de integracao antes de promover a branch para producao.
