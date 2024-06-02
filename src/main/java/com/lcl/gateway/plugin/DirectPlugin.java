package com.lcl.gateway.plugin;

import com.lcl.gateway.AbstarctGatewayPlugin;
import com.lcl.gateway.GatewayPluginChain;
import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * direct plugin
 * @Author conglongli
 * @date 2024/6/2 20:25
 */
@Slf4j
@Component("directPlugin")
public class DirectPlugin extends AbstarctGatewayPlugin {

    public static final String NAME = "direct";
    private static final String prefix = GATEWAY_PREFIX + "/" + NAME + "/";


    @Autowired
    RegistryCenter rc;
    Loadbalancer<InstanceMeta> loadbalancer = new RoundRibonLoadbalancer<>();

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        log.info(" =======>>>>> [LclRpcPlugin]......");

        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("lcl.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("lcl.gw.plugin", getName());

        // 路径中不存在 backend，原样返回请求报文
        if(StringUtils.isBlank(backend)){
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x)))
                    .then(chain.handle(exchange));
        }

        // 通过 WebClient 发送请求
        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);

        // 通过 entity 获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);

        return body.flatMap( x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                .then(chain.handle(exchange));
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
