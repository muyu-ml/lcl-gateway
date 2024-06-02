package com.lcl.gateway;

import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author conglongli
 * @date 2024/5/31 01:15
 */
@Component
@Slf4j
public class GatewayHandler {

    @Autowired
    RegistryCenter rc;
    Loadbalancer<InstanceMeta> loadbalancer = new RoundRibonLoadbalancer<>();

    Mono<ServerResponse> handle(ServerRequest request) {
        log.info("=====>{}", request.path());
        // 通过请求路径获取服务名
        String service = request.path().substring(4);
        log.info("=====>{}", service);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service).app("app1").env("dev").namespace("public").build();
        // 通过注册中心获取活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        // 先简化处理，获得第一个实例的url
        InstanceMeta instanceMeta = loadbalancer.choose(instanceMetas);
        String url = instanceMeta.toUrl();
        // 拿到请求的报文
        Mono<String> requestMono = request.bodyToMono(String.class);
        return requestMono.flatMap(x -> {
            return invokeFromRegistry(x, url);
        });
    }

    @NotNull
    private static Mono<ServerResponse> invokeFromRegistry(String x, String url) {
        // 通过 WebClient 发送请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(x).retrieve().toEntity(String.class);
        // 通过 entity 获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(souce -> System.out.println("response:" + souce));
        // 组装响应报文
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("lcl.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
