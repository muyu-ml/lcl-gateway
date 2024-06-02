package com.lcl.gateway;

import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.registry.lcl.LclReigstryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;
import java.util.Properties;

/**
 * gateway config
 * @Author conglongli
 * @date 2024/6/1 23:37
 */
@Configuration
public class GatewayConfig {
    @Bean
    public RegistryCenter rc(){
        return new LclReigstryCenter();
    }


    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put("/ga/**", "GatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();

        };
    }
}
