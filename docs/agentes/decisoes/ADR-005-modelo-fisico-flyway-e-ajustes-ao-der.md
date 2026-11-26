# ADR-005 - Modelo físico, Flyway e ajustes ao DER

**Status:** Proposta (aguarda aprovação da arquitetura, D15). As propostas P-DER-1 a P-DER-4 precisam ser anotadas pelo orquestrador em `decisoes.md`.
**Data:** 2026-10-16

## Contexto
O DER é o modelo lógico, mas tem lacunas para a implementação: BOLSAS sem `criada_por` (RNF06 exige), NOTAS_PROVAS sem registro de correção (Q07), valores de enum não definidos, e PROCESSOS_SELETIVOS sem unicidade por prova (Q16). O DER não pode ser editado (D17).

## Decisão
- Migração com Flyway. Scripts em `backend/src/main/resources/db/migration/`. Primeiro script: `V1__esquema_mvp.sql`. Fonte no projeto: `docs/agentes/scripts/V1__esquema_mvp.sql`.
- Hibernate em `validate`. Nenhum perfil usa `update` ou `create`.
- Tipos físicos: `enum` do DER vira `VARCHAR` com `CHECK`. `timestamp` vira `DATETIME(3)` em UTC. `datetime` vira `DATETIME(3)`. Motivos em `03-modelo-fisico.md`, seção 3.
- Ajustes aplicados no V1 e listados como propostas ao DER: P-DER-1 (`BOLSAS.criada_por`), P-DER-2 (`NOTAS_PROVAS.alterada_por` e `alterada_em`), P-DER-3 (valores de enum), P-DER-4 (`UNIQUE (prova_id)` em PROCESSOS_SELETIVOS).
- Proposta não aplicada: P-DER-5 (`ano_escolar` em CLASSIFICACOES, para UNIQUE por ano e posição). Não aplicada por D05.
- Índices únicos da D04 aplicados sem alteração.
- Tabelas P1 não são criadas (D06).

## Alternativas
- Editar o DER antes: descartado. Regra D17 e o gate D15 pedem que o orquestrador decida.
- Usar `ENUM` do MySQL: descartado. Risco de divergência de tipo com a validação do Hibernate. `CHECK` cobre o mesmo caso.
- Usar `TIMESTAMP`: descartado. Limite em 2038 e conversão de fuso que complica a comparação de datas.
- Hibernate `update` no MVP: descartado. Alteraria o banco sem histórico.
- Script inicial com dados de teste: descartado. Contas de teste são criadas pela aplicação só em `dev` e `test` (ADR-006).

## Consequências
- Se o orquestrador recusar alguma proposta, o V1 é ajustado antes do primeiro `migrate` em qualquer ambiente. Depois de aplicado, muda-se só por V2.
- O DER do projeto continua sem alteração. Quem ler o DER encontra as diferenças listadas em `03-modelo-fisico.md`, seção 5.
