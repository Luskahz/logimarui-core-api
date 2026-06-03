package com.logimarui.authentication.core.application.results.login;

public sealed interface LoginResult permits
        AuthenticatedLoginResult,
        PasswordChangeRequiredLoginResult {
}