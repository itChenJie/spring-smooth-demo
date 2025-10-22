package com.fast.smooth.openfeign.controller;

import com.fast.smooth.openfeign.feign.OrderDemoFeign;
import com.fast.smooth.openfeign.feign.vo.OrderListRequest;
import com.fast.smooth.openfeign.feign.vo.OrderListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @Autowired
    private OrderDemoFeign orderDemoFeign;

    @PostMapping("list")
    public String list() {
        OrderListRequest request = new OrderListRequest();
        request.setToken("1222");
        OrderListResponse orders = orderDemoFeign.orders(request);
        System.out.println(orders.toString());
        System.out.println(orders.getCode());
        return "success";
    }
}
