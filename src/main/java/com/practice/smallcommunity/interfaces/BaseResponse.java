package com.practice.smallcommunity.interfaces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {

    private int code;
    private String reason;
    private T data;

    public BaseResponse(int code, String reason, T data) {
        this.code = code;
        this.reason = reason;
        this.data = data;
    }

    public static <T> BaseResponse<T> Ok(T data) {
        return new BaseResponse<>(0, "", data);
    }
}
