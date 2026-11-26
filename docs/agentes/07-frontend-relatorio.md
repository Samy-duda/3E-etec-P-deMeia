# 07 - Relatório do frontend (MVP P0)

**Data:** 2026-10-16
**Responsável:** dev-frontend
**Código:** `frontend/` (Angular 22, standalone, sem NgModules)
**Contrato usado:** `docs/agentes/04-contrato-api.yaml` (sem alterações)
**Referências:** `01-requisitos-mvp.md`, `02-arquitetura.md`, `05-estrutura-pastas.md`, `decisoes.md`, ADR-002 e ADR-006

## 1. Versões e ambiente

| Item | Versão instalada |
|---|---|
| Angular (`@angular/core`, `router`, `forms`, `build`, `cli`) | 22.2.3 (última no npm na data) |
| TypeScript | 6.0.3 |
| Test runner | Vitest 5.0.5 (runner padrão do CLI 22), ambiente jsdom 30.1.3 |
| Node / npm | v26.8.1 / 12.0.2 |

Observações de ambiente:
- O Chromium do sistema (`/usr/local/bin/chromium`) é um wrapper para `/usr/bin/chromium`, que não existe. Por isso o runner é o Vitest (sem navegador), e não Karma.
- `npm ci` mostra avisos de `install-scripts` bloqueados (esbuild, lmdb, @parcel/watcher, msgpackr-extract). O build e os testes passaram sem eles, porque o esbuild usa binários opcionais. Se a equipe quiser os scripts, rodar `npm install-scripts approve <pacote>`.

## 2. Comandos e resultados

Executados a partir de `frontend/`:

| Comando | Resultado |
|---|---|
| `npm ci` | Passou (exit 0), instalação limpa a partir do `package-lock.json`. |
| `npm test` | Passou: 20 arquivos de teste, 76 testes, 0 falhas. Script: `ng test --watch=false`. |
| `npm run build` | Passou (exit 0), sem avisos. Bundle inicial 271,95 kB bruto (75,91 kB transferido). Telas em chunks lazy por rota. Saída em `frontend/dist/pedemeia-frontend/`. |

Durante o desenvolvimento, dois problemas foram encontrados e corrigidos:
- `erroInterceptor`: chamava `inject()` dentro do `catchError` (fora do contexto de injeção). Corrigido capturando `AuthService` e `Router` no início do interceptor. O teste de 401 pegou o erro.
- Scaffold inicial: o CLI criou os arquivos em uma pasta `home/` aninhada, por causa de caminho absoluto em `--directory`. A pasta foi removida e o projeto foi gerado de novo com caminho relativo.

## 3. Estrutura entregue

Raiz: `frontend/`. Árvore conforme `05-estrutura-pastas.md`, seção 3.

- `src/app/app.routes.ts`: todas as rotas da seção 5, com `loadComponent` e `canActivate: [authGuard, perfilGuard]` (exceto `/login` e `/`).
- `src/app/app.config.ts`: `provideHttpClient` com `authInterceptor` e `erroInterceptor`.
- `core/auth/`: `perfil.ts` (constantes de perfil), `sessao.service.ts` (sessionStorage, chave `pedemeia.sessao`), `auth.service.ts` (login, logout, signals de usuário e perfil), `auth.guard.ts`, `perfil.guard.ts`, `auth.interceptor.ts`.
- `core/erros/`: `erro.interceptor.ts` (401 limpa a sessão e vai para `/login`; 403 só mostra mensagem), `mensagens-erro.ts` (único lugar com texto de erro; `Record<CodigoErro, string>` garante que todo código tem mensagem).
- `core/api/`: `api-base.ts` (`/api`), `parametros.ts` (monta query params, ignora vazios).
- `shared/modelos/`: tipos de todas as entidades do contrato, `Pagina<T>`, `ErroResposta`, `CodigoErro`.
- `shared/componentes/`: `layout` (menu filtrado por perfil), `mensagem-erro` (código, texto e campos), `paginacao`.
- `shared/validadores/formatos.ts`: só formato (até duas casas decimais). Regra de negócio fica com a API.
- `shared/formatacao/formatacao.ts`: moeda, nota, data e instante em pt-BR.
- `features/`: login, alunos (lista e cadastro), periodos (períodos letivos), matriculas, provas (lista e nova), notas (lançamento e correção), processos (lista, novo e detalhe com classificação e bolsas), usuarios (ADMIN), nao-autorizado.
- Serviços por domínio: `alunos`, `periodos`, `matriculas`, `provas`, `notas`, `processos`, `classificacao`, `bolsas`, `usuarios`. Todos usam só as rotas do contrato. Não há método de cancelamento de bolsa.
- Testes: 20 arquivos `.spec.ts` (serviços, guards, interceptors, erros, sessão, componentes de login e detalhe do processo, utilitários e app).

