package com.study.otp2fa.otp.dto;

import com.study.otp2fa.channel.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SendOtpRequest {

    @NotNull(message = "채널은 필수입니다.")
    private ChannelType channel;

    @NotBlank(message = "수신처(이메일/전화번호/FCM 토큰)는 필수입니다.")
    private String destination;

    @NotBlank(message = "발송 목적은 필수입니다.")
    private String purpose;
}
