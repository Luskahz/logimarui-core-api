package com.logimarui.authorization.core.application.result;

import com.logimarui.authorization.core.domain.model.Role;

import java.util.List;

public record RolesResult(
        List<Role> roles
) {
}
