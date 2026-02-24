package com.study.otp2fa.otp.service;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.channel.NotificationChannel;
import com.study.otp2fa.channel.NotificationChannelFactory;
import com.study.otp2fa.common.exception.OtpException;
import com.study.otp2fa.otp.domain.OtpLog;
import com.study.otp2fa.otp.domain.OtpStatus;
import com.study.otp2fa.otp.dto.OtpStatusResponse;
import com.study.otp2fa.otp.repository.OtpLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;
    private final NotificationChannelFactory channelFactory;
    private final OtpLogRepository otpLogRepository;

    @Value("${otp.ttl-seconds:300}")
    private long ttlSeconds;

    @Value("${otp.resend-cooldown-seconds:60}")
    private long resendCooldownSeconds;

    @Value("${otp.max-attempts:5}")
    private int maxAttempts;

    @Value("${otp.lock-seconds:1800}")
    private long lockSeconds;

    @Override
    @Transactional
    public void send(ChannelType channel, String destination, String purpose, String ipAddress) {
        String lockKey   = redisKey("lock", channel, destination);
        String resendKey = redisKey("resend", channel, destination);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw OtpException.locked("너무 많은 시도로 인해 잠금 상태입니다. 잠시 후 다시 시도해 주세요.");
        }
        if (Boolean.TRUE.equals(redisTemplate.hasKey(resendKey))) {
            Long ttl = redisTemplate.getExpire(resendKey, TimeUnit.SECONDS);
            throw OtpException.tooManyRequests("재발송은 " + ttl + "초 후에 가능합니다.");
        }

        NotificationChannel notificationChannel = channelFactory.get(channel);

        if (notificationChannel.isManagedByProvider()) {
            // Provider(Twilio Verify 등)가 OTP를 생성·관리 → Redis 저장 불필요
            notificationChannel.send(destination, null);
        } else {
            // 우리 서버에서 OTP 생성 → Redis 저장 → 채널로 발송
            String otp    = generateOtp();
            String otpKey = redisKey("otp", channel, destination);
            redisTemplate.opsForValue().set(otpKey, otp, ttlSeconds, TimeUnit.SECONDS);
            notificationChannel.send(destination, otp);
        }

        redisTemplate.opsForValue().set(resendKey, "1", resendCooldownSeconds, TimeUnit.SECONDS);

        otpLogRepository.save(OtpLog.builder()
                .channel(channel)
                .destination(destination)
                .purpose(purpose)
                .status(OtpStatus.SENT)
                .ipAddress(ipAddress)
                .expiresAt(LocalDateTime.now().plusSeconds(ttlSeconds))
                .build());

        log.debug("OTP sent. channel={}, destination={}", channel, destination);
    }

    @Override
    @Transactional
    public boolean verify(ChannelType channel, String destination, String code) {
        String lockKey = redisKey("lock", channel, destination);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw OtpException.locked("너무 많은 시도로 인해 잠금 상태입니다. 잠시 후 다시 시도해 주세요.");
        }

        NotificationChannel notificationChannel = channelFactory.get(channel);

        if (notificationChannel.isManagedByProvider()) {
            // Provider에게 검증 위임 (Redis 미사용)
            boolean verified = notificationChannel.verifyWithProvider(destination, code);
            if (verified) {
                otpLogRepository.findByChannelAndDestinationOrderBySentAtDesc(channel, destination)
                        .stream().findFirst().ifPresent(OtpLog::markVerified);
            }
            return verified;
        }

        // Redis 기반 검증 (Email, Push)
        String otpKey      = redisKey("otp", channel, destination);
        String attemptsKey = redisKey("attempts", channel, destination);

        String stored = redisTemplate.opsForValue().get(otpKey);
        if (stored == null) {
            throw OtpException.notFound("인증번호가 만료되었거나 존재하지 않습니다.");
        }

        if (!stored.equals(code)) {
            long attempts = redisTemplate.opsForValue().increment(attemptsKey);
            redisTemplate.expire(attemptsKey, ttlSeconds * 2, TimeUnit.SECONDS);

            if (attempts >= maxAttempts) {
                redisTemplate.opsForValue().set(lockKey, "1", lockSeconds, TimeUnit.SECONDS);
                redisTemplate.delete(otpKey);
            }
            return false;
        }

        // 검증 성공 → 단일 사용 처리
        redisTemplate.delete(otpKey);
        redisTemplate.delete(attemptsKey);

        otpLogRepository.findByChannelAndDestinationOrderBySentAtDesc(channel, destination)
                .stream().findFirst().ifPresent(OtpLog::markVerified);

        return true;
    }

    @Override
    public OtpStatusResponse status(ChannelType channel, String destination) {
        String lockKey   = redisKey("lock", channel, destination);
        String resendKey = redisKey("resend", channel, destination);

        boolean locked = Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        Long resendTtl = redisTemplate.getExpire(resendKey, TimeUnit.SECONDS);

        return OtpStatusResponse.builder()
                .locked(locked)
                .resendAvailableInSeconds(resendTtl != null && resendTtl > 0 ? resendTtl : null)
                .build();
    }

    private String generateOtp() {
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(otp);
    }

    private String redisKey(String type, ChannelType channel, String destination) {
        return String.format("otp:%s:%s:%s", type, channel.name().toLowerCase(), destination);
    }
}
