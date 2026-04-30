package com.logimarui.auth.infra.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
public final class RequestContextUtils {

    private static final String UNKNOWN = "unknown";

    public static String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    public static String resolveDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader("X-Device-Id");

        if (deviceId != null && !deviceId.isBlank()) {
            return deviceId.trim();
        }

        return UUID.randomUUID().toString();
    }

    public static String resolveUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || userAgent.isBlank()) {
            return UNKNOWN;
        }

        return userAgent.trim();
    }
}