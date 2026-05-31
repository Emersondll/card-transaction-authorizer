package com.caju.transactionauthorizer.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link RequestCorrelationFilter}.
 *
 * <p>Verifies both branches of correlation ID resolution:
 * header present (reuse) and header absent (generate UUID).</p>
 */
@DisplayName("RequestCorrelationFilter Unit Tests")
class RequestCorrelationFilterTest {

    private RequestCorrelationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RequestCorrelationFilter();
    }

    @Test
    @DisplayName("should reuse X-Correlation-Id from request header when present")
    void shouldReuseCorrelationIdFromHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/transaction");
        request.addHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER, "my-trace-id-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertEquals("my-trace-id-123", response.getHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER));
    }

    @Test
    @DisplayName("should generate a new UUID as correlation ID when header is absent")
    void shouldGenerateCorrelationIdWhenHeaderAbsent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/transaction");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        String correlationId = response.getHeader(RequestCorrelationFilter.CORRELATION_ID_HEADER);
        assertNotNull(correlationId);
        assertEquals(36, correlationId.length()); // UUID format: 8-4-4-4-12
    }
}
