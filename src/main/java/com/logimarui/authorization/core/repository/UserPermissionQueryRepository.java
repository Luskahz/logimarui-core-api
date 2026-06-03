package com.logimarui.authorization.core.repository;

import java.util.List;

public interface UserPermissionQueryRepository {

    List<String> findPermissionCodesByUserId(Long userId);
}