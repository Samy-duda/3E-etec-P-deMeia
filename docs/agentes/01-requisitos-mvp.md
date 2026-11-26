# Requisitos e critérios de aceite do MVP (P0) - Pé-de-Meia

**Versão:** 1.0
**Data:** 2026-10-16
**Autor:** analista de requisitos
**Situação:** aguardando aprovação do escopo (gate humano, D15)

**Fontes:** `PRD_Pe_de_Meia.md` (seções 4, 5, 6, 7, 8 e 9.1), `mer_pe_de_meia.mmd`, `der_pe_de_meia.mmd`, `docs/agentes/decisoes.md` (D01 a D18 e P01 a P05).

**Convenções**
- Requisitos do MVP: `MVP-RFnn` (funcionais) e `MVP-RNFnn` (não funcionais).
- Critérios de aceite: `MVP-CAnn`. Cada um traz o rastreio para o PRD (RF, RN ou CA).
- Itens marcados como **PREMISSA** vêm de `decisoes.md` (P01 a P05) e devem ser validados pela instituição antes do uso real.
- Itens marcados como **PROPOSTA** foram definidos neste documento para cobrir um caso de erro pedido, sem regra explícita no PRD. Também devem ser confirmados (ver seção 8).
- Os exemplos de dados nos critérios são ilustrativos. Use as contas de teste no padrão `test@IA_<finalidade>` (D18).

---

## 1. Escopo do MVP

### 1.1 Dentro do escopo (P0)

| Funcionalidade | Rastreio PRD | Requisito MVP | Observação |
|---|---|---|---|
| Cadastro e consulta de alunos | RF01, CA01 | MVP-RF01, MVP-RF02 | P0 (9.1) |
| Período letivo e matrícula com ano escolar | RF01 (indispensável); CA03 | MVP-RF03 | Não há RF próprio no PRD. Entra porque a classificação por ano depende da matrícula (D05, D06). |
| Cadastro de provas | RF02, CA02 | MVP-RF04 | P0 (9.1) |
| Registro de notas | RF03, CA02 | MVP-RF05 | P0 (9.1) |
| Processo seletivo (passos iniciais do fluxo) | 7.1, passos 1 a 3 | MVP-RF06 | Liga prova e período; sem ele não há classificação. |
| Classificação por ano escolar | RF04, CA03 | MVP-RF07 | P0 (9.1) |
| Seleção dos 10 melhores por ano | RF05, CA04, RN01, RN02 | MVP-RF08 | P0 (9.1) |
| Concessão da bolsa de R$ 300,00 | RF06, CA05, RN03 | MVP-RF09 | P0 (9.1) |
| Consulta de classificações e bolsas | RF10 (parcial, D06) | MVP-RF10 | Somente classificações e situação de bolsas. |
| Controle de acesso por perfil | Segurança (seção 5), CA10 | MVP-RNF01 | Transversal a todo o MVP. |
| Integridade de notas, alunos e bolsas | Integridade (seção 5) | MVP-RNF03 | Transversal. |

### 1.2 Fora do escopo

| Item | Rastreio PRD | Motivo |
|---|---|---|
| Controle de frequência e justificativas | RF07, CA07, RN05, RN06 | P1 (9.1; D06) |
| Monitoramento de médias por disciplina | RF08, CA06, RN04 | P1 (9.1; D06) |
| Gestão do benefício, manutenção e cancelamento | RF09, CA08, RN07 | P1 (9.1; D06) |
| Reintegração no mesmo período letivo | RN08, CA09 | Só tem efeito com cancelamento; fica para P1 (D06). |
| Limite numérico de faltas | Pendência do PRD (seção 6); P02 | Depende de frequência (P1). |
| Revisão administrativa de cancelamentos | Pendência do PRD (seção 6); P03; risco 6 | Depende de cancelamento (P1). |
| Relatórios avançados, notificações, painel de indicadores | 9.1, P2 | P2 |
| Criação automática do conteúdo das provas | Seção 3 (fora do escopo do PRD) | Fora do PRD. |
| Concessão de valores diferentes de R$ 300,00 | Seção 3 (fora do escopo do PRD); RN03 | Ver MVP-CA25. |
| Controle financeiro completo | Seção 3 (fora do escopo do PRD) | Fora do PRD. |
| Ensino fundamental e superior | Seção 3 (fora do escopo do PRD) | Fora do PRD. |
| Pagamento efetivo das bolsas | Risco "falta de recursos" (9.2) | O MVP registra a concessão; não processa pagamento. |
| Acesso do próprio aluno ao sistema | Não definido no PRD | Ver Q11. |

