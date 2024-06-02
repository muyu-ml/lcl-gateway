package com.lcl.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway Plugin Chain
 * @Author conglongli
 * @date 2024/6/2 21:49
 */
public interface GatewayPluginChain {
    Mono<Void> handle(ServerWebExchange exchange);
}
