# Design Spec — Card Transaction Authorizer Refactor

**Date:** 2026-05-26  
**Branch:** claude-fix  
**Author:** Claude (Sonnet 4.6)

---

## Goal

Refactor `card-transaction-authorizer` to apply Hexagonal Architecture (Ports & Adapters), SOLID principles, KISS, clean naming, 80% JUnit test coverage, and update the README.

---

## Package Structure

```
com.caju.transactionauthorizer/
├── domain/
│   ├── model/
│   │   ├── Balance.java                      ← mutable domain object with business logic
│   │   ├── Merchant.java                     ← immutable record
│   │   └── MerchantCategory.java             ← immutable record
│   ├── port/
│   │   ├── in/
│   │   │   └── AuthorizeTransactionPort.java  ← driving port (use case interface)
│   │   └── out/
│   │       ├── BalanceRepositoryPort.java
│   │       ├── MerchantRepositoryPort.java
│   │       ├── MerchantCategoryRepositoryPort.java
│   │       └── TransactionRepositoryPort.java
│   └── service/
│       ├── TransactionAuthorizerService.java  ← implements AuthorizeTransactionPort
│       └── BalanceDeductionService.java       ← isolated balance deduction logic
├── application/
│   ├── dto/
│   │   ├── TransactionRequest.java            ← record (replaces TransactionModel)
│   │   └── TransactionResponse.java          ← record (replaces TransactionCodeModel)
│   └── mapper/
│       └── TransactionMapper.java             ← MapStruct
├── infrastructure/
│   ├── web/
│   │   └── TransactionController.java
│   ├── persistence/
│   │   ├── document/
│   │   │   ├── BalanceDocument.java           ← Lombok @Data (mutable, @Version)
│   │   │   ├── MerchantDocument.java          ← record
│   │   │   ├── MerchantCategoryDocument.java  ← record
│   │   │   └── TransactionDocument.java       ← record
│   │   ├── repository/
│   │   │   ├── BalanceMongoRepository.java
│   │   │   ├── MerchantMongoRepository.java
│   │   │   ├── MerchantCategoryMongoRepository.java
│   │   │   └── TransactionMongoRepository.java
│   │   └── adapter/
│   │       ├── BalanceRepositoryAdapter.java
│   │       ├── MerchantRepositoryAdapter.java
│   │       ├── MerchantCategoryRepositoryAdapter.java
│   │       └── TransactionRepositoryAdapter.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── AccountNotFoundException.java
│       └── InsufficientFundsException.java
└── config/
    └── MapStructConfig.java
```

---

## Domain Layer

### `Balance` (mutable domain object)
- Fields: `id`, `account`, `food`, `meal`, `cash`, `version`
- Methods:
  - `deductFrom(CategoryCode category, BigDecimal amount): boolean` — primary deduction
  - `deductCash(BigDecimal amount): boolean` — fallback deduction
  - Internal `tryDeduct(BigDecimal current, BigDecimal amount): Optional<BigDecimal>`

### `BalanceDeductionService`
- `deduct(Balance, CategoryCode, BigDecimal): boolean`
- Handles primary → cash fallback logic
- Fully unit-testable without Spring context

### `TransactionAuthorizerService`
- Implements `AuthorizeTransactionPort`
- Constructor-injected with all 4 output ports (DIP compliant)
- Resolves merchant MCC override, resolves category, delegates deduction
- Uses `Optional` — no `null` as control flow

### Enums (in `domain/model/`)
- `CategoryCode` (replaces `CategoryCodeName`)
- `AuthorizationStatus` (replaces `TransactionStatusCode`)

---

## Application Layer

### DTOs (records)
- `TransactionRequest(String account, BigDecimal totalAmount, String mcc, String merchant)`
- `TransactionResponse(String code)`

### Mapper
- `TransactionMapper` — MapStruct, maps Request → domain, domain → Response

---

## Infrastructure Layer

### Adapters
Each implements its domain port and converts Document ↔ domain model:
- `BalanceRepositoryAdapter` → `BalanceRepositoryPort`
- `MerchantRepositoryAdapter` → `MerchantRepositoryPort`
- `MerchantCategoryRepositoryAdapter` → `MerchantCategoryRepositoryPort` (fixes null-check order bug)
- `TransactionRepositoryAdapter` → `TransactionRepositoryPort`

### Controller
- Route: `POST /transaction`
- Response always `200 OK` (removes conflicting `@ResponseStatus(ACCEPTED)`)
- Uses `@Valid` on request body

### Exception Handling
- `GlobalExceptionHandler` (`@ControllerAdvice`)
- `AccountNotFoundException` → `TransactionResponse("07")`
- `MethodArgumentNotValidException` → `TransactionResponse("07")`

---

## Test Strategy (≥80% coverage)

| Test Class | Scenarios |
|---|---|
| `TransactionAuthorizerServiceTest` | approved, insufficient funds, processing error, fallback cash |
| `BalanceDeductionServiceTest` | FOOD/MEAL/CASH categories, fallback, insufficient |
| `MerchantCategoryRepositoryAdapterTest` | null MCC, found, fallback CASH |
| `BalanceRepositoryAdapterTest` | found, not found |
| `MerchantRepositoryAdapterTest` | found, not found |
| `GlobalExceptionHandlerTest` | each exception type |

Existing tests adapted and bug-fixed (constructor argument order).

---

## Changes to pom.xml

- Add Lombok dependency
- Add MapStruct dependency + annotation processor
- Add `spring-boot-starter-validation`
- Add JaCoCo plugin with 80% minimum threshold

---

## README Update

Sections: Description, Stack, Architecture, How to Run (Docker + Maven), API Usage, Test Coverage.
