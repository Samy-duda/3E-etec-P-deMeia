# Banco de Dados — Projeto Pé-de-Meia

**Documento:** Modelo de dados, transformação em tabelas e implementação SQL  
**SGBD:** MySQL 8.0+

## 1. Modelo de dados

O banco de dados do projeto Pé-de-Meia organiza os dados dos estudantes, períodos letivos, provas, notas, disciplinas, médias, frequência, processos seletivos, classificações e bolsas. O objetivo é apoiar a seleção dos bolsistas e acompanhar os critérios para manutenção do benefício.

### 1.1 Entidades principais

- **Alunos:** dados cadastrais dos estudantes.
- **Períodos letivos:** datas e identificação do período escolar.
- **Matrículas:** vínculo do aluno com o período letivo e o ano escolar.
- **Provas:** provas usadas no processo seletivo.
- **Notas das provas:** notas obtidas pelos estudantes.
- **Disciplinas:** disciplinas escolares.
- **Médias das disciplinas:** médias de cada aluno por disciplina e período.
- **Frequências:** presenças, faltas e justificativas.
- **Usuários:** pessoas autorizadas a acessar o sistema.
- **Processos seletivos:** organização de cada seleção.
- **Classificações:** nota final, posição e indicação de seleção.
- **Bolsas:** benefício concedido, valor e situação.
- **Histórico de bolsas:** registro das mudanças de situação do benefício.

### 1.2 Relacionamentos

- Um aluno pode ter matrículas em diferentes períodos letivos.
- Cada matrícula pertence a um aluno e a um período letivo.
- Uma prova pode ter notas de vários alunos.
- As notas relacionam uma prova à matrícula do estudante.
- Uma matrícula pode ter médias em várias disciplinas e registros de frequência.
- Um processo seletivo utiliza uma prova e pertence a um período letivo.
- A classificação relaciona o processo seletivo às matrículas participantes.
- Uma bolsa é vinculada à matrícula e ao processo seletivo.
- O histórico registra alterações feitas em cada bolsa.

**Legenda:** `PK` significa chave primária; `FK` significa chave estrangeira; `UNIQUE` indica que o valor deve ser único.

## 2. Transformação do modelo em tabelas de dados

A seguir estão as tabelas principais e seus campos. Os tipos e restrições detalhados estão implementados no script SQL da seção 3.

### 2.1 `alunos`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `nome` | VARCHAR(150) | NOT NULL |
| `matricula_escolar` | VARCHAR(50) | UNIQUE |
| `data_nascimento` | DATE | Opcional |
| `email` | VARCHAR(190) | Opcional |
| `telefone` | VARCHAR(30) | Opcional |
| `ativo` | BOOLEAN | Padrão TRUE |

### 2.2 `periodos_letivos`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `nome` | VARCHAR(100) | NOT NULL |
| `data_inicio` | DATE | NOT NULL |
| `data_fim` | DATE | NOT NULL |
| `ativo` | BOOLEAN | Padrão TRUE |

### 2.3 `matriculas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `aluno_id` | BIGINT | FK → `alunos.id` |
| `periodo_letivo_id` | BIGINT | FK → `periodos_letivos.id` |
| `ano_escolar` | TINYINT | 1, 2 ou 3 |
| `turma` | VARCHAR(30) | Opcional |
| `status` | ENUM | Situação da matrícula |

### 2.4 `provas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `periodo_letivo_id` | BIGINT | FK → `periodos_letivos.id` |
| `titulo` | VARCHAR(150) | NOT NULL |
| `descricao` | TEXT | Opcional |
| `data_aplicacao` | DATE | Opcional |
| `nota_maxima` | DECIMAL(5,2) | NOT NULL |
| `criada_por` | BIGINT | FK → `usuarios.id` |

### 2.5 `notas_provas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `prova_id` | BIGINT | FK → `provas.id` |
| `matricula_id` | BIGINT | FK → `matriculas.id` |
| `nota` | DECIMAL(5,2) | NOT NULL |
| `lancada_por` | BIGINT | FK → `usuarios.id` |
| `lancada_em` | TIMESTAMP | Data do lançamento |

### 2.6 `disciplinas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `nome` | VARCHAR(100) | UNIQUE |
| `ativa` | BOOLEAN | Padrão TRUE |

### 2.7 `medias_disciplinas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `matricula_id` | BIGINT | FK → `matriculas.id` |
| `disciplina_id` | BIGINT | FK → `disciplinas.id` |
| `periodo_letivo_id` | BIGINT | FK → `periodos_letivos.id` |
| `media` | DECIMAL(4,2) | De 0 a 10 |
| `atualizada_por` | BIGINT | FK → `usuarios.id` |

