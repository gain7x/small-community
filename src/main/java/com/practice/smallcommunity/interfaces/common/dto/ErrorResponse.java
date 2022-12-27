package com.practice.smallcommunity.interfaces.common.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String code;
    private String message;

    private List<FieldErrorResponse> errors = new ArrayList<>();

    @Builder
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addFieldError(String field, String reason) {
        errors.add(FieldErrorResponse.builder()
            .field(field)
            .reason(reason)
            .build());
    }

    @Getter
    public static class FieldErrorResponse {

        private final String field;
        private final String reason;

        @Builder
        public FieldErrorResponse(String field, String reason) {
            this.field = field;
            this.reason = reason;
        }
    }
}
