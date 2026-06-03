package com.logimarui.authentication.core.port;

public interface NotificationPort {
    void sendPasswordResetLink(String email, String resetLink);
}
