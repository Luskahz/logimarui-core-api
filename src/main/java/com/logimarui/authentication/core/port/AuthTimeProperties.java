package com.logimarui.auth.core.port;

import java.time.Duration;

public interface AuthTimeProperties {
    Duration sessionTtl();
    Duration refreshTokenTtl();
    Duration passwordChangeRequestTtl();
}
