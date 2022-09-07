package com.practice.smallcommunity.interfaces;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectionResponse<T> extends BaseResponse<Collection<T>> {

    private int count;

    public CollectionResponse(int code, String reason, Collection<T> data) {
        super(code, reason, data);
        this.count = data.size();
    }

    public static <T> CollectionResponse<T> Ok(Collection<T> data) {
        return new CollectionResponse<>(0, "", data);
    }
}