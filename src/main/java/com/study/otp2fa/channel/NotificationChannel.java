package com.study.otp2fa.channel;

/**
 * 채널별 OTP 발송을 추상화하는 Strategy 인터페이스.
 * Email / SMS / Push 각 구현체가 이를 구현한다.
 *
 * Provider가 OTP 생명주기(생성·검증)를 직접 관리하는 경우(e.g. Twilio Verify)
 * isManagedByProvider()를 true로, verifyWithProvider()를 오버라이드한다.
 */
public interface NotificationChannel {

    ChannelType channelType();

    /**
     * OTP를 발송한다.
     * isManagedByProvider()가 true인 채널은 otp 파라미터를 무시하고
     * 자체적으로 코드를 생성·발송한다.
     */
    void send(String destination, String otp);

    /**
     * Provider가 OTP 생명주기(생성·저장·검증)를 직접 관리하는지 여부.
     * true이면 OtpService는 Redis 저장·검증을 건너뛰고 verifyWithProvider()에 위임한다.
     */
    default boolean isManagedByProvider() {
        return false;
    }

    /**
     * Provider에게 OTP 검증을 위임한다.
     * isManagedByProvider()가 true인 채널만 구현한다.
     */
    default boolean verifyWithProvider(String destination, String code) {
        throw new UnsupportedOperationException(channelType() + " 채널은 provider 검증을 지원하지 않습니다.");
    }
}
