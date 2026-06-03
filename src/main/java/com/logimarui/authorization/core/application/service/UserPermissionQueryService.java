package com.logimarui.authorization.core.application.service;

import com.logimarui.authorization.core.repository.UserPermissionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermissionQueryService {

    private final UserPermissionQueryRepository userPermissionQueryRepository;

    public List<String> findPermissionCodesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }

        return userPermissionQueryRepository.findPermissionCodesByUserId(userId);
    }
}