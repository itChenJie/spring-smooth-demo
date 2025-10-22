# 平滑发布简单解决方案
    处理服务发布期间
        1：网关请求失败，大量报错信息响应给前端
        2：服务间调用失败
    目标 减小服务发布影响时间
## OpenFeign
### 自动重试
   分两层
   - 客户端重试（Feign / LoadBalancer 的重试策略：先在同实例重试，再换实例重试）   
     spring-boot-starter-parent:2.2.2.RELEASE，spring-cloud-loadbalancer 模块刚刚取代 Ribbon；   
     还没有提供完整的 Retry 配置支持使用Ribbon    
     - 方案 1：Ribbon（老机制）——自动换实例（Hoxton 默认仍支持）   
         Ribbon 自身带有重试逻辑：

            ribbon:
            MaxAutoRetries: 1                   # 同实例重试次数
            MaxAutoRet  riesNextServer: 2         # 其他实例重试次数
            OkToRetryOnAllOperations: true
            ConnectTimeout: 2000
            ReadTimeout: 2000

         📘解释：   
           第一次失败 → 同实例重试一次；   
           第二次失败 → Ribbon 自动切换到其他实例；   
           整体可以实现“重试 + 自动换实例”。   

     - 方案 2：完全使用 LoadBalancer（无 Ribbon）   
        Spring Cloud LoadBalancer（Hoxton 版本），默认没有自动换实例功能。  
        可以通过 自定义 Feign 的 Client 实现，让每次重试都重新调用 LoadBalancerClient.choose()。  

            public class RetryableLoadBalancerFeignClient extends LoadBalancerFeignClient {
                private final LoadBalancerClient loadBalancerClient;
                public RetryableLoadBalancerFeignClient(Client delegate, LoadBalancerClient loadBalancerClient) {
                    super(delegate, loadBalancerClient, new LoadBalancerClientFactory());
                    this.loadBalancerClient = loadBalancerClient;
                }
                @Override
                public Response execute(Request request, Request.Options options) throws IOException {
                    int maxAttempts = 3;
                    for (int i = 0; i < maxAttempts; i++) {
                       ServiceInstance instance = loadBalancerClient.choose(getServiceId(request));
                       if (instance == null) throw new IOException("No available instance");
                       try {
                            return super.execute(rebuildRequest(request, instance), options);
                       } catch (IOException e) {
                            if (i == maxAttempts - 1) throw e;
                       }
                    }
                    throw new IOException("All retries failed");
                }
                private String getServiceId(Request request) {
                    URI uri = URI.create(request.url());
                    return uri.getHost();
                }
                private Request rebuildRequest(Request original, ServiceInstance instance) {
                    String newUrl = instance.getUri().toString() + original.url().substring(original.url().indexOf('/', 8));
                    return Request.create(original.httpMethod(), newUrl, original.headers(), original.body(), original.charset());
                }
            }
| 方案                                                        | 是否会换实例   | 控制重试层级         | 配置方式                                | 适合场景       |
| --------------------------------------------------------- | -------- | -------------- | ----------------------------------- | ---------- |
| **Ribbon Retry**（Hoxton 默认）                               | ✅ 会自动换实例 | Ribbon 层       | `ribbon.*` 配置                       | 推荐旧项目      |
| **Spring Cloud LoadBalancer + Retry**（Spring Cloud 2020+） | ✅ 支持     | LoadBalancer 层 | `spring.cloud.loadbalancer.retry.*` | 推荐新项目      |
| **自定义 LoadBalancerFeignClient**                           | ✅ 可控     | 自定义封装          | Java 配置                             | 对接多注册中心、自研 |

   - 业务/熔断重试（Resilience4j 的 Retry/CircuitBreaker，用于控制整体调用幅度）  
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