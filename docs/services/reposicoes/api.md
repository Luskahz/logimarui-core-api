# API - REPOSIÇÕES

## Base URL
{provável domínio Imaruí}/logistica/logimarui/reposicoes/

## Authenticação
Ainda não implementado, porém o projeto
visa usar o codigo motorista já 
existente no sistema promax da ***imaruí
ambev***, a ideia é associar o motorista a seu código do promax já existente, porém possibilitar o gerenciamento de acesso do perfil ao motorista portador do codigo/matrícula

### Funcionalidades planejadas:
 - Token de acesso padrão(Acess token)
 - Token de acesso refresh(Refresh token)
 - Atualização do RefreshToken diariamente
 - Gerenciamento de sessão
 - Logout de sessão funcional

### Rotas existentes
Não há rotas implementadas definidademente, apenas chamadas na pasta controller
gerenciando o service para deixar as rotas funcionais, abaixo segue as rotas funcionais:
 - não há rotas funcionais
### Rotas planejadas
#### Tela login
- ***POST*** → /auth/login/  
- ***POST*** → /auth/refresh/  
- ***POST*** → /auth/logout/
- ***POST*** → /auth/register/
- ***POST*** → /auth/change-password/
- ***POST*** → /auth/forgot-password/
- ***GET*** → /auth/me/
#### Tela Lançamentos
- ***GET*** → /buscar/ 
  - @Authentication AccessTokenMotorista 
  - @Parametro codigoMotorista
  - @Parametro codigoCliente

- ***GET*** → /buscar/ 
  - @Authentication AccessTokenMotorista 
  - @Parametro codigoMotorista
  - @Parametro **codigoCliente**
  - @Parametro valorPaginacao

- ***GET*** → /buscar/
    - @Authentication AccessTokenMotorista 
    - @Parametro codigoMotorista
    - @Parametro **String**
    - @Parametro valorPaginacao

- ***GET*** → /buscar/
    - @Authentication AccessTokenMotorista
    - @Parametro codigoMotorista
    - @Parametro **codigoMapa**
    - @Parametro valorPaginacao

- ***GET*** → /buscar/
  - @Authentication AccessTokenMotorista
  - @Parametro codigoMotorista
  - @Parametro **codigoReposicao**
  - @Parametro valorPaginacao
#### Tela Lançamento Reposição

- ***GET*** → /buscar/mapas/
  - @Authentication AccessTokenMotorista
  - @Parametro codigoMotorista

- ***GET*** → /buscar/clientes/
  - @Authentication AccessTokenMotorista
  - @Parametro codigosMapa (todos os mapas para gerar uma lista completa dos clientes)

- ***GET*** → /buscar/clientes/
    - @Authentication AccessTokenMotorista
    - @Parametro codigosMapa
    - @Parametro codigoNotaFiscal (especificidade, list Reduzida)

- ***GET*** → /buscar/produtos/
  - @Authentication AccessTokenMotorista
  - @Parametro notaFiscal
  - @Parametro serieNotaFiscal

- ***GET*** → /buscar/notas-fiscais/
  - @Authentication AccessTokenMotorista
  - @Parametro codigoMapa
  - @Parametro codigoCliente

- ***GET*** → /buscar/motivos/
- @Authentication AccessTokenMotorista

- ***POST*** → /registrar-reposicao/
- @Authentication AccessTokenMotorista
- @todos-atributos-classe-necessarios
