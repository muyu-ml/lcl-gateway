package com.lcl.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author conglongli
 * @date 2024/6/2 19:26
 */
@Component
@Slf4j
public class GatewayPostWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info(" =======>>>>> Lcl Gateway Post Web Filter start......");
        return chain.filter(exchange).doFinally(s -> {
            log.info("post filter");
            exchange.getAttributes().forEach((k,v) -> log.info(k + "_" + v));
        });
    }
}