Decisões técnicas:
- Perfil só ajusta a tela (menu, botões de escrita, colunas de matrícula). A API é a autoridade. Colunas de matrícula e de auditoria aparecem só se a resposta trouxer o campo.
- Erro sempre traduzido pelo `codigo` do `ErroResposta`, nunca pelo texto da API.
- Responsividade: abaixo de 600 px, as tabelas viram cartões empilhados (`data-rotulo`), sem rolagem horizontal por design.
- Script de teste: `ng test --watch=false`. Assim `npm test` roda uma vez, e `npm test -- --watch=false` também funciona.
- Proxy de desenvolvimento: `proxy.conf.json` (`/api` para `localhost:8080`), ligado em `angular.json` no `serve`.
- Login: campo de e-mail `type="text"`, conforme ADR-006.

## 4. Cobertura por critério (MVP-CA)

Legenda: **Teste** = coberto por teste unitário do frontend. **Tela** = implementado na interface, sem teste automatizado. **API** = regra no backend, a tela só exibe o código de erro. **Pendente** = depende do backend ou do navegador.

| Critério | Situação | Como é atendido |
|---|---|---|
| MVP-CA01 Cadastro de aluno válido | Teste (serviço) + Tela | `aluno-form` envia o corpo do contrato; `AlunosService.cadastrar`. |
| MVP-CA02 Consulta por matrícula | Teste (serviço) + Tela | `alunos-lista` filtra por `matriculaEscolar` e `nome`. |
| MVP-CA03 Aluno duplicado | Teste (mapa de erro) + API | `ALUNO_DUPLICADO` vira mensagem fixa; sem alteração de dados na tela. |
| MVP-CA04 Dados obrigatórios | Tela + API | Campos `required` no formulário; a API devolve `campos` em 400. |
| MVP-CA05 Matrícula com ano válido | Teste (serviço) + Tela | `matriculas` envia `anoEscolar`; `MatriculasService.matricular`. |
| MVP-CA06 Ano inválido | Tela + API | Seletor só oferece 1, 2 e 3; a API confere. |
| MVP-CA07 Matrícula duplicada | Teste (mapa) + API | `MATRICULA_DUPLICADA`. |
| MVP-CA08 Cadastro de prova | Teste (serviço) + Tela | `prova-form` e `ProvasService.cadastrar`. |
| MVP-CA09 Nota máxima inválida | Tela + API | `min` e formato de duas casas; a API confere o valor. |
| MVP-CA10 Acesso sem perfil | Teste (guards, componente) + Tela + API | `perfilGuard` (CONSULTA vai para `/nao-autorizado`); `authGuard`; menu filtrado; teste do detalhe do processo: CONSULTA não vê "Gerar classificação". Negação real é da API. |
| MVP-CA11 Registro de nota válida | Teste (serviço) + Tela | `notas-prova` e `NotasService.lancar`. |
| MVP-CA12 Nota fora do intervalo | Tela + API | Campo com `min` e `max`; `NOTA_FORA_DO_INTERVALO` mapeado. |
| MVP-CA13 Nota duplicada | Teste (mapa) + API | `NOTA_DUPLICADA`; a nota existente não muda. |
| MVP-CA14 Aluno não matriculado | Tela + API | Só lista matrículas do período da prova; `ALUNO_NAO_MATRICULADO_NO_PERIODO` mapeado. |
| MVP-CA15 Criação de processo | Teste (serviço) + Tela | `processo-form` (período, depois prova do período). |
| MVP-CA16 Prova sem notas | Tela + API | `SEM_NOTAS_PARA_CLASSIFICACAO` exibido na geração; a API não grava nada. |
| MVP-CA17 Classificação por ano | Teste (serviço) + Tela | Filtro por ano (`anoEscolar`); lista na ordem da API. |
| MVP-CA18 Posições sequenciais | Tela + API | Posição exibida como vem da API; nenhum cálculo no cliente. |
| MVP-CA19 e CA20 Desempate | API (pendente de teste no backend) | Regra no servidor (P01). A tela exibe a posição. |
| MVP-CA21 e CA23 Seleção dos 10 melhores | API | Marcador "Selecionado" pelo campo `selecionado`. |
| MVP-CA22 Empate na fronteira | API | Mesmo caso. |
| MVP-CA24 Concessão em lote | Teste (serviço) + Tela | Botão "Conceder bolsas aos selecionados" (`POST` sem matrícula). |
| MVP-CA25 Valor diferente de R$ 300,00 | Tela (serviço aceita `valorMensal`) + API | A tela não envia valor; `VALOR_BOLSA_INVALIDO` mapeado. Teste de envio de `valorMensal` não escrito. |
| MVP-CA26 Não selecionado sem bolsa | Tela + API | Botão "Conceder bolsa" só para `selecionado`; `ALUNO_NAO_SELECIONADO` mapeado. |
| MVP-CA27 Bolsa não duplicada | Tela + API | Botão some quando o aluno já tem bolsa; `BOLSA_JA_EXISTE` mapeado. |
| MVP-CA28 Consulta de classificação por ano | Teste (serviço) + Tela | Detalhe do processo, filtro por ano. |
| MVP-CA29 Consulta de bolsas | Tela | Lista com valor em R$ 300,00 e situação. |
| MVP-CA30 Cancelamento indisponível | Teste (serviço) + Tela | `BolsasService` não tem método de cancelamento; sem botão. |
| MVP-CA31 Telas em 375 px | Tela (CSS) + **Pendente** | Cartões abaixo de 600 px. Não verificado em navegador (Chromium quebrado). |
| MVP-RNF01 Autenticação e perfis | Teste | Guards, interceptors, sessão, `AuthService`. |
| MVP-RNF02 LGPD mínima | Tela | CONSULTA não recebe nem exibe matrícula, data de nascimento ou contatos (o contrato não envia). Sem log de dados. |
| MVP-RNF05 Responsividade | Tela + **Pendente** | Igual a MVP-CA31. |
| MVP-RNF06 Rastreabilidade | Tela | Exibe "Lançada em" e "Corrigida em" na nota. |

