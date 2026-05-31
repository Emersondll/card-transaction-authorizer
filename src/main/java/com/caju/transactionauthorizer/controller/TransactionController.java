package com.caju.transactionauthorizer.controller;

import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caju.transactionauthorizer.model.TransactionCodeModel;
import com.caju.transactionauthorizer.model.TransactionModel;
import com.caju.transactionauthorizer.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for the transaction authorization endpoint.
 *
 * <p>Handles HTTP request validation and response mapping.
 * All business logic is delegated to {@link TransactionService}.</p>
 *
 * <p>Per the challenge specification, the HTTP response is always {@code 200 OK}.
 * The authorization result is communicated via the {@code code} field in the
 * response body ({@code "00"}, {@code "51"}, or {@code "07"}).</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see TransactionService for authorization logic
 */
@RestController
@RequestMapping("/transaction")
@Validated
@Slf4j
@Tag(name = "Transaction", description = "MCC-based card transaction authorization endpoint")
public class TransactionController {

    private final TransactionService service;

    /**
     * Constructor-based dependency injection.
     *
     * @param service the transaction authorization service (non-null)
     * @throws NullPointerException if {@code service} is null
     */
    public TransactionController(TransactionService service) {
        this.service = Objects.requireNonNull(service, "TransactionService cannot be null");
    }

    /**
     * Processes a transaction authorization request.
     *
     * <p>HTTP response is always {@code 200 OK} per challenge spec.
     * Validation failures result in {@code 400 BAD REQUEST} with field-level errors.</p>
     *
     * @param transactionModel the incoming transaction payload (validated via {@code @Valid})
     * @return {@link ResponseEntity} with {@code 200 OK} and the authorization result code
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Authorize a card transaction",
            description = "Evaluates the transaction against the account balance using MCC category rules (L1), " +
                    "CASH fallback (L2), merchant override (L3), and optimistic locking for concurrency (L4). " +
                    "Always returns HTTP 200 OK — the result is in the 'code' field."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authorization processed (check 'code' for result)",
                    content = @Content(schema = @Schema(implementation = TransactionCodeModel.class),
                            examples = {
                                    @ExampleObject(name = "Approved", value = "{\"code\":\"00\"}"),
                                    @ExampleObject(name = "Insufficient Funds", value = "{\"code\":\"51\"}"),
                                    @ExampleObject(name = "Processing Error", value = "{\"code\":\"07\"}")
                            })),
            @ApiResponse(responseCode = "400", description = "Validation failed — missing or invalid request fields",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ValidationErrorResponse.class)))
    })
    public ResponseEntity<TransactionCodeModel> performTransaction(
            @Valid @RequestBody TransactionModel transactionModel) {
        log.info("POST /transaction - account={}, mcc={}, amount={}",
                transactionModel.account(), transactionModel.mcc(), transactionModel.totalAmount());

        TransactionCodeModel result = service.performTransaction(transactionModel);

        log.info("Transaction result. account={}, code={}", transactionModel.account(), result.code());
        return ResponseEntity.ok(result);
    }
}
