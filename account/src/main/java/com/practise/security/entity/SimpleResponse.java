package com.practise.security.entity;

/**
 * @author wangjiantao
 * @date 2020/4/28 10:31
 */
public class SimpleResponse {
    private String message;

    public SimpleResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
