# ADR-004 - Transações e concorrência

**Status:** Proposta (aguarda aprovação da arquitetura, D15)
**Data:** 2026-10-16

## Contexto
MVP-RNF03 pede que uma operação que falha não deixe registros parciais (classificação e concessão em lote). Também é preciso impedir duas gerações de classificação para o mesmo processo e evitar que uma nota seja lançada enquanto a classificação está sendo gerada. Duas requisições podem chegar ao mesmo tempo.

## Decisão
- A transação fica no serviço (`@Transactional`). O resource não tem transação.
- Geração de classificação: transação única. Primeiro um `SELECT ... FOR UPDATE` na linha de PROVAS da prova do processo. Depois verifica se já existe classificação (409) e se há notas (422). Só então grava todas as linhas de CLASSIFICACOES. Se qualquer passo falhar, a transação faz rollback.
- Lançamento e correção de nota: transação única com o mesmo `SELECT ... FOR UPDATE` na prova. Assim, uma nota não entra entre a verificação de "classificação existe" e a geração.
- Concessão de bolsas: `SELECT ... FOR UPDATE` no processo. Em lote, uma transação; se uma bolsa violar regra, nenhuma é criada.
- O lock é na prova porque a relação é 1 para 1 (P-DER-4), e assim um único ponto serializa notas e classificação.
- Isolamento padrão do InnoDB (`REPEATABLE READ`).
- Exceções de negócio herdam de `RuntimeException`, o que causa rollback automático.
- Como segunda barreira, as constraints UNIQUE do V1 pegam duplicidade que escape da verificação (mapa de erros em `02-arquitetura.md`).

## Alternativas
- Lock otimista (`@Version`): descartado. O conflito é raro, e o lock pessimista é mais simples de explicar e testar. Se a contenção crescer, reavaliar.
- Serializar tudo com um lock global: descartado. Exagero para o volume do MVP.
- Confiar só na UNIQUE do banco: descartado. A UNIQUE não cobre "classificação já existe" nem "nota lançada depois da classificação".
- Gravar a classificação em partes (uma transação por ano): descartado. Viola MVP-RNF03.

## Consequências
- Operações de escrita concorrentes na mesma prova são serializadas. Aceitável para o volume do MVP.
- Testes de concorrência ficam como teste de integração com duas threads (uma gera, outra lança nota). Se o tempo não permitir, o teste fica registrado como pendência.
- P-DER-5 (posição única por ano no banco) não foi aplicada. A garantia de posição única por ano é desta transação.
