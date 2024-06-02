package com.lcl.gateway.web.handler;

import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * gateway web handler
 * @Author conglongli
 * @date 2024/6/2 15:09
 */
@Slf4j
@Component("GatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Autowired
    RegistryCenter rc;
    Loadbalancer<InstanceMeta> loadbalancer = new RoundRibonLoadbalancer<>();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        log.info(" =======>>>>> Lcl Gateway Web Handler start......");

        // 通过请求路径获取服务名
        String service = exchange.getRequest().getPath().value().substring(4);
        log.info("=====>{}", service);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service).app("app1").env("dev").namespace("public").build();

        // 通过注册中心获取活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);

        // 先简化处理，获得第一个实例的url
        InstanceMeta instanceMeta = loadbalancer.choose(instanceMetas);
        String url = instanceMeta.toUrl();

        // 拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();


        // 通过 WebClient 发送请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);

        // 通过 entity 获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
//        body.subscribe(souce -> System.out.println("response:" + souce));

        // 组装响应报文
        HttpHeaders headers = exchange.getResponse().getHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("lcl.gw.version", "v1.0.0");

        return body.flatMap( x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))));
    }
}
