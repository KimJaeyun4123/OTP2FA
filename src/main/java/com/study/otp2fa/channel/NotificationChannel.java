package com.study.otp2fa.channel;

/**
 * 채널별 OTP 발송을 추상화하는 Strategy 인터페이스.
 * Email / SMS / Push 각 구현체가 이를 구현한다.
 */
public interface NotificationChannel {

    ChannelType channelType();

    void send(String destination, String otp);
}
