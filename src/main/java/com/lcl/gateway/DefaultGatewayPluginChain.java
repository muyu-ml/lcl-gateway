package com.lcl.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Default Gateway Plugin Chain
 * @Author conglongli
 * @date 2024/6/2 21:52
 */
public class DefaultGatewayPluginChain implements GatewayPluginChain{

    List<GatewayPlugin> plugins;
    int index = 0;

    public DefaultGatewayPluginChain(List<GatewayPlugin> plugins){
        this.plugins = plugins;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return Mono.defer(() -> {
            if (index < plugins.size()) {
                return plugins.get(index++).handle(exchange, this);
            }
            return Mono.empty();
        });
    }
}
