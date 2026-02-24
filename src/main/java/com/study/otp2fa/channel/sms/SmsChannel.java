package com.study.otp2fa.channel.sms;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsChannel implements NotificationChannel {

    @Override
    public ChannelType channelType() {
        return ChannelType.SMS;
    }

    @Override
    public void send(String destination, String otp) {
        // TODO: Twilio / CoolSMS 연동
        log.info("[SMS STUB] to={}, otp={}", destination, otp);
    }
}
