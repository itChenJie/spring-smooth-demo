package com.fast.smooth.openfeign.config;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class EurekaStatusConfig {
    
    @Autowired
    private ApplicationInfoManager applicationInfoManager;
    
    @PostConstruct
    public void setInstanceStatus() {
        // 延迟设置状态，确保应用完全启动
        new Thread(() -> {
            try {
                Thread.sleep(10000); // 等待10秒确保应用完全启动
                applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
                System.out.println("Eureka instance status set to UP");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}