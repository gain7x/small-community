package com.practice.smallcommunity.interfaces.common.dto;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectionResponse<T> extends BaseResponse<Collection<T>> {

    private int count;

    public CollectionResponse(Collection<T> data) {
        super(data);
        this.count = data.size();
    }

    public static <T> CollectionResponse<T> Ok(Collection<T> data) {
        return new CollectionResponse<>(data);
    }
}