package com.glo.lending.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

/**
 * Security configuration using API Key authentication.
 * <p>
 * All requests to {@code /api/**} must include a valid {@code X-API-KEY} header.
 * Actuator and health endpoints are excluded from authentication.
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${app.security.api-key}")
    private String apiKey;

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**", "/health").permitAll()
                        .anyExchange().permitAll()
                )
                .addFilterBefore(apiKeyFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * WebFilter that validates the X-API-KEY header on all /api/** requests.
     */
    private WebFilter apiKeyFilter() {
        return (exchange, chain) -> {
            final String path = exchange.getRequest().getPath().value();
            if (!path.startsWith("/api/")) {
                return chain.filter(exchange);
            }
            final String requestApiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
            if (apiKey.equals(requestApiKey)) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
            final String body = "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid or missing API key\"}";
            final byte[] bytes = body.getBytes();
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
            );
        };
    }
}

