package com.study.otp2fa.channel.sms;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
import com.study.otp2fa.common.exception.OtpException;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsChannel implements NotificationChannel {

    @Value("${twilio.from-phone}")
    private String fromPhone;

    @Override
    public ChannelType channelType() {
        return ChannelType.SMS;
    }

    @Override
    public void send(String destination, String otp) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(destination),
                    new PhoneNumber(fromPhone),
                    "[OTP2FA] 인증번호: " + otp + "\n5분 이내에 입력해 주세요."
            ).create();

            log.debug("SMS sent. to={}, sid={}", destination, message.getSid());
        } catch (ApiException e) {
            log.error("Twilio SMS 발송 실패. to={}, errorCode={}, message={}", destination, e.getCode(), e.getMessage());
            throw OtpException.badRequest("SMS 발송에 실패했습니다. 전화번호를 확인해 주세요.");
        }
    }
}
