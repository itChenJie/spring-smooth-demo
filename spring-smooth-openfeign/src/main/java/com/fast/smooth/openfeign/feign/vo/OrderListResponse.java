package com.fast.smooth.openfeign.feign.vo;

import java.util.List;

public class OrderListResponse extends BaseResponse {
    private List<OrderListBean> orders; //订单集合

	private int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<OrderListBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderListBean> orders) {
        this.orders = orders;
    }

	@Override
	public String toString() {
		return "OrderListResponse{" +
				"orders=" + orders +
				", total=" + total +
				'}';
	}


}
