# API - REPOSIÇÕES

## Base URL
{provável domínio Imaruí}/logistica/logimarui/replenishment/

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
- ***POST*** → **/auth/login/**  
- ***POST*** → **/auth/refresh/**  
- ***POST*** → **/auth/logout/**
- ***POST*** → **/auth/register/**
- ***POST*** → **/auth/change-password/**
- ***POST*** → **/auth/forgot-password/**
- ***GET*** → **/auth/me/**

#### Tela Lançamentos
- ***GET*** → **/search/** (busca de reposições, abaixo cabeçalhos e DTO corpo)
  - @Authentication AccessTokenMotorista @obrigatorio
  - @Valid driverId @obrigatorio
  - @Valid **posId** @opcional
  - @Valid **routeId** @opcional
  - @Valid **ReplenishmentId** @opcional
  - @Valid pageValue

#### Tela Lançamento Reposição

#### capturando dados
- ***GET*** → **/auth/me/**
  - Puxa a assinatura do motorista e os mapas atrelados a ele
  - @Authentication DriverAccessToken
  - @Parametro DriverId

- ***GET*** → **/line-replenishment/lookup/**
  - @Authentication DriverAccessToken
  - @Valid RouteId @opcional

- ***GET*** → **/line-replenishment/lookup/reason/**
  - @Authentication DriverAccessToken

#### registrando reposições
- ***POST*** → **/replenishment/**
  - Cria a replenishment e a replenishmentLine espera o body de ambos
  - @Authentication DriverAccessToken
  -
  - @RequestBody routeId @obrigatorio
  - @RequestBody posId @obrigatorio
  - @RequestBody InvoiceNumber @obrigatorio
  - @RequestBody InvoiceSeries @obrigatorio
  -
  - @RequestBody productId @obrigatorio
  - @RequestBody ReplenishmentReason @obrigatorio
  - @RequestBody quantity @obrigatorio
  - @RequestBody unitOfMeansure @obrigatorio
  - @RequestBody damageRecord @obrigatorio(se-reason-for-avaria)
  -
  - @RequestBody initDataReplenishment
  - @RequestBody initTimeReplenishment
  - @Service ReplenishmentStatus = "MOTORISTA_ESCREVENDO"
  -
  - @ReturnBody Replenishment.Id @oculto
  - @ReturnBody LineReplenishment.Id @oculto


- ***POST*** → **/replenishment-line/**
- insere linha de reposiçao no replenishment
  - @Authentication DriverAccessToken
  - 
  - @Valid replenishment.id (já existente)
  - @RequestBody insertionDate
  - @RequestBody insertionTime
  - @RequestBody replenishment.body
  - 
  - @ReturnBody replenishment.id
  - @ReturnBody replenishment.body


- ***PATCH*** → **/replenishment/**
- rec chamada após a criação da replenishmentLine para registrar continuação da escrita do processo de reposição
  - @Authentication DriverAccessToken
  -
  - @RequestBody Replenishment.id
  - @RequestBody lastUpdateData
  - @RequestBody lastUpdateTime

- ***PATCH*** → **/replenishment/**
- rec chamada para finalização da escrita do processo
  - @Authentication DriverAccessToken
  - 
  - @RequestBody Replenishment.id
  - @RequestBody lastUpdateDate
  - @RequestBody lastUpdateTime
  - @Service replenishmentStatus = "REGISTRADO" (garantir enum)
  - 
  - @ReturnBody replenishment.id
  - @ReturnBody replenishment.body (para criação do ticket)

- ***PATCH*** → **/replenishment/**
- deleta a linha de reposição,
  - @Authentication DriverAccessToken
  - 
  - @Valid replenishment.id
  - @RequestBody canceledData
  - @RequestBody canceledTime
  - @Service replenishmentStatus = "CANCELADO"

- ***PATCH*** → **/line-replenishment/**
- alteração da linha reposição, cancelamento apenas a reposição completa
  - @Authentication DriverAccessToken
  - @Valid replenishmentLine.id
  - @RequestBody replenishmentLine.body (pega os dados que n foram alterados e manda o body novo com as alterações)
  - @RequestBody lastUpdateDate
  - @RequestBody lastUpdateTime


##### Rotas improváveis (caso as rotas planejadas fiquem pesadas no mobile)
- ***GET*** → **/line-replenishment/lookup/pos/** (encontrar os clientes disponiveis na rota/rotas motorista com base nos filtros)
  - @Authentication AccessTokenMotorista
  - @Valid codigosMapa @opcional
  - @Valid codigoNotaFiscal @opcional
  - @Valid codigoSerieNotaFiscal @opcional

- ***GET*** → **/line-replenishment/lookup/invoice/**
  - @Authentication AccessTokenMotorista
  - @Valid codigoMapa
  - @Valid codigoCliente

- ***GET*** → **/line-replenishment/lookup/product/**
  - @Authentication AccessTokenMotorista
  - @Valid codigosMapa @opcional
  - @Valid codigoCliente @opcional
  - @Valid notaFiscal @opcional
  - @Valid serieNotaFiscal @opcional

- ***GET*** → **/line-replenishment/lookup/uom/**
  - @Authentication AccessTokenMotorista
  - @Valid notaFiscal
  - @Valid serieNotaFiscal
  - @Valid codigoProduto