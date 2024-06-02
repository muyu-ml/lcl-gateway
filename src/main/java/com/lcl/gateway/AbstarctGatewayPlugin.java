package com.lcl.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Abstarct Gateway Plugin
 * @Author conglongli
 * @date 2024/6/2 20:16
 */
@Slf4j
public abstract class AbstarctGatewayPlugin implements GatewayPlugin{
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain) {
        boolean isSupported = support(exchange);
        log.info("========>>>> plugin[{}], support = {}", getName(), isSupported);
        return isSupported ? doHandle(exchange, chain) : chain.handle(exchange);
    }

    @Override
    public boolean support(ServerWebExchange exchange){
        return doSupport(exchange);
    }

    public abstract Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain);

    public abstract boolean doSupport(ServerWebExchange exchange);
}
