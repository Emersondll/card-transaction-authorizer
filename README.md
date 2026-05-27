# Card Transaction Authorizer

Serviço HTTP de autorização de transações de cartão de crédito, implementado com Spring Boot e arquitetura Hexagonal (Ports & Adapters).

---

## Stack

- Java 21
- Spring Boot 3.3.1
- MongoDB
- Lombok
- MapStruct 1.6.0
- JUnit 5 + Mockito
- JaCoCo (cobertura de testes)

---

## Arquitetura

O projeto segue o padrão **Hexagonal Architecture** (Ports & Adapters):

```
domain/
  model/          — Objetos de domínio puros (Balance, Merchant, enums)
  port/in/        — Porta de entrada: AuthorizeTransactionPort (use case)
  port/out/       — Portas de saída: repositórios como interfaces
  service/        — Serviços de domínio: TransactionAuthorizerService, BalanceDeductionService

application/
  dto/            — TransactionRequest, TransactionResponse (records)
  mapper/         — TransactionMapper (MapStruct)

infrastructure/
  web/            — TransactionController (REST)
  persistence/
    document/     — Documentos MongoDB (BalanceDocument com Lombok, demais como records)
    repository/   — Spring Data MongoDB repositories
    adapter/      — Adaptadores que implementam as portas de saída
  exception/      — GlobalExceptionHandler (@RestControllerAdvice)
```

### Fluxo de uma transação

```
POST /transaction
  → TransactionController
  → AuthorizeTransactionPort.authorize()
  → TransactionAuthorizerService
      → resolve MCC do comerciante (merchant override)
      → resolve categoria (FOOD / MEAL / CASH)
      → BalanceDeductionService.deduct() [com fallback para CASH]
      → salva saldo atualizado
      → salva registro da transação
  ← { "code": "00" | "51" | "07" }
```

---

## Regras de autorização

| MCC | Categoria |
|-----|-----------|
| `5411`, `5412` | FOOD |
| `5811`, `5812` | MEAL |
| Qualquer outro | CASH |

- **L1:** Usa apenas o MCC para selecionar a categoria de saldo.
- **L2:** Se o saldo da categoria for insuficiente, tenta débito no saldo CASH (fallback).
- **L3:** O nome do comerciante tem precedência sobre o MCC — se o comerciante estiver cadastrado, o MCC dele sobrescreve o da requisição.

### Respostas possíveis

| Code | Significado |
|------|-------------|
| `00` | Transação aprovada |
| `51` | Saldo insuficiente |
| `07` | Erro de processamento |

---

## Como executar

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose

### 1. Subir o MongoDB

```bash
cd src/main/resources/docker
docker-compose up -d
```

### 2. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## API

### POST /transaction

Autoriza uma transação de cartão.

**Request:**
```json
{
  "account": "123",
  "totalAmount": 100.00,
  "mcc": "5811",
  "merchant": "PADARIA DO ZE               SAO PAULO BR"
}
```

**Response (sempre HTTP 200):**
```json
{ "code": "00" }
```

**Exemplos de uso:**
```bash
# Transação aprovada
curl -X POST http://localhost:8080/transaction \
  -H "Content-Type: application/json" \
  -d '{"account":"123","totalAmount":50.00,"mcc":"5411","merchant":"SUPERMERCADO"}'

# Saldo insuficiente
curl -X POST http://localhost:8080/transaction \
  -H "Content-Type: application/json" \
  -d '{"account":"123","totalAmount":9999.00,"mcc":"5411","merchant":"SUPERMERCADO"}'
```

---

## Testes

```bash
# Executar todos os testes com relatório de cobertura
./mvnw clean test

# Relatório de cobertura (gerado em target/site/jacoco/index.html)
./mvnw jacoco:report
```

- **36 testes unitários**
- **97% de cobertura de linhas** (JaCoCo)
- Testes sem dependência de MongoDB (Mockito)

---

## Concorrência (L4 — Questão Aberta)

Para garantir que apenas uma transação por conta seja processada simultaneamente, esta implementação usa:

- **`@Version` no `BalanceDocument`** — Spring Data MongoDB aplica Optimistic Locking. Se duas transações tentam atualizar o mesmo saldo simultaneamente, a segunda falha com `OptimisticLockingFailureException`, retornando `{ "code": "07" }`.
- **`@Transactional` no serviço** — garante atomicidade da operação de débito + persistência.

Para cenários de alta concorrência com requisito de latência < 100ms, uma alternativa complementar seria usar **MongoDB transactions com retry automático** ou **Redis com locks distribuídos** (SETNX + TTL) para serializar transações por `accountId`.
