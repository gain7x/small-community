package com.practice.smallcommunity.interfaces;

import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docs")
public class CommonController {

    @GetMapping("/base")
    public BaseResponse<String> baseResponse() {
        return BaseResponse.Ok("응답 데이터");
    }

    @GetMapping("/collection")
    public CollectionResponse<String> collectionResponse() {
        return CollectionResponse.Ok(List.of("데이터1", "데이터2"));
    }

    @GetMapping("/page")
    public PageResponse<String> pageResponse() {
        return PageResponse.Ok(new PageImpl<>(List.of("데이터1", "데이터2")));
    }
}