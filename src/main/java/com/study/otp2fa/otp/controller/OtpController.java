package com.study.otp2fa.otp.controller;

import com.study.otp2fa.channel.ChannelType;
import com.study.otp2fa.common.response.ApiResponse;
import com.study.otp2fa.otp.dto.OtpStatusResponse;
import com.study.otp2fa.otp.dto.SendOtpRequest;
import com.study.otp2fa.otp.dto.VerifyOtpRequest;
import com.study.otp2fa.otp.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> send(
            @Valid @RequestBody SendOtpRequest request,
            HttpServletRequest httpRequest) {

        otpService.send(
                request.getChannel(),
                request.getDestination(),
                request.getPurpose(),
                httpRequest.getRemoteAddr()
        );
        return ResponseEntity.ok(ApiResponse.ok("인증번호가 발송되었습니다."));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Valid @RequestBody VerifyOtpRequest request) {

        boolean verified = otpService.verify(
                request.getChannel(),
                request.getDestination(),
                request.getCode()
        );

        if (!verified) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("인증번호가 올바르지 않습니다."));
        }
        return ResponseEntity.ok(ApiResponse.ok("인증이 완료되었습니다."));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<OtpStatusResponse>> status(
            @RequestParam ChannelType channel,
            @RequestParam String destination) {

        OtpStatusResponse status = otpService.status(channel, destination);
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}
