package com.fast.smooth.openfeign.feign.vo;


/**
 * 订单列表
 */
public class OrderListBean {
    private String order; //订单号
    private String licenseNo; //车牌号
	private String uuserName;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getUuserName() {
        return uuserName;
    }

    public void setUuserName(String uuserName) {
        this.uuserName = uuserName;
    }
}
