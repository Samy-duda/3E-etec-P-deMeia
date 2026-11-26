# ADR-001 - Camadas, DTOs e stack do backend

**Status:** Proposta (aguarda aprovação da arquitetura, D15)
**Data:** 2026-10-16

## Contexto
O MVP precisa de API REST com regras de negócio, persistência em MySQL e autenticação por perfil. A stack já foi fixada (D01, D02). Falta definir a divisão interna e o que entra como dependência.

## Decisão
- Camadas: `resource` (JAX-RS), `servico` (regras e transação), `repositorio` (Panache), `entidade` (JPA), `dto` (records do Java 17). O resource nunca acessa repositório.
- Módulos por domínio em `br.etec.pedemeia` (lista em `02-arquitetura.md`, seção 3). Módulo chama outro pelo serviço, nunca pelo repositório.
- Mapeamento manual com métodos estáticos em um `Mapeador` por módulo. Sem MapStruct.
- Entidades não saem da camada de serviço. Respostas usam DTO, inclusive para o perfil CONSULTA (ADR-006).
- JSON em camelCase. Tabelas e colunas seguem o DER (maiúsculas nas tabelas, snake_case nas colunas).
- Dependências do backend: `quarkus-rest-jackson`, `quarkus-hibernate-orm-panache`, `quarkus-jdbc-mysql`, `quarkus-flyway`, `quarkus-hibernate-validator`, `quarkus-smallrye-jwt`, `quarkus-smallrye-jwt-build`, `quarkus-elytron-security-common`, `quarkus-smallrye-openapi`. Testes: `quarkus-junit5`, `rest-assured`.
- Frontend: Angular standalone, rotas com carregamento lazy, `HttpClient` com interceptors, reactive forms, sem biblioteca de estado.

## Alternativas
- MapStruct para mapeamento: descartado. Adiciona processador de anotações sem ganho relevante em um MVP deste tamanho.
- Expor entidades diretamente: descartado. Vazaria campos sensíveis (`senha_hash`) e acoplaria a API ao modelo.
- Spring Boot: descartado pela stack fixada (D01).
- NgRx ou similar no frontend: descartado. Não há estado compartilhado complexo no MVP.

## Consequências
- Mais código de mapeamento, mas localizado e fácil de testar.
- Qualquer mudança de campo exige alteração em três pontos: entidade, DTO e `Mapeador`. Aceitável para o tamanho do MVP.
