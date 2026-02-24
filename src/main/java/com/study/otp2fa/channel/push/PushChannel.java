package com.study.otp2fa.channel.push;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
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
        // TODO: Firebase Admin SDK (FCM) 연동
        log.info("[PUSH STUB] token={}, otp={}", destination, otp);
    }
}
