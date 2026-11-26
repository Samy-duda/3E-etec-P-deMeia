# 02 - Arquitetura do MVP (P0)

**Situação:** proposta do arquiteto, aguardando aprovação da arquitetura (gate D15). Os devs só começam após a aprovação.
**Fontes:** `CLAUDE.md`, `docs/agentes/decisoes.md` (D01 a D18, P01 a P05), `docs/agentes/01-requisitos-mvp.md`, `der_pe_de_meia.mmd`, `mer_pe_de_meia.mmd`.
**Documentos derivados:** `03-modelo-fisico.md` (esquema), `04-contrato-api.yaml` (contrato), `05-estrutura-pastas.md` (pastas, build, teste), `decisoes/ADR-001` a `ADR-007`.

---

## 1. Visão geral

```
[Navegador]  --HTTP/JSON, Bearer JWT-->  [Quarkus :8080, base /api]  --JDBC/Flyway-->  [MySQL 8.0]
 Angular SPA                               resource -> servico -> repositorio
 (standalone, :4200 em dev)                 (módulos por domínio)
```

- O frontend só conversa com a API. Não acessa o banco.
- O contrato `04-contrato-api.yaml` é a fronteira. Backend e frontend implementam o mesmo contrato, sem combinar detalhes por fora.
- Todas as regras de negócio ficam no backend. O frontend mostra o que a API devolve e valida só formato (campos obrigatórios), para dar retorno rápido.

## 2. Stack fixada

| Camada | Tecnologia | Observação |
|---|---|---|
| Backend | Quarkus 3.x (versão LTS compatível com Java 17), Java 17, Maven | D01. Usar o wrapper `mvnw`. |
| API REST | `quarkus-rest` com `quarkus-rest-jackson` | JSON. |
| Persistência | `quarkus-hibernate-orm-panache` e `quarkus-jdbc-mysql` | Entidades JPA com Panache. Sem MapStruct: mapeamento manual, para ter menos tecnologia. |
| Migração | `quarkus-flyway` | ADR-005. |
| Validação | `quarkus-hibernate-validator` | Anotações Bean Validation nos DTOs de entrada. |
| Segurança | `quarkus-smallrye-jwt`, `quarkus-smallrye-jwt-build`, `quarkus-elytron-security-common` | JWT (D02). BCrypt via `BcryptUtil` do elytron-common, sem biblioteca extra. |
| OpenAPI | `quarkus-smallrye-openapi` | Usado para conferir a implementação com o contrato. O contrato escrito à mão é a referência. |
| Testes | `quarkus-junit5`, `rest-assured` | Testes de API com DevServices (ADR-007). |
| Frontend | Angular (versão estável atual), standalone components, TypeScript em modo estrito | Sem NgModules. Sem biblioteca de estado. |
| HTTP no frontend | `HttpClient` com interceptors | Um para token, um para erro. |
| Formulários | Reactive forms | Validação de formato no cliente. |
| Banco | MySQL 8.0 | D01. Container (ADR-007). |

Nenhuma outra tecnologia entra no MVP sem ADR.

## 3. Módulos e fronteiras (backend)

Pacote base: `br.etec.pedemeia`. Cada módulo tem sua pasta, e dentro dela ficam `entidade`, `repositorio`, `servico`, `resource` e `dto`. A lista completa de pastas está em `05-estrutura-pastas.md`.

| Módulo | Responsabilidade | Requisitos | Depende de |
|---|---|---|---|
| `comum` | Erro único, códigos de erro, paginação, configuração de negócio, mapeamento de exceções. | MVP-RNF03, seção 7 | - |
| `seguranca` | Emissão e validação de JWT, hash de senha (BCrypt), contexto do usuário logado. Não conhece entidades de negócio. | MVP-RNF01, D02 | - |
| `usuarios` | Entidade `Usuario`, cadastro e listagem de contas. Seed de contas de teste (dev/test) e bootstrap do primeiro ADMIN. | MVP-RNF01, D18, Q10 | `seguranca` |
| `auth` | `POST /auth/login` e `GET /auth/me`. | MVP-RNF01 | `seguranca`, `usuarios` |
| `alunos` | Cadastro, consulta e listagem paginada. | MVP-RF01, MVP-RF02, MVP-RNF02 | `comum` |
| `periodos` | Períodos letivos. | MVP-RF03 | `comum` |
| `matriculas` | Matrícula de aluno em período, com ano escolar. | MVP-RF03, P05 | `alunos`, `periodos` |
| `provas` | Cadastro e consulta de provas. | MVP-RF04 | `periodos` |
| `notas` | Lançamento, correção e consulta de notas. | MVP-RF05 | `provas`, `matriculas` |
| `processos` | Criação e consulta do processo seletivo. | MVP-RF06 | `provas`, `periodos` |
| `classificacao` | Ranking por ano, desempate P01, seleção dos 10 melhores. Gera e consulta. | MVP-RF07, MVP-RF08 | `processos`, `notas`, `matriculas` |
| `bolsas` | Concessão e consulta de bolsas. | MVP-RF09, MVP-RF10 | `classificacao`, `processos`, `comum` |

