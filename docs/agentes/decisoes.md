# Decisões e premissas do MVP Pé-de-Meia

Registro das escolhas técnicas e de negócio feitas pelo orquestrador para entregar o P0 (PRD seção 9.1).

Legenda:
- **Decisão**: escolha técnica ou de negócio tomada pela equipe.
- **Premissa**: regra escolhida para um ponto que o PRD deixou em aberto. Não é regra oficial e deve ser validada pela instituição.

## Stack e arquitetura

**D01 - Stack.** Backend em Quarkus 3.x (Java 17, Maven); frontend em Angular (standalone components); banco MySQL 8.0.
- Justificativa: os perfis disponíveis indicam Quarkus/Java e Angular; o DER pede MySQL 8.0+.
- Motivo para Java 17 e não 21: o ambiente tem JDK 17 instalado e o Quarkus 3.x suporta Java 17. Não foi instalado JDK novo.
- Ambiente: Docker está disponível; MySQL 8.0 será usado via container nos testes de integração. Não há MySQL nativo instalado.

**D02 - Autenticação e perfis.** Login com JWT (SmallRye JWT no Quarkus). Perfis: `ADMIN` (acesso total), `SECRETARIA` (cadastros, provas, notas, processo seletivo e bolsas), `CONSULTA` (somente leitura de classificações e bolsas, sem dados pessoais além do necessário).
- Justificativa: CA10 e o requisito de segurança do PRD exigem autenticação e controle por perfil.
- Premissa: os perfis são uma proposta; a instituição deve confirmar quem pode ver dados pessoais.

**D03 - Estrutura do repositório.** `backend/` (Quarkus) e `frontend/` (Angular) dentro do diretório do projeto; documentos de agentes em `docs/agentes/`.
- Conflito registrado: o `CLAUDE.md` atual diz que o diretório não contém código de aplicação. A instrução do usuário, que tem prioridade, define a estrutura acima. O `CLAUDE.md` será atualizado ao final (D16).

**D04 - Modelo físico.** O DER é mantido sem alteração lógica. Ajustes físicos para implementação:
- `NOTAS_PROVAS`: índice único (`prova_id`, `matricula_id`), para impedir duas notas da mesma prova para o mesmo aluno (PRD: integridade).
- `MATRICULAS`: índice único (`aluno_id`, `periodo_letivo_id`).
- `CLASSIFICACOES`: índice único (`processo_seletivo_id`, `matricula_id`).
- `BOLSAS`: índice único (`matricula_id`, `processo_seletivo_id`).
- Justificativa: não alteram o modelo conceitual; apenas impõem integridade que o DER não expressa.

**D05 - Ranking por ano escolar.** `CLASSIFICACOES` não tem `ano_escolar`. O ranking por ano é derivado pelo join com `MATRICULAS.ano_escolar`, sem alterar o DER.
- Justificativa: cumpre CA03 sem duplicar dado. O DER não é alterado.

## Escopo

**D06 - Escopo do MVP.** Entram somente os itens P0: cadastro de alunos (RF01), cadastro de provas (RF02), registro de notas (RF03), classificação (RF04), seleção de bolsistas (RF05), concessão da bolsa (RF06) e consulta de resultados (RF10, parcial, somente classificações e bolsas).
- Ficam fora: monitoramento de médias (RF08), controle de frequência (RF07), cancelamento e manutenção (RF09), relatórios e notificações (P2).
- Justificativa: a seleção e a concessão funcionam sem esses itens, então não são indispensáveis para um P0.
- Consequência: as tabelas `MEDIAS_DISCIPLINAS`, `FREQUENCIAS`, `HISTORICO_BOLSAS` e o fluxo de cancelamento não serão implementados nesta entrega, embora existam no DER. Para `BOLSAS`, a situação inicial é `ATIVA` e não há transição implementada.
- RN08 (sem reintegração no mesmo período) só tem efeito com cancelamento. Fica pendente para o módulo P1.

