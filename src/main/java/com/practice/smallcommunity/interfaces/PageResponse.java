package com.practice.smallcommunity.interfaces;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class PageResponse<T> extends CollectionResponse<T>{

    private int pageNumber;
    private int totalPages;
    private long totalElements;

    public PageResponse(int code, String reason, Page<T> page) {
        super(code, reason, page.getContent());
        this.pageNumber = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }

    public static <T> PageResponse<T> Ok(Page<T> page) {
        return new PageResponse<>(0, "", page);
    }
}
