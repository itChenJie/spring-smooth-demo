package com.fast.smooth.openfeign.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class EurekaShutdownConfig {
    
    @Autowired(required = false)
    private EurekaRegistration eurekaRegistration;
    
    @Autowired(required = false)
    private EurekaServiceRegistry eurekaServiceRegistry;
    
    @PreDestroy
    public void destroy() {
        if (eurekaRegistration != null && eurekaServiceRegistry != null) {
            // 主动从Eureka注销
            eurekaServiceRegistry.deregister(eurekaRegistration);
            eurekaServiceRegistry.close();
            System.out.println("Eureka instance deregistered");
        }
    }
    
    @Bean
    public EurekaShutdownHook eurekaShutdownHook() {
        return new EurekaShutdownHook();
    }
    
    public class EurekaShutdownHook {
        @PreDestroy
        public void shutdown() {
            destroy();
        }
    }
}