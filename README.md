# 平滑发布简单解决方案
    处理服务发布期间
        1：网关请求失败，大量报错信息响应给前端
        2：服务间调用大量失败
    目标 减小服务发布的影响时间

    spring-boot-starter-parent:2.2.2.RELEASE,spring-cloud-loadbalancer 模块刚刚取代 Ribbon；
    还 没有提供完整的 Retry 配置支持；当时重试逻辑仍依赖 Spring Retry + Feign 的 Retryer 机制 或 Ribbon 的老逻辑；
    如果采用RestTemplate可以直接使用Spring Retry 来包裹重试逻辑
## OpenFeign
### 自动重试
    分两层
    - 客户端重试（Feign / LoadBalancer 的重试策略：先在同实例重试，再换实例重试）
    - 业务/熔断重试（Resilience4j 的 Retry/CircuitBreaker，用于控制整体调用幅度）
### 熔断
### 动态负载机制
    
### Gateway
