package com.livestream.global.error;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반을 {@link ErrorCode}와 함께 알린다.
 * {@link GlobalExceptionHandler}가 공통 에러 응답으로 바꾼다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
