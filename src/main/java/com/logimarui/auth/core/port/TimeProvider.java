package com.logimarui.auth.core.port;

import java.time.Instant;


public interface TimeProvider {
    Instant now();
}