### 2.8 `frequencias`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `matricula_id` | BIGINT | FK → `matriculas.id` |
| `disciplina_id` | BIGINT | FK → `disciplinas.id`, opcional |
| `data_aula` | DATE | NOT NULL |
| `situacao` | ENUM | Presente ou falta |
| `justificativa` | TEXT | Opcional |
| `justificativa_aceita` | BOOLEAN | Padrão FALSE |

### 2.9 `usuarios`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `nome` | VARCHAR(150) | NOT NULL |
| `email` | VARCHAR(190) | UNIQUE |
| `senha_hash` | VARCHAR(255) | NOT NULL |
| `perfil` | ENUM | Administrador, pedagógico ou responsável |
| `ativo` | BOOLEAN | Padrão TRUE |

### 2.10 `processos_seletivos`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `periodo_letivo_id` | BIGINT | FK → `periodos_letivos.id` |
| `prova_id` | BIGINT | FK → `provas.id` |
| `status` | ENUM | Planejado, em andamento ou encerrado |
| `iniciado_em` | DATETIME | Opcional |
| `encerrado_em` | DATETIME | Opcional |

### 2.11 `classificacoes`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `processo_seletivo_id` | BIGINT | FK → `processos_seletivos.id` |
| `matricula_id` | BIGINT | FK → `matriculas.id` |
| `nota_final` | DECIMAL(5,2) | NOT NULL |
| `posicao` | SMALLINT | Posição no ranking |
| `selecionado` | BOOLEAN | Indica se foi selecionado |

### 2.12 `bolsas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `matricula_id` | BIGINT | FK → `matriculas.id` |
| `processo_seletivo_id` | BIGINT | FK → `processos_seletivos.id` |
| `valor_mensal` | DECIMAL(8,2) | R$ 300,00 |
| `data_inicio` | DATE | NOT NULL |
| `data_fim` | DATE | Opcional |
| `situacao` | ENUM | Ativa, cancelada ou encerrada |
| `motivo_cancelamento` | TEXT | Opcional |

### 2.13 `historico_bolsas`

| Campo | Tipo | Restrição |
|---|---|---|
| `id` | BIGINT | PK |
| `bolsa_id` | BIGINT | FK → `bolsas.id` |
| `situacao_anterior` | ENUM | Opcional |
| `situacao_nova` | ENUM | NOT NULL |
| `motivo` | TEXT | Opcional |
| `alterada_por` | BIGINT | FK → `usuarios.id` |
| `alterada_em` | TIMESTAMP | Data da alteração |

## 3. Código SQL

O script abaixo cria o banco `pe_de_meia`, suas tabelas, chaves primárias e estrangeiras, restrições, índices e consultas de exemplo.

> Para executar, utilize o MySQL 8.0 ou superior. O script cria a estrutura; ele não insere dados reais de alunos.

