package com.study.otp2fa.otp.dto;

import com.study.otp2fa.channel.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class VerifyOtpRequest {

    @NotNull(message = "채널은 필수입니다.")
    private ChannelType channel;

    @NotBlank(message = "수신처는 필수입니다.")
    private String destination;

    @NotBlank(message = "인증번호는 필수입니다.")
    @Pattern(regexp = "^\\d{6}$", message = "인증번호는 6자리 숫자여야 합니다.")
    private String code;
}
