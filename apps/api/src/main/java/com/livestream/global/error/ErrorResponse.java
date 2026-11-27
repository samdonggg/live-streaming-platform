package com.livestream.global.error;

/**
 * 모든 API 에러 응답의 본문 형식이다: {@code { "error": { "code": "...", "message": "..." } }}
 */
public record ErrorResponse(Detail error) {

    public record Detail(String code, String message) {
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(new Detail(errorCode.code(), errorCode.getMessage()));
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(new Detail(errorCode.code(), message));
    }
}
