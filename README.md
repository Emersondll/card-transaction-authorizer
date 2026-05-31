# Transaction Authorizer

Autorizador de transações de cartão de crédito/débito implementado em Java 22 + Spring Boot 3, cobrindo os quatro níveis do desafio técnico.

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

O `MCC` contém a classificação do estabelecimento. Baseado no seu valor, deve-se decidir qual o saldo será utilizado (na totalidade do valor da transação). Por simplicidade, usa-se a seguinte regra:

- Se o `mcc` for `"5411"` ou `"5412"`, deve-se utilizar o saldo de **FOOD**.
- Se o `mcc` for `"5811"` ou `"5812"`, deve-se utilizar o saldo de **MEAL**.
- Para quaisquer outros valores do `mcc`, deve-se utilizar o saldo de **CASH**.

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

**Solução implementada:** `MerchantCategoryCodesServiceImpl.checkCategory()` resolve a categoria pelo MCC. `TransactionServiceImpl.updateWalletBalance()` debita o bucket correspondente via switch expression.

---

### L2 — Autorizador com Fallback

Para despesas não relacionadas a benefícios, existe a categoria **CASH**. O autorizador com fallback funciona como o simples, com a seguinte diferença:
- Se a MCC não puder ser mapeada para uma categoria de benefícios, **ou** se o saldo da categoria fornecida não for suficiente, verifica o saldo de **CASH** e, se for suficiente, debita esse saldo.

**Solução implementada:** `TransactionServiceImpl.updateBalanceWithFallback()` recebe getters/setters do bucket primário e do CASH via `Supplier`/`Consumer`. Se o primário falhar, tenta o fallback automaticamente.

---

### L3 — Dependente do Comerciante

Às vezes, os MCCs estão incorretos e uma transação deve ser processada levando em consideração também os dados do comerciante. O nome do comerciante tem **maior precedência** sobre o MCC.

Exemplos de nomes que podem ter MCC incorreto na rede:
- `UBER TRIP                   SAO PAULO BR`
- `UBER EATS                   SAO PAULO BR`
- `PAG*JoseDaSilva          RIO DE JANEI BR`
- `PICPAY*BILHETEUNICO           GOIANIA BR`

**Solução implementada:** `TransactionServiceImpl.determineMccCategory()` consulta primeiro a coleção `merchant` pelo nome exato do estabelecimento. Se encontrado, usa o MCC armazenado (override); caso contrário, usa o MCC do payload.

---

### L4 — Questão Aberta: Transações Simultâneas

> Dado que o mesmo cartão de crédito pode ser utilizado em diferentes serviços online, existe uma pequena mas existente probabilidade de ocorrerem duas transações ao mesmo tempo. O que você faria para garantir que apenas uma transação por conta fosse processada em um determinado momento? Esteja ciente de que todas as solicitações de transação são síncronas e devem ser processadas rapidamente (menos de 100ms), ou a transação atingirá o timeout.

**Solução implementada — Optimistic Locking com `@Version`:**

O campo `@Version` em `BalanceDocument` é gerenciado automaticamente pelo Spring Data MongoDB. A cada `save()`, o MongoDB verifica se a versão do documento ainda é a mesma que foi lida. Se outra transação já atualizou o saldo, o Spring lança `OptimisticLockingFailureException`, capturada pelo serviço, que retorna código `"07"`.

```
T1 lê balance (version=1) ─────────────────┐
T2 lê balance (version=1) ─────────┐       │
T2 salva com sucesso (version → 2) ─┘       │
T1 tenta salvar (version=1 ≠ 2) ────────────┘ → OptimisticLockingException → "07"
```

Vantagens sobre pessimistic locking:
- Sem bloqueio de banco — throughput alto com baixa latência
- Processamento < 100ms mantido (sem espera por lock release)
- Adequado para a baixa frequência de conflitos por conta

---

## Solução — Visão Geral

### Stack Tecnológica

| Tecnologia | Versão | Papel |
|-----------|--------|-------|
| Java | 22 | Records, Switch Expressions, Pattern Matching |
| Spring Boot | 3.3.1 | Framework principal |
| Spring Data MongoDB | 3.3.x | Persistência + Optimistic Locking (`@Version`) |
| Spring Validation | 3.3.x | Validação de entrada (`@Valid`, `@NotBlank`) |
| Lombok | latest | `@Data`, `@Builder`, `@Slf4j` nos documents |
| JUnit 5 + Mockito | latest | Testes unitários |
| JaCoCo | 0.8.11 | Cobertura de código (mínimo 80%) |
| MongoDB | 4.2+ | Banco de dados NoSQL |
| Docker & Docker Compose | latest | Infraestrutura local |

### Arquitetura

```
src/main/java/com/caju/transactionauthorizer/
├── controller/
│   ├── TransactionController.java        # POST /transaction
│   └── GlobalExceptionHandler.java       # @RestControllerAdvice
├── service/
│   ├── TransactionService.java           # Contrato do fluxo principal (L1+L2+L3+L4)
│   ├── BalanceService.java
│   ├── MerchantService.java              # Override por comerciante (L3)
│   ├── MerchantCategoryCodesService.java # Resolução de MCC (L1)
│   └── impl/
│       ├── TransactionServiceImpl.java   # Orquestração completa
│       ├── BalanceServiceImpl.java
│       ├── MerchantServiceImpl.java
│       └── MerchantCategoryCodesServiceImpl.java
├── document/                             # Entidades MongoDB
│   ├── BalanceDocument.java              # @Version → optimistic locking (L4)
│   ├── TransactionDocument.java          # Registro de auditoria
│   ├── MerchantDocument.java             # Tabela de override de MCC (L3)
│   └── MerchantCategoryCodesDocument.java
├── model/
│   ├── TransactionModel.java             # Record de entrada (request)
│   └── TransactionCodeModel.java         # Record de saída ("00"/"51"/"07")
├── repository/                           # Spring Data MongoDB
├── enums/
│   ├── CategoryCodeName.java             # FOOD | MEAL | CASH
│   └── TransactionStatusCode.java        # 00 | 51 | 07
└── Application.java
```

### Fluxo de autorização

```
POST /transaction
  └─► TransactionController (@Valid)
        └─► TransactionServiceImpl.performTransaction()
              ├─► MerchantServiceImpl.findByName()       ← L3: override por comerciante
              ├─► MerchantCategoryCodesServiceImpl.checkCategory()  ← L1: resolve MCC → categoria
              ├─► BalanceServiceImpl.findByAccount()
              ├─► updateWalletBalance() + updateBalanceWithFallback()  ← L1 + L2
              ├─► BalanceServiceImpl.save()               ← L4: @Version check
              └─► TransactionRepository.save()            ← audit record
        └─► { "code": "00" | "51" | "07" }
```

---

## Como Executar Localmente

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 22 (`java -version`)
- Maven 3.9+

### 1. Subir o MongoDB

```bash
cd src/main/resources/docker
docker-compose up -d
```

Sobe o MongoDB na porta `27017`. O script `init.js` cria automaticamente o banco `transactionauthorizer` com as coleções e dados de seed necessários.

### 2. Compilar e executar

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

Para usar uma URI diferente de MongoDB, exporte antes de rodar:

```bash
export MONGO_URI=mongodb://user:password@localhost:27017/transactionauthorizer
```

### 3. Executar os testes

```bash
./mvnw test
```

Relatório de cobertura JaCoCo:

```bash
./mvnw test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

---

## Endpoint

### `POST /transaction`

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

**Resposta:** `200 OK` (sempre)

```json
{ "code": "00" }
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
