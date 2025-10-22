package com.fast.smooth.openfeign.feign.vo;


/**
 * 响应基类
 */
public class BaseResponse {
    private String code;
    private String desc;
    private String stepCode;

    public BaseResponse() {
    }

    public BaseResponse(String code, String desc) {
        super();
        this.code = code;
        this.desc = desc;
    }
    public void build(String code, String desc) {
    	this.code = code;
    	this.desc = desc;
    }

    public static BaseResponse buildResponse(String code, String desc){
        return new BaseResponse(code,desc);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", stepCode='" + stepCode + '\'' +
                '}';
    }
}
