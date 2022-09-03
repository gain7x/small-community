package com.practice.smallcommunity.interfaces;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectionResponse<T> extends BaseResponse<Collection<T>> {

    private int count;

    public CollectionResponse(String reason, Collection<T> data) {
        super(reason, data);
        this.count = data.size();
    }
}