---

## 2. Atores e perfis

| Ator | Papel no MVP | Perfil de acesso (D02, premissa) |
|---|---|---|
| Aluno | Pessoa cadastrada e classificada. No MVP não opera o sistema (Q11). | Nenhum |
| Administrador escolar | Gerencia cadastros, provas, notas, processo seletivo e bolsas. | `ADMIN` (acesso total) |
| Secretaria (administrador escolar operacional) | Mesmas funções do administrador no P0. | `SECRETARIA` |
| Equipe pedagógica e responsáveis pelo programa | Consulta classificações e bolsas. | `CONSULTA` (somente leitura, sem dados pessoais além do necessário) |

Observação: o PRD cita "administradores escolares" e não separa os perfis. A separação `ADMIN`, `SECRETARIA` e `CONSULTA` é proposta de D02 (ver Q10).

---

## 3. Termos do domínio

- **Aluno:** estudante do ensino médio cadastrado no sistema, identificado pela matrícula escolar.
- **Período letivo:** intervalo de tempo ao qual matrículas e provas são associadas.
- **Matrícula:** vínculo de um aluno com um período letivo, com um ano escolar (1, 2 ou 3) (P05).
- **Prova:** avaliação aplicada em um período letivo, com nota máxima definida.
- **Nota:** resultado de um aluno matriculado em uma prova.
- **Processo seletivo:** conjunto formado por um período letivo e uma prova, do qual saem a classificação e as bolsas.
- **Classificação:** ordenação dos alunos de um ano escolar, pela nota da prova, com posição atribuída.
- **Selecionado:** aluno que ocupa as 10 primeiras posições do seu ano escolar.
- **Bolsa:** benefício mensal de R$ 300,00 concedido a um selecionado dentro de um processo seletivo.

---

## 4. Requisitos funcionais do MVP

| ID | Requisito | Descrição | Rastreio | Perfis |
|---|---|---|---|---|
| MVP-RF01 | Cadastrar aluno | Registrar um aluno com nome, matrícula escolar (identificador único) e data de nascimento. A data é necessária para o desempate (P01). | PRD RF01, CA01 | ADMIN, SECRETARIA |
| MVP-RF02 | Consultar alunos | Listar e consultar alunos cadastrados, por matrícula escolar ou nome. | PRD RF01, CA01 | ADMIN, SECRETARIA. CONSULTA sem dados pessoais (Q03). |
| MVP-RF03 | Período letivo e matrícula | Cadastrar períodos letivos e matricular um aluno em um período, informando o ano escolar (1, 2 ou 3). Um aluno tem no máximo uma matrícula por período. | PRD RF01 (indispensável); CA03; D05; P05 | ADMIN, SECRETARIA |
| MVP-RF04 | Cadastrar prova | Registrar uma prova com título, data de aplicação, nota máxima (maior que zero) e período letivo. | PRD RF02, CA02 | ADMIN, SECRETARIA |
| MVP-RF05 | Registrar notas | Lançar a nota de um aluno matriculado no período da prova. A nota deve estar entre 0 e a nota máxima (P04). Há no máximo uma nota por aluno por prova. Correções seguem Q07. | PRD RF03, CA02; risco "erros de lançamento" | ADMIN, SECRETARIA |
| MVP-RF06 | Criar processo seletivo | Criar um processo seletivo ligando um período letivo e uma prova. | PRD 7.1 (passos 1 a 3), RF05 | ADMIN, SECRETARIA |
| MVP-RF07 | Gerar classificação | Gerar, para um processo seletivo, a classificação separada por ano escolar, com base na nota da prova (RN02). Aplica o desempate de P01 e atribui posições sequenciais (sem ex-aequo). Não gera classificação se não houver nenhuma nota (MVP-CA16). | PRD RF04, CA03; RN02; P01; P04 | ADMIN, SECRETARIA |
| MVP-RF08 | Selecionar os 10 melhores | Marcar como selecionados os 10 primeiros de cada ano escolar na classificação do processo (P05). Se o ano tiver menos de 10 alunos, todos são selecionados (PROPOSTA, Q05). | PRD RF05, CA04; RN01; P05 | ADMIN, SECRETARIA |
| MVP-RF09 | Conceder bolsa | Registrar, para cada aluno selecionado, uma bolsa vinculada ao processo, com valor mensal de R$ 300,00 e situação ATIVA. Um aluno recebe no máximo uma bolsa por processo. O gatilho segue Q04. | PRD RF06, CA05; RN03; D07 | ADMIN, SECRETARIA |
| MVP-RF10 | Consultar resultados | Consultar a classificação de um processo por ano escolar e a lista de bolsas concedidas com sua situação. | PRD RF10 (parcial, D06) | ADMIN, SECRETARIA, CONSULTA |

