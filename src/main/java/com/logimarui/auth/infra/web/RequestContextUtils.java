package com.logimarui.auth.infra.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class RequestContextUtils {
    public static String resolveClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) {
            return ip;
        }
        return request.getRemoteAddr();
    }
    public static String resolveDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }
}
