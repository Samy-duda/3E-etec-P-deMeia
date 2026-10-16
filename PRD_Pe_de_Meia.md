# PRD — Projeto Pé-de-Meia

**Documento de Requisitos do Produto**  
**Versão:** 1.0

## 1. Visão geral e objetivo

O **Pé-de-Meia** é um sistema de incentivo ao desempenho acadêmico que busca aumentar a motivação, a dedicação e a frequência dos estudantes do ensino médio por meio de um processo seletivo interno e da concessão de bolsas mensais.

**Objetivo:** desenvolver um sistema para registrar alunos, gerenciar provas, classificar os resultados, conceder bolsas e acompanhar os critérios necessários para a manutenção do benefício.

## 2. Público-alvo

- **Alunos do ensino médio:** participam do processo seletivo e concorrem às bolsas.
- **Administradores escolares:** cadastram estudantes, provas, notas e resultados.
- **Equipe pedagógica:** acompanha médias, frequência e cumprimento das regras.
- **Responsáveis pelo programa:** consultam os bolsistas e gerenciam a situação dos benefícios.

## 3. Escopo

| Dentro do escopo | Fora do escopo |
|---|---|
| Cadastro de alunos | Ensino fundamental e superior |
| Registro de provas e notas | Criação automática do conteúdo das provas |
| Classificação por ano escolar | Seleção sem critérios definidos |
| Concessão de bolsas de R$ 300,00 | Concessão de valores diferentes sem autorização |
| Monitoramento de médias e faltas | Controle financeiro completo da escola |
| Manutenção e cancelamento de bolsas | Reintegração no mesmo período letivo |

## 4. Requisitos funcionais

| ID | Requisito | Descrição |
|---|---|---|
| RF01 | Cadastro de alunos | Permitir cadastrar e consultar os estudantes do ensino médio. |
| RF02 | Cadastro de provas | Permitir registrar as provas utilizadas no processo seletivo. |
| RF03 | Registro de notas | Registrar e consultar as notas obtidas pelos alunos. |
| RF04 | Classificação | Classificar os alunos por ano escolar, considerando as notas da prova. |
| RF05 | Seleção de bolsistas | Selecionar os 10 melhores colocados de cada ano do ensino médio. |
| RF06 | Concessão da bolsa | Registrar a concessão do benefício mensal de R$ 300,00. |
| RF07 | Controle de frequência | Registrar e consultar faltas justificadas e injustificadas. |
| RF08 | Monitoramento de médias | Verificar se o bolsista mantém média igual ou superior a 6,0 em todas as disciplinas. |
| RF09 | Gestão do benefício | Manter ou cancelar a bolsa conforme as regras estabelecidas. |
| RF10 | Consulta de resultados | Permitir consultar classificações e situações das bolsas. |

## 5. Requisitos não funcionais

| Categoria | Requisito |
|---|---|
| Desempenho | As consultas e operações comuns devem responder em até 3 segundos em condições normais de uso. |
| Segurança | O sistema deve exigir autenticação e controlar o acesso conforme o perfil do usuário. |
| Usabilidade | A interface deve ser simples, intuitiva e adaptada a computadores e celulares. |
| LGPD | Os dados pessoais dos estudantes devem ser coletados apenas para finalidades legítimas, com acesso restrito e medidas de proteção adequadas à Lei nº 13.709/2018. |
| Disponibilidade | O sistema deve estar disponível durante os períodos de inscrição, seleção e acompanhamento. |
| Integridade | O sistema deve impedir registros inconsistentes de notas, frequência e situação das bolsas. |

*Observação: os prazos de resposta e a disponibilidade são metas propostas para o projeto, ainda sujeitas à validação.*

## 6. Regras de negócio

| ID | Regra |
|---|---|
| RN01 | Serão selecionados os 10 melhores colocados de cada ano do ensino médio. |
| RN02 | A seleção será baseada no resultado da prova com conteúdos já estudados. |
| RN03 | Cada bolsista receberá R$ 300,00 mensais enquanto mantiver o benefício. |
| RN04 | O aluno deverá manter média igual ou superior a 6,0 em todas as disciplinas. |
| RN05 | O aluno deverá respeitar o limite de faltas estabelecido pela instituição ou pelo projeto. |
| RN06 | Faltas somente serão desconsideradas mediante justificativa formal aceita pela instituição. |
| RN07 | O descumprimento dos critérios de desempenho ou frequência poderá resultar no cancelamento da bolsa, conforme as regras do programa. |
| RN08 | O aluno que perder a bolsa não poderá recuperá-la no mesmo período letivo. |

