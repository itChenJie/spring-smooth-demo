package com.fast.smooth.openfeign.config;

import feign.RetryableException;
import feign.Retryer;

public class CustomFeignRetryer implements Retryer {
    /**
     * 最大重试次数
     */
    private final int maxAttempts;
    /**
     * 重试间隔
     */
    private final long backoff;
     
    private int attempt = 1;

    public CustomFeignRetryer() {
        // 默认重试间隔 2 秒，最多 3 次
        this(2000, 3); 
    }

    public CustomFeignRetryer(long backoff, int maxAttempts) {
        this.backoff = backoff;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) {
            throw e;
        }
        try {
            Thread.sleep(backoff);
        } catch (InterruptedException ignored) {}
    }

    @Override
    public Retryer clone() {
        return new CustomFeignRetryer(backoff, maxAttempts);
    }
}
