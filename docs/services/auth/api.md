# API Autenticacao

## Base path

`/api/v1/authentication`

## Rotas publicas

- `POST /api/v1/authentication/login`
- `POST /api/v1/authentication/login/password-change`
- `POST /api/v1/authentication/register`
- `POST /api/v1/authentication/refresh`
- `POST /api/v1/authentication/password-recovery/requests`
- `POST /api/v1/authentication/password-recovery/requests/email-token`
- `POST /api/v1/authentication/password-recovery/requests/token/reset`

## Rotas autenticadas

- `GET /api/v1/authentication/me`
- `POST /api/v1/authentication/logout`

## Rotas administrativas

- `POST /api/v1/authentication/admin/password-recovery/requests/users/{userId}/temporary-password`
- `POST /api/v1/authentication/admin/password-recovery/requests/users/{userId}/reset-link`
