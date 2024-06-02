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
public class GatewayWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info(" =======>>>>> Lcl Gateway Web Filter start......");
        if(exchange.getRequest().getQueryParams().getFirst("mock") == null){
            return chain.filter(exchange);
        }
        String mock = """
                {
                  "result": mock
                }
                """;
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
