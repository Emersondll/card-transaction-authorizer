package com.caju.transactionauthorizer.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Servlet filter that injects a correlation ID into the MDC (Mapped Diagnostic Context)
 * for every incoming HTTP request.
 *
 * <p>The correlation ID is taken from the {@code X-Correlation-Id} request header if present;
 * otherwise a new UUID is generated. The ID is propagated as an MDC variable named
 * {@code correlationId}, making it available in all log statements for the duration of
 * the request. The response also echoes the ID back via the same header.</p>
 *
 * <p>This ensures every log line in a request's execution path shares a common identifier,
 * enabling end-to-end tracing across log aggregation systems (ELK, Datadog, Grafana Loki).</p>
 *
 * @author Emerson Lima
 * @version 1.0
 * @since 1.0.0
 * @see org.slf4j.MDC for MDC documentation
 */
@Component
@Slf4j
public class RequestCorrelationFilter extends OncePerRequestFilter {

    /** Header name for the correlation identifier. */
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    /** MDC key used in log pattern as {@code %X{correlationId}}. */
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    /**
     * Resolves or generates a correlation ID, stores it in the MDC,
     * delegates to the filter chain, then clears the MDC.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the remaining filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = resolveCorrelationId(request);

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        log.debug("Request received. method={}, path={}, correlationId={}",
                request.getMethod(), request.getRequestURI(), correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }

    /**
     * Reads the {@code X-Correlation-Id} header from the request,
     * or generates a new UUID if the header is absent or blank.
     *
     * @param request the incoming HTTP request
     * @return a non-null, non-blank correlation ID string
     */
    private String resolveCorrelationId(HttpServletRequest request) {
        String headerValue = request.getHeader(CORRELATION_ID_HEADER);
        if (headerValue != null && !headerValue.isBlank()) {
            return headerValue;
        }
        return UUID.randomUUID().toString();
    }
}