---

## 5. Requisitos não funcionais aplicáveis

| ID | Categoria | Requisito | Rastreio |
|---|---|---|---|
| MVP-RNF01 | Segurança (autenticação e perfis) | O sistema exige autenticação para qualquer operação sobre dados de alunos, notas, classificações ou bolsas. O acesso é controlado pelo perfil `ADMIN`, `SECRETARIA` ou `CONSULTA` (D02). Operação fora do perfil é negada e não altera dados. | PRD seção 5 (Segurança), CA10; D02 (premissa) |
| MVP-RNF02 | LGPD (mínima) | Coletar apenas os dados necessários ao processo seletivo: nome, matrícula escolar e data de nascimento. Contatos (e-mail, telefone) são opcionais (Q03). O acesso a dados pessoais é restrito aos perfis `ADMIN` e `SECRETARIA`. O perfil `CONSULTA` não vê dados pessoais além do definido em Q03. Atende à Lei nº 13.709/2018 no que se refere a finalidade e acesso restrito. | PRD seção 5 (LGPD); risco "acesso indevido" (9.2) |
| MVP-RNF03 | Integridade | O sistema impede registros inconsistentes: aluno com mesma matrícula escolar, nota fora do intervalo, mais de uma nota por aluno e prova, mais de uma matrícula por aluno e período, bolsa com valor diferente do configurado e mais de uma bolsa por aluno e processo. Uma operação que falha (por exemplo, geração de classificação ou concessão de bolsas) não deixa registros parciais. | PRD seção 5 (Integridade); riscos 1 e 2 (9.2) |
| MVP-RNF04 | Desempenho | Consultas e operações comuns respondem em até 3 segundos em condições normais de uso. Meta do PRD, ainda sujeita a validação (Q14). | PRD seção 5 (Desempenho) |
| MVP-RNF05 | Usabilidade e responsividade | As telas de cadastro de alunos, registro de notas, classificação e bolsas funcionam em computador e celular, sem rolagem horizontal em largura de 375 px. | PRD seção 5 (Usabilidade) |
| MVP-RNF06 | Rastreabilidade mínima | As notas e as concessões de bolsa registram quem as lançou ou concedeu e quando. Os campos correspondem a `lancada_por`/`lancada_em` e a `criada_por` do DER. | DER (NOTAS_PROVAS, BOLSAS); PRD risco 4 |
| MVP-RNF07 | Disponibilidade | Pendente. O PRD pede disponibilidade durante inscrição, seleção e acompanhamento, mas não define métrica (Q14). Não há critério de aceite neste documento. | PRD seção 5 (Disponibilidade) |

---

## 6. Regras de negócio aplicáveis ao MVP

