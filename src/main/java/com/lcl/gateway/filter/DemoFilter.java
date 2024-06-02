package com.lcl.gateway.filter;

import com.lcl.gateway.GatewayFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author conglongli
 * @date 2024/6/2 22:39
 */
@Slf4j
@Component("demoFilter")
public class DemoFilter implements GatewayFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        log.info(" =======>>>>> Demo Filter......");
        exchange.getRequest().getHeaders().forEach((k,v) -> log.info("{}_{}", k, v));
        return Mono.empty();
    }
}
