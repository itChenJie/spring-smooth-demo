# 平滑发布简单解决方案
    处理服务发布期间
        1：网关请求失败，大量报错信息响应给前端
        2：服务间调用失败
    目标 减小服务发布影响时间
## 服务接口高可用
### OpenFeign 自动重试
   - 客户端负载均衡层
        <!-- LoadBalancer 重试 -->
        - 重试目标：针对整个服务实例调用（实例维度）  
        - 典型重试场景：当前实例调用失败 → 切换到另一个实例重试
        - 默认是否开启：默认开启
        - 可重试操作：可通过 retry-on-all-operations 控制（默认仅 GET）
        - 适合场景：微服务级高可用，节点故障自动切换
     

            <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-loadbalancer</artifactId>
            </dependency>
            
            spring:
                application:
                    cloud:
                        loadbalancer:
                            enabled: true
                            retry:
                                # 该参数用来开启或关闭重试机制，默认是开启
                                enabled: true
                                # 对当前实例重试的次数，默认值: 0
                                max-retries-on-same-service-instance: 1
                                # 切换实例进行重试的次数，默认值: 1
                                max-retries-on-next-service-instance: 1
                                # 对所有的操作请求都进行重试
                                retry-on-all-operations: true
                            health-check:
                                # 是否重新获取服务实例列表，默认为false
                                refetch-instances: true
                                # 重新获取服务实例列表的时间间隔，默认为5秒
                                refetch-instances-interval: 5s
                                # 是否重复进行健康检查，默认为false
                                repeat-health-check: false
   - Feign 调用层（HTTP 层）
        - 重试目标：针对单个 HTTP 请求（同一实例）
        - 典型重试场景：网络抖动、Socket 超时、服务端返回特定异常
        - 控制的粒度：请求级（默认同一实例）
        - 默认是否开启：❌ 关闭，需要显式配置 Retryer Bean
        - 可重试操作：任意 HTTP 操作，只要异常触发
        - 适合场景：临时网络错误、短连接重试
        - 生效前提：显式指定 configuration = FeignRetryConfig.class
        
   - 熔断 限流Resilience4j
        - ✅ 熔断（Circuit Breaking）：当调用失败率过高时自动“断开电路”，阻止继续访问故障服务。
        - 🔁 重试（Retry）：请求失败时可自动重试几次。
        - ⏱️ 限流（Rate Limiter）：限制访问频率，防止过载。
        - ⌛ 超时控制（Timeout）：防止调用时间过长。
        - 🧯 回退（Fallback）：当发生异常时执行备用逻辑（如返回缓存或默认值）。


          resilience4j:
          circuitbreaker:
          instances:
          myService:
          slidingWindowType: COUNT_BASED       # 滑动窗口类型：基于请求数
          registerHealthIndicator: true
          slidingWindowSize: 100         # 检测窗口大小（100次调用）
          failureRateThreshold: 50       # 失败率超过50%则熔断
          waitDurationInOpenState: 10s   # 熔断后等待10秒再半开
          permittedNumberOfCallsInHalfOpenState: 10 # 半开状态允许请求数：10 个
          automaticTransitionFromOpenToHalfOpenEnabled: true
          recordExceptions: # 记为失败的异常
          - java.io.IOException
            - java.util.concurrent.TimeoutException
            - org.springframework.web.client.HttpServerErrorException  # 5xx 错误
            ignoreExceptions: # 不记为失败的异常（如客户端错误）
            - org.springframework.web.client.HttpClientErrorException  # 4xx 错误
            retry:
            instances:
            myService:
            maxAttempts: 3 # 最大重试次数：3 次（不含首次调用）
            waitDuration: 1s  # 重试间隔：1s
            retryExceptions: # 需要重试的异常
            - java.io.IOException
            - java.util.concurrent.TimeoutException
            timelimiter:
            instances:
            myService:
            timeoutDuration: 2s  # 异步调用超时：5s
   
### 熔断
### 动态负载机制
    
## Gateway

## 服务注册(启动阶段保护)
    eureka:
    instance:
    initial-status: STARTING
  启动完成

    @Autowired
    private EurekaClient eurekaClient;
    @PostConstruct
    public void onStart() {
        eurekaClient.getApplicationInfoManager()
        .setInstanceStatus(InstanceInfo.InstanceStatus.UP);
    }
## 服务下线
   优雅下线
    在发布时执行：

    curl -X POST http://localhost:8080/actuator/shutdown
   或通过自定义控制器：

    @PreDestroy
    public void gracefulShutdown() {
      eurekaClient.getApplicationInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
    }