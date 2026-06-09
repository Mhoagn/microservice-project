package com.myproject.api_gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println(">>> GATEWAY FILTER HIT");

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("PATH = " + path);

        String method = exchange.getRequest().getMethod().name();

        // PUBLIC ROUTES
        if (isPublic(path,method)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        System.out.println("TOKEN = " + token);

        boolean valid = jwtService.validateAccessToken(token);
        System.out.println("VALID = " + valid);

        if (!valid) {
            return unauthorized(exchange);
        }

        Long userId = jwtService.extractUserIdFromAccessToken(token);
        String role = jwtService.extractRole(token);

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId.toString())
                .header("X-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    private boolean isPublic(String path, String method) {

        // public GET only
        if (method.equals("GET")) {
            return path.equals("/api/restaurants")
                    || path.matches("/api/restaurants/\\d+/menu-items")
                    || path.matches("/api/restaurants/\\d+")
                    || path.matches("/api/menu-items/\\d+");
        }

        return path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/refresh");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}