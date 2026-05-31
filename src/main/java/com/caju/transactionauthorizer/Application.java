package com.caju.transactionauthorizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Card Transaction Authorizer application.
 *
 * <p>API documentation is available as a standalone OpenAPI specification file at
 * {@code src/main/resources/static/openapi.yaml}, served at {@code /openapi.yaml}
 * and rendered by Swagger UI at {@code /swagger-ui.html}.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@SpringBootApplication
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
