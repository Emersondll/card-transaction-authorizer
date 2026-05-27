# Card Transaction Authorizer — Hexagonal Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor card-transaction-authorizer to Hexagonal Architecture with SOLID/KISS principles, Lombok/MapStruct, and ≥80% JUnit test coverage.

**Architecture:** Domain layer (pure Java: models, ports, services) decoupled from infrastructure via port interfaces. Application layer holds DTOs and MapStruct mapper. Infrastructure layer has Spring Data repositories, adapters, REST controller, and exception handler.

**Tech Stack:** Spring Boot 3.3.1, Java 22, MongoDB, Lombok, MapStruct 1.6.0, JUnit 5, Mockito, JaCoCo

---

## File Map

### Create — Domain
- `src/main/java/com/caju/transactionauthorizer/domain/model/CategoryCode.java`
- `src/main/java/com/caju/transactionauthorizer/domain/model/AuthorizationStatus.java`
- `src/main/java/com/caju/transactionauthorizer/domain/model/Balance.java`
- `src/main/java/com/caju/transactionauthorizer/domain/model/Merchant.java`
- `src/main/java/com/caju/transactionauthorizer/domain/model/MerchantCategory.java`
- `src/main/java/com/caju/transactionauthorizer/domain/port/in/AuthorizeTransactionPort.java`
- `src/main/java/com/caju/transactionauthorizer/domain/port/out/BalanceRepositoryPort.java`
- `src/main/java/com/caju/transactionauthorizer/domain/port/out/MerchantRepositoryPort.java`
- `src/main/java/com/caju/transactionauthorizer/domain/port/out/MerchantCategoryRepositoryPort.java`
- `src/main/java/com/caju/transactionauthorizer/domain/port/out/TransactionRepositoryPort.java`
- `src/main/java/com/caju/transactionauthorizer/domain/service/BalanceDeductionService.java`
- `src/main/java/com/caju/transactionauthorizer/domain/service/TransactionAuthorizerService.java`

### Create — Application
- `src/main/java/com/caju/transactionauthorizer/application/dto/TransactionRequest.java`
- `src/main/java/com/caju/transactionauthorizer/application/dto/TransactionResponse.java`
- `src/main/java/com/caju/transactionauthorizer/application/mapper/TransactionMapper.java`

### Create — Infrastructure
- `src/main/java/com/caju/transactionauthorizer/infrastructure/web/TransactionController.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/document/BalanceDocument.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/document/MerchantDocument.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/document/MerchantCategoryDocument.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/document/TransactionDocument.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/repository/BalanceMongoRepository.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/repository/MerchantMongoRepository.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/repository/MerchantCategoryMongoRepository.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/repository/TransactionMongoRepository.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/BalanceRepositoryAdapter.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/MerchantRepositoryAdapter.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/MerchantCategoryRepositoryAdapter.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/TransactionRepositoryAdapter.java`
- `src/main/java/com/caju/transactionauthorizer/infrastructure/exception/GlobalExceptionHandler.java`

### Create — Tests
- `src/test/java/com/caju/transactionauthorizer/domain/service/BalanceDeductionServiceTest.java`
- `src/test/java/com/caju/transactionauthorizer/domain/service/TransactionAuthorizerServiceTest.java`
- `src/test/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/BalanceRepositoryAdapterTest.java`
- `src/test/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/MerchantRepositoryAdapterTest.java`
- `src/test/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/MerchantCategoryRepositoryAdapterTest.java`
- `src/test/java/com/caju/transactionauthorizer/infrastructure/exception/GlobalExceptionHandlerTest.java`

### Modify
- `pom.xml`
- `README.md`

### Delete (Task 13)
All old packages: `controller/`, `document/`, `enums/`, `model/`, `repository/`, `service/`
Old tests: `service/impl/TransactionServiceImplTest.java`, `service/impl/TestConstants.java`, `ApplicationTest.java`

---

### Task 1: Update pom.xml

**Files:** Modify `pom.xml`

- [ ] **Add dependencies and plugins**

