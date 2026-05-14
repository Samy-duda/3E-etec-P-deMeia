# P-demeia
Integrantes do grupo:
Sâmylla  Eduarda da Silva Magalhães
Emanuelly Vitoria Pereira da Costa
Thomaz Thiago de Oliveira Melo

É inegável que, em muitos momentos, o ato de estudar pode se tornar cansativo e até desmotivador, o que acaba refletindo na falta de interesse em frequentar a escola. Pensando nisso, nosso grupo teve a ideia de criar  um processo seletivo interno, com o objetivo de incentivar o desempenho acadêmico dos alunos.
A ideia consiste em selecionar os 10 melhores colocados de cada ano do ensino médio, com base em uma prova que abrangerá os conteúdos já estudados até o momento. Como forma de reconhecimento e estímulo, os alunos aprovados receberiam uma bolsa mensal no valor de R$ 300,00.
Entretanto, para a manutenção desse benefício, seria necessário que os estudantes  cumprissem alguns critérios, como não apresentar faltas injustificadas e manter um bom desempenho escolar, sem ficar abaixo da média exigida. Caso essas condições não sejam atendidas, o aluno perderá o direito à bolsa.
Essa proposta tem como principal finalidade motivar os estudantes a se dedicarem mais aos estudos, valorizando o esforço, a disciplina e o compromisso com a aprendizagem




/projeto-bolsa-escolar
│
├── database/
│   ├── schema.sql
│   └── seed.sql
│
├── docs/
│   ├── regras-negocio.md
│   └── diagrama.png
│
├── README.md



                 ┌───────────────────────────────┐
                 │ Início do Processo Seletivo   │
                 └───────────────┬───────────────┘
                                 │
                 ┌───────────────▼───────────────┐
                 │ ALUNOS DO ENSINO MÉDIO        │
                 └───────────────┬───────────────┘
                                 │
                 ┌───────────────▼───────────────┐
                 │ Aplicar PROVA com conteúdos   │
                 │ já estudados                  │
                 └───────────────┬───────────────┘
                                 │
                 ┌───────────────▼───────────────┐
                 │ Registrar RESULTADOS          │
                 │ (nota por aluno)              │
                 └───────────────┬───────────────┘
                                 │
                 ┌───────────────▼───────────────┐
                 │ Gerar CLASSIFICAÇÃO por ano   │
                 └───────────────┬───────────────┘
                                 │
                       ┌─────────▼─────────┐
                       │ Aluno está entre  │
                       │ os 10 melhores?   │
                       └───────┬───────┬───┘
                               │       │
                             Não      Sim
                               │       │
                               │   ┌───▼──────────────────────┐
                               │   │ Conceder BOLSA           │
                               │   │ (R$ 300,00 / mês)        │
                               │   └───────────┬──────────────┘
                               │               │
                               │   ┌───────────▼──────────────┐
                               │   │ Registrar BOLSA no       │
                               │   │ sistema (status: ATIVA)  │
                               │   └───────────┬──────────────┘
                               │               │
                               │   ┌───────────▼──────────────┐
                               │   │ Iniciar AVALIAÇÃO        │
                               │   │ periódica                │
                               │   └───────────┬──────────────┘
                               │               │
                               │   ┌───────────▼──────────────┐
                               │   │ Coletar dados:           │
                               │   │ - média escolar          │
                               │   │ - faltas injustificadas  │
                               │   └───────────┬──────────────┘
                               │               │
                               │     ┌─────────▼─────────┐
                               │     │ Média >= exigida? │
                               │     └───────┬───────┬───┘
                               │             │       │
                               │           Não      Sim
                               │             │       │
                               │   ┌─────────▼───┐   │
                               │   │ Atualizar   │   │
                               │   │ BOLSA       │   │
                               │   │ CANCELADA   │   │
                               │   └───────┬─────┘   │
                               │           │         │
                               │           │   ┌─────▼────────────────────┐
                               │           │   │ Faltas dentro do limite? │
                               │           │   └───────┬─────────┬────────┘
                               │           │           │         │
                               │           │         Não        Sim
                               │           │           │         │
                               │           │   ┌───────▼────┐    │
                               │           │   │ CANCELAR    │    │
                               │           │   │ BOLSA       │    │
                               │           │   └───────┬────┘    │
                               │           │           │         │
                               │           │     ┌─────▼──────────────┐
                               │           │     │ Manter BOLSA ATIVA │
                               │           │     └─────────┬──────────┘
                               │           │               │
                               │           └───────────────┘
                               │                   │
                               │          (volta para avaliação)
                               │
                               ▼
                 ┌───────────────────────────────┐
                 │       Fim do benefício        │
                 └───────────────────────────────┘



                 Regras:

                 1. Desempenho Acadêmico (Média Mínima)
Nota de Corte: O bolsista deve manter um desempenho igual ou superior a 6,0 (seis) em todas as disciplinas.

Penalidade: Caso o aluno fique abaixo de 6,0 em qualquer matéria, ele será automaticamente retirado do programa e perderá o direito ao recebimento da bolsa.

2. Assiduidade e Frequência Escolar
Limite de Faltas: O aluno não pode ultrapassar o número de faltas permitido pela instituição (ou o limite estabelecido especificamente para o projeto).

Justificativas: As faltas só serão desconsideradas para fins de manutenção da bolsa mediante a apresentação de justificativa formal (atestado médico ou documentos legais comprobatórios).

Penalidade: Faltas injustificadas que excedam o limite estipulado causarão a perda do benefício.

3. Critérios de Reintegração
Uma vez perdida a bolsa por desempenho ou falta, o aluno não poderá reavê-la no mesmo período letivo, reforçando o caráter de compromisso e disciplina do projeto.
