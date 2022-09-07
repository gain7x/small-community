package com.practice.smallcommunity.interfaces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {

    private T data;

    public BaseResponse(T data) {
        this.data = data;
    }

    public static <T> BaseResponse<T> Ok(T data) {
        return new BaseResponse<>(data);
    }
}
