package com.caju.transactionauthorizer.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.caju.transactionauthorizer.enums.TransactionStatusCode;
import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.service.TransactionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc integration tests for {@link TransactionController}.
 *
 * <p>Uses {@code @WebMvcTest} to load only the web layer (controller + filters + handler).
 * The service is mocked to isolate HTTP contract validation from business logic.</p>
 *
 * <p>Verifies:
 * <ul>
 *   <li>HTTP status is always {@code 200 OK} per challenge specification</li>
 *   <li>Response body contains the {@code code} field</li>
 *   <li>Bean Validation returns {@code 400 BAD REQUEST} with field errors</li>
 *   <li>JSON deserialization works for all request fields</li>
 * </ul>
 */
@WebMvcTest(controllers = {TransactionController.class, GlobalExceptionHandler.class})
@DisplayName("TransactionController MockMvc Integration Tests")
class TransactionControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    @DisplayName("POST /transaction should return 200 OK with code 00 when transaction is approved")
    void shouldReturn200WithApprovedCodeOnValidRequest() throws Exception {
        when(transactionService.performTransaction(any()))
                .thenReturn(new TransactionCodeModel(TransactionStatusCode.APPROVED.getCode()));

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "123",
                                  "totalAmount": 100.00,
                                  "mcc": "5811",
                                  "merchant": "PADARIA DO ZE  SAO PAULO BR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"));
    }

    @Test
    @DisplayName("POST /transaction should return 200 OK with code 51 when funds are insufficient")
    void shouldReturn200WithInsufficientFundsCode() throws Exception {
        when(transactionService.performTransaction(any()))
                .thenReturn(new TransactionCodeModel(TransactionStatusCode.INSUFFICIENT_FUNDS.getCode()));

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "123",
                                  "totalAmount": 999.00,
                                  "mcc": "5411",
                                  "merchant": "SUPERMERCADO"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("51"));
    }

    @Test
    @DisplayName("POST /transaction should return 400 BAD REQUEST with field errors when account is blank")
    void shouldReturn400WithFieldErrorsWhenAccountIsBlank() throws Exception {
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "",
                                  "totalAmount": 100.00,
                                  "mcc": "5811",
                                  "merchant": "PADARIA"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.account").exists());
    }

    @Test
    @DisplayName("POST /transaction should return 400 BAD REQUEST when totalAmount is missing")
    void shouldReturn400WhenTotalAmountIsMissing() throws Exception {
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "123",
                                  "mcc": "5811",
                                  "merchant": "PADARIA"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.totalAmount").exists());
    }

    @Test
    @DisplayName("POST /transaction should return 400 BAD REQUEST when merchant is blank")
    void shouldReturn400WhenMerchantIsBlank() throws Exception {
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "account": "123",
                                  "totalAmount": 50.00,
                                  "mcc": "5811",
                                  "merchant": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.merchant").exists());
    }
}
