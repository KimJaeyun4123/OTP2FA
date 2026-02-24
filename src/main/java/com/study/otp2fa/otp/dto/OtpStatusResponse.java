package com.study.otp2fa.otp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtpStatusResponse {

    private boolean locked;
    private Long resendAvailableInSeconds;
}
