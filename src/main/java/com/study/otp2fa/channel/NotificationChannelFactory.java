package com.study.otp2fa.channel;

import com.study.otp2fa.common.exception.OtpException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationChannelFactory {

    private final Map<ChannelType, NotificationChannel> channels;

    public NotificationChannelFactory(List<NotificationChannel> channelList) {
        this.channels = channelList.stream()
                .collect(Collectors.toMap(NotificationChannel::channelType, Function.identity()));
    }

    public NotificationChannel get(ChannelType type) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) {
            throw OtpException.badRequest("지원하지 않는 채널입니다: " + type);
        }
        return channel;
    }
}
