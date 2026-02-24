package com.study.otp2fa.channel.sms;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
import com.study.otp2fa.common.exception.OtpException;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsChannel implements NotificationChannel {

    @Value("${twilio.verify-service-sid}")
    private String verifyServiceSid;

    @Override
    public ChannelType channelType() {
        return ChannelType.SMS;
    }

    /**
     * Twilio Verify API로 OTP 발송.
     * Twilio가 코드를 생성·관리하므로 otp 파라미터는 사용하지 않는다.
     */
    @Override
    public void send(String destination, String otp) {
        try {
            Verification verification = Verification.creator(verifyServiceSid, destination, "sms").create();
            log.debug("Twilio Verify SMS sent. to={}, status={}", destination, verification.getStatus());
        } catch (ApiException e) {
            log.error("Twilio Verify 발송 실패. to={}, errorCode={}, message={}", destination, e.getCode(), e.getMessage());
            throw OtpException.badRequest("SMS 발송에 실패했습니다. 전화번호를 확인해 주세요.");
        }
    }

    /**
     * Twilio Verify API로 OTP 검증.
     * Redis를 거치지 않고 Twilio에 직접 위임한다.
     */
    @Override
    public boolean verifyWithProvider(String destination, String code) {
        try {
            VerificationCheck check = VerificationCheck.creator(verifyServiceSid)
                    .setTo(destination)
                    .setCode(code)
                    .create();
            boolean approved = "approved".equals(check.getStatus().toString());
            log.debug("Twilio Verify check. to={}, status={}", destination, check.getStatus());
            return approved;
        } catch (ApiException e) {
            log.error("Twilio Verify 검증 실패. to={}, errorCode={}, message={}", destination, e.getCode(), e.getMessage());
            throw OtpException.badRequest("인증번호 검증에 실패했습니다.");
        }
    }

    @Override
    public boolean isManagedByProvider() {
        return true;
    }
}
