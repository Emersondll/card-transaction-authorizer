# Card Transaction Authorizer

Autorizador de transações de cartão de crédito/débito baseado em MCC (Merchant Category Code), implementando os quatro níveis do desafio técnico Caju.

---

## Desafios Implementados

| Nível | Descrição | Status |
|-------|-----------|--------|
| **L1 — Autorizador simples** | Usa o MCC para mapear a transação para uma categoria de benefícios e debita o saldo correspondente | IMPLEMENTADO |
| **L2 — Autorizador com fallback** | Se o saldo da categoria primária for insuficiente, debita o saldo CASH | IMPLEMENTADO |
| **L3 — Dependente do comerciante** | O nome do comerciante tem precedência sobre o MCC — override via tabela `merchant` no MongoDB | IMPLEMENTADO |
| **L4 — Transações simultâneas** | Optimistic Locking via `@Version` no `BalanceDocument` — detalhes abaixo | IMPLEMENTADO |

### L4 — Discussão: Transações Simultâneas

**Problema:** Dois débitos simultâneos no mesmo cartão podem resultar em saldo negativo se ambos lerem o saldo antes de qualquer um salvar.

**Solução implementada — Optimistic Locking:**

O campo `@Version` em `BalanceDocument` é gerenciado automaticamente pelo Spring Data MongoDB. A cada `save()`, o MongoDB verifica se a versão do documento ainda corresponde à que foi lida. Se outra transação já incrementou a versão, o Spring lança `OptimisticLockingFailureException`, que é capturada pela camada de serviço e retorna código `"07"` — sinalizando ao cliente para retentar.

```
T1 lê balance (version=1) ─────────────┐
T2 lê balance (version=1) ─────┐       │
T2 salva (version → 2)  ───────┘       │
T1 tenta salvar (version=1) ───────────┘ → OptimisticLockingException → "07"
```

Vantagens sobre pessimistic locking:
- Sem bloqueio de banco — alta concorrência com baixo overhead
- Processamento < 100ms mantido (sem espera por lock release)
- Adequado para conflitos raros em transações do mesmo cartão

---

## Regras de MCC

| MCC | Categoria | Saldo Debitado |
|-----|-----------|----------------|
| `5411`, `5412` | FOOD | Saldo alimentação |
| `5811`, `5812` | MEAL | Saldo refeição |
| Qualquer outro | CASH | Saldo cash |

Se o saldo da categoria mapeada for insuficiente → tenta CASH (L2 fallback).

---

## Stack Tecnológica

| Tecnologia | Versão | Papel |
|-----------|--------|-------|
| Java | 22 | Linguagem principal (Records, Switch Expressions) |
| Spring Boot | 3.3.1 | Framework principal |
| Spring Data MongoDB | 3.3.x | Persistência + Optimistic Locking |
| Spring Validation | 3.3.x | Validação de entrada (`@Valid`) |
| Lombok | latest | `@Data`, `@Builder`, `@Slf4j` nos documents |
| JUnit 5 + Mockito | latest | Testes unitários |
| JaCoCo | 0.8.11 | Cobertura de código (mínimo 80%) |
| MongoDB | 4.2+ | Banco de dados NoSQL |
| Docker & Docker Compose | latest | Infraestrutura local |

---

## Arquitetura

```
src/main/java/com/caju/transactionauthorizer/
├── controller/
│   ├── TransactionController.java      # POST /transaction
│   └── GlobalExceptionHandler.java     # @RestControllerAdvice
├── service/
│   ├── TransactionService.java         # Contrato do fluxo principal
│   ├── BalanceService.java             # Contrato de saldo
│   ├── MerchantService.java            # Contrato de override de comerciante
│   ├── MerchantCategoryCodesService.java # Contrato de resolução de MCC
│   └── impl/
│       ├── TransactionServiceImpl.java  # L1+L2+L3+L4
│       ├── BalanceServiceImpl.java
│       ├── MerchantServiceImpl.java
│       └── MerchantCategoryCodesServiceImpl.java
├── document/                            # Entidades MongoDB
│   ├── BalanceDocument.java             # @Version → optimistic locking
│   ├── TransactionDocument.java
│   ├── MerchantDocument.java
│   └── MerchantCategoryCodesDocument.java
├── model/
│   ├── TransactionModel.java            # Record de entrada (request)
│   └── TransactionCodeModel.java        # Record de saída (response)
├── repository/                          # Spring Data MongoDB
├── enums/
│   ├── CategoryCodeName.java            # FOOD | MEAL | CASH
│   └── TransactionStatusCode.java       # 00 | 51 | 07
└── Application.java
```

**Fluxo de autorização:**
```
POST /transaction
  → TransactionController
    → TransactionServiceImpl.performTransaction()
      → MerchantService (L3: override de MCC por comerciante)
      → MerchantCategoryCodesService (L1: resolve categoria)
      → BalanceService.findByAccount()
      → updateWalletBalance() com fallback CASH (L2)
      → BalanceService.save() com @Version check (L4)
      → TransactionRepository.save() (audit record)
  ← { "code": "00" | "51" | "07" }
```

---

## Como Executar Localmente

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 22 instalado (`java -version`)
- Maven 3.9+ instalado

### 1. Subir o MongoDB

```bash
cd src/main/resources/docker
docker-compose up -d
```

O MongoDB sobe na porta `27017` com banco `miniautorizador`, usuário `user` e senha `password` (via script de inicialização).

### 2. Configurar credenciais (opcional)

O `application.yml` usa variável de ambiente com fallback local:

```bash
export MONGO_URI=mongodb://user:password@localhost:27017/miniautorizador
```

### 3. Compilar e executar

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### 4. Executar os testes

```bash
./mvnw test
```

Relatório de cobertura JaCoCo:

```bash
./mvnw test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

---

## API

### Autorizar transação

```http
POST /transaction
Content-Type: application/json

{
  "account": "123",
  "totalAmount": 100.00,
  "mcc": "5811",
  "merchant": "PADARIA DO ZE               SAO PAULO BR"
}
```

**Resposta:** `200 OK` (sempre)

```json
{ "code": "00" }
```

| Código | Significado |
|--------|-------------|
| `"00"` | Transação aprovada — saldo debitado |
| `"51"` | Saldo insuficiente em todos os buckets |
| `"07"` | Erro de processamento (conta não encontrada, conflito concorrente, erro inesperado) |

### Exemplo cURL

```bash
curl -s -X POST http://localhost:8080/transaction \
  -H 'Content-Type: application/json' \
  -d '{
    "account": "123",
    "totalAmount": 100.00,
    "mcc": "5811",
    "merchant": "PADARIA DO ZE               SAO PAULO BR"
  }'
```

---

## Desenvolvedor

| Campo | Informação |
|-------|-----------|
| **Nome** | Emerson Lima |
| **GitHub** | [github.com/Emersondll](https://github.com/Emersondll) |
| **LinkedIn** | [linkedin.com/in/stackdeveloper](https://www.linkedin.com/in/stackdeveloper/) |

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/stackdeveloper/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Emersondll)
