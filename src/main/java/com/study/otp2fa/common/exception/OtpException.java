package com.study.otp2fa.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OtpException extends RuntimeException {

    private final HttpStatus status;

    public OtpException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // 자주 쓰는 케이스 정적 팩토리
    public static OtpException notFound(String message) {
        return new OtpException(HttpStatus.NOT_FOUND, message);
    }

    public static OtpException badRequest(String message) {
        return new OtpException(HttpStatus.BAD_REQUEST, message);
    }

    public static OtpException tooManyRequests(String message) {
        return new OtpException(HttpStatus.TOO_MANY_REQUESTS, message);
    }

    public static OtpException locked(String message) {
        return new OtpException(HttpStatus.LOCKED, message);
    }
}
