package com.practice.smallcommunity.common.interfaces;

import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.common.interfaces.dto.CollectionResponse;
import com.practice.smallcommunity.common.interfaces.dto.PageResponse;
import com.practice.smallcommunity.common.interfaces.dto.BaseResponse;
import com.practice.smallcommunity.common.interfaces.dto.ErrorResponse;

import java.util.ArrayList;
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

    @GetMapping("/error")
    public ErrorResponse errorResponse() {
        throw new BusinessException(ErrorCode.DUPLICATED_EMAIL, "email");
    }

    @GetMapping("/errorCodes")
    public CollectionResponse<ErrorResponse> errorCodes() {
        List<ErrorResponse> errorCodes = new ArrayList<>();

        for (ErrorCode errorCode : ErrorCode.values()) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getDefaultMessage())
                .build();

            errorCodes.add(errorResponse);
        }

        return CollectionResponse.Ok(errorCodes);
    }
}