| ID | Regra | Origem | Situação no MVP |
|---|---|---|---|
| RN01 | Serão selecionados os 10 melhores colocados de cada ano do ensino médio. | PRD | Aplicada (MVP-RF08) |
| RN02 | A seleção é baseada no resultado da prova com conteúdos já estudados. | PRD | Aplicada: a nota da prova é o único critério de classificação no MVP. |
| RN03 | Cada bolsista recebe R$ 300,00 mensais enquanto mantiver o benefício. | PRD | Aplicada na concessão (MVP-RF09). A manutenção (fim do benefício) está fora do MVP. |
| P01 | **PREMISSA - desempate.** Em nota final igual, vence a data de nascimento mais antiga. Persistindo o empate, vence a ordem alfabética do nome e, por último, o menor id. Os empatados recebem posições sequenciais, não ex-aequo. | decisoes.md, P01; PRD "regra de desempate" (CA04) | Aplicada (MVP-RF07). Validar antes do uso real (Q01). |
| P04 | **PREMISSA - intervalo de nota.** A nota fica entre 0 e a nota máxima da prova. A nota final da classificação é a nota da prova, pois há uma prova por processo seletivo. | decisoes.md, P04 | Aplicada (MVP-RF05, MVP-RF07). |
| P05 | **PREMISSA - vagas.** São 10 bolsas por ano escolar, conforme RN01. Os anos escolares válidos são 1, 2 e 3. | decisoes.md, P05 | Aplicada (MVP-RF03, MVP-RF08). |
| D07 | O valor da bolsa é R$ 300,00. Cada bolsa grava o valor no campo de valor mensal. O valor padrão vem de configuração, não de regra fixa no fluxo. | decisoes.md, D07 | Aplicada (MVP-RF09). Ver Q13. |
| PROPOSTA-A | Se o processo não tiver nenhuma nota, a classificação não é gerada. Nenhum aluno é selecionado e nenhuma bolsa é criada. | Proposta deste documento | Aplicada (MVP-CA16). Confirmar (Q06). |
| PROPOSTA-B | Se um ano tiver menos de 10 alunos classificados, todos são selecionados. A vaga restante não é repassada a alunos de outro ano. | Proposta deste documento (RN01 não cobre o caso) | Aplicada (MVP-CA23). Confirmar (Q05). |
| PROPOSTA-C | Aluno duplicado é aquele com a mesma matrícula escolar. | Proposta deste documento (DER: `matricula_escolar` é único) | Aplicada (MVP-CA03). Confirmar (Q02). |
| P02 | Limite de faltas. | decisoes.md, P02 | Fora do MVP (depende de P1). |
| P03 | Revisão de cancelamentos. | decisoes.md, P03 | Fora do MVP (depende de P1). |
| RN04 a RN08 | Média mínima, limite de faltas, justificativas, cancelamento e reintegração. | PRD | Fora do MVP (P1; D06). |

---

## 7. Critérios de aceite

Os critérios são testáveis por API (requisições de um perfil autenticado) ou por tela. A coluna de rastreio aponta para o PRD ou para o requisito MVP.

### 7.1 Cadastro de alunos e matrícula

**MVP-CA01 - Cadastro de aluno válido** (MVP-RF01; PRD CA01)
- **Dado** que a SECRETARIA está autenticada,
- **Quando** cadastra um aluno com nome "Ana Souza", matrícula escolar "2026-0001" e data de nascimento 15/03/2008,
- **Então** o aluno é criado com esses dados e aparece na consulta de alunos.

**MVP-CA02 - Consulta de aluno** (MVP-RF02; PRD CA01)
- **Dado** que o aluno "Ana Souza" (matrícula "2026-0001") está cadastrado,
- **Quando** a SECRETARIA consulta pela matrícula "2026-0001",
- **Então** o sistema retorna os dados de Ana Souza.

**MVP-CA03 - Aluno duplicado** (MVP-RF01; PROPOSTA-C)
- **Dado** que o aluno com matrícula escolar "2026-0001" está cadastrado,
- **Quando** a SECRETARIA tenta cadastrar outro aluno com a mesma matrícula escolar (nome "Ana S. Lima"),
- **Então** o sistema recusa o cadastro com mensagem de duplicidade, não cria registro e mantém os dados originais de Ana Souza sem alteração.