**D07 - Valor do benefício.** R$ 300,00 (RN03) é gravado em `BOLSAS.valor_mensal` a cada concessão. O valor padrão vem de uma propriedade de configuração do backend, não de regra espalhada no código.

## Premissas (pontos em aberto no PRD)

**P01 - Desempate (PRD: "regra de desempate", CA04).** Premissa: em nota final igual, vence o aluno de data de nascimento mais antiga; se persistir o empate, ordem alfabética do nome e, por último, menor id, para que o resultado seja determinístico. Os empatados recebem posições sequenciais, não ex-aequo.
- Status: PREMISSA, não regra oficial. Deve ser confirmada antes do uso real.

**P02 - Limite de faltas (PRD: RN05).** Não se aplica ao P0, pois frequência é P1. Pendente, sem implementação.

**P03 - Revisão de cancelamentos (PRD: RN07 e risco 6).** Não se aplica ao P0, pois cancelamento é P1. Pendente, sem implementação.

**P04 - Nota máxima e intervalo de nota.** Premissa: a nota fica entre 0 e `nota_maxima` da prova; a nota final da classificação é a nota da prova (uma prova por processo seletivo).

**P05 - Número de vagas.** Premissa: 10 bolsas por ano escolar, conforme RN01. Ano escolar válido: 1, 2 e 3 (ensino médio).

## Governança

**D15 - Gates humanos.** O orquestrador deveria parar para aprovação do usuário após o escopo, após a arquitetura e antes do merge. O usuário autorizou explicitamente que as decisões técnicas e de negócio fossem tomadas sem nova consulta. Por isso, escopo e arquitetura foram registrados aqui e seguiram sem parada. Nenhum commit ou merge foi feito, pois a restrição proíbe commit e publicação. Os itens P01 a P05 devem ser validados pelo usuário antes de uso real.

**D16 - CLAUDE.md.** Será atualizado ao final com a nova estrutura (`backend/`, `frontend/`) e com os comandos de build e teste.

**D17 - Documentos existentes.** PRD, MER, DER, `.docx` e `DER.png` não foram alterados.

**D18 - Contas de teste.** Os testes usam contas no padrão `test@IA_<finalidade>` (ex.: `test@IA_admin`, `test@IA_secretaria`, `test@IA_consulta`), criadas se não existirem. Nenhuma conta pessoal é usada.

## Ajustes ao DER para implementação (não editam o DER original)

Os ajustes abaixo são físicos ou de restrição. O DER original (`der_pe_de_meia.mmd`, `.puml`, `DER.png`) não foi alterado.

**P-DER-1. `BOLSAS` ganha `criada_por` (FK para USUARIOS).** Necessário para a rastreabilidade exigida pelo RNF06 do MVP. Aplicado no V1.
**P-DER-2. `NOTAS_PROVAS` ganha `alterada_por` e `alterada_em`.** Necessário para correções auditadas de nota (Q07 do analista). Aplicado no V1.
**P-DER-3. Valores de enum não definidos no DER.** Adotados: `MATRICULAS.status` = `ATIVA`; `PROCESSOS_SELETIVOS.status` = `CRIADO`, `EM_CLASSIFICACAO`, `ENCERRADO`; `BOLSAS.situacao` = `ATIVA`, `CANCELADA`. Implementados como VARCHAR com CHECK. Aplicado no V1.
**P-DER-4. `UNIQUE (prova_id)` em `PROCESSOS_SELETIVOS`.** Uma prova por processo seletivo (P04 e Q16). Aplicado no V1.
**P-DER-5. `ano_escolar` em `CLASSIFICACOES` NÃO aplicado.** Mantém-se D05 (ranking derivado via `MATRICULAS`). A garantia de unicidade de posição fica na transação com lock (ADR-004 do arquiteto).
**Desvios físicos.** Enum vira VARCHAR com CHECK; timestamps vira DATETIME(3) em UTC.

## Premissas adicionais