**Regra de dependência:** um módulo chama outro módulo pelo serviço dele, nunca pelo repositório. Ciclos não são permitidos. A ordem de dependência é a da tabela, de cima para baixo: `comum` e `seguranca` primeiro, depois `usuarios`, `auth`, `alunos`, `periodos`, `matriculas`, `provas`, `notas`, `processos`, `classificacao` e `bolsas`.

## 4. Camadas

| Camada | O que faz | O que não faz |
|---|---|---|
| **resource** (JAX-RS) | Recebe a requisição, valida o DTO com `@Valid`, chama o serviço, devolve o DTO e o status HTTP. Declara o perfil com `@RolesAllowed`. | Não tem regra de negócio. Não acessa repositório. Não é transacional. |
| **servico** (`@ApplicationScoped`) | Regras de negócio, verificações de duplicidade, transações, lançamento de `NegocioException`. | Não conhece HTTP. Não devolve entidade. |
| **repositorio** (Panache) | Consultas e persistência. Métodos com nome de consulta, por exemplo `buscarPorMatriculaEscolar`. | Não tem regra de negócio. |
| **entidade** (JPA) | Mapeia as tabelas do V1. Sem anotação JSON. | Não sai da camada de serviço. |
| **dto** | `record` do Java 17. Entrada tem anotações de validação. Saída nunca traz entidade. | Não tem regra. |

**Mapeamento:** cada módulo tem uma classe `Mapeador` com métodos estáticos (`paraResposta`, `paraEntidade`). Sem MapStruct.

**Nomes:** classes, pacotes e métodos em português (`Aluno`, `Matricula`, `ProcessoSeletivo`). Atributos Java em camelCase (`matriculaEscolar`). No banco, o nome da tabela segue o DER (`ALUNOS`) e o campo é snake_case (`matricula_escolar`), via `@Table` e `@Column`.

**JSON:** nomes de campos em camelCase (`matriculaEscolar`, `anoEscolar`). Datas em ISO 8601 (`yyyy-MM-dd`). Instantes em UTC (`2026-11-20T13:00:00Z`). Valores monetários e notas são números com duas casas decimais.

## 5. Decisões de transação

Regra: **a transação fica no serviço**, com `@Transactional` (Jakarta) no método público. O resource nunca abre transação.

- Operações de escrita de um único registro: uma transação por chamada.
- **Geração de classificação (MVP-RF07 e MVP-RF08)**: uma transação só. Ou todas as posições e seleções são gravadas, ou nenhuma (MVP-RNF03). Detalhes no ADR-004.
- **Concessão de bolsas em lote (MVP-RF09)**: uma transação só. Se uma bolsa falhar, nenhuma é gravada.
- **Lançamento e correção de nota**: uma transação, com lock na prova (ADR-004).
- **Consultas**: sem transação de escrita. Podem rodar fora de transação ou com a transação somente leitura, conforme a anotação aceita pela versão do Quarkus.
- `RuntimeException` causa rollback (padrão). Todas as exceções de negócio herdam de `RuntimeException`.
- Isolamento: `REPEATABLE READ` (padrão do MySQL InnoDB). Os locks pessimistas são o mecanismo de concorrência (ADR-004).

## 6. Regra de desempate P01 (ordenação determinística)

Implementada **em Java**, no `classificacao`, não no `ORDER BY` do MySQL. Motivo: a ordem alfabética depende da collation do banco e da JVM. Em Java a regra é a mesma em qualquer ambiente (ADR-003).

Para cada ano escolar (1, 2 e 3), separadamente:

1. Monta a lista de notas das matrículas desse ano, com a nota da prova (P04), a data de nascimento, o nome e o id.
2. Ordena com a chave, nesta ordem:
   1. nota final, **decrescente** (compara `BigDecimal` pelo valor, ignorando escala);
   2. data de nascimento, **crescente** (mais antiga primeiro);
   3. nome, **crescente**, pela chave de nome: nome sem acentos, em caixa alta, comparado por código Unicode;
   4. id da matrícula, **crescente**.
3. Atribui posição `1, 2, 3...` sequencial. Não há ex-aequo (MVP-CA18).
4. Marca `selecionado = true` quando `posicao <= vagasPorAno` (padrão 10, P05).

Consequência verificada nos critérios: MVP-CA19 (nascimento), MVP-CA20 (nome e id), MVP-CA22 (fronteira do top 10). A ordem é a mesma em duas gerações seguidas (MVP-CA20, complemento).

Status: **PREMISSA P01**, não regra oficial. Confirmar com a instituição (Q01) antes do uso real. Se a regra mudar, muda só a chave de comparação, em um único método.

## 7. Tratamento de erros (formato único)

Todas as respostas de erro usam o mesmo JSON. Ver ADR-002.

```json
{
  "timestamp": "2026-11-20T13:00:00Z",
  "status": 422,
  "codigo": "NOTA_FORA_DO_INTERVALO",
  "mensagem": "A nota deve estar entre 0 e a nota máxima da prova (10,00).",
  "caminho": "/api/provas/3/notas",
  "campos": []
}
```

- `campos` só vem preenchido em `400 VALIDACAO_INVALIDA`, com `{ "campo": "dataNascimento", "mensagem": "é obrigatório" }`.
- `codigo` é um valor do enum de erros (lista abaixo). O frontend decide a mensagem pelo `codigo`, não pelo texto.
- Implementação: um `ExceptionMapper` para `NegocioException` (com o código) e outro para `ConstraintViolationException` e para exceções de validação. Um mapper para `PersistenceException` traduz violação de UNIQUE pelo nome da constraint (ver tabela abaixo). Um mapper genérico devolve `500 ERRO_INTERNO` sem detalhe de stack.

**Classificação de status:**

| HTTP | Quando |
|---|---|
| 400 | Campo ausente, formato inválido, valor fora do que o schema declara (ano escolar 1 a 3, nota máxima maior que 0). |
| 401 | Sem token, token inválido ou expirado (`NAO_AUTENTICADO`). Login com senha errada (`CREDENCIAIS_INVALIDAS`). |
| 403 | Token válido, perfil sem permissão (`ACESSO_NEGADO`). |
| 404 | Recurso inexistente ou rota fora do MVP, por exemplo cancelamento de bolsa (`RECURSO_NAO_ENCONTRADO`). |
| 409 | Conflito de estado ou duplicidade (`*_DUPLICADA`, `*_DUPLICADO`, `*_JA_*`). |
| 422 | Regra de negócio violada (intervalo de nota, aluno não matriculado, aluno não selecionado, valor de bolsa diferente de R$ 300,00). |
| 500 | Erro inesperado (`ERRO_INTERNO`). |

**Catálogo de códigos:**

| Código | HTTP | Critério |
|---|---|---|
| VALIDACAO_INVALIDA | 400 | MVP-CA04, MVP-CA06, MVP-CA09 |
| CREDENCIAIS_INVALIDAS | 401 | MVP-RNF01 |
| NAO_AUTENTICADO | 401 | MVP-CA10 (cenário b) |
| ACESSO_NEGADO | 403 | MVP-CA10 (cenário a) |
| RECURSO_NAO_ENCONTRADO | 404 | Geral |
| ALUNO_DUPLICADO | 409 | MVP-CA03 |
| EMAIL_DUPLICADO | 409 | MVP-RNF01 |
| MATRICULA_DUPLICADA | 409 | MVP-CA07 |
| NOTA_DUPLICADA | 409 | MVP-CA13 |
| PROCESSO_JA_EXISTE_PARA_PROVA | 409 | Q16 (P-DER-4) |
| CLASSIFICACAO_JA_GERADA | 409 | Q07; lançamento ou correção de nota depois da classificação |
| BOLSA_JA_EXISTE | 409 | MVP-CA27 |
| BOLSAS_JA_CONCEDIDAS | 409 | Lote sem selecionado pendente |
| NOTA_FORA_DO_INTERVALO | 422 | MVP-CA12, P04 |
| ALUNO_NAO_MATRICULADO_NO_PERIODO | 422 | MVP-CA14 |
| PROVA_NAO_PERTENCE_AO_PERIODO | 422 | MVP-CA15 |
| SEM_NOTAS_PARA_CLASSIFICACAO | 422 | MVP-CA16 |
| CLASSIFICACAO_NAO_GERADA | 422 | Concessão antes da classificação |
| ALUNO_NAO_CLASSIFICADO | 422 | Matrícula sem linha em CLASSIFICACOES do processo |
| ALUNO_NAO_SELECIONADO | 422 | MVP-CA26 |
| VALOR_BOLSA_INVALIDO | 422 | MVP-CA25, D07 |
| ERRO_INTERNO | 500 | Falha inesperada |