**MVP-CA04 - Dados obrigatórios ausentes** (MVP-RF01; P01)
- **Dado** que a SECRETARIA está autenticada,
- **Quando** cadastra um aluno sem data de nascimento,
- **Então** o sistema recusa, indica o campo ausente e nenhum aluno é criado.

**MVP-CA05 - Matrícula com ano escolar válido** (MVP-RF03; PRD CA03; P05)
- **Dado** que o período "2026-1" está cadastrado e o aluno "Ana Souza" existe,
- **Quando** a SECRETARIA matricula Ana Souza nesse período com ano escolar 2,
- **Então** a matrícula é criada com ano escolar 2 e período "2026-1".

**MVP-CA06 - Ano escolar inválido** (MVP-RF03; P05)
- **Dado** que o período "2026-1" está cadastrado e o aluno "Ana Souza" existe,
- **Quando** a SECRETARIA tenta matricular Ana Souza com ano escolar 4 (ou 0),
- **Então** o sistema recusa e nenhuma matrícula é criada.

**MVP-CA07 - Matrícula duplicada no período** (MVP-RF03)
- **Dado** que Ana Souza está matriculada no período "2026-1" com ano escolar 2,
- **Quando** a SECRETARIA tenta criar outra matrícula de Ana Souza no período "2026-1" (com qualquer ano escolar),
- **Então** o sistema recusa e mantém a matrícula original.

### 7.2 Provas e notas

**MVP-CA08 - Cadastro de prova válida** (MVP-RF04; PRD CA02)
- **Dado** que o período "2026-1" está cadastrado,
- **Quando** a SECRETARIA cadastra a prova "Simulado de Matemática", data de aplicação 20/11/2026 e nota máxima 10,00,
- **Então** a prova é criada e vinculada ao período "2026-1".

**MVP-CA09 - Nota máxima inválida** (MVP-RF04; P04)
- **Dado** que o período "2026-1" está cadastrado,
- **Quando** a SECRETARIA cadastra uma prova com nota máxima 0, com nota máxima -5 ou sem nota máxima,
- **Então** o sistema recusa cada tentativa e nenhuma prova é criada.

**MVP-CA10 - Acesso sem perfil adequado** (PRD CA10; MVP-RNF01, MVP-RNF02)
- **Cenário a.** Dado que o usuário `test@IA_consulta` (perfil `CONSULTA`) está autenticado e existe a prova "Simulado de Matemática" com notas,
  **Quando** tenta cadastrar um aluno ou registrar uma nota,
  **Então** o sistema nega a operação e nenhum dado é alterado.
- **Cenário b.** Dado que uma requisição é feita sem autenticação,
  **Quando** tenta consultar alunos, notas ou classificações,
  **Então** o sistema nega o acesso e não retorna nenhum dado pessoal.
- **Cenário c.** Dado que o usuário `test@IA_consulta` está autenticado,
  **Quando** consulta a classificação de um processo,
  **Então** a resposta contém somente os dados definidos em Q03 (sem data de nascimento).

**MVP-CA11 - Registro de nota válida** (MVP-RF05; PRD CA02)
- **Dado** que Ana Souza está matriculada no período "2026-1" e a prova "Simulado de Matemática" tem nota máxima 10,00,
- **Quando** a SECRETARIA registra a nota 7,5 para Ana Souza nessa prova,
- **Então** a nota fica registrada e a consulta de resultados retorna 7,5 para Ana Souza.

**MVP-CA12 - Nota fora do intervalo** (MVP-RF05; P04; PRD CA02)
- **Dado** que a prova "Simulado de Matemática" tem nota máxima 10,00 e Ana Souza está matriculada no período,
- **Quando** a SECRETARIA registra a nota -0,5 ou a nota 10,5,
- **Então** o sistema recusa os dois lançamentos e nenhuma nota é registrada.
- **Complemento:** as notas 0 e 10,00 são aceitas.

**MVP-CA13 - Nota duplicada na mesma prova** (MVP-RF05; RNF03)
- **Dado** que Ana Souza já tem a nota 7,5 na prova "Simulado de Matemática",
- **Quando** a SECRETARIA tenta registrar uma segunda nota (8,0) para ela nessa prova,
- **Então** o sistema recusa o novo lançamento e mantém a nota 7,5. A correção de nota segue Q07.

