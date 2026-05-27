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