**Pontos a definir antes da implementação:**
- Regra de desempate na classificação.
- Limite numérico de faltas permitido.
- Procedimento para análise e revisão de cancelamentos.

## 7. Fluxos principais

### 7.1 Seleção de bolsistas

1. Iniciar o processo seletivo.
2. Cadastrar os alunos participantes.
3. Aplicar a prova com os conteúdos já estudados.
4. Registrar as notas.
5. Gerar a classificação separada por ano escolar.
6. Identificar os 10 melhores colocados de cada ano.
7. Registrar a bolsa de R$ 300,00 mensais para os selecionados.
8. Encaminhar os bolsistas para o acompanhamento periódico.

**Resultado:** os alunos selecionados passam a ter o benefício registrado como ativo.

### 7.2 Manutenção da bolsa

1. Consultar a situação do bolsista.
2. Verificar a média de todas as disciplinas.
3. Verificar as faltas e as justificativas registradas.
4. Se os critérios forem atendidos, manter a bolsa ativa.
5. Se algum critério não for atendido, registrar o motivo e cancelar o benefício conforme as regras do programa.
6. Impedir a reintegração no mesmo período letivo após o cancelamento.

## 8. Critérios de aceitação

| ID | Critério de aceitação |
|---|---|
| CA01 | O sistema permite cadastrar e consultar estudantes do ensino médio. |
| CA02 | O sistema registra as notas das provas corretamente. |
| CA03 | A classificação é gerada separadamente para cada ano escolar. |
| CA04 | Os 10 melhores colocados de cada ano são identificados corretamente, respeitando a regra de desempate que vier a ser definida. |
| CA05 | A bolsa é registrada no valor de R$ 300,00 mensais para os alunos selecionados. |
| CA06 | O sistema identifica médias inferiores a 6,0 em qualquer disciplina. |
| CA07 | O sistema permite registrar faltas e suas justificativas. |
| CA08 | O sistema atualiza a situação da bolsa quando os critérios de manutenção não são atendidos. |
| CA09 | O sistema impede a reintegração do aluno no programa durante o mesmo período letivo após o cancelamento. |
| CA10 | Usuários sem autorização não conseguem acessar dados restritos dos estudantes. |

## 9. Priorização (MVP) e riscos

### 9.1 Priorização do MVP

O MVP (*Produto Mínimo Viável*) corresponde à primeira versão funcional do sistema.

| Prioridade | Funcionalidades |
|---|---|
| **P0 — Essencial** | Cadastro de alunos, registro de provas e notas, classificação, seleção dos bolsistas e cadastro do benefício. |
| **P1 — Importante** | Monitoramento das médias, controle de frequência, registro de justificativas e cancelamento de bolsas. |
| **P2 — Evolução** | Relatórios avançados, notificações automáticas e painel de indicadores de desempenho. |

### 9.2 Principais riscos

| Risco | Impacto | Mitigação |
|---|---|---|
| Erros no lançamento de notas | Classificação incorreta | Validar dados e permitir correções autorizadas. |
| Empate entre estudantes | Dúvidas na seleção | Definir previamente uma regra de desempate. |
| Critérios de frequência indefinidos | Cancelamentos inconsistentes | Estabelecer um limite formal de faltas. |
| Acesso indevido a dados | Violação de privacidade | Implementar autenticação, permissões e registros de acesso. |
| Falta de recursos para as bolsas | Interrupção dos pagamentos | Planejar e confirmar o orçamento antes de iniciar o programa. |
| Cancelamentos indevidos | Prejuízo ao estudante | Registrar os motivos, validar os dados e permitir revisão administrativa. |

## Resultado esperado

O sistema **Pé-de-Meia** deverá tornar o processo seletivo mais organizado, transparente e confiável, incentivando a dedicação aos estudos e permitindo acompanhar os estudantes contemplados com a bolsa.

## Observações

Este PRD foi elaborado com base no arquivo de referência do projeto. O nome utilizado é **Pé-de-Meia**, conforme solicitado. Os requisitos técnicos e as prioridades são propostas para orientar o desenvolvimento e devem ser validados pela equipe responsável antes da implementação.
