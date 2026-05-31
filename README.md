# Transaction Authorizer

Autorizador de transações de cartão de crédito/débito implementado em Java 22 + Spring Boot 3, cobrindo os quatro níveis do desafio técnico com qualidade enterprise.

---

## O Desafio

Uma versão simplificada de um transaction payload de cartão de crédito é o seguinte:

```json
{
  "account": "123",
  "totalAmount": 100.00,
  "mcc": "5811",
  "merchant": "PADARIA DO ZE               SAO PAULO BR"
}
```

### Atributos

- **account** — identificador único da conta.
- **totalAmount** — o valor a ser debitado de um saldo.
- **merchant** — o nome do estabelecimento.
- **mcc** — um código numérico de 4 dígitos que classifica os estabelecimentos comerciais de acordo com o tipo de produto vendido ou serviço prestado.

O `MCC` contém a classificação do estabelecimento. Baseado no seu valor, deve-se decidir qual saldo será utilizado:

- Se o `mcc` for `"5411"` ou `"5412"` → saldo de **FOOD**.
- Se o `mcc` for `"5811"` ou `"5812"` → saldo de **MEAL**.
- Para quaisquer outros valores → saldo de **CASH**.

### Respostas possíveis

- `{ "code": "00" }` — transação **aprovada**
- `{ "code": "51" }` — transação **rejeitada** por saldo insuficiente
- `{ "code": "07" }` — qualquer outro problema que impeça o processamento

> O HTTP Status Code é sempre `200 OK`.

---

## Níveis do Desafio

### L1 — Autorizador Simples

O autorizador simples funciona da seguinte forma:
- Recebe a transação
- Usa **apenas** a MCC para mapear a transação para uma categoria de benefícios
- Aprova ou rejeita a transação
- Caso aprovada, o saldo da categoria mapeada é diminuído em `totalAmount`

**Solução:** `MerchantCategoryCodesServiceImpl.checkCategory()` resolve a categoria via `findByCode()`. `TransactionServiceImpl.updateWalletBalance()` debita o bucket correspondente com switch expression Java 22.

---

### L2 — Autorizador com Fallback

O autorizador com fallback funciona como o simples com a seguinte diferença:
- Se a MCC não puder ser mapeada para uma categoria de benefícios, **ou** se o saldo da categoria fornecida não for suficiente, verifica o saldo de **CASH** e, se for suficiente, debita esse saldo.

**Solução:** `TransactionServiceImpl.updateBalanceWithFallback()` recebe getters/setters do bucket primário e do CASH via `Supplier`/`Consumer`. Se o primário falhar, tenta o fallback automaticamente.

---

### L3 — Dependente do Comerciante

Às vezes, os MCCs estão incorretos e uma transação deve ser processada levando em consideração também os dados do comerciante. O nome do comerciante tem **maior precedência** sobre o MCC.

Exemplos:
- `UBER TRIP                   SAO PAULO BR`
- `UBER EATS                   SAO PAULO BR`
- `PAG*JoseDaSilva          RIO DE JANEI BR`
- `PICPAY*BILHETEUNICO           GOIANIA BR`

**Solução:** `TransactionServiceImpl.determineMccCategory()` consulta primeiro a coleção `merchant` pelo nome exato. Se encontrado, usa o MCC armazenado (override); caso contrário, usa o MCC do payload.

---

### L4 — Questão Aberta: Transações Simultâneas

> Dado que o mesmo cartão de crédito pode ser utilizado em diferentes serviços online, existe uma pequena mas existente probabilidade de ocorrerem duas transações ao mesmo tempo. O que você faria para garantir que apenas uma transação por conta fosse processada em um determinado momento? Esteja ciente de que todas as solicitações de transação são síncronas e devem ser processadas rapidamente (menos de 100ms), ou a transação atingirá o timeout.

**Solução — Optimistic Locking com `@Version`:**

O campo `@Version` em `BalanceDocument` é gerenciado automaticamente pelo Spring Data MongoDB. A cada `save()`, o MongoDB verifica se a versão do documento ainda é a mesma que foi lida. Se outra transação já atualizou o saldo, o Spring lança `OptimisticLockingFailureException`, capturada pelo serviço, que retorna código `"07"`.

```
T1 lê balance (version=1) ─────────────────┐
T2 lê balance (version=1) ─────────┐       │
T2 salva com sucesso (version → 2) ─┘       │
T1 tenta salvar (version=1 ≠ 2) ────────────┘ → OptimisticLockingException → "07"
```

