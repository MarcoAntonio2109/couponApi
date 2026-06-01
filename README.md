# Coupon API

API REST para gerenciamento de cupons promocionais, construida com Spring Boot, Spring MVC, Spring Data JPA e banco H2 em memoria.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- H2 Database
- Springdoc OpenAPI / Swagger UI
- Maven Wrapper
- Docker

## Funcionalidades

- Criar cupom
- Listar cupons com paginacao
- Buscar cupom por codigo
- Atualizar cupom
- Excluir cupom com soft delete
- Validar valor minimo de desconto
- Validar data de expiracao
- Normalizar codigo do cupom para 6 caracteres alfanumericos em uppercase

## Requisitos

- JDK 21
- Maven, ou o Maven Wrapper incluido no projeto
- Docker, opcional

## Como Rodar Localmente

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

A aplicacao sobe por padrao em:

```text
http://localhost:8080
```

## Como Rodar com Docker

```bash
docker compose up --build
```

## Banco de Dados

O projeto usa H2 em memoria com as seguintes configuracoes:

```properties
spring.datasource.url=jdbc:h2:mem:coupondb
spring.datasource.username=sa
spring.datasource.password=
```

Console H2:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:coupondb
```

## Swagger

A documentacao interativa da API fica disponivel em:

```text
http://localhost:8080/swagger-ui.html
```

ou:

```text
http://localhost:8080/swagger-ui/index.html
```

## Endpoints

### Criar Cupom

```http
POST /api/coupons
```

Exemplo de requisicao:

```json
{
  "code": "ABC123",
  "description": "Cupom de desconto",
  "discountValue": 10.0,
  "expirationDate": "2026-12-31",
  "published": true
}
```

Exemplo de resposta:

```json
{
  "id": 1,
  "code": "ABC123",
  "description": "Cupom de desconto",
  "discountValue": 10.0,
  "expirationDate": "2026-12-31",
  "published": true,
  "deleted": false,
  "createdAt": "2026-05-31T22:00:00-03:00"
}
```

### Listar Cupons

```http
GET /api/coupons?page=0&size=10
```

### Buscar Cupom por Codigo

```http
GET /api/coupons/{code}
```

Exemplo:

```http
GET /api/coupons/ABC123
```

### Atualizar Cupom

```http
PUT /api/coupons/{code}
```

Exemplo de requisicao:

```json
{
  "description": "Cupom atualizado",
  "discountValue": 15.0,
  "expirationDate": "2026-12-31",
  "published": true
}
```

### Excluir Cupom

```http
DELETE /api/coupons/{code}
```

A exclusao e logica. O registro permanece no banco com `deleted=true` e `deletedAt` preenchido.

## Regras de Negocio

- O codigo do cupom e normalizado removendo caracteres nao alfanumericos e convertendo para uppercase.
- Apos a normalizacao, o codigo precisa ter exatamente 6 caracteres.
- A data de expiracao nao pode estar no passado.
- O valor minimo de desconto e `0.5`.
- Nao e permitido criar dois cupons com o mesmo codigo.
- Excluir novamente um cupom ja excluido retorna conflito.

## Testes

No Windows:

```powershell
.\mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```

Os testes de integracao cobrem criacao, busca, atualizacao, listagem, exclusao e validacao de data expirada.

## Observacoes de Desenvolvimento

As configuracoes atuais sao voltadas para desenvolvimento local:

- H2 em memoria
- Console H2 habilitado
- Rotas da API liberadas no Spring Security
- CSRF desabilitado
- `spring.jpa.hibernate.ddl-auto=update`
- SQL exibido no console

Para producao, recomenda-se separar configuracoes por perfil, usar um banco persistente, revisar seguranca e substituir `ddl-auto=update` por migracoes versionadas.
