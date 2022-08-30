package com.practice.smallcommunity.interfaces;

import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectionResponse<T> {

    private int count;
    private Collection<T> data;

    public CollectionResponse(Collection<T> data) {
        this.count = data.size();
        this.data = data;
    }
}