Vantagens:
- Sem bloqueio de banco — throughput alto com baixa latência
- Processamento < 100ms mantido (sem espera por lock release)
- Adequado para a baixa frequência de conflitos por conta

---

## Stack Tecnológica

| Tecnologia | Versão | Papel |
|-----------|--------|-------|
| Java | 22 | Records, Switch Expressions, Pattern Matching |
| Spring Boot | 3.3.1 | Framework principal |
| Spring Data MongoDB | 3.3.x | Persistência + Optimistic Locking (`@Version`) |
| Spring Validation | 3.3.x | Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`) |
| Spring Actuator | 3.3.x | Health checks, métricas (`/actuator/health`, `/actuator/prometheus`) |
| Micrometer Prometheus | latest | Métricas customizadas de negócio exportadas para Prometheus |
| Resilience4j | 2.2.0 | Circuit Breaker para chamadas ao MongoDB |
| springdoc-openapi | 2.5.0 | Swagger UI (`/swagger-ui.html`) e OpenAPI spec (`/api-docs`) |
| Lombok | latest | `@Data`, `@Builder`, `@Slf4j` nos documents |
| JUnit 5 + Mockito | latest | Testes unitários e MockMvc |
| JaCoCo | 0.8.11 | Cobertura de código (mínimo 80% — atual: 99%+) |
| MongoDB | 4.2+ | Banco de dados NoSQL com índices otimizados |
| Docker & Docker Compose | latest | Infraestrutura local e stack de observabilidade |

---

## Arquitetura

```
card-transaction-authorizer/
├── Dockerfile                          # Multi-stage build (Java 22 JRE)
├── docker/
│   ├── docker-compose.yml              # MongoDB apenas (desenvolvimento)
│   ├── docker-compose-observability.yml # App + MongoDB + Prometheus + Grafana
│   └── prometheus.yml                  # Configuração de scrape do Actuator
├── src/main/java/com/caju/transactionauthorizer/
│   ├── Application.java                # @SpringBootApplication + @OpenAPIDefinition
│   ├── controller/
│   │   ├── TransactionController.java  # POST /transaction — @Tag @Operation @ApiResponse
│   │   └── GlobalExceptionHandler.java # @RestControllerAdvice — 400/500 com Map de erros
│   ├── filter/
│   │   └── RequestCorrelationFilter.java # MDC + X-Correlation-Id em todos os logs
│   ├── service/
│   │   ├── TransactionService.java     # Contrato (L1+L2+L3+L4)
│   │   ├── BalanceService.java
│   │   ├── MerchantService.java        # Override por comerciante (L3)
│   │   ├── MerchantCategoryCodesService.java
│   │   └── impl/
│   │       ├── TransactionServiceImpl.java  # @CircuitBreaker + MeterRegistry
│   │       ├── BalanceServiceImpl.java      # @Transactional
│   │       ├── MerchantServiceImpl.java
│   │       └── MerchantCategoryCodesServiceImpl.java
│   ├── document/
│   │   ├── BalanceDocument.java        # @Version + @Indexed(unique=true) em account
│   │   ├── TransactionDocument.java    # Registro de auditoria
│   │   ├── MerchantDocument.java       # @Indexed(unique=true) em name
│   │   └── MerchantCategoryCodesDocument.java
│   ├── model/
│   │   ├── TransactionModel.java       # Record request com @NotBlank @NotNull @Positive
│   │   └── TransactionCodeModel.java   # Record response
│   ├── repository/                     # @Repository — Spring Data MongoDB
│   └── enums/
│       ├── CategoryCodeName.java       # FOOD | MEAL | CASH
│       └── TransactionStatusCode.java  # 00 | 51 | 07
└── src/main/resources/
    ├── application.yml                 # Actuator, Resilience4j, springdoc, Prometheus
    └── logback-spring.xml              # Log pattern com [correlationId], perfis dev/prod
```

### Fluxo de Autorização

```
POST /transaction
  └─► RequestCorrelationFilter (MDC: X-Correlation-Id)
        └─► TransactionController (@Validated @Valid)
              └─► TransactionServiceImpl (@CircuitBreaker: mongoCircuitBreaker)
                    ├─► MerchantServiceImpl.findByName()      ← L3: override por comerciante
                    ├─► MerchantCategoryCodesServiceImpl.checkCategory()  ← L1: MCC → categoria
                    ├─► BalanceServiceImpl.findByAccount()
                    ├─► updateWalletBalance() + updateBalanceWithFallback()  ← L1 + L2
                    ├─► BalanceServiceImpl.save() @Transactional  ← L4: @Version check
                    ├─► TransactionRepository.save()              ← audit record
                    └─► MeterRegistry.counter(approved|rejected|error)  ← Spec 05 métricas
              └─► { "code": "00" | "51" | "07" }
```

### Métricas Customizadas (Prometheus)

| Métrica | Tags | Descrição |
|---------|------|-----------|
| `transaction.approved` | `category=FOOD\|MEAL\|CASH` | Transações aprovadas por categoria |
| `transaction.rejected` | `reason=insufficient_funds` | Transações rejeitadas por saldo |
| `transaction.error` | — | Transações com erro de processamento |

Acesse em: `http://localhost:8080/actuator/prometheus`

---

## Como Executar Localmente

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 22 (`java -version`)
- Maven 3.9+

### Opção 1 — Desenvolvimento (MongoDB apenas)

```bash
# Subir MongoDB
cd docker
docker-compose up -d

# Rodar aplicação
./mvnw spring-boot:run
```

### Opção 2 — Stack Completa (App + MongoDB + Prometheus + Grafana)

```bash
cd docker
docker-compose -f docker-compose-observability.yml up -d
```

| Serviço | URL |
|---------|-----|
| API | http://localhost:8080/transaction |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI Spec | http://localhost:8080/api-docs |
| Actuator Health | http://localhost:8080/actuator/health |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 (admin/admin) |

### Opção 3 — Docker image

```bash
# Build da imagem (multi-stage)
docker build -t card-transaction-authorizer:1.0.0 .

# Rodar com MongoDB externo
docker run -p 8080:8080 \
  -e MONGO_URI=mongodb://user:password@localhost:27017/miniautorizador \
  card-transaction-authorizer:1.0.0
```

### Executar os Testes

```bash
# Testes unitários + MockMvc + cobertura JaCoCo
./mvnw clean test jacoco:report

# Relatório: target/site/jacoco/index.html
```

---

## Endpoint

### `POST /transaction`

Documentação interativa disponível em: **http://localhost:8080/swagger-ui.html**

```bash
curl -s -X POST http://localhost:8080/transaction \
  -H 'Content-Type: application/json' \
  -H 'X-Correlation-Id: my-trace-id-123' \
  -d '{
    "account": "123",
    "totalAmount": 100.00,
    "mcc": "5811",
    "merchant": "PADARIA DO ZE               SAO PAULO BR"
  }'
```

**Resposta: `200 OK`** (sempre)

```json
{ "code": "00" }
```

#### Resposta de validação (400 BAD REQUEST)

```json
{
  "code": "VALIDATION_FAILED",
  "message": "Input validation failed",
  "errors": {
    "account": "Account is required",
    "totalAmount": "Total amount must be positive"
  },
  "timestamp": "2026-05-31T17:00:00"
}
```

---

## Cobertura de Testes

| Tipo de Teste | Arquivo(s) | Cenários |
|--------------|-----------|---------|
| Unidade — serviço | `TransactionServiceImplTest` | Aprovação, saldo insuficiente, conta inexistente, OptimisticLocking, exceção genérica |
| Unidade — serviço | `BalanceServiceImplTest` | findByAccount found/empty, save |
| Unidade — serviço | `MerchantServiceImplTest` | found, empty |
| Unidade — serviço | `MerchantCategoryCodesServiceImplTest` | findByCode, checkCategory found/empty |
| **Parametrizado** | `MccCategoryResolutionTest` | **8 MCCs + null** via `@ParameterizedTest @CsvSource` |
| Unidade — controller | `TransactionControllerTest` | 3 códigos de resposta (00/51/07) |
| Unidade — handler | `GlobalExceptionHandlerTest` | Validação 400, exceção genérica 500 |
| **MockMvc** | `TransactionControllerMvcTest` | **5 testes HTTP** via `@WebMvcTest` (200/400) |

**Cobertura total: 99%+ de linhas** (JaCoCo, mínimo configurado: 80%)

---

## Desenvolvedor

| Campo | Informação |
|-------|-----------|
| **Nome** | Emerson Lima |
| **GitHub** | [github.com/Emersondll](https://github.com/Emersondll) |
| **LinkedIn** | [linkedin.com/in/stackdeveloper](https://www.linkedin.com/in/stackdeveloper/) |

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/stackdeveloper/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Emersondll)
