package com.lcl.gateway;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author conglongli
 * @date 2024/6/2 22:36
 */
public interface GatewayFilter {
    Mono<Void> filter(ServerWebExchange exchange);
}
