# 05 - Estrutura de pastas, comandos e ambiente

**Situação:** proposta do arquiteto, aguardando aprovação (gate D15).
**Base:** `02-arquitetura.md`, `03-modelo-fisico.md`, `04-contrato-api.yaml`, ADR-001 a ADR-007.

Caminho raiz: `/home/scutulatus/Downloads/ETEC - 3E - 2026/Projetos 3E/Projeto_07_Pe_de_Meia/`

Os devs criam as pastas e os projetos. Esta entrega só documenta a estrutura (regra da entrega).

---

## 1. Árvore do projeto (alvo)

```
Projeto_07_Pe_de_Meia/
├── CLAUDE.md, PRD, MER, DER (sem alteração)
├── docs/agentes/           (documentos desta entrega)
├── backend/                (Quarkus, Java 17, Maven)
└── frontend/               (Angular, standalone)
```

## 2. Backend

```
backend/
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/wrapper/
├── docker-compose.yml          (MySQL 8.0 para desenvolvimento; seção 4)
├── src/main/java/br/etec/pedemeia/
│   ├── comum/
│   │   ├── erro/               NegocioException, CodigoErro (enum), ErroResposta, CampoErro,
│   │   │                       MapeadorNegocio, MapeadorValidacao, MapeadorPersistencia,
│   │   │                       MapeadorSeguranca (401/403), MapeadorGenerico (500)
│   │   ├── paginacao/          PaginaRequisicao, Pagina<T> (record)
│   │   └── configuracao/       ConfiguracaoNegocio (@ConfigMapping prefixo pedemeia)
│   ├── seguranca/              TokenService, SenhaService, UsuarioLogado
│   │                           (sem dependência de módulo de negócio)
│   ├── usuarios/               (módulo padrão, expandido abaixo) + ContasTesteSeed (dev/test),
│   │                           BootstrapAdmin
│   ├── auth/                   AuthResource, AuthServico,
│   │                           dto/ LoginRequisicao, LoginResposta, UsuarioResumoResposta
│   ├── alunos/                 (módulo padrão, expandido abaixo)
│   ├── periodos/               (módulo padrão)
│   ├── matriculas/             (módulo padrão)
│   ├── provas/                 (módulo padrão)
│   ├── notas/                  (módulo padrão)
│   ├── processos/              (módulo padrão)
│   ├── classificacao/          (módulo padrão + CriterioDesempate)
│   └── bolsas/                 (módulo padrão)
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/
│       └── V1__esquema_mvp.sql   (cópia de docs/agentes/scripts/V1__esquema_mvp.sql, sem alteração)
└── src/test/java/br/etec/pedemeia/
    ├── classificacao/CriterioDesempateTest.java        (unitário, sem banco)
    ├── auth/AuthApiTest.java                           (MVP-CA10 b, RNF01)
    ├── seguranca/PerfisApiTest.java                    (MVP-CA10 a e c)
    ├── alunos/CadastroAlunoApiTest.java                (MVP-CA01 a CA04)
    ├── matriculas/MatriculaApiTest.java                (MVP-CA05 a CA07)
    ├── notas/NotaApiTest.java                          (MVP-CA11 a CA14)
    ├── processos/ProcessoApiTest.java                  (MVP-CA15)
    ├── classificacao/ClassificacaoApiTest.java         (MVP-CA16 a CA23, CA28)
    └── bolsas/BolsaApiTest.java                        (MVP-CA24 a CA27, CA29, CA30)
```

**Módulo padrão** (vale para `usuarios`, `alunos`, `periodos`, `matriculas`, `provas`, `notas`, `processos`, `classificacao`, `bolsas`):

```
<modulo>/
├── entidade/        entidade JPA (@Entity, @Table com o nome do DER)
├── repositorio/     Panache (PanacheRepository<T>), consultas nomeadas
├── servico/         regras de negócio, @Transactional, lança NegocioException
├── resource/        JAX-RS, @RolesAllowed, @Valid, sem regra
├── dto/             records de entrada (com validação) e saída
└── Mapeador.java    conversão entidade <-> DTO (métodos estáticos)
```

**Exemplo expandido: `alunos/`**

```
alunos/
├── entidade/Aluno.java                (ALUNOS)
├── repositorio/AlunoRepositorio.java  (buscarPorMatriculaEscolar, buscarPaginado com filtros)
├── servico/AlunoServico.java          (cadastrar, consultar, listar; verifica ALUNO_DUPLICADO)
├── resource/AlunoResource.java        (/alunos, /alunos/{alunoId})
├── dto/AlunoRequisicao.java, AlunoResposta.java
└── Mapeador.java                      (CONSULTA nunca recebe este DTO; ver ADR-006)
```

**Notas por módulo**