**Mapa de constraint para código (fallback do banco):**

| Constraint | Código |
|---|---|
| `uk_alunos_matricula_escolar` | ALUNO_DUPLICADO |
| `uk_usuarios_email` | EMAIL_DUPLICADO |
| `uk_matriculas_aluno_periodo` | MATRICULA_DUPLICADA |
| `uk_notas_prova_matricula` | NOTA_DUPLICADA |
| `uk_processos_prova` | PROCESSO_JA_EXISTE_PARA_PROVA |
| `uk_bolsas_matricula_processo` | BOLSA_JA_EXISTE |

A checagem antes do INSERT é a fonte principal da mensagem. O mapa é a rede de segurança para corrida entre duas requisições.

## 8. Segurança

Detalhes e decisões em ADR-006.

**Autenticação**
- `POST /auth/login` recebe e-mail e senha, e devolve um JWT (`Bearer`) com `sub` (id do usuário), `groups` (perfil), `upn` (e-mail), `iat` e `exp`.
- Expiração: 8 horas (`pedemeia.jwt.expiracao-segundos=28800`).
- Senha guardada com BCrypt (`BcryptUtil`). Nunca gravada, nunca devolvida, nunca logada.
- Login do e-mail `test@IA_admin` e similares: o campo de e-mail do backend valida só não-vazio e tamanho, **sem `@Email`**, porque o domínio contém `_`. No frontend, o campo de login é `type="text"`, não `type="email"`.

**Autorização**
- `quarkus.security.jaxrs.deny-unannotated-endpoints=true`: endpoint sem anotação de perfil é negado.
- Cada método de resource tem `@RolesAllowed` com os perfis da coluna `x-perfis` do contrato.
- Mapeamento de perfis: `ADMIN` e `SECRETARIA` acessam cadastros, notas, processos e bolsas. `CONSULTA` acessa somente classificações, lista de bolsas e metadados de processo (MVP-RF10).
- `CONSULTA` **não acessa** `/alunos`, `/periodos-letivos/{id}/matriculas` e `/provas/{id}/notas`. Devolver nome na classificação é permitido pela Q03.
- Operação negada não altera dado (MVP-RNF01). A verificação ocorre antes do serviço.

**LGPD mínima (MVP-RNF02)**
- Coleta: nome, matrícula escolar e data de nascimento (obrigatórios). E-mail e telefone são opcionais.
- Respostas de `CONSULTA` nunca incluem `dataNascimento`, `email`, `telefone` ou `matriculaEscolar`. Essa regra é aplicada no `Mapeador`, com DTO separado para o perfil de consulta.
- Dados pessoais não vão para o log. A matrícula escolar aparece em query string em `GET /alunos?matriculaEscolar=`; o access log fica desligado por padrão.
- Retenção e exclusão: fora do MVP (Q19).

**CORS:** em desenvolvimento, o frontend usa proxy para `/api`, então não há chamada cross-origin. `quarkus.http.cors.origins=http://localhost:4200` só é necessário se a SPA rodar em outra origem. Produção é configurada por variável de ambiente.

**Frontend:** o token fica em `sessionStorage`. O `AuthInterceptor` adiciona o cabeçalho. Resposta 401 limpa a sessão e redireciona para o login. Resposta 403 mostra mensagem e não redireciona.

## 9. Migração do banco (Flyway)

