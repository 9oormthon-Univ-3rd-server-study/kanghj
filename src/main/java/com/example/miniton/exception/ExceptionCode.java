package com.example.miniton.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCode {

    // Member Exception
    MEMBER_NOT_EXIST(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다."),
    PASSWORD_DUPLICATE(HttpStatus.BAD_REQUEST,"같은 비밀번호로 수정 할 수 없습니다."),

    // Token Exception
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "잘못된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "토큰이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
    ExceptionCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
