package com.lcl.gateway.web.handler;

import com.lcl.gateway.GatewayPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * gateway web handler
 * @Author conglongli
 * @date 2024/6/2 15:09
 */
@Slf4j
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Autowired
    List<GatewayPlugin> plugins;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.info("========>>>> Lcl Gateway Web Handler ......");
        // 如果不存在 plugin，返回默认异常信息
        if(plugins == null || plugins.isEmpty()) {
            String mock = """
                    {
                      "result": "no plugin"
                    }
                    """;
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }
        // 循环所有的 plugin 处理
        for(GatewayPlugin plugin : plugins){
            // 如果当前plugin支持本次请求，则调用 plugin handle 处理
            if(plugin.support(exchange)){
                return plugin.handle(exchange);
            }
        }
        String mock = """
                    {
                      "result": "no support plugin"
                    }
                    """;
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