| Módulo | Pontos específicos |
|---|---|
| `usuarios` | `UsuarioResource` em `/usuarios`. `ContasTesteSeed` e `BootstrapAdmin` são `@Startup` e controlados por configuração (`02-arquitetura.md`, seção 10). |
| `periodos` | `PeriodoLetivoResource` em `/periodos-letivos`. Sem paginação. |
| `matriculas` | `MatriculaResource` em `/periodos-letivos/{periodoId}/matriculas`. Verifica MATRICULA_DUPLICADA. |
| `provas` | `ProvaResource` em `/provas`. |
| `notas` | `NotaResource` em `/provas/{provaId}/notas`. Lock na prova (ADR-004). |
| `processos` | `ProcessoSeletivoResource` em `/processos-seletivos`. Verifica PROVA_NAO_PERTENCE_AO_PERIODO. |
| `classificacao` | `ClassificacaoResource` em `/processos-seletivos/{processoId}/classificacao`. `CriterioDesempate` é Java puro, sem anotação de framework, e tem os testes de P01. |
| `bolsas` | `BolsaResource` em `/processos-seletivos/{processoId}/bolsas`. Sem endpoint de cancelamento (MVP-CA30). |

**Arquivo de configuração** (`src/main/resources/application.properties`): chaves da seção 10 de `02-arquitetura.md`. Perfis `%dev` e `%test` ficam no mesmo arquivo, com prefixo de perfil do Quarkus.

**Dependências Maven** estão em ADR-001. O `pom.xml` é gerado pelo CLI do Quarkus com as extensões acima.

## 3. Frontend

```
frontend/
├── angular.json, package.json, package-lock.json
├── tsconfig.json, tsconfig.app.json, tsconfig.spec.json
├── proxy.conf.json                 (/api -> http://localhost:8080)
├── src/
│   ├── index.html
│   ├── main.ts
│   ├── styles.css                  (responsivo, sem rolagem horizontal em 375 px, MVP-RNF05)
│   └── app/
│       ├── app.ts (ou app.component.ts, conforme o CLI)
│       ├── app.config.ts           (provideRouter, provideHttpClient com interceptors)
│       ├── app.routes.ts           (rotas da seção 5)
│       ├── core/
│       │   ├── api/                apiBase.ts (constante '/api')
│       │   ├── auth/               auth.service.ts, sessao.service.ts (sessionStorage),
│       │   │                       auth.guard.ts, perfil.guard.ts, perfil.ts (tipo e constantes),
│       │   │                       auth.interceptor.ts
│       │   └── erros/              erro.interceptor.ts, mensagens-erro.ts (código -> texto)
│       ├── shared/
│       │   ├── modelos/            aluno, periodo-letivo, matricula, prova, nota,
│       │   │                       processo-seletivo, classificacao, bolsa, usuario, pagina, erro-api
│       │   └── componentes/        layout (menu responsivo), paginacao, mensagem-erro
│       └── features/
│           ├── login/              login.component.ts
│           ├── alunos/             alunos-lista.component.ts, aluno-form.component.ts, alunos.service.ts
│           ├── periodos/           periodos-letivos.component.ts, periodos.service.ts
│           ├── matriculas/         matriculas.component.ts, matriculas.service.ts
│           ├── provas/             provas-lista.component.ts, prova-form.component.ts, provas.service.ts
│           ├── notas/              notas-prova.component.ts, notas.service.ts
│           ├── processos/          processos-lista.component.ts, processo-form.component.ts,
│           │                       processo-detalhe.component.ts, processos.service.ts,
│           │                       classificacao.service.ts
│           ├── bolsas/             bolsas-processo.component.ts, bolsas.service.ts
│           ├── usuarios/           usuarios-lista.component.ts, usuarios.service.ts
│           └── nao-autorizado/     nao-autorizado.component.ts
```

Cada componente tem `.ts`, `.html` e `.css` no mesmo diretório, e um `.spec.ts`. Não há NgModules.

**Responsabilidade de cada tipo**

| Tipo | Papel |
|---|---|
| `*.service.ts` (features) | Chamadas HTTP de um domínio, tipadas pelos modelos de `shared/modelos`. Não guarda estado de tela. |
| `auth.service.ts` | Login, logout, perfil atual. Expõe o usuário com `signal`. |
| `auth.guard.ts` | Exige sessão. Sem sessão, redireciona para `/login`. |
| `perfil.guard.ts` | Lê `data: { perfis: [...] }` da rota. Sem perfil, redireciona para `/nao-autorizado`. |
| `auth.interceptor.ts` | Adiciona `Authorization: Bearer`. |
| `erro.interceptor.ts` | 401 limpa a sessão e vai para `/login`. Demais erros seguem para o componente com o `ErroResposta`. |
| `mensagens-erro.ts` | Mapa código -> mensagem em português. É o único lugar com texto de erro. |

**Regra de exibição por perfil:** o componente de classificação e de bolsas mostra as ações de escrita (gerar classificação, conceder bolsa) só para ADMIN e SECRETARIA. Para CONSULTA, esconde as ações. A coluna de matrícula só aparece se a resposta trouxer o campo (a API decide).

