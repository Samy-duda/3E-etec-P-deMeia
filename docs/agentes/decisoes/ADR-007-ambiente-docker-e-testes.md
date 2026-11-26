# ADR-007 - Ambiente Docker e testes

**Status:** Proposta (aguarda aprovação da arquitetura, D15)
**Data:** 2026-10-16

## Contexto
D01 diz que o MySQL 8.0 roda em container, e que não há MySQL nativo. Falta definir onde fica o arquivo do Docker Compose e como os testes de integração obtêm o banco. Os devs precisam dos dois para trabalhar sem perguntar.

## Decisão
- Arquivo: `backend/docker-compose.yml`. Motivo: só o backend usa o MySQL. O frontend não depende dele, então o compose fica junto de quem o consome. Se no futuro houver orquestração da stack completa, o arquivo sobe para a raiz sem alteração de conteúdo.
- Serviço único `mysql` com imagem `mysql:8.0`, banco `pedemeia`, usuário de aplicação com senha vinda de variável de ambiente (`backend/.env`, fora do git), porta `3306`, healthcheck e volume nomeado.
- Desenvolvimento (`%dev`): a aplicação conecta em `localhost:3306` ao MySQL do compose. Subir com `docker compose up -d` dentro de `backend/`.
- Testes (`%test`): Quarkus DevServices sobe um MySQL 8.0 via Testcontainers, sem passo manual. Exige Docker ativo. Não depende do compose.
- O V1 é aplicado pelo Flyway nos dois casos.

## Alternativas
- Compose na raiz do projeto: descartado por ora. Adiciona um arquivo na raiz para um único serviço. Pode ser movido depois.
- Testes apontando para o compose: descartado. O teste passaria a depender de um passo manual e de um estado do banco compartilhado, o que quebra a independência dos testes.
- H2 nos testes: descartado. A stack é MySQL (D01). H2 esconde diferença de dialeto, de CHECK e de collation, que são justamente os pontos de integridade do V1.
- MySQL instalado na máquina: descartado por D01.

## Consequências
- Testes exigem Docker. Documentado em `05-estrutura-pastas.md`.
- Credenciais do compose ficam fora do repositório. Cada dev cria o seu `backend/.env` local.
- Se a porta 3306 estiver em uso na máquina, troca-se a porta do host no compose e na URL de desenvolvimento. Não há alteração de código.
