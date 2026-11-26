-- =====================================================================
-- V1__esquema_mvp.sql
-- Projeto Pé-de-Meia - esquema do MVP (P0). MySQL 8.0.16 ou superior
-- (CHECK constraints são aplicadas a partir da 8.0.16).
--
-- Fonte: der_pe_de_meia.mmd, com os ajustes físicos da D04 e as
-- propostas P-DER-1 a P-DER-4 descritas em docs/agentes/03-modelo-fisico.md.
--
-- Destino: copiar para backend/src/main/resources/db/migration/
--          V1__esquema_mvp.sql (o nome e o conteúdo não devem ser alterados
--          depois de aplicados; mudanças exigem V2, V3, ...).
--
-- Não cria as tabelas P1 (DISCIPLINAS, MEDIAS_DISCIPLINAS, FREQUENCIAS,
-- HISTORICO_BOLSAS), conforme D06.
-- =====================================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- USUARIOS (contas de acesso; perfis conforme D02)
-- ---------------------------------------------------------------------
CREATE TABLE USUARIOS (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nome        VARCHAR(120) NOT NULL,
  email       VARCHAR(255) NOT NULL,
  senha_hash  VARCHAR(100) NOT NULL,
  perfil      VARCHAR(20)  NOT NULL,
  ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
  criado_em   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  CONSTRAINT uk_usuarios_email UNIQUE (email),
  CONSTRAINT ck_usuarios_perfil CHECK (perfil IN ('ADMIN', 'SECRETARIA', 'CONSULTA'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- ALUNOS (LGPD mínima: nome, matrícula escolar e data de nascimento
-- obrigatórios; contatos opcionais, Q03)
-- ---------------------------------------------------------------------
CREATE TABLE ALUNOS (
  id                 BIGINT       NOT NULL AUTO_INCREMENT,
  nome               VARCHAR(120) NOT NULL,
  matricula_escolar  VARCHAR(30)  NOT NULL,
  data_nascimento    DATE         NOT NULL,
  email              VARCHAR(255) NULL,
  telefone           VARCHAR(20)  NULL,
  ativo              BOOLEAN      NOT NULL DEFAULT TRUE,
  criado_em          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  CONSTRAINT uk_alunos_matricula_escolar UNIQUE (matricula_escolar),
  KEY ix_alunos_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- PERIODOS_LETIVOS
-- ---------------------------------------------------------------------
CREATE TABLE PERIODOS_LETIVOS (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  nome         VARCHAR(60) NOT NULL,
  data_inicio  DATE        NOT NULL,
  data_fim     DATE        NOT NULL,
  ativo        BOOLEAN     NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  CONSTRAINT ck_periodos_datas CHECK (data_fim >= data_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- MATRICULAS (aluno x período, com ano escolar 1, 2 ou 3; P05)
-- Índice único da D04: (aluno_id, periodo_letivo_id)
-- status: valores não definidos no DER (P-DER-3). MVP usa somente 'ATIVA'.
-- turma: opcional (Q09), não usada nas regras.
-- ---------------------------------------------------------------------
CREATE TABLE MATRICULAS (
  id                 BIGINT      NOT NULL AUTO_INCREMENT,
  aluno_id           BIGINT      NOT NULL,
  periodo_letivo_id  BIGINT      NOT NULL,
  ano_escolar        TINYINT     NOT NULL,
  turma              VARCHAR(20) NULL,
  status             VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
  PRIMARY KEY (id),
  CONSTRAINT uk_matriculas_aluno_periodo UNIQUE (aluno_id, periodo_letivo_id),
  CONSTRAINT ck_matriculas_ano_escolar CHECK (ano_escolar BETWEEN 1 AND 3),
  CONSTRAINT ck_matriculas_status CHECK (status IN ('ATIVA')),
  KEY ix_matriculas_periodo (periodo_letivo_id),
  CONSTRAINT fk_matriculas_aluno FOREIGN KEY (aluno_id) REFERENCES ALUNOS (id),
  CONSTRAINT fk_matriculas_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES PERIODOS_LETIVOS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- PROVAS
-- nota_maxima > 0 (MVP-CA09). A relação com o período da prova é FK.
-- ---------------------------------------------------------------------
CREATE TABLE PROVAS (
  id                 BIGINT       NOT NULL AUTO_INCREMENT,
  periodo_letivo_id  BIGINT       NOT NULL,
  titulo             VARCHAR(150) NOT NULL,
  descricao          TEXT         NULL,
  data_aplicacao     DATE         NOT NULL,
  nota_maxima        DECIMAL(5,2) NOT NULL,
  criada_por         BIGINT       NULL,
  criada_em          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  CONSTRAINT ck_provas_nota_maxima CHECK (nota_maxima > 0),
  KEY ix_provas_periodo (periodo_letivo_id),
  CONSTRAINT fk_provas_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES PERIODOS_LETIVOS (id),
  CONSTRAINT fk_provas_criada_por FOREIGN KEY (criada_por) REFERENCES USUARIOS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- NOTAS_PROVAS
-- Índice único da D04: (prova_id, matricula_id).
-- nota >= 0 aqui; nota <= nota_maxima da prova é validado na aplicação (P04).
-- alterada_por / alterada_em: P-DER-2 (correção de nota, Q07).
-- ---------------------------------------------------------------------
CREATE TABLE NOTAS_PROVAS (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  prova_id      BIGINT       NOT NULL,
  matricula_id  BIGINT       NOT NULL,
  nota          DECIMAL(5,2) NOT NULL,
  lancada_por   BIGINT       NOT NULL,
  lancada_em    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  alterada_por  BIGINT       NULL,
  alterada_em   DATETIME(3)  NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_notas_prova_matricula UNIQUE (prova_id, matricula_id),
  CONSTRAINT ck_notas_valor CHECK (nota >= 0),
  KEY ix_notas_matricula (matricula_id),
  CONSTRAINT fk_notas_prova FOREIGN KEY (prova_id) REFERENCES PROVAS (id),
  CONSTRAINT fk_notas_matricula FOREIGN KEY (matricula_id) REFERENCES MATRICULAS (id),
  CONSTRAINT fk_notas_lancada_por FOREIGN KEY (lancada_por) REFERENCES USUARIOS (id),
  CONSTRAINT fk_notas_alterada_por FOREIGN KEY (alterada_por) REFERENCES USUARIOS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- PROCESSOS_SELETIVOS
-- uk_processos_prova: P-DER-4 (um processo por prova, Q16).
-- status: único valor formal é 'CRIADO' (Q12, P-DER-3).
-- ---------------------------------------------------------------------
CREATE TABLE PROCESSOS_SELETIVOS (
  id                 BIGINT      NOT NULL AUTO_INCREMENT,
  periodo_letivo_id  BIGINT      NOT NULL,
  prova_id           BIGINT      NOT NULL,
  status             VARCHAR(20) NOT NULL DEFAULT 'CRIADO',
  iniciado_em        DATETIME(3) NULL,
  encerrado_em       DATETIME(3) NULL,
  criado_por         BIGINT      NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_processos_prova UNIQUE (prova_id),
  CONSTRAINT ck_processos_status CHECK (status IN ('CRIADO')),
  KEY ix_processos_periodo (periodo_letivo_id),
  CONSTRAINT fk_processos_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES PERIODOS_LETIVOS (id),
  CONSTRAINT fk_processos_prova FOREIGN KEY (prova_id) REFERENCES PROVAS (id),
  CONSTRAINT fk_processos_criado_por FOREIGN KEY (criado_por) REFERENCES USUARIOS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- CLASSIFICACOES
-- Índice único da D04: (processo_seletivo_id, matricula_id).
-- Não há ano_escolar (D05): a posição é única por (processo, ano), mas o
-- banco não consegue impor isso. Garantia pela transação com lock (ADR-004).
-- ---------------------------------------------------------------------
CREATE TABLE CLASSIFICACOES (
  id                    BIGINT       NOT NULL AUTO_INCREMENT,
  processo_seletivo_id  BIGINT       NOT NULL,
  matricula_id          BIGINT       NOT NULL,
  nota_final            DECIMAL(5,2) NOT NULL,
  posicao               SMALLINT     NOT NULL,
  selecionado           BOOLEAN      NOT NULL DEFAULT FALSE,
  calculada_em          DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  CONSTRAINT uk_classificacoes_processo_matricula UNIQUE (processo_seletivo_id, matricula_id),
  CONSTRAINT ck_classificacoes_posicao CHECK (posicao >= 1),
  KEY ix_classificacoes_processo_posicao (processo_seletivo_id, posicao),
  KEY ix_classificacoes_matricula (matricula_id),
  CONSTRAINT fk_classificacoes_processo FOREIGN KEY (processo_seletivo_id) REFERENCES PROCESSOS_SELETIVOS (id),
  CONSTRAINT fk_classificacoes_matricula FOREIGN KEY (matricula_id) REFERENCES MATRICULAS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- BOLSAS
-- Índice único da D04: (matricula_id, processo_seletivo_id).
-- valor_mensal: gravado a cada concessão (D07, Q13). A aplicação só aceita
--               o valor de configuração (R$ 300,00).
-- criada_por: P-DER-1 (RNF06 exige quem concedeu).
-- situacao: DER não lista valores (P-DER-3). MVP grava somente 'ATIVA';
--           'CANCELADA' fica reservado para P1 (D06).
-- ---------------------------------------------------------------------
CREATE TABLE BOLSAS (
  id                    BIGINT        NOT NULL AUTO_INCREMENT,
  matricula_id          BIGINT        NOT NULL,
  processo_seletivo_id  BIGINT        NOT NULL,
  valor_mensal          DECIMAL(10,2) NOT NULL,
  data_inicio           DATE          NOT NULL,
  data_fim              DATE          NULL,
  situacao              VARCHAR(20)   NOT NULL DEFAULT 'ATIVA',
  motivo_cancelamento   TEXT          NULL,
  cancelada_por         BIGINT        NULL,
  cancelada_em          DATETIME(3)   NULL,
  criada_por            BIGINT        NOT NULL,
  criada_em             DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  CONSTRAINT uk_bolsas_matricula_processo UNIQUE (matricula_id, processo_seletivo_id),
  CONSTRAINT ck_bolsas_valor CHECK (valor_mensal > 0),
  CONSTRAINT ck_bolsas_situacao CHECK (situacao IN ('ATIVA', 'CANCELADA')),
  KEY ix_bolsas_processo (processo_seletivo_id),
  CONSTRAINT fk_bolsas_matricula FOREIGN KEY (matricula_id) REFERENCES MATRICULAS (id),
  CONSTRAINT fk_bolsas_processo FOREIGN KEY (processo_seletivo_id) REFERENCES PROCESSOS_SELETIVOS (id),
  CONSTRAINT fk_bolsas_cancelada_por FOREIGN KEY (cancelada_por) REFERENCES USUARIOS (id),
  CONSTRAINT fk_bolsas_criada_por FOREIGN KEY (criada_por) REFERENCES USUARIOS (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