## 4. Docker Compose (MySQL)

**Local e justificativa:** `backend/docker-compose.yml` (ADR-007). Só o backend usa o banco.

**Conteúdo esperado (definição, não código de aplicação):**

| Item | Valor |
|---|---|
| Serviço | `mysql` |
| Imagem | `mysql:8.0` |
| Banco | `pedemeia` |
| Usuário da aplicação | definido por `MYSQL_USER`, senha por `MYSQL_PASSWORD` |
| Root | senha por `MYSQL_ROOT_PASSWORD` |
| Porta | `3306:3306` |
| Charset | `utf8mb4` (`--character-set-server=utf8mb4`) |
| Volume | nomeado, para manter os dados entre execuções |
| Healthcheck | `mysqladmin ping` |
| Variáveis | arquivo `backend/.env` local, fora do git. Cada dev cria o seu. |

Credenciais não são versionadas.

## 5. Rotas e guards do frontend

| Rota | Componente | Perfis (`perfil.guard`) |
|---|---|---|
| `/login` | `LoginComponent` | público |
| `/` | redireciona para `/processos-seletivos` | autenticado |
| `/alunos` | `AlunosListaComponent` | ADMIN, SECRETARIA |
| `/alunos/novo` | `AlunoFormComponent` | ADMIN, SECRETARIA |
| `/periodos-letivos` | `PeriodosLetivosComponent` | ADMIN, SECRETARIA |
| `/periodos-letivos/:periodoId/matriculas` | `MatriculasComponent` | ADMIN, SECRETARIA |
| `/provas` | `ProvasListaComponent` | ADMIN, SECRETARIA |
| `/provas/nova` | `ProvaFormComponent` | ADMIN, SECRETARIA |
| `/provas/:provaId/notas` | `NotasProvaComponent` | ADMIN, SECRETARIA |
| `/processos-seletivos` | `ProcessosListaComponent` | ADMIN, SECRETARIA, CONSULTA |
| `/processos-seletivos/novo` | `ProcessoFormComponent` | ADMIN, SECRETARIA |
| `/processos-seletivos/:processoId` | `ProcessoDetalheComponent` (classificação e bolsas) | ADMIN, SECRETARIA, CONSULTA |
| `/usuarios` | `UsuariosListaComponent` | ADMIN |
| `/nao-autorizado` | `NaoAutorizadoComponent` | autenticado |
| `**` | redireciona para `/` | - |

Todas as rotas, exceto `/login`, usam `authGuard` e `perfilGuard`. Carregamento lazy com `loadComponent`.

**Telas e requisitos:** MVP-RNF05 (375 px) vale para alunos, notas, classificação e bolsas (MVP-CA31).

## 6. Comandos

Pré-requisitos: JDK 17, Maven via wrapper (não precisa instalar Maven), Docker ativo, Node LTS e npm.

### Backend

| Ação | Comando (a partir de `backend/`) |
|---|---|
| Subir o MySQL de desenvolvimento | `docker compose up -d` |
| Parar o MySQL | `docker compose down` (use `-v` só para apagar os dados) |
| Rodar em desenvolvimento | `./mvnw quarkus:dev` (Windows: `mvnw.cmd quarkus:dev`). Porta 8080. |
| Testes | `./mvnw test`. Inclui os testes de API, que sobem MySQL 8.0 via DevServices. Requer Docker. |
| Build | `./mvnw package`. Pacote em `target/quarkus-app/`. Para build rápido, `./mvnw package -DskipTests`. |

### Frontend

| Ação | Comando (a partir de `frontend/`) |
|---|---|
| Instalar dependências | `npm ci` |
| Rodar em desenvolvimento | `npm start`. Porta 4200. O proxy (`proxy.conf.json`) encaminha `/api` para `localhost:8080`. |
| Testes | `npm test -- --watch=false` (o runner é o do CLI Angular instalado) |
| Build | `npm run build`. Saída em `dist/`. |

## 7. Ordem de execução para os devs

1. Subir o MySQL (`backend/docker-compose.yml`).
2. Criar o backend com as extensões de ADR-001, copiar o V1 e aplicar com o `quarkus:dev`.
3. Implementar os módulos na ordem de dependência da seção 3 de `02-arquitetura.md`.
4. Criar o frontend e implementar as rotas da seção 5 contra o contrato.
5. Os testes de API (seção 2) validam cada MVP-CA.

Enquanto houver dúvida de contrato, a decisão vai para o arquiteto, e o contrato é atualizado antes do código.

## 8. Itens que o dev não deve versionar

- `backend/.env`, `target/`, `node_modules/`, `dist/`.
- Qualquer conta pessoal ou senha real. Contas de teste seguem `test@IA_<finalidade>` (D18).
