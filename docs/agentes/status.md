# Status da entrega: MVP Pé-de-Meia (P0)

Atualizado pelo orquestrador. Escopo: PRD seção 9.1, prioridade P0. Detalhes e premissas em `decisoes.md`.

| # | Tarefa | Agente | Situação | Bloqueios / observações |
|---|---|---|---|---|
| 1 | Requisitos e critérios de aceite P0 | analista-requisitos | Concluída (31 CAs em 01-requisitos-mvp.md) | 3 CAs marcadas PROPOSTA (A, B, C), a confirmar |
| 2 | Arquitetura, modelo físico, contrato da API, estrutura de pastas | arquiteto | Concluída (02 a 05, ADR-001 a 007, V1__esquema_mvp.sql) | Propostas P-DER-1 a 5 registradas em decisoes.md. Contrato sem lint OpenAPI |
| 3 | Backend (Quarkus + MySQL) | dev-backend (af6fe6a50bbf17b08) | EM ANDAMENTO no momento da pausa. Resultado ainda não recebido | Sem relatório 06-backend-relatorio.md confirmado. Não disparar outro dev-backend. Ao terminar, registrar build, testes e pendências |
| 4 | Frontend (Angular) | dev-frontend (afff76616faabfcd6) | Concluída: 76 testes OK, build OK, 0 avisos. Relatório em 07-frontend-relatorio.md | Sem verificação em navegador (Chromium ausente). Integração real com API não testada |
| 5 | Casos de teste e execução | qa | NÃO INICIADA (pausa) | Depende de 3 concluída |
| 6 | Revisão final | revisor | NÃO INICIADA (pausa) | Depende de 5 aprovado |
| 7 | Atualização do CLAUDE.md | orquestrador | NÃO INICIADA (pausa) | Depende de 3 e 4. D16 |

## Inconsistências do contrato 04-contrato-api.yaml (apontadas, não corrigidas)

1. `ProcessoSeletivoResposta.status` só aceita `CRIADO`; P-DER-3 prevê `EM_CLASSIFICACAO` e `ENCERRADO`.
2. Perfil CONSULTA lista processos sem acesso a `/periodos-letivos` nem `/provas`. Sugestão: incluir `periodoNome` e `provaTitulo`.
3. Matrículas do período paginadas (máx. 100) e sem busca. Sugestão: filtro por nome ou matrícula.

## Gates humanos pendentes

- Confirmação das propostas PROPOSTA-A, B e C e das premissas P01 a P08 (escopo e arquitetura, D15).
- Gate de merge: ainda não atingido. Nenhum commit foi feito, por restrição.

## Ponto exato de parada

- **Concluído:** itens 1, 2 e 4.
- **Em andamento:** item 3 (backend).
- **Próximo passo, ao retomar:** (a) confirmar o término do dev-backend e ler `docs/agentes/06-backend-relatorio.md` e o resultado de `mvn verify` em `backend/`; (b) acionar o qa com os MVP-CA de `01-requisitos-mvp.md`, sobre backend e frontend; (c) revisor; (d) atualizar o CLAUDE.md com `backend/`, `frontend/` e os comandos de build e teste; (e) pedir confirmação do usuário antes do merge.

## Registro de falhas e devoluções

- Nenhuma tarefa devolvida até a pausa. Pendência de ambiente: Chromium do sistema não encontrado, sem verificação visual.
