package com.caju.transactionauthorizer.controller;

import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles transaction requests.
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService service;

    /**
     * Performs a transaction and returns the result code.
     *
     * @param transactionModel the details of the transaction to perform
     * @return the result code of the transaction
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<TransactionCodeModel> performTransaction(final @RequestBody TransactionModel transactionModel) {
        return ResponseEntity.ok().body(service.performTransaction(transactionModel));
    }

}