# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Natureza do repositório

Este diretório **não contém código de aplicação**. É um conjunto de artefatos de documentação e modelagem do Projeto Pé-de-Meia (trabalho escolar, ETEC 3E 2026). Não há comandos de build, lint ou testes, nem é um repositório git.

## Arquivos e como se relacionam

- `PRD_Pe_de_Meia.md`: fonte de verdade dos requisitos (RF01–RF10, RN01–RN08, CA01–CA10, prioridades P0–P2, riscos). Os itens "Pontos a definir" (desempate, limite de faltas, procedimento de revisão de cancelamentos) ainda estão em aberto.
- `mer_pe_de_meia.mmd` / `.puml`: MER conceitual, com relacionamentos N:N ainda não resolvidos.
- `der_pe_de_meia.mmd` / `.puml`: DER lógico/físico para MySQL 8.0+. Resolve os N:N do MER por meio de tabelas associativas (`NOTAS_PROVAS`, `MEDIAS_DISCIPLINAS`, `CLASSIFICACOES`) e acrescenta controle de auditoria (`criada_por`, `lancada_por`, `analisada_por`, `alterada_por`, `cancelada_por`, etc.).
- `DER.png`: imagem do DER.
- `Projeto_07_Pe_de_Meia.docx`: documento de entrega do projeto.

Os pares `.mmd`/`.puml` descrevem o mesmo modelo em duas sintaxes. Ao alterar um, altere o outro e o MER/DER correspondente para manter os três alinhados.

## Modelo de domínio (visão geral)

O fluxo central, que atravessa PRD e DER, é:

`PERIODO_LETIVO` → `MATRICULA` (aluno × período, com `ano_escolar`) → `PROVA` / `NOTAS_PROVAS` → `PROCESSO_SELETIVO` / `CLASSIFICACOES` (ranking por ano escolar) → `BOLSA` → `HISTORICO_BOLSA` (trilha de mudanças de situação).

Pontos que exigem leitura cruzada:

- A regra de seleção (10 melhores por ano escolar, RN01) depende de `CLASSIFICACOES.posicao` **e** de `MATRICULAS.ano_escolar`; o DER não tem `ano_escolar` em `CLASSIFICACOES`, então o ranking por ano é derivado via matrícula.
- A manutenção da bolsa (RN04 média ≥ 6,0 em todas as disciplinas; RN05/RN06 faltas com justificativa aceita) cruza `MEDIAS_DISCIPLINAS` e `FREQUENCIAS` com `BOLSAS`. Cancelamentos são registrados em `BOLSAS` (`motivo_cancelamento`, `cancelada_por`) e auditados em `HISTORICO_BOLSAS`.
- RN08 / CA09 (sem reintegração no mesmo período letivo) não tem constraint explícita no DER; qualquer implementação precisa impô-la na aplicação ou em regra de banco.
- Valor da bolsa: R$ 300,00 mensais (RN03), armazenado em `BOLSAS.valor_mensal`. Não hardcodar o valor fora desse campo.
- O DER cita como fonte um "script SQL do arquivo banco_de_dados (seção 3)" que **não está neste repositório**. Se for necessário conferir tipos ou constraints, peça esse arquivo ao usuário.

## Ao editar

- Idioma: os artefatos estão em português; mantenha nomes de entidades e campos no padrão já usado (plural maiúsculo no DER/MER, snake_case em minúsculas nos atributos).
- Mantenha a rastreabilidade: requisitos citam IDs (RF/RN/CA); não renumere sem atualizar as referências.
- Não invente regras de negócio. Pontos ainda não definidos no PRD (desempate, limite de faltas, revisão de cancelamento) devem permanecer marcados como pendentes, não resolvidos por suposição.
