package com.lcl.gateway;

import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.registry.lcl.LclReigstryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
