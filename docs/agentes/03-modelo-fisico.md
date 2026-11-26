# 03 - Modelo físico (MySQL 8.0)

**Situação:** proposta do arquiteto, aguardando aprovação (gate D15).
**Fontes:** `der_pe_de_meia.mmd`, `docs/agentes/decisoes.md` (D04, D05, D06, D07), `docs/agentes/01-requisitos-mvp.md`.

## 1. Fonte única do DDL

O script é o arquivo **`/home/scutulatus/Downloads/ETEC - 3E - 2026/Projetos 3E/Projeto_07_Pe_de_Meia/docs/agentes/scripts/V1__esquema_mvp.sql`**.

- Destino no backend: `backend/src/main/resources/db/migration/V1__esquema_mvp.sql`. O dev copia o arquivo sem alterar o conteúdo.
- Motivo do caminho em `docs/agentes/scripts/`: a regra da entrega proíbe criar arquivos fora de `docs/agentes/`. O caminho de destino fica registrado aqui e em `05-estrutura-pastas.md`.
- Depois de aplicada, uma migração não é editada. Qualquer mudança vira `V2__...sql`.

## 2. Escopo

| Tabela (DER) | No MVP | Observação |
|---|---|---|
| USUARIOS | Sim | Contas de acesso (D02). |
| ALUNOS | Sim | RF01, RNF02. |
| PERIODOS_LETIVOS | Sim | Necessária para matrícula e prova (D05, D06). |
| MATRICULAS | Sim | `ano_escolar` (P05). |
| PROVAS | Sim | RF04. |
| NOTAS_PROVAS | Sim | RF05. |
| PROCESSOS_SELETIVOS | Sim | MVP-RF06. |
| CLASSIFICACOES | Sim | MVP-RF07 e MVP-RF08. |
| BOLSAS | Sim | MVP-RF09. |
| DISCIPLINAS | **Não** | P1 (D06). |
| MEDIAS_DISCIPLINAS | **Não** | P1 (D06). |
| FREQUENCIAS | **Não** | P1 (D06). |
| HISTORICO_BOLSAS | **Não** | P1 (D06). Cancelamento fica fora do MVP (MVP-CA30). |

O script não cria nenhuma das quatro tabelas P1, nem FKs para elas.

## 3. Mapeamento de tipos (DER para MySQL)

| Tipo no DER | Tipo físico | Motivo |
|---|---|---|
| `bigint` (id, FKs) | `BIGINT` com `AUTO_INCREMENT` | Igual ao DER. Mapeia para `Long` no Java. |
| `varchar` | `VARCHAR(n)`, com tamanho definido | Os tamanhos são limites de negócio. |
| `text` | `TEXT` | Igual ao DER. |
| `date` | `DATE` | Igual ao DER. |
| `decimal` (nota, nota_maxima, nota_final) | `DECIMAL(5,2)` | Duas casas decimais (Q08). Até 999,99. |
| `decimal` (valor_mensal) | `DECIMAL(10,2)` | Valor monetário (D07). |
| `tinyint` (ano_escolar) | `TINYINT` com `CHECK` 1 a 3 | P05. |
| `smallint` (posicao) | `SMALLINT` com `CHECK` maior ou igual a 1 | Posição por ano. |
| `boolean` | `BOOLEAN` (alias de `TINYINT(1)`) | Igual ao DER. |
| `enum` | `VARCHAR` com `CHECK ... IN (...)` | Ajuste físico (ver nota abaixo). |
| `timestamp` | `DATETIME(3)` | Ajuste físico (ver nota abaixo). |
| `datetime` | `DATETIME(3)` | Precisão de milissegundos, igual ao restante. |

**Nota sobre enum:** o DER usa `enum`. Foi implementado como `VARCHAR` com `CHECK`. Motivo: o Hibernate valida o esquema com `validate` e tipos `enum` do MySQL costumam gerar divergência de tipo. Além disso, `CHECK` é mais simples de alterar em migração. O conjunto de valores é o mesmo.

**Nota sobre timestamp:** `TIMESTAMP` do MySQL tem limite em 2038 e converte para o fuso da sessão. Por isso o DER `timestamp` vira `DATETIME(3)` em UTC. A aplicação grava `Instant` em UTC. Não é alteração lógica.

## 4. Tabelas, chaves e índices

