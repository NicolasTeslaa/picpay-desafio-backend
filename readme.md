# Desafio PicPay - API

Documentação rápida do projeto, com foco em como executar, testar e
entender a arquitetura.

## Visão Geral

API de transferências entre carteiras, com validações, autorização
externa e notificação via Kafka.

## Stack

-   Java 25
-   Spring Boot 4
-   Spring Data JDBC
-   H2
-   Kafka (via Docker)

## Estrutura de Pastas

-   `src/main/java/com/picpay/desafio_picpay/` - código da aplicação
-   `src/main/resources/` - configuração e scripts SQL
-   `src/test/java/com/picpay/desafio_picpay/` - testes
-   `docker-compose.yml` - Kafka + API
-   `Dockerfile` - build da aplicação

## Como Rodar Localmente

### 1) Subir Kafka + API via Docker

``` bash
docker compose up --build
```

A API expõe `http://localhost:8080`.

### 2) Rodar fora do Docker (apenas API)

``` bash
./mvnw spring-boot:run
```

Nesse modo, o Kafka precisa estar disponível em `localhost:9092` (ou
ajuste `spring.kafka.bootstrap-servers`).

## H2 Console

-   URL: `http://localhost:8080/h2-console`

### JDBC URL

-   **Docker**: `jdbc:h2:file:/app/data/picpay`
-   **Local**: `jdbc:h2:file:./data/picpay`
-   **Em memória**: `jdbc:h2:mem:picpay`

### Credenciais

-   User: `sa`
-   Password: `1234`

## Endpoints

-   `POST /transaction` - cria uma transação
-   `GET /transaction` - lista transações

## Kafka

-   Tópico: `transaction-notification`
-   Producer: `NotificationProducer`
-   Consumer: `NotificationConsumer`

No Docker, o broker interno costuma ser `broker:29092`. Fora do Docker,
normalmente `localhost:9092`.

## Testes

### Rodar testes

``` bash
./mvnw -q test
```

### Cobertura (JaCoCo)

``` bash
./mvnw -q test
```

Relatório:

-   `target/site/jacoco/index.html`

## Observações Importantes

-   O `schema.sql` define as tabelas para H2.
-   O projeto usa Spring Data JDBC, não JPA.
-   Integrações externas devem ser simuladas/mocadas nos testes para
    evitar dependências reais.