```sql
-- Banco de dados do Projeto Pé-de-Meia
-- SGBD: MySQL 8.0+
-- Observação: regras ainda pendentes no PRD (desempate e limite de faltas)
-- devem ser definidas pela instituição antes da implantação.

CREATE DATABASE IF NOT EXISTS pe_de_meia
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE pe_de_meia;

-- 1. Usuários que acessam o sistema
CREATE TABLE usuarios (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil ENUM('administrador', 'pedagogico', 'responsavel_programa') NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Cadastro dos estudantes
CREATE TABLE alunos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    matricula_escolar VARCHAR(50) NOT NULL UNIQUE,
    data_nascimento DATE NULL,
    email VARCHAR(190) NULL,
    telefone VARCHAR(30) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Períodos letivos (ex.: ano letivo de 2026)
CREATE TABLE periodos_letivos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_periodo_datas CHECK (data_fim >= data_inicio)
);

-- Matrícula liga o aluno ao período e registra o ano escolar naquele período.
CREATE TABLE matriculas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    aluno_id BIGINT UNSIGNED NOT NULL,
    periodo_letivo_id BIGINT UNSIGNED NOT NULL,
    ano_escolar TINYINT UNSIGNED NOT NULL,
    turma VARCHAR(30) NULL,
    status ENUM('ativa', 'transferida', 'concluida', 'cancelada') NOT NULL DEFAULT 'ativa',
    UNIQUE (aluno_id, periodo_letivo_id),
    CONSTRAINT chk_ano_escolar CHECK (ano_escolar IN (1, 2, 3)),
    CONSTRAINT fk_matricula_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    CONSTRAINT fk_matricula_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES periodos_letivos(id)
);

-- 4. Provas do processo seletivo
CREATE TABLE provas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    periodo_letivo_id BIGINT UNSIGNED NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT NULL,
    data_aplicacao DATE NULL,
    nota_maxima DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    criada_por BIGINT UNSIGNED NULL,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_prova_nota_maxima CHECK (nota_maxima > 0),
    CONSTRAINT fk_prova_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES periodos_letivos(id),
    CONSTRAINT fk_prova_usuario FOREIGN KEY (criada_por) REFERENCES usuarios(id)
);

-- Nota de cada estudante em cada prova
CREATE TABLE notas_provas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    prova_id BIGINT UNSIGNED NOT NULL,
    matricula_id BIGINT UNSIGNED NOT NULL,
    nota DECIMAL(5,2) NOT NULL,
    lancada_por BIGINT UNSIGNED NULL,
    lancada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (prova_id, matricula_id),
    CONSTRAINT chk_nota_prova CHECK (nota >= 0),
    CONSTRAINT fk_nota_prova FOREIGN KEY (prova_id) REFERENCES provas(id),
    CONSTRAINT fk_nota_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_nota_usuario FOREIGN KEY (lancada_por) REFERENCES usuarios(id)
);

-- 5. Disciplinas e médias dos estudantes
CREATE TABLE disciplinas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    ativa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE medias_disciplinas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    matricula_id BIGINT UNSIGNED NOT NULL,
    disciplina_id BIGINT UNSIGNED NOT NULL,
    periodo_letivo_id BIGINT UNSIGNED NOT NULL,
    media DECIMAL(4,2) NOT NULL,
    atualizada_por BIGINT UNSIGNED NULL,
    atualizada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (matricula_id, disciplina_id, periodo_letivo_id),
    CONSTRAINT chk_media CHECK (media >= 0 AND media <= 10),
    CONSTRAINT fk_media_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_media_disciplina FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id),
    CONSTRAINT fk_media_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES periodos_letivos(id),
    CONSTRAINT fk_media_usuario FOREIGN KEY (atualizada_por) REFERENCES usuarios(id)
);

-- 6. Frequência e justificativas
CREATE TABLE frequencias (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    matricula_id BIGINT UNSIGNED NOT NULL,
    disciplina_id BIGINT UNSIGNED NULL,
    data_aula DATE NOT NULL,
    situacao ENUM('presente', 'falta') NOT NULL,
    justificativa TEXT NULL,
    justificativa_aceita BOOLEAN NOT NULL DEFAULT FALSE,
    analisada_por BIGINT UNSIGNED NULL,
    analisada_em DATETIME NULL,
    registrada_por BIGINT UNSIGNED NULL,
    registrada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_frequencia_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_frequencia_disciplina FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id),
    CONSTRAINT fk_frequencia_analisador FOREIGN KEY (analisada_por) REFERENCES usuarios(id),
    CONSTRAINT fk_frequencia_registrador FOREIGN KEY (registrada_por) REFERENCES usuarios(id)
);

-- 7. Processo seletivo e classificação
CREATE TABLE processos_seletivos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    periodo_letivo_id BIGINT UNSIGNED NOT NULL,
    prova_id BIGINT UNSIGNED NOT NULL,
    status ENUM('planejado', 'em_andamento', 'encerrado') NOT NULL DEFAULT 'planejado',
    iniciado_em DATETIME NULL,
    encerrado_em DATETIME NULL,
    criado_por BIGINT UNSIGNED NULL,
    UNIQUE (periodo_letivo_id, prova_id),
    CONSTRAINT fk_processo_periodo FOREIGN KEY (periodo_letivo_id) REFERENCES periodos_letivos(id),
    CONSTRAINT fk_processo_prova FOREIGN KEY (prova_id) REFERENCES provas(id),
    CONSTRAINT fk_processo_usuario FOREIGN KEY (criado_por) REFERENCES usuarios(id)
);

-- Guarda o resultado calculado para auditoria e consulta histórica.
CREATE TABLE classificacoes (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    processo_seletivo_id BIGINT UNSIGNED NOT NULL,
    matricula_id BIGINT UNSIGNED NOT NULL,
    nota_final DECIMAL(5,2) NOT NULL,
    posicao SMALLINT UNSIGNED NOT NULL,
    selecionado BOOLEAN NOT NULL DEFAULT FALSE,
    calculada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (processo_seletivo_id, matricula_id),
    UNIQUE (processo_seletivo_id, matricula_id, posicao),
    CONSTRAINT chk_classificacao_nota CHECK (nota_final >= 0),
    CONSTRAINT chk_classificacao_posicao CHECK (posicao >= 1),
    CONSTRAINT fk_classificacao_processo FOREIGN KEY (processo_seletivo_id) REFERENCES processos_seletivos(id),
    CONSTRAINT fk_classificacao_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id)
);

-- 8. Bolsas concedidas aos estudantes
CREATE TABLE bolsas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    matricula_id BIGINT UNSIGNED NOT NULL,
    processo_seletivo_id BIGINT UNSIGNED NOT NULL,
    valor_mensal DECIMAL(8,2) NOT NULL DEFAULT 300.00,
    data_inicio DATE NOT NULL,
    data_fim DATE NULL,
    situacao ENUM('ativa', 'cancelada', 'encerrada') NOT NULL DEFAULT 'ativa',
    motivo_cancelamento TEXT NULL,
    cancelada_por BIGINT UNSIGNED NULL,
    cancelada_em DATETIME NULL,
    criada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (matricula_id, processo_seletivo_id),
    CONSTRAINT chk_bolsa_valor CHECK (valor_mensal = 300.00),
    CONSTRAINT chk_bolsa_datas CHECK (data_fim IS NULL OR data_fim >= data_inicio),
    CONSTRAINT fk_bolsa_matricula FOREIGN KEY (matricula_id) REFERENCES matriculas(id),
    CONSTRAINT fk_bolsa_processo FOREIGN KEY (processo_seletivo_id) REFERENCES processos_seletivos(id),
    CONSTRAINT fk_bolsa_usuario FOREIGN KEY (cancelada_por) REFERENCES usuarios(id)
);

-- Histórico de mudanças da situação da bolsa
CREATE TABLE historico_bolsas (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    bolsa_id BIGINT UNSIGNED NOT NULL,
    situacao_anterior ENUM('ativa', 'cancelada', 'encerrada') NULL,
    situacao_nova ENUM('ativa', 'cancelada', 'encerrada') NOT NULL,
    motivo TEXT NULL,
    alterada_por BIGINT UNSIGNED NULL,
    alterada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_bolsa FOREIGN KEY (bolsa_id) REFERENCES bolsas(id),
    CONSTRAINT fk_historico_usuario FOREIGN KEY (alterada_por) REFERENCES usuarios(id)
);

-- Índices para consultas comuns
CREATE INDEX idx_matriculas_ano_periodo ON matriculas(periodo_letivo_id, ano_escolar);
CREATE INDEX idx_notas_provas_matricula ON notas_provas(matricula_id);
CREATE INDEX idx_medias_matricula_periodo ON medias_disciplinas(matricula_id, periodo_letivo_id);
CREATE INDEX idx_frequencias_matricula_data ON frequencias(matricula_id, data_aula);
CREATE INDEX idx_bolsas_situacao ON bolsas(situacao);

-- Exemplo de consulta: classificação por ano escolar em um processo seletivo.
-- Substitua 1 pelo ID do processo seletivo desejado.
SELECT
    m.ano_escolar,
    a.nome AS aluno,
    c.nota_final,
    c.posicao,
    c.selecionado
FROM classificacoes c
JOIN matriculas m ON m.id = c.matricula_id
JOIN alunos a ON a.id = m.aluno_id
WHERE c.processo_seletivo_id = 1
ORDER BY m.ano_escolar, c.posicao;

-- Exemplo de consulta: estudantes com média inferior a 6,0 no período.
-- Substitua 1 pelo ID do período letivo desejado.
SELECT
    a.nome AS aluno,
    d.nome AS disciplina,
    md.media
FROM medias_disciplinas md
JOIN matriculas m ON m.id = md.matricula_id
JOIN alunos a ON a.id = m.aluno_id
JOIN disciplinas d ON d.id = md.disciplina_id
WHERE md.periodo_letivo_id = 1
  AND md.media < 6.0
ORDER BY a.nome, d.nome;

-- Exemplo de consulta: faltas não justificadas ou com justificativa não aceita.
SELECT
    a.nome AS aluno,
    f.data_aula,
    f.justificativa,
    f.justificativa_aceita
FROM frequencias f
JOIN matriculas m ON m.id = f.matricula_id
JOIN alunos a ON a.id = m.aluno_id
WHERE f.situacao = 'falta'
  AND f.justificativa_aceita = FALSE
ORDER BY f.data_aula DESC;
```

## 4. Regras e pontos pendentes

- A bolsa mensal está definida em R$ 300,00.
- A seleção prevê os 10 melhores colocados de cada ano do ensino médio.
- A média mínima para manutenção é 6,0 em todas as disciplinas.
- O limite numérico de faltas ainda precisa ser definido.
- A regra de desempate da classificação ainda precisa ser definida.
- O acesso aos dados deve ser restrito conforme o perfil do usuário e as regras de proteção de dados.

## 5. Como usar este arquivo no GitHub

1. Entre no repositório do projeto.
2. Clique em **Add file** → **Create new file**.
3. Digite `banco_de_dados.md` como nome do arquivo.
4. Cole ou envie o conteúdo deste documento.
5. Clique em **Commit changes** para salvar no repositório.