**MVP-CA14 - Nota de aluno não matriculado no período** (MVP-RF05)
- **Dado** que Pedro Alves está matriculado no período "2025-2" e não no período "2026-1",
- **Quando** a SECRETARIA registra uma nota para Pedro Alves na prova de "2026-1",
- **Então** o sistema recusa o lançamento e nenhuma nota é registrada.

### 7.3 Processo seletivo, classificação e seleção

**MVP-CA15 - Criação de processo seletivo** (MVP-RF06; PRD 7.1)
- **Dado** que a prova "Simulado de Matemática" existe no período "2026-1",
- **Quando** a SECRETARIA cria um processo seletivo com essa prova e esse período,
- **Então** o processo é criado vinculado à prova e ao período, e a classificação só pode ser gerada para esse processo.

**MVP-CA16 - Prova sem notas: classificação bloqueada** (MVP-RF07; PROPOSTA-A)
- **Dado** que existe um processo seletivo cuja prova não tem nenhuma nota registrada,
- **Quando** a SECRETARIA solicita a geração da classificação,
- **Então** o sistema recusa a operação e informa que não há notas. Nenhuma classificação é criada, nenhum aluno é selecionado e nenhuma bolsa é criada.

**MVP-CA17 - Classificação separada por ano escolar** (MVP-RF07; PRD CA03)
- **Dado** que o processo tem notas de 6 alunos do ano 1, 6 do ano 2 e 6 do ano 3,
- **Quando** a classificação é gerada,
- **Então** existem três ordenações independentes, cada uma com posições de 1 a 6. A posição de um aluno não considera alunos de outro ano.

**MVP-CA18 - Posições sequenciais e nota final** (MVP-RF07; P04; PRD CA04)
- **Dado** que, no ano 2, Ana Souza tem nota 9,0 (nascida em 15/03/2008), Bruno Lima tem nota 9,0 (nascido em 10/05/2008) e Carla Mendes tem nota 7,0,
- **Quando** a classificação é gerada,
- **Então** Ana Souza fica em 1º, Bruno Lima em 2º e Carla Mendes em 3º. As posições são sequenciais (não há duas posições iguais), e a nota final de cada aluno é a nota da prova.

**MVP-CA19 - Desempate por data de nascimento** (MVP-RF07; P01; PRD CA04)
- **Dado** que, no ano 2, Ana Souza e Bruno Lima têm a mesma nota 9,0, Ana nasceu em 15/03/2008 e Bruno nasceu em 10/05/2007,
- **Quando** a classificação é gerada,
- **Então** Bruno Lima fica em 1º (data de nascimento mais antiga) e Ana Souza em 2º.

**MVP-CA20 - Desempate por nome e por id** (MVP-RF07; P01)
- **Dado** que, no ano 1, Carla Mendes e Diego Rocha têm a mesma nota 8,0 e a mesma data de nascimento (01/01/2008),
- **Quando** a classificação é gerada,
- **Então** Carla Mendes fica em 1º e Diego Rocha em 2º (ordem alfabética do nome).
- **Complemento:** se também o nome for igual, vence o menor id. O resultado é o mesmo em duas gerações seguidas.

**MVP-CA21 - Seleção dos 10 melhores por ano** (MVP-RF08; PRD CA04; RN01; P05)
- **Dado** que o ano 2 tem 12 alunos classificados com notas distintas,
- **Quando** a seleção do processo é executada,
- **Então** exatamente os 10 primeiros colocados do ano 2 são marcados como selecionados, e os colocados em 11º e 12º não são.

**MVP-CA22 - Empate na fronteira do top 10** (MVP-RF08; PRD CA04; P01)
- **Dado** que o ano 1 tem 11 alunos e os colocados em 10º e 11º têm a mesma nota 7,0. Ana Souza (nascida em 15/03/2008) e Bruno Lima (nascido em 10/05/2008) ocupam essas posições,
- **Quando** a seleção do processo é executada,
- **Então** Ana Souza (10º) é selecionada e Bruno Lima (11º) não é. Exatamente 10 alunos do ano 1 são selecionados, e as posições 10 e 11 são distintas.

