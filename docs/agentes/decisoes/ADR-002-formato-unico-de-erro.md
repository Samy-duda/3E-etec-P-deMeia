# ADR-002 - Formato único de erro

**Status:** Proposta (aguarda aprovação da arquitetura, D15)
**Data:** 2026-10-16

## Contexto
O frontend precisa mostrar mensagens diferentes para cada erro (duplicidade, nota fora do intervalo, sem perfil). O backend tem várias fontes de erro: validação do Bean Validation, regra de negócio, constraint do banco e falha inesperada. Sem padrão, cada endpoint responderia de um jeito.

## Decisão
- Todo erro devolve o JSON `ErroResposta`: `timestamp`, `status`, `codigo`, `mensagem`, `caminho`, `campos`.
- `codigo` é um valor fixo do enum `CodigoErro` (lista no contrato). O frontend mapeia o código para a mensagem, não o texto.
- `campos` só é preenchido em `VALIDACAO_INVALIDA` (400).
- Status HTTP: 400 formato/validação; 401 autenticação; 403 perfil; 404 inexistente; 409 duplicidade ou estado conflitante; 422 regra de negócio; 500 erro inesperado.
- Implementação: `ExceptionMapper` para `NegocioException` (carrega o código), para violação de Bean Validation e para `PersistenceException`. A violação de UNIQUE é traduzida pelo nome da constraint (mapa em `02-arquitetura.md`, seção 7). Erro inesperado devolve `ERRO_INTERNO` sem stack trace.
- Erros 401 e 403 de segurança passam pelo mesmo formato (o Quarkus precisa de mapeamento próprio para isso).

## Alternativas
- RFC 7807 (Problem Details): descartado. É formato válido, mas exige `type` e `instance` com URIs, sem ganho para o MVP. O formato fica simples e documentado no contrato.
- Texto livre na mensagem sem código: descartado. O frontend teria de comparar texto.
- Status 400 para toda regra de negócio: descartado. Separar 409 (conflito) e 422 (regra) deixa o cliente distinguir o caso sem ler a mensagem.

## Consequências
- O contrato lista os códigos. Adicionar código novo exige atualizar o enum no contrato antes do código.
- Testes podem verificar o código, e não a mensagem, o que deixa o texto livre para revisão.
