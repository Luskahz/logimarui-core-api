package com.logimarui.authentication.core.port;

import java.time.Instant;


public interface TimeProvider {
    Instant now();
}
