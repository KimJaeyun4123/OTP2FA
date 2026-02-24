package com.study.otp2fa.otp.repository;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.otp.domain.OtpLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpLogRepository extends JpaRepository<OtpLog, Long> {

    List<OtpLog> findByChannelAndDestinationOrderBySentAtDesc(ChannelType channel, String destination);
}
