package com.example.miniton.common;

public record CommonResponseDto<T>(
        String msg,
        T result
) {
}