package com.study.otp2fa.otp.service;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.otp.dto.OtpStatusResponse;

public interface OtpService {

    void send(ChannelType channel, String destination, String purpose, String ipAddress);

    boolean verify(ChannelType channel, String destination, String code);

    OtpStatusResponse status(ChannelType channel, String destination);
}