**MVP-CA23 - Menos de 10 alunos no ano** (MVP-RF08; P05; PROPOSTA-B)
- **Dado** que o ano 3 tem 7 alunos classificados com notas,
- **Quando** a seleção do processo é executada,
- **Então** os 7 alunos do ano 3 são selecionados, sem erro bloqueante. Nenhuma vaga é preenchida por aluno de outro ano. O aviso sobre vagas não preenchidas depende de Q05.

### 7.4 Concessão e consulta de bolsas

**MVP-CA24 - Concessão de bolsa de R$ 300,00** (MVP-RF09; PRD CA05; RN03; D07)
- **Dado** que o processo tem 10 selecionados no ano 1 e 10 no ano 2,
- **Quando** a SECRETARIA registra a concessão das bolsas do processo (gatilho sujeito a Q04),
- **Então** cada um dos 20 selecionados tem uma bolsa com situação ATIVA, vinculada ao processo e com valor mensal de R$ 300,00. Nenhum aluno não selecionado recebe bolsa.

**MVP-CA25 - Valor de bolsa diferente de R$ 300,00** (MVP-RF09; RN03; D07; MVP-RNF03)
- **Dado** que a SECRETARIA está autenticada e há um selecionado sem bolsa no processo,
- **Quando** a SECRETARIA tenta conceder a bolsa informando o valor mensal de R$ 350,00 (ou qualquer valor diferente de R$ 300,00),
- **Então** o sistema recusa a concessão e nenhuma bolsa é criada com esse valor.

**MVP-CA26 - Aluno não selecionado não recebe bolsa** (MVP-RF09; RN01)
- **Dado** que Diego Rocha está classificado em 11º no ano 1 e não foi selecionado,
- **Quando** a SECRETARIA tenta conceder bolsa a Diego Rocha nesse processo,
- **Então** o sistema recusa e nenhuma bolsa é criada para ele.

**MVP-CA27 - Bolsa não duplicada no processo** (MVP-RF09; MVP-RNF03; D04)
- **Dado** que Ana Souza já tem uma bolsa ATIVA no processo,
- **Quando** a SECRETARIA tenta conceder outra bolsa a Ana Souza no mesmo processo,
- **Então** o sistema recusa e mantém somente a bolsa existente.

**MVP-CA28 - Consulta de classificação por ano** (MVP-RF10; RF04; PRD RF10)
- **Dado** que o processo tem classificação gerada,
- **Quando** o usuário consulta a classificação do ano 2,
- **Então** o sistema lista os alunos do ano 2 em ordem de posição, mostrando posição, nota final e situação (selecionado ou não). Os dados pessoais exibidos seguem Q03.

**MVP-CA29 - Consulta de bolsas** (MVP-RF10; PRD RF10; RN03)
- **Dado** que o processo tem bolsas concedidas,
- **Quando** o usuário consulta as bolsas do processo,
- **Então** o sistema lista cada bolsa com o aluno, o valor mensal de R$ 300,00 e a situação ATIVA.

**MVP-CA30 - Cancelamento indisponível no MVP** (Escopo; D06; RF09)
- **Dado** que existe uma bolsa com situação ATIVA,
- **Quando** a SECRETARIA tenta cancelar a bolsa ou alterar sua situação,
- **Então** a operação não é oferecida na tela e não é aceita pela API. A bolsa permanece ATIVA e nenhum histórico de cancelamento é criado.

### 7.5 Responsividade

**MVP-CA31 - Telas utilizáveis em celular** (MVP-RNF05; PRD usabilidade)
- **Dado** que a SECRETARIA está autenticada,
- **Quando** acessa as telas de cadastro de alunos, registro de notas, classificação e bolsas em uma largura de 375 px e, depois, em 1280 px,
- **Então** todos os campos, botões e colunas essenciais estão visíveis e utilizáveis, sem rolagem horizontal.

---

## 8. Dúvidas em aberto (não bloqueiam a implementação)