| Tabela | PK | Índice único (D04 ou proposta) | Outros índices | Checks |
|---|---|---|---|---|
| USUARIOS | id | `uk_usuarios_email` (`email`) | - | `ck_usuarios_perfil` (ADMIN, SECRETARIA, CONSULTA) |
| ALUNOS | id | `uk_alunos_matricula_escolar` (`matricula_escolar`) | `ix_alunos_nome` | - |
| PERIODOS_LETIVOS | id | - | - | `ck_periodos_datas` (fim maior ou igual a início) |
| MATRICULAS | id | **D04** `uk_matriculas_aluno_periodo` (`aluno_id`, `periodo_letivo_id`) | `ix_matriculas_periodo` | `ck_matriculas_ano_escolar` (1 a 3), `ck_matriculas_status` (ATIVA) |
| PROVAS | id | - | `ix_provas_periodo` | `ck_provas_nota_maxima` (maior que 0) |
| NOTAS_PROVAS | id | **D04** `uk_notas_prova_matricula` (`prova_id`, `matricula_id`) | `ix_notas_matricula` | `ck_notas_valor` (maior ou igual a 0) |
| PROCESSOS_SELETIVOS | id | **P-DER-4** `uk_processos_prova` (`prova_id`) | `ix_processos_periodo` | `ck_processos_status` (CRIADO) |
| CLASSIFICACOES | id | **D04** `uk_classificacoes_processo_matricula` (`processo_seletivo_id`, `matricula_id`) | `ix_classificacoes_processo_posicao` (`processo_seletivo_id`, `posicao`), `ix_classificacoes_matricula` | `ck_classificacoes_posicao` (maior ou igual a 1) |
| BOLSAS | id | **D04** `uk_bolsas_matricula_processo` (`matricula_id`, `processo_seletivo_id`) | `ix_bolsas_processo` | `ck_bolsas_valor` (maior que 0), `ck_bolsas_situacao` (ATIVA, CANCELADA) |

As FKs têm nomes `fk_<tabela_origem>_<campo>` e usam o comportamento padrão (`RESTRICT`). O MVP não tem exclusão física de registros. Não há `ON DELETE CASCADE`.

## 5. Propostas de ajuste ao DER (não aplicadas ao DER)

O DER não foi editado (D17). As propostas abaixo estão no script V1. **O orquestrador deve anotá-las em `decisoes.md`** e confirmar ou recusar. Se recusar alguma, o dev remove a coluna ou o índice do V1 antes do primeiro `migrate`, e este documento é atualizado.

| ID | Problema no DER | Proposta aplicada no V1 | Requisito que motiva |
|---|---|---|---|
| **P-DER-1** | BOLSAS não tem `criada_por`. RNF06 diz que a concessão registra quem concedeu. | Coluna `criada_por BIGINT NOT NULL` com FK para USUARIOS. | MVP-RNF06, MVP-RF09 |
| **P-DER-2** | NOTAS_PROVAS só tem `lancada_por` e `lancada_em`. A correção (Q07) apagaria quem lançou a nota original. | Colunas `alterada_por` e `alterada_em` (nulas até a primeira correção). | MVP-RNF06, Q07 |
| **P-DER-3** | Valores de enum não definidos: `MATRICULAS.status`, `PROCESSOS_SELETIVOS.status`, `BOLSAS.situacao`. | `MATRICULAS.status`: ATIVA. `PROCESSOS_SELETIVOS.status`: CRIADO (Q12). `BOLSAS.situacao`: ATIVA e CANCELADA. CANCELADA fica reservado para P1 e não é gravado no MVP. | Q12, MVP-CA30 |
| **P-DER-4** | `PROCESSOS_SELETIVOS.prova_id` não é único. Q16 padrão diz "um processo por prova". | `UNIQUE (prova_id)`. | Q16, MVP-CA15 |

**Proposta opcional, não aplicada (P-DER-5):** adicionar `ano_escolar` em CLASSIFICACOES para impor `UNIQUE (processo_seletivo_id, ano_escolar, posicao)` no banco. Não foi aplicada porque D05 diz para não duplicar o dado. A integridade da posição fica com a transação (ADR-004). Decisão do orquestrador.

## 6. Regras que o banco não impõe (ficam na aplicação)

Estas regras estão no serviço, dentro da transação, com erro de negócio (ver `02-arquitetura.md`, seção 7).

1. Nota entre 0 e `nota_maxima` da prova (P04, MVP-CA12).
2. A matrícula da nota pertence ao período da prova (MVP-CA14).
3. O período do processo é o mesmo período da prova (MVP-CA15).
4. Posições sequenciais por ano, de 1 a N, sem ex-aequo (MVP-CA18).
5. Uma classificação por processo: a aplicação verifica sob lock (MVP-RNF03).
6. Bolsa só para matrícula com `selecionado = TRUE` no processo (MVP-CA26).
7. Valor da bolsa igual ao valor de configuração, R$ 300,00 (D07, MVP-CA25).
8. Nenhuma nota é lançada ou corrigida depois que a classificação do processo existe (Q07 padrão).
9. RN08 (reintegração) não se aplica ao MVP (D06).

## 7. Script de dados iniciais

O V1 não insere usuários. Contas de teste `test@IA_<finalidade>` (D18) são criadas pela aplicação só nos perfis `dev` e `test` (ADR-006). Credenciais de produção vêm de variáveis de ambiente.

## 8. Verificação

- O dev sobe o MySQL 8.0 (ver `05-estrutura-pastas.md`, seção 4) e roda a aplicação. Com `quarkus.hibernate-orm.database.generation=validate`, o Hibernate confere o esquema no boot.
- Testes de integração rodam o V1 em um MySQL 8.0 descartável (ADR-007).
- Os CHECKs e UNIQUEs são testados no nível de integração com inserções que devem falhar (MVP-RNF03).