## 5. Pendências

1. **Integração com o backend (pendente).** Em 16/10 a API não responde em `localhost:8080` (pasta `backend/` com `pom.xml` e `target/`, sem processo rodando). Os testes usam `HttpTestingController`, sem chamadas reais. Quando a API subir, rodar a integração de ponta a ponta e conferir os códigos de erro das telas.
2. **Verificação visual e responsiva (MVP-CA31, MVP-RNF05).** Não foi possível rodar navegador: o Chromium do sistema está quebrado. Corrigir o binário e rodar a verificação em 375 px e 1280 px. Sem isso, a rolagem horizontal em 375 px é só uma previsão de CSS.
3. **Cobertura de componentes.** Só login e detalhe do processo têm teste de componente. As demais telas têm teste de serviço, guard e interceptor. Ampliar após a estabilização do contrato.
4. **Validação do PRD e das premissas.** Premissas P01 a P05 e propostas A a C (01-requisitos) continuam sujeitas à confirmação da instituição. O frontend não implementa regra delas.
5. **`backend/` não foi tocado.** A pasta apareceu durante a sessão, criada pelo dev-backend. Não há alterações minhas nela.

## 6. Inconsistências e sugestões para o arquiteto (contrato 04)

Nenhuma alteração foi feita no contrato, conforme a regra da tarefa.

1. **Estados do processo seletivo.** `ProcessoSeletivoResposta.status` aceita só `CRIADO` (Q12). `decisoes.md`, P-DER-3, define também `EM_CLASSIFICACAO` e `ENCERRADO`. Sugestão: alinhar. Se o MVP só tem `CRIADO`, ajustar P-DER-3 e o V1; se tiver mais estados, ampliar o enum do contrato.
2. **CONSULTA vê processos sem nome de período ou prova.** `GET /processos-seletivos` é permitido a CONSULTA, mas devolve só `periodoLetivoId` e `provaId`. CONSULTA não tem acesso a `GET /periodos-letivos` nem a `GET /provas`, então a tela mostra apenas números. Sugestão: incluir `periodoNome` e `provaTitulo` em `ProcessoSeletivoResposta` (sem dado pessoal), ou liberar leitura de `GET /provas/{id}` e `GET /periodos-letivos` a CONSULTA.
3. **Lançamento de notas sem busca de aluno.** `GET /periodos-letivos/{periodoId}/matriculas` é paginado (tamanho máximo 100) e não tem filtro. Com muitas matrículas, a escolha de aluno depende de navegar páginas. Sugestão: aceitar `nome` ou `matriculaEscolar` nesse endpoint (ADMIN e SECRETARIA).
4. **Documentação de comando.** `05-estrutura-pastas.md` (seção 6) indica `npm test -- --watch=false`. A tarefa pede `npm test` sem watch. O script agora é `ng test --watch=false`, e os dois formatos funcionam. Sugestão: registrar isso no 05.
5. **Endpoints não usados.** `GET /auth/me` e `GET /usuarios` (somente a listagem é usada, pela tela de usuários). `GET /auth/me` não é usado porque o usuário vem da resposta do login. Não é inconsistência, apenas registro.

## 7. Arquivos de configuração alterados

- `frontend/angular.json`: `serve.options.proxyConfig`.
- `frontend/package.json`: script `test` como `ng test --watch=false`.
- `frontend/src/index.html`: `lang="pt-BR"` e título "Pé-de-Meia".
- `frontend/proxy.conf.json`: novo.
- `frontend/src/styles.css`: estilo global responsivo.

Nenhum arquivo fora de `frontend/` e `docs/agentes/` foi alterado por mim. Nenhum commit ou publicação foi feito.
