package com.lcl.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author conglongli
 * @date 2024/5/31 01:15
 */
@Component
public class HelloHandler {




    Mono<ServerResponse> handle(ServerRequest request) {
        String url = "http://localhost:8081/lclrpc";
        String requestJson = """
            {
              "service": "com.lcl.lclrpc.demo.api.UserService",
              "methodSign": "findById@1_int",
              "args": [100]
            }
            """;

        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestJson).retrieve().toEntity(String.class);
//        return ServerResponse.ok().bodyValue("hello spring webflux");
        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(souce -> System.out.println("response:" + souce));
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("lcl.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