- Scripts em `backend/src/main/resources/db/migration/`. Nome: `V<n>__<descricao>.sql`.
- Primeiro script: `V1__esquema_mvp.sql`. Fonte: `docs/agentes/scripts/V1__esquema_mvp.sql`. Cópia sem alteração.
- `quarkus.flyway.migrate-at-start=true`.
- `quarkus.hibernate-orm.database.generation=validate`. O Hibernate não cria nem altera tabela. Só confere.
- Não existe `hibernate-orm.database.generation=update` em nenhum perfil.
- Usuários iniciais: não há INSERT em migração. Ver seção 10.

## 10. Configuração e contas

| Chave (`application.properties`) | Valor padrão | Uso |
|---|---|---|
| `pedemeia.bolsa.valor-mensal` | `300.00` | Valor padrão da bolsa (D07, RN03). Único lugar com o valor. |
| `pedemeia.processo.vagas-por-ano` | `10` | Vagas por ano (P05, RN01). |
| `pedemeia.jwt.expiracao-segundos` | `28800` | 8 horas. |
| `pedemeia.seed.contas-teste.ativo` | `false` (`true` em `%dev` e `%test`) | Cria `test@IA_admin`, `test@IA_secretaria`, `test@IA_consulta` se não existirem (D18). Classe no módulo `usuarios`. |
| `pedemeia.bootstrap.admin.email` e `.senha` | Variáveis `PEDEMEIA_ADMIN_EMAIL` e `PEDEMEIA_ADMIN_SENHA` | Cria o primeiro ADMIN **somente se** `USUARIOS` estiver vazia (Q10). Classe no módulo `usuarios`. |

Contas de teste em produção são proibidas. A seed tem o flag desligado por padrão e não roda sem o perfil `dev` ou `test`.

## 11. Regras de qualidade e testes

- Cada critério MVP-CA tem ao menos um teste de API (`@QuarkusTest` com RestAssured), com o nome do critério no método, por exemplo `mvpCa19_desempateNascimento`.
- O cálculo de ranking (P01 e P05) tem testes unitários em Java puro, sem banco, para cobrir as combinações de empate.
- Testes de perfil (MVP-CA10) rodam com as três contas de teste.
- Testes que alteram dados usam dados próprios de cada teste (sem depender de ordem).
- Nenhum teste usa conta pessoal (D18).

## 12. Fronteira frontend x backend

- O backend é a fonte da verdade. O frontend nunca calcula posição, seleção ou valor de bolsa.
- O frontend monta as telas a partir do contrato. Se a resposta trouxer um campo, a tela exibe. Se não trouxer (caso de `CONSULTA`), a tela oculta. A ocultação não depende de cálculo no cliente.
- Rotas e guards são descritos em `05-estrutura-pastas.md`, seção 5.
- Pendências que afetam telas: nenhuma bloqueia o início. Q05 (aviso de vagas não preenchidas) e Q14 (meta de desempenho) ficam para depois.

## 13. Pendências e riscos desta arquitetura

| Item | Situação |
|---|---|
| P01 (desempate) | Premissa. Implementada como ordenação determinística. Validar com a instituição (Q01). |
| Propostas de ajuste ao DER (P-DER-1 a P-DER-4) | Aplicadas no V1. O orquestrador anota em `decisoes.md`. Ver `03-modelo-fisico.md`, seção 5. |
| P-DER-5 (`ano_escolar` em CLASSIFICACOES) | Não aplicada. Decisão do orquestrador. |
| Q03 (dados visíveis a CONSULTA) | Aplicado o padrão da Q03: nome, posição, nota e situação. |
| Q18 (busca de alunos) | Implementada como "contém", sem diferenciar maiúsculas. Compatível com "nome completo". |
| Q14 (desempenho) | Sem métrica formal. Testes de desempenho ficam para depois. |

## 14. Índice de ADRs

| ADR | Assunto |
|---|---|
| ADR-001 | Camadas, DTOs e stack |
| ADR-002 | Formato único de erro |
| ADR-003 | Desempate P01 como ordenação determinística em Java |
| ADR-004 | Transações e concorrência |
| ADR-005 | Modelo físico, Flyway e ajustes ao DER |
| ADR-006 | Segurança, perfis e LGPD |
| ADR-007 | Ambiente Docker e testes |

Arquivos em `docs/agentes/decisoes/`.
