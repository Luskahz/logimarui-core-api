package com.logimarui.authorization.core.application.result;

import com.logimarui.authorization.core.domain.model.Permission;

import java.util.List;

public record PermissionsResult(
        List<Permission> permissions
) {
}
