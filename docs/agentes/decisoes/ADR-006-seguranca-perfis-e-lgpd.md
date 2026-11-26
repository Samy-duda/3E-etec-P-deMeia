# ADR-006 - Segurança, perfis e LGPD mínima

**Status:** Proposta (aguarda aprovação da arquitetura, D15). Perfis são premissa de D02, a confirmar pela instituição.
**Data:** 2026-10-16

## Contexto
CA10 e MVP-RNF01 exigem autenticação e controle por perfil. MVP-RNF02 limita o acesso a dados pessoais. D02 define três perfis (ADMIN, SECRETARIA, CONSULTA). D18 pede contas de teste no padrão `test@IA_<finalidade>`, sem usar conta pessoal.

## Decisão
- Login por JWT (SmallRye JWT). Claims: `sub` (id), `upn` (e-mail), `groups` (perfil), `iat`, `exp`. Expiração de 8 horas.
- Senha com BCrypt (`BcryptUtil` do `quarkus-elytron-security-common`). Senha nunca é devolvida nem logada.
- `quarkus.security.jaxrs.deny-unannotated-endpoints=true`. Cada método tem `@RolesAllowed` conforme `x-perfis` do contrato.
- CONSULTA não acessa `/alunos`, `/periodos-letivos/{id}/matriculas` e `/provas/{id}/notas`. Acessa classificações, bolsas e metadados de processo. Nas classificações e bolsas, a resposta não traz `dataNascimento` nem `matriculaEscolar`. Isso é feito com DTO separado, e não com filtro de campo no front.
- E-mail de login sem validação de formato `@Email`, porque as contas de teste têm `_` no domínio. O campo de login no frontend é `type="text"`.
- Contas de teste (`test@IA_admin`, `test@IA_secretaria`, `test@IA_consulta`) são criadas pela aplicação somente nos perfis `dev` e `test`, se não existirem.
- Primeiro ADMIN de produção: criado a partir das variáveis `PEDEMEIA_ADMIN_EMAIL` e `PEDEMEIA_ADMIN_SENHA`, somente se a tabela USUARIOS estiver vazia.
- Frontend guarda o token em `sessionStorage`. Interceptor adiciona o cabeçalho. 401 limpa a sessão.
- LGPD mínima: coleta só nome, matrícula escolar e data de nascimento (obrigatórios), com e-mail e telefone opcionais. Sem log de dados pessoais. Retenção e exclusão ficam para a instituição (Q19).

## Alternativas
- Sessão de servidor com cookie: descartado. Exige estado no servidor e não casa com o JWT pedido por D02.
- Filtro de campos no frontend para CONSULTA: descartado. Dado sensível não deve sair da API.
- Permissão por recurso (ACL): descartado. Três perfis fixos bastam.
- Hash com Argon2: descartado. BCrypt já vem no ecossistema Quarkus e basta para o MVP.
- Usar `@Email` e criar contas com domínio válido: descartado. D18 fixa o padrão `test@IA_<finalidade>`.

## Consequências
- Mudança de perfil de uma matriz de permissão exige alteração no contrato (`x-perfis`) e nos `@RolesAllowed`. Os dois devem andar juntos.
- Testes de perfil (MVP-CA10) rodam com as três contas de teste.
- `sessionStorage` é vulnerável a XSS se houver HTML não escapado. O Angular escapa por padrão, e isso fica como regra de revisão.