**P06. Três casos de erro sem regra no PRD**, marcados como PROPOSTA pelo analista (a confirmar pelo usuário):
- PROPOSTA-A: prova sem notas bloqueia a classificação.
- PROPOSTA-B: ano com menos de 10 alunos seleciona todos, sem repasse de vagas.
- PROPOSTA-C: duplicidade de aluno verificada pela matrícula escolar.

**P07. Perfis ADMIN, SECRETARIA e CONSULTA** (D02) são premissa a confirmar.
**P08. Desempate P01** implementado como ordenação determinística em Java (chave sem acento e caixa alta, id como última chave). Validar com a instituição (Q01).

Artefatos de referência: `docs/agentes/01-requisitos-mvp.md`, `02-arquitetura.md`, `03-modelo-fisico.md`, `04-contrato-api.yaml`, `05-estrutura-pastas.md`, `scripts/V1__esquema_mvp.sql`, `decisoes/ADR-001` a `ADR-007`.

## Ponto de parada (pausa solicitada pelo usuário)

Pausa pedida pelo usuário. Nenhuma nova etapa de delegação foi iniciada depois dela. As etapas já em andamento foram concluídas antes de parar.

**Concluído:**
- Requisitos P0 (analista-requisitos): `01-requisitos-mvp.md`, 31 critérios MVP-CA01 a MVP-CA31.
- Arquitetura (arquiteto): `02-arquitetura.md`, `03-modelo-fisico.md`, `04-contrato-api.yaml`, `05-estrutura-pastas.md`, `scripts/V1__esquema_mvp.sql`, `decisoes/ADR-001` a `ADR-007`.
- Registro das propostas P-DER-1 a P-DER-5 e das premissas P06 a P08 neste arquivo.
- Implementação de backend e frontend: iniciada em paralelo (dev-backend e dev-frontend) e concluída antes desta pausa. Resultado registrado na seção de status abaixo.

**Pendente (não iniciado):**
- QA (casos de teste e registro de defeitos) contra os MVP-CA.
- Correção de defeitos e nova rodada de QA, se necessário.
- Revisão (revisor) antes de dar a entrega por concluída.
- Atualização do `CLAUDE.md` com a nova estrutura e os comandos de build e teste (D16).
- Confirmação pelo usuário das propostas PROPOSTA-A, PROPOSTA-B, PROPOSTA-C e das premissas P01 a P08 (gate de escopo e de arquitetura, D15).
- Lint do contrato OpenAPI, que o arquiteto revisou só manualmente.

**Próximo passo, ao retomar:** acionar o qa com os MVP-CA de `01-requisitos-mvp.md`, usando o backend e o frontend que estiverem em `backend/` e `frontend/`. Depois, o revisor. Só então atualizar o CLAUDE.md e pedir o gate de merge (sem commit, conforme as restrições).

### Atualização do ponto de parada: frontend concluído

- Frontend concluído (dev-frontend): Angular 22.2.3, `npm test` com 76 testes e 0 falhas, `npm run build` sem avisos. Relatório em `07-frontend-relatorio.md`.
- Pendências do frontend: não houve verificação em navegador (Chromium do sistema não encontrado), e a integração real com a API não foi testada.
- Inconsistências do contrato `04-contrato-api.yaml` apontadas pelo dev-frontend, ainda não corrigidas:
  1. `ProcessoSeletivoResposta.status` só aceita `CRIADO`, mas P-DER-3 prevê também `EM_CLASSIFICACAO` e `ENCERRADO`.
  2. O perfil CONSULTA lista processos, mas não tem acesso a `/periodos-letivos` nem a `/provas`. Sugestão: incluir `periodoNome` e `provaTitulo` na resposta.
  3. Matrículas do período são paginadas (máximo 100) e sem busca. Sugestão: filtro por nome ou matrícula.
- Backend: ainda em andamento no momento desta atualização. O resultado será registrado quando o dev-backend terminar.
