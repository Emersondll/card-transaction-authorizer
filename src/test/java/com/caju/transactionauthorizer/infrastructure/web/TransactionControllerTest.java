package com.caju.transactionauthorizer.infrastructure.web;

import com.caju.transactionauthorizer.application.dto.TransactionResponse;
import com.caju.transactionauthorizer.application.mapper.TransactionMapper;
import com.caju.transactionauthorizer.domain.model.AuthorizationStatus;
import com.caju.transactionauthorizer.domain.port.in.AuthorizeTransactionPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AuthorizeTransactionPort authorizeTransactionPort;
    @MockBean TransactionMapper transactionMapper;

    @Test
    void authorize_validRequest_returns200WithApprovedCode() throws Exception {
        when(authorizeTransactionPort.authorize(any(), any(), any(), any()))
                .thenReturn(AuthorizationStatus.APPROVED);
        when(transactionMapper.toResponse(AuthorizationStatus.APPROVED))
                .thenReturn(new TransactionResponse("00"));

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"123\",\"totalAmount\":50.00,\"mcc\":\"5411\",\"merchant\":\"PADARIA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("00"));
    }

    @Test
    void authorize_insufficientFunds_returns200WithCode51() throws Exception {
        when(authorizeTransactionPort.authorize(any(), any(), any(), any()))
                .thenReturn(AuthorizationStatus.INSUFFICIENT_FUNDS);
        when(transactionMapper.toResponse(AuthorizationStatus.INSUFFICIENT_FUNDS))
                .thenReturn(new TransactionResponse("51"));

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"123\",\"totalAmount\":50.00,\"mcc\":\"5411\",\"merchant\":\"PADARIA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("51"));
    }

    @Test
    void authorize_blankAccount_returns200WithProcessingErrorCode() throws Exception {
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"\",\"totalAmount\":50.00,\"mcc\":\"5411\",\"merchant\":\"PADARIA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("07"));
    }

    @Test
    void authorize_negativeTotalAmount_returns200WithProcessingErrorCode() throws Exception {
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"123\",\"totalAmount\":-10.00,\"mcc\":\"5411\",\"merchant\":\"PADARIA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("07"));
    }
}
