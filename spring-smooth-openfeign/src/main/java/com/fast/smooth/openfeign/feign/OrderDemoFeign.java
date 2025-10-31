package com.fast.smooth.openfeign.feign;


import com.fast.smooth.openfeign.config.FeignRetryConfig;
import com.fast.smooth.openfeign.feign.vo.OrderListRequest;
import com.fast.smooth.openfeign.feign.vo.OrderListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="sd-ins-order-service2222",contextId="order",
		configuration = FeignRetryConfig.class)
public interface OrderDemoFeign {

	@PostMapping("list")
	OrderListResponse orders(@RequestBody OrderListRequest request);
}
