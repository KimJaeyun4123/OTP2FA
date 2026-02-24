package com.study.otp2fa.channel.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
import com.study.otp2fa.common.exception.OtpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushChannel implements NotificationChannel {

    @Override
    public ChannelType channelType() {
        return ChannelType.PUSH;
    }

    @Override
    public void send(String destination, String otp) {
        try {
            Message message = Message.builder()
                    .setToken(destination)
                    .setNotification(Notification.builder()
                            .setTitle("[OTP2FA] 인증번호 도착")
                            .setBody("인증번호: " + otp + " (5분 이내 입력)")
                            .build())
                    .putData("otp", otp)
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.debug("Push sent. token={}, messageId={}", destination, messageId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM 발송 실패. token={}, errorCode={}, message={}", destination, e.getMessagingErrorCode(), e.getMessage());
            throw OtpException.badRequest("Push 발송에 실패했습니다. FCM 토큰을 확인해 주세요.");
        }
    }
}