Cada dúvida tem um padrão de trabalho adotado enquanto não for respondida. A implementação pode seguir com esse padrão, que deve ser revisto quando a resposta chegar.

| ID | Dúvida | Padrão adotado até a resposta |
|---|---|---|
| Q01 | Confirmar a premissa P01 (desempate: data de nascimento, nome, id). | P01 como está. |
| Q02 | A matrícula escolar é o único identificador de duplicidade? Nomes iguais são permitidos? | Duplicidade só pela matrícula escolar (PROPOSTA-C). |
| Q03 | Quais dados pessoais são obrigatórios ou opcionais (e-mail, telefone)? O que o perfil `CONSULTA` pode ver? | Contatos opcionais. `CONSULTA` vê nome e posição/situação, sem data de nascimento, e-mail ou telefone. |
| Q04 | A concessão de bolsas é uma ação separada da SECRETARIA ou acontece automaticamente ao executar a seleção? | Ação separada da SECRETARIA. |
| Q05 | Quando um ano tem menos de 10 alunos, o sistema deve avisar sobre as vagas não preenchidas? As vagas não preenchidas são repassadas? | Seleciona todos os alunos, sem repasse de vagas, sem aviso bloqueante (PROPOSTA-B). |
| Q06 | Confirmar que a classificação de prova sem notas deve ser bloqueada. | Bloqueada (PROPOSTA-A). |
| Q07 | A nota pode ser corrigida? Pode ser corrigida depois de gerada a classificação ou a concessão de bolsas? Como registrar a correção? | Correção permitida só antes da geração da classificação. Registra quem e quando (MVP-RNF06). |
| Q08 | Qual é a precisão das notas (casas decimais)? A escala é sempre de 0 até a nota máxima? | Duas casas decimais, de 0 até a nota máxima. |
| Q09 | A turma é obrigatória na matrícula, ou o campo `turma` do DER é opcional? | Opcional. Não é usada nas regras do MVP. |
| Q10 | Quem cria as contas de usuário? Além de cadastrar, `ADMIN` tem funções a mais que `SECRETARIA` no MVP? | Contas criadas pelo `ADMIN`. No MVP, `ADMIN` e `SECRETARIA` têm as mesmas funções do P0. |
| Q11 | O aluno acessa o sistema no MVP (por exemplo, para ver a própria classificação)? | Não. O aluno apenas é cadastrado. |
| Q12 | Quais são os estados do processo seletivo? A DER tem um campo de status, mas os valores não estão definidos. | Não há estados formais além de "criado". |
| Q13 | Se o valor padrão de R$ 300,00 mudar na configuração, as bolsas já concedidas mantêm o valor antigo? | Cada bolsa grava o valor vigente no momento da concessão. |
| Q14 | Quais são as metas de desempenho (volume de referência) e de disponibilidade (percentual)? | Meta de 3 segundos sobre uma base de referência a definir. Sem SLA formal. |
| Q15 | Os registros de acesso (logs de quem consultou dados) entram no MVP ou ficam para P1? | Ficam fora do MVP. Só vale a rastreabilidade de MVP-RNF06. |
| Q16 | Pode haver mais de um processo seletivo por período e por ano? Um aluno pode receber bolsa em mais de um processo? | Um processo por prova. Uma bolsa por aluno por processo (D04). |
| Q17 | Qual é a vigência da bolsa (data de início e de fim)? | Início na data da concessão. Fim em branco. |
| Q18 | A busca de alunos é por nome parcial ou completo? | Busca por matrícula escolar e nome completo. |
| Q19 | Como tratar a exclusão ou retenção de dados pessoais (LGPD)? Há prazo de retenção? | Fora do MVP. Decisão da instituição. |

---

## 9. Próximos passos

1. Aprovação do escopo deste documento pelo usuário (gate D15).
2. Após a aprovação, o arquiteto define o modelo físico, o contrato da API e a estrutura de pastas com base nos requisitos e critérios acima.
3. As dúvidas Q01 a Q19 não bloqueiam o arquiteto. Devem ser respondidas antes do uso real do sistema.