Replace the `<dependencies>` and `<build>` sections:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.6.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.6.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.12</version>
            <executions>
                <execution>
                    <goals><goal>prepare-agent</goal></goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals><goal>report</goal></goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals><goal>check</goal></goals>
                    <configuration>
                        <excludes>
                            <exclude>com/caju/transactionauthorizer/Application.class</exclude>
                            <exclude>com/caju/transactionauthorizer/application/mapper/*Impl.class</exclude>
                        </excludes>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

- [ ] **Commit**
```bash
git add pom.xml
git commit -m "build: add Lombok, MapStruct, validation, JaCoCo to pom.xml"
```

---

### Task 2: Domain Enums

**Files:** Create `domain/model/CategoryCode.java`, `domain/model/AuthorizationStatus.java`

- [ ] **Create CategoryCode.java**
```java
package com.caju.transactionauthorizer.domain.model;

public enum CategoryCode {
    FOOD, MEAL, CASH
}
```

- [ ] **Create AuthorizationStatus.java**
```java
package com.caju.transactionauthorizer.domain.model;

public enum AuthorizationStatus {
    APPROVED("00"),
    INSUFFICIENT_FUNDS("51"),
    PROCESSING_ERROR("07");

    private final String code;

    AuthorizationStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
```

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/domain/
git commit -m "feat: add domain enums CategoryCode and AuthorizationStatus"
```

---

### Task 3: Domain Models

**Files:** Create `Balance.java`, `Merchant.java`, `MerchantCategory.java`

- [ ] **Create Balance.java** (mutable, contains deduction logic)
```java
package com.caju.transactionauthorizer.domain.model;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class Balance {

    private final String id;
    private final String account;
    private BigDecimal food;
    private BigDecimal meal;
    private BigDecimal cash;
    private final Long version;

    public Balance(String id, String account, BigDecimal food, BigDecimal meal, BigDecimal cash, Long version) {
        this.id = id;
        this.account = account;
        this.food = food;
        this.meal = meal;
        this.cash = cash;
        this.version = version;
    }

    public boolean deductFrom(CategoryCode category, BigDecimal amount) {
        return switch (category) {
            case FOOD -> tryDeduct(food, amount, v -> food = v);
            case MEAL -> tryDeduct(meal, amount, v -> meal = v);
            case CASH -> tryDeduct(cash, amount, v -> cash = v);
        };
    }

    public boolean deductCash(BigDecimal amount) {
        return tryDeduct(cash, amount, v -> cash = v);
    }

    private boolean tryDeduct(BigDecimal balance, BigDecimal amount, Consumer<BigDecimal> setter) {
        BigDecimal result = balance.subtract(amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        setter.accept(result);
        return true;
    }

    public String getId() { return id; }
    public String getAccount() { return account; }
    public BigDecimal getFood() { return food; }
    public BigDecimal getMeal() { return meal; }
    public BigDecimal getCash() { return cash; }
    public Long getVersion() { return version; }
}
```

- [ ] **Create Merchant.java**
```java
package com.caju.transactionauthorizer.domain.model;

public record Merchant(String id, String name, String mcc) {}
```

- [ ] **Create MerchantCategory.java**
```java
package com.caju.transactionauthorizer.domain.model;

public record MerchantCategory(String id, String code, CategoryCode category) {}
```

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/domain/model/
git commit -m "feat: add domain models Balance, Merchant, MerchantCategory"
```

---

### Task 4: Domain Ports

**Files:** Create all port interfaces

- [ ] **Create AuthorizeTransactionPort.java**
```java
package com.caju.transactionauthorizer.domain.port.in;

import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import java.math.BigDecimal;

public interface AuthorizeTransactionPort {
    AuthorizationStatus authorize(String accountId, BigDecimal amount, String mcc, String merchant);
}
```

- [ ] **Create BalanceRepositoryPort.java**
```java
package com.caju.transactionauthorizer.domain.port.out;

import com.caju.transactionauthorizer.domain.model.Balance;
import java.util.Optional;

public interface BalanceRepositoryPort {
    Optional<Balance> findByAccount(String accountId);
    void save(Balance balance);
}
```

- [ ] **Create MerchantRepositoryPort.java**
```java
package com.caju.transactionauthorizer.domain.port.out;

import com.caju.transactionauthorizer.domain.model.Merchant;
import java.util.Optional;

public interface MerchantRepositoryPort {
    Optional<Merchant> findByName(String name);
}
```

- [ ] **Create MerchantCategoryRepositoryPort.java**
```java
package com.caju.transactionauthorizer.domain.port.out;

import com.caju.transactionauthorizer.domain.model.MerchantCategory;
import java.util.Optional;

public interface MerchantCategoryRepositoryPort {
    Optional<MerchantCategory> findByCode(String code);
}
```

- [ ] **Create TransactionRepositoryPort.java**
```java
package com.caju.transactionauthorizer.domain.port.out;

import java.math.BigDecimal;

public interface TransactionRepositoryPort {
    void save(String accountId, BigDecimal amount, String merchant, String mcc);
}
```

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/domain/port/
git commit -m "feat: add domain ports (in/out interfaces)"
```

---

### Task 5: BalanceDeductionService + Tests (TDD)

**Files:** `domain/service/BalanceDeductionService.java`, `domain/service/BalanceDeductionServiceTest.java`

- [ ] **Write failing test first**

Create `src/test/java/com/caju/transactionauthorizer/domain/service/BalanceDeductionServiceTest.java`:
```java
package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.domain.model.CategoryCode;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BalanceDeductionServiceTest {

    private final BalanceDeductionService service = new BalanceDeductionService();

    @Test
    void deduct_foodWithSufficientFood_deductsFoodReturnsTrue() {
        Balance balance = new Balance("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertTrue(service.deduct(balance, CategoryCode.FOOD, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getFood());
    }

    @Test
    void deduct_mealWithSufficientMeal_deductsMealReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, new BigDecimal("100.00"), BigDecimal.ZERO, 1L);
        assertTrue(service.deduct(balance, CategoryCode.MEAL, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getMeal());
    }

    @Test
    void deduct_cashWithSufficientCash_deductsCashReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        assertTrue(service.deduct(balance, CategoryCode.CASH, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getCash());
    }

    @Test
    void deduct_foodInsufficientButCashSufficient_fallbackToCashReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        assertTrue(service.deduct(balance, CategoryCode.FOOD, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getCash());
        assertEquals(BigDecimal.ZERO, balance.getFood());
    }

    @Test
    void deduct_mealInsufficientButCashSufficient_fallbackToCashReturnsTrue() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        assertTrue(service.deduct(balance, CategoryCode.MEAL, new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50.00"), balance.getCash());
    }

    @Test
    void deduct_allInsufficientNoFallback_returnsFalse() {
        Balance balance = new Balance("id", "acc", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertFalse(service.deduct(balance, CategoryCode.FOOD, new BigDecimal("50.00")));
    }

    @Test
    void deduct_cashCategoryInsufficientNoCashFallback_returnsFalse() {
        Balance balance = new Balance("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        assertFalse(service.deduct(balance, CategoryCode.CASH, new BigDecimal("50.00")));
    }
}
```

- [ ] **Run test — expect compilation failure**
```bash
cd "card-transaction-authorizer" && ./mvnw test -pl . -Dtest=BalanceDeductionServiceTest 2>&1 | tail -20
```
Expected: `COMPILATION ERROR` — BalanceDeductionService does not exist yet.

- [ ] **Create BalanceDeductionService.java**
```java
package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.domain.model.CategoryCode;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class BalanceDeductionService {

    public boolean deduct(Balance balance, CategoryCode category, BigDecimal amount) {
        if (balance.deductFrom(category, amount)) {
            return true;
        }
        if (category != CategoryCode.CASH) {
            return balance.deductCash(amount);
        }
        return false;
    }
}
```

- [ ] **Run test — expect PASS**
```bash
./mvnw test -Dtest=BalanceDeductionServiceTest -DskipITs 2>&1 | tail -10
```
Expected: `BUILD SUCCESS`, 7 tests pass.

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/domain/service/BalanceDeductionService.java \
        src/test/java/com/caju/transactionauthorizer/domain/service/BalanceDeductionServiceTest.java
git commit -m "feat: add BalanceDeductionService with fallback logic and unit tests"
```

---

### Task 6: TransactionAuthorizerService + Tests (TDD)

**Files:** `domain/service/TransactionAuthorizerService.java`, `domain/service/TransactionAuthorizerServiceTest.java`

- [ ] **Write failing test first**

Create `src/test/java/com/caju/transactionauthorizer/domain/service/TransactionAuthorizerServiceTest.java`:
```java
package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.*;
import com.caju.transactionauthorizer.domain.port.out.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionAuthorizerServiceTest {

    @Mock BalanceRepositoryPort balancePort;
    @Mock MerchantRepositoryPort merchantPort;
    @Mock MerchantCategoryRepositoryPort categoryPort;
    @Mock TransactionRepositoryPort transactionPort;
    @Mock BalanceDeductionService deductionService;

    @InjectMocks TransactionAuthorizerService service;

    private static final String ACCOUNT_ID = "123";
    private static final BigDecimal AMOUNT = new BigDecimal("50.00");
    private static final String MCC = "5411";
    private static final String MERCHANT = "PADARIA DO ZE";

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void authorize_accountNotFound_returnsProcessingError() {
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.empty());
        assertEquals(AuthorizationStatus.PROCESSING_ERROR,
                service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
    }

    @Test
    void authorize_deductionSucceeds_returnsApproved() {
        Balance balance = new Balance("id", ACCOUNT_ID, new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(categoryPort.findByCode(MCC)).thenReturn(Optional.of(new MerchantCategory("id", MCC, CategoryCode.FOOD)));
        when(deductionService.deduct(balance, CategoryCode.FOOD, AMOUNT)).thenReturn(true);

        assertEquals(AuthorizationStatus.APPROVED, service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
        verify(balancePort).save(balance);
        verify(transactionPort).save(ACCOUNT_ID, AMOUNT, MERCHANT, MCC);
    }

    @Test
    void authorize_deductionFails_returnsInsufficientFunds() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(categoryPort.findByCode(MCC)).thenReturn(Optional.of(new MerchantCategory("id", MCC, CategoryCode.FOOD)));
        when(deductionService.deduct(balance, CategoryCode.FOOD, AMOUNT)).thenReturn(false);

        assertEquals(AuthorizationStatus.INSUFFICIENT_FUNDS,
                service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
        verify(balancePort, never()).save(any());
    }

    @Test
    void authorize_merchantOverridePresentUsesMerchantMcc() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, new BigDecimal("100.00"), BigDecimal.ZERO, 1L);
        Merchant merchant = new Merchant("mid", MERCHANT, "5812");
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.of(merchant));
        when(categoryPort.findByCode("5812")).thenReturn(Optional.of(new MerchantCategory("id", "5812", CategoryCode.MEAL)));
        when(deductionService.deduct(balance, CategoryCode.MEAL, AMOUNT)).thenReturn(true);

        service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT);
        verify(deductionService).deduct(balance, CategoryCode.MEAL, AMOUNT);
    }

    @Test
    void authorize_nullMcc_usesCashCategory() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(deductionService.deduct(balance, CategoryCode.CASH, AMOUNT)).thenReturn(true);

        service.authorize(ACCOUNT_ID, AMOUNT, null, MERCHANT);
        verify(deductionService).deduct(balance, CategoryCode.CASH, AMOUNT);
    }

    @Test
    void authorize_unknownMcc_usesCashCategory() {
        Balance balance = new Balance("id", ACCOUNT_ID, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), 1L);
        when(balancePort.findByAccount(ACCOUNT_ID)).thenReturn(Optional.of(balance));
        when(merchantPort.findByName(MERCHANT)).thenReturn(Optional.empty());
        when(categoryPort.findByCode("9999")).thenReturn(Optional.empty());
        when(deductionService.deduct(balance, CategoryCode.CASH, AMOUNT)).thenReturn(true);

        service.authorize(ACCOUNT_ID, AMOUNT, "9999", MERCHANT);
        verify(deductionService).deduct(balance, CategoryCode.CASH, AMOUNT);
    }

    @Test
    void authorize_exceptionThrown_returnsProcessingError() {
        when(balancePort.findByAccount(any())).thenThrow(new RuntimeException("DB error"));
        assertEquals(AuthorizationStatus.PROCESSING_ERROR,
                service.authorize(ACCOUNT_ID, AMOUNT, MCC, MERCHANT));
    }
}
```

- [ ] **Create TransactionAuthorizerService.java**
```java
package com.caju.transactionauthorizer.domain.service;

import com.caju.transactionauthorizer.domain.model.*;
import com.caju.transactionauthorizer.domain.port.in.AuthorizeTransactionPort;
import com.caju.transactionauthorizer.domain.port.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransactionAuthorizerService implements AuthorizeTransactionPort {

    private final BalanceRepositoryPort balancePort;
    private final MerchantRepositoryPort merchantPort;
    private final MerchantCategoryRepositoryPort categoryPort;
    private final TransactionRepositoryPort transactionPort;
    private final BalanceDeductionService deductionService;

    public TransactionAuthorizerService(
            BalanceRepositoryPort balancePort,
            MerchantRepositoryPort merchantPort,
            MerchantCategoryRepositoryPort categoryPort,
            TransactionRepositoryPort transactionPort,
            BalanceDeductionService deductionService) {
        this.balancePort = balancePort;
        this.merchantPort = merchantPort;
        this.categoryPort = categoryPort;
        this.transactionPort = transactionPort;
        this.deductionService = deductionService;
    }

    @Override
    @Transactional
    public AuthorizationStatus authorize(String accountId, BigDecimal amount, String mcc, String merchant) {
        try {
            Optional<Balance> balanceOpt = balancePort.findByAccount(accountId);
            if (balanceOpt.isEmpty()) {
                return AuthorizationStatus.PROCESSING_ERROR;
            }

            Balance balance = balanceOpt.get();
            String resolvedMcc = resolveMcc(merchant, mcc);
            CategoryCode category = resolveCategory(resolvedMcc);

            boolean deducted = deductionService.deduct(balance, category, amount);
            if (!deducted) {
                return AuthorizationStatus.INSUFFICIENT_FUNDS;
            }

            balancePort.save(balance);
            transactionPort.save(accountId, amount, merchant, resolvedMcc);
            return AuthorizationStatus.APPROVED;

        } catch (Exception e) {
            return AuthorizationStatus.PROCESSING_ERROR;
        }
    }

    private String resolveMcc(String merchant, String mcc) {
        return merchantPort.findByName(merchant)
                .map(Merchant::mcc)
                .orElse(mcc);
    }

    private CategoryCode resolveCategory(String mcc) {
        if (mcc == null) {
            return CategoryCode.CASH;
        }
        return categoryPort.findByCode(mcc)
                .map(MerchantCategory::category)
                .orElse(CategoryCode.CASH);
    }
}
```

- [ ] **Run tests**
```bash
./mvnw test -Dtest="BalanceDeductionServiceTest,TransactionAuthorizerServiceTest" -DskipITs 2>&1 | tail -10
```
Expected: `BUILD SUCCESS`, 14 tests pass.

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/domain/service/TransactionAuthorizerService.java \
        src/test/java/com/caju/transactionauthorizer/domain/service/TransactionAuthorizerServiceTest.java
git commit -m "feat: add TransactionAuthorizerService with unit tests"
```

---

### Task 7: Application DTOs + Mapper

**Files:** `application/dto/TransactionRequest.java`, `application/dto/TransactionResponse.java`, `application/mapper/TransactionMapper.java`

- [ ] **Create TransactionRequest.java**
```java
package com.caju.transactionauthorizer.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransactionRequest(
        @JsonProperty("account") @NotBlank String account,
        @JsonProperty("totalAmount") @NotNull @Positive BigDecimal totalAmount,
        @JsonProperty("mcc") String mcc,
        @JsonProperty("merchant") @NotBlank String merchant
) {}
```

- [ ] **Create TransactionResponse.java**
```java
package com.caju.transactionauthorizer.application.dto;

public record TransactionResponse(String code) {}
```

- [ ] **Create TransactionMapper.java**
```java
package com.caju.transactionauthorizer.application.mapper;

import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    default TransactionResponse toResponse(AuthorizationStatus status) {
        return new TransactionResponse(status.getCode());
    }
}
```

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/application/
git commit -m "feat: add application DTOs and MapStruct mapper"
```

---

### Task 8: Infrastructure Persistence Documents + Repositories

**Files:** All four documents and four repositories

- [ ] **Create BalanceDocument.java** (Lombok — mutable with @Version)
```java
package com.caju.transactionauthorizer.infrastructure.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "balance")
public class BalanceDocument {
    @Id private String id;
    private String account;
    private BigDecimal food;
    private BigDecimal meal;
    private BigDecimal cash;
    @Version private Long version;
}
```

- [ ] **Create MerchantDocument.java** (record)
```java
package com.caju.transactionauthorizer.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant")
public record MerchantDocument(@Id String id, String name, String mcc) {}
```

- [ ] **Create MerchantCategoryDocument.java** (record — field named `description` matches MongoDB)
```java
package com.caju.transactionauthorizer.infrastructure.persistence.document;

import com.caju.transactionauthorizer.domain.model.CategoryCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mcc")
public record MerchantCategoryDocument(@Id String id, String code, CategoryCode description) {}
```

- [ ] **Create TransactionDocument.java** (record)
```java
package com.caju.transactionauthorizer.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "transaction")
public record TransactionDocument(
        @Id String id,
        String accountId,
        BigDecimal amount,
        String merchant,
        String mcc,
        Instant timestamp
) {}
```

- [ ] **Create BalanceMongoRepository.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.BalanceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BalanceMongoRepository extends MongoRepository<BalanceDocument, String> {
    Optional<BalanceDocument> findByAccount(String account);
}
```

- [ ] **Create MerchantMongoRepository.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MerchantMongoRepository extends MongoRepository<MerchantDocument, String> {
    Optional<MerchantDocument> findByName(String name);
}
```

- [ ] **Create MerchantCategoryMongoRepository.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantCategoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MerchantCategoryMongoRepository extends MongoRepository<MerchantCategoryDocument, String> {
    Optional<MerchantCategoryDocument> findByCode(String code);
}
```

- [ ] **Create TransactionMongoRepository.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.repository;

import com.caju.transactionauthorizer.infrastructure.persistence.document.TransactionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionMongoRepository extends MongoRepository<TransactionDocument, String> {}
```

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/
git commit -m "feat: add infrastructure documents (Lombok/records) and Spring Data repositories"
```

---

### Task 9: Adapters + Tests

**Files:** Four adapters and three test classes

- [ ] **Create BalanceRepositoryAdapter.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.domain.port.out.BalanceRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.document.BalanceDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.BalanceMongoRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class BalanceRepositoryAdapter implements BalanceRepositoryPort {

    private final BalanceMongoRepository repository;

    public BalanceRepositoryAdapter(BalanceMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Balance> findByAccount(String accountId) {
        return repository.findByAccount(accountId).map(this::toBalance);
    }

    @Override
    public void save(Balance balance) {
        repository.save(toDocument(balance));
    }

    private Balance toBalance(BalanceDocument doc) {
        return new Balance(doc.getId(), doc.getAccount(), doc.getFood(), doc.getMeal(), doc.getCash(), doc.getVersion());
    }

    private BalanceDocument toDocument(Balance balance) {
        return new BalanceDocument(balance.getId(), balance.getAccount(), balance.getFood(), balance.getMeal(), balance.getCash(), balance.getVersion());
    }
}
```

- [ ] **Create BalanceRepositoryAdapterTest.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Balance;
import com.caju.transactionauthorizer.infrastructure.persistence.document.BalanceDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.BalanceMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BalanceRepositoryAdapterTest {

    @Mock BalanceMongoRepository repository;
    @InjectMocks BalanceRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByAccount_found_returnsMappedBalance() {
        BalanceDocument doc = new BalanceDocument("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        when(repository.findByAccount("acc")).thenReturn(Optional.of(doc));

        Optional<Balance> result = adapter.findByAccount("acc");

        assertTrue(result.isPresent());
        assertEquals("acc", result.get().getAccount());
        assertEquals(new BigDecimal("100.00"), result.get().getFood());
    }

    @Test
    void findByAccount_notFound_returnsEmpty() {
        when(repository.findByAccount("acc")).thenReturn(Optional.empty());
        assertTrue(adapter.findByAccount("acc").isEmpty());
    }

    @Test
    void save_persistsBalance() {
        Balance balance = new Balance("id", "acc", new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO, 1L);
        adapter.save(balance);
        verify(repository).save(any(BalanceDocument.class));
    }
}
```

- [ ] **Create MerchantRepositoryAdapter.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Merchant;
import com.caju.transactionauthorizer.domain.port.out.MerchantRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantMongoRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class MerchantRepositoryAdapter implements MerchantRepositoryPort {

    private final MerchantMongoRepository repository;

    public MerchantRepositoryAdapter(MerchantMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return repository.findByName(name)
                .map(doc -> new Merchant(doc.id(), doc.name(), doc.mcc()));
    }
}
```

- [ ] **Create MerchantRepositoryAdapterTest.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.Merchant;
import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MerchantRepositoryAdapterTest {

    @Mock MerchantMongoRepository repository;
    @InjectMocks MerchantRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByName_found_returnsMappedMerchant() {
        when(repository.findByName("PADARIA")).thenReturn(Optional.of(new MerchantDocument("id", "PADARIA", "5411")));
        Optional<Merchant> result = adapter.findByName("PADARIA");
        assertTrue(result.isPresent());
        assertEquals("5411", result.get().mcc());
    }

    @Test
    void findByName_notFound_returnsEmpty() {
        when(repository.findByName("PADARIA")).thenReturn(Optional.empty());
        assertTrue(adapter.findByName("PADARIA").isEmpty());
    }
}
```

- [ ] **Create MerchantCategoryRepositoryAdapter.java** (fixes null-check order bug from original)
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.MerchantCategory;
import com.caju.transactionauthorizer.domain.port.out.MerchantCategoryRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantCategoryMongoRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class MerchantCategoryRepositoryAdapter implements MerchantCategoryRepositoryPort {

    private final MerchantCategoryMongoRepository repository;

    public MerchantCategoryRepositoryAdapter(MerchantCategoryMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<MerchantCategory> findByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return repository.findByCode(code)
                .map(doc -> new MerchantCategory(doc.id(), doc.code(), doc.description()));
    }
}
```

- [ ] **Create MerchantCategoryRepositoryAdapterTest.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.model.CategoryCode;
import com.caju.transactionauthorizer.domain.model.MerchantCategory;
import com.caju.transactionauthorizer.infrastructure.persistence.document.MerchantCategoryDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.MerchantCategoryMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MerchantCategoryRepositoryAdapterTest {

    @Mock MerchantCategoryMongoRepository repository;
    @InjectMocks MerchantCategoryRepositoryAdapter adapter;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByCode_nullCode_returnsEmptyWithoutQueryingDb() {
        Optional<MerchantCategory> result = adapter.findByCode(null);
        assertTrue(result.isEmpty());
        verify(repository, never()).findByCode(any());
    }

    @Test
    void findByCode_found_returnsMappedCategory() {
        when(repository.findByCode("5411")).thenReturn(
                Optional.of(new MerchantCategoryDocument("id", "5411", CategoryCode.FOOD)));
        Optional<MerchantCategory> result = adapter.findByCode("5411");
        assertTrue(result.isPresent());
        assertEquals(CategoryCode.FOOD, result.get().category());
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(repository.findByCode("9999")).thenReturn(Optional.empty());
        assertTrue(adapter.findByCode("9999").isEmpty());
    }
}
```

- [ ] **Create TransactionRepositoryAdapter.java**
```java
package com.caju.transactionauthorizer.infrastructure.persistence.adapter;

import com.caju.transactionauthorizer.domain.port.out.TransactionRepositoryPort;
import com.caju.transactionauthorizer.infrastructure.persistence.document.TransactionDocument;
import com.caju.transactionauthorizer.infrastructure.persistence.repository.TransactionMongoRepository;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionMongoRepository repository;

    public TransactionRepositoryAdapter(TransactionMongoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(String accountId, BigDecimal amount, String merchant, String mcc) {
        repository.save(new TransactionDocument(
                UUID.randomUUID().toString(), accountId, amount, merchant, mcc, Instant.now()));
    }
}
```

- [ ] **Run adapter tests**
```bash
./mvnw test -Dtest="BalanceRepositoryAdapterTest,MerchantRepositoryAdapterTest,MerchantCategoryRepositoryAdapterTest" -DskipITs 2>&1 | tail -10
```
Expected: `BUILD SUCCESS`, 8 tests pass.

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/infrastructure/persistence/adapter/ \
        src/test/java/com/caju/transactionauthorizer/infrastructure/persistence/
git commit -m "feat: add persistence adapters implementing domain ports, with unit tests"
```

---

### Task 10: Exception Handler + Controller

**Files:** `infrastructure/exception/GlobalExceptionHandler.java`, `infrastructure/web/TransactionController.java`

- [ ] **Create GlobalExceptionHandler.java**
```java
package com.caju.transactionauthorizer.infrastructure.exception;

import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TransactionResponse> handleValidationError(MethodArgumentNotValidException ex) {
        return ResponseEntity.ok(new TransactionResponse(AuthorizationStatus.PROCESSING_ERROR.getCode()));
    }
}
```

- [ ] **Create GlobalExceptionHandlerTest.java**
```java
package com.caju.transactionauthorizer.infrastructure.exception;

import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationError_returnsProcessingErrorCode() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var response = handler.handleValidationError(ex);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("07", response.getBody().code());
    }
}
```

- [ ] **Create TransactionController.java**
```java
package com.caju.transactionauthorizer.infrastructure.web;

import com.caju.transactionauthorizer.application.dto.TransactionRequest;
import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import com.caju.transactionauthorizer.application.mapper.TransactionMapper;
import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import com.caju.transactionauthorizer.domain.port.in.AuthorizeTransactionPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final AuthorizeTransactionPort authorizeTransactionPort;
    private final TransactionMapper transactionMapper;

    public TransactionController(AuthorizeTransactionPort authorizeTransactionPort,
                                 TransactionMapper transactionMapper) {
        this.authorizeTransactionPort = authorizeTransactionPort;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> authorize(@Valid @RequestBody TransactionRequest request) {
        AuthorizationStatus status = authorizeTransactionPort.authorize(
                request.account(), request.totalAmount(), request.mcc(), request.merchant());
        return ResponseEntity.ok(transactionMapper.toResponse(status));
    }
}
```

- [ ] **Run exception handler test**
```bash
./mvnw test -Dtest=GlobalExceptionHandlerTest -DskipITs 2>&1 | tail -10
```
Expected: `BUILD SUCCESS`.

- [ ] **Commit**
```bash
git add src/main/java/com/caju/transactionauthorizer/infrastructure/exception/ \
        src/main/java/com/caju/transactionauthorizer/infrastructure/web/ \
        src/test/java/com/caju/transactionauthorizer/infrastructure/exception/
git commit -m "feat: add GlobalExceptionHandler and TransactionController"
```

---

### Task 11: Delete Old Code

**Files:** Remove all old packages and tests

- [ ] **Delete old source files**
```bash
rm -rf src/main/java/com/caju/transactionauthorizer/controller/
rm -rf src/main/java/com/caju/transactionauthorizer/document/
rm -rf src/main/java/com/caju/transactionauthorizer/enums/
rm -rf src/main/java/com/caju/transactionauthorizer/model/
rm -rf src/main/java/com/caju/transactionauthorizer/repository/
rm -rf src/main/java/com/caju/transactionauthorizer/service/
```

- [ ] **Delete old test files**
```bash
rm -rf src/test/java/com/caju/transactionauthorizer/service/
rm -f src/test/java/com/caju/transactionauthorizer/ApplicationTest.java
```

- [ ] **Full build to verify no broken references**
```bash
./mvnw compile test-compile 2>&1 | tail -20
```
Expected: `BUILD SUCCESS`.

- [ ] **Commit**
```bash
git add -A
git commit -m "refactor: remove old layered packages replaced by hexagonal architecture"
```

---

### Task 12: Full Test Run + Coverage Check

- [ ] **Run all tests with JaCoCo**
```bash
./mvnw test 2>&1 | tail -30
```
Expected: `BUILD SUCCESS`, all tests pass, coverage ≥ 80%.

- [ ] If coverage check fails, identify uncovered lines:
```bash
./mvnw jacoco:report 2>&1 | tail -5
# Then open: target/site/jacoco/index.html
```

---

### Task 13: Update README.md

- [ ] **Rewrite README.md** with sections: Description, Stack, Architecture, How to Run (Docker + Maven), API Usage, Test Coverage.

---

### Task 14: Push Branch

- [ ] **Final commit and push**
```bash
git add README.md
git commit -m "docs: update README with architecture, setup, and API docs"
git push -u origin claude-fix
```
