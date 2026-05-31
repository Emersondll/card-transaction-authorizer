package com.caju.transactionauthorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Entry point for the Card Transaction Authorizer application.
 *
 * <p>Swagger UI available at {@code /swagger-ui.html} when the application is running.
 * API docs available at {@code /api-docs}.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Card Transaction Authorizer API",
                version = "1.0.0",
                description = "MCC-based credit/debit card transaction authorizer implementing L1–L4 challenge levels. " +
                        "Always returns HTTP 200 OK; authorization result is in the response body code field.",
                contact = @Contact(
                        name = "Emerson Lima",
                        url = "https://github.com/Emersondll"
                )
        )
)
public class Application {

    /**
     * Application bootstrap.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
