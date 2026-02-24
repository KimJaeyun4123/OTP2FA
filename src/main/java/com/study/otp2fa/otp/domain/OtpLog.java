package com.study.otp2fa.otp.domain;

import com.study.otp2fa.channel.ChannelType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OtpLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChannelType channel;

    @Column(nullable = false, length = 255)
    private String destination;

    @Column(nullable = false, length = 50)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OtpStatus status;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    public OtpLog(ChannelType channel, String destination, String purpose,
                  OtpStatus status, String ipAddress, LocalDateTime expiresAt) {
        this.channel = channel;
        this.destination = destination;
        this.purpose = purpose;
        this.status = status;
        this.ipAddress = ipAddress;
        this.attempts = 0;
        this.sentAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void markVerified() {
        this.status = OtpStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void markExpired() {
        this.status = OtpStatus.EXPIRED;
    }

    public void markFailed() {
        this.status = OtpStatus.FAILED;
    }
}
