# ADR-003 - Desempate P01 como ordenação determinística em Java

**Status:** Proposta (aguarda aprovação da arquitetura, D15). A premissa P01 depende de validação da instituição (Q01).
**Data:** 2026-10-16

## Contexto
CA04 do PRD pede uma "regra de desempate", e P01 é a premissa escolhida: nota igual, vence a data de nascimento mais antiga; depois, nome em ordem alfabética; por último, menor id. O resultado precisa ser o mesmo em duas gerações seguidas (MVP-CA20, complemento). Três pontos de risco: a collation do MySQL muda a ordem de nomes com acento; a collation da JVM também; e `ORDER BY` com nulos e empates no banco não é garantido sem chave completa.

## Decisão
- A ordenação fica no `classificacao`, em Java, em um único método de comparação.
- Chave, nesta ordem: (1) nota final decrescente, comparada como `BigDecimal` pelo valor; (2) data de nascimento crescente; (3) nome crescente pela chave de nome, que é o nome sem acentos, em caixa alta, comparado por código Unicode; (4) id da matrícula crescente.
- Os dados chegam do banco sem `ORDER BY` de negócio. A ordem vem só do comparador.
- Posições são sequenciais por ano escolar (1, 2 e 3 separadamente), sem ex-aequo. Seleção: posição até `vagasPorAno` (padrão 10).
- O comparador é testado isoladamente, com os casos MVP-CA18, CA19, CA20 e CA22.

## Alternativas
- `ORDER BY nota DESC, data_nascimento, nome, id` no MySQL: descartado. O resultado do nome depende da collation (`utf8mb4_0900_ai_ci` ignora acentos, por exemplo), e a mudança de collation mudaria o ranking.
- Usar `Collator` do Java para nomes: descartado como chave principal. A tabela de regras da JVM pode variar entre versões. A chave manual (sem acento, caixa alta, código Unicode) é a mesma em qualquer ambiente.
- Desempate aleatório: descartado. Viola a exigência de resultado repetível.

## Consequências
- Se a instituição definir outra regra, muda-se a chave em um único lugar.
- Nomes que diferem só por acento (exemplo: "Ana" e "Âna") caem no mesmo grupo e vão para o id. É um comportamento documentado, não um erro.
- O volume é pequeno (dezenas a centenas de alunos por processo), então ordenar em memória não é gargalo.
