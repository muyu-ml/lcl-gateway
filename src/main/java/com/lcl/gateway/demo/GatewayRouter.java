package com.lcl.gateway.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * gateway router
 * @Author conglongli
 * @date 2024/5/31 00:39
 */
@Component
public class GatewayRouter {

    @Autowired
    HelloHandler helloHandler;
    @Autowired
    GatewayHandler gatewayHandler;

    @Bean
    public RouterFunction<?> helloRouterFunction() {
//        return route(GET("/hello"),
//                request -> ServerResponse.ok()
//                        .body(Mono.just("hello gateway"), String.class));
        return route(GET("/hello"), helloHandler::handle);
    }


    @Bean
    public RouterFunction<?> gwRouterFunction() {
        return route(GET("/gwdemo").or(POST("/gwdemo/**")), gatewayHandler::handle);
    }

}
