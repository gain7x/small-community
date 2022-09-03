package com.practice.smallcommunity.interfaces;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {

    private String reason;
    private T data;

    public BaseResponse(String reason, T data) {
        this.reason = reason;
        this.data = data;
    }
}
