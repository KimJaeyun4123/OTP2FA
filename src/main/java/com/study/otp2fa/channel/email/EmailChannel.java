package com.study.otp2fa.channel.email;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannel implements NotificationChannel {

    private final JavaMailSender mailSender;

    @Value("${otp.mail.from}")
    private String from;

    @Override
    public ChannelType channelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public void send(String destination, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(destination);
        message.setSubject("[OTP2FA] 인증번호 안내");
        message.setText("인증번호: " + otp + "\n\n5분 이내에 입력해 주세요.");
        mailSender.send(message);
        log.debug("Email OTP sent to {}", destination);
    }
}
