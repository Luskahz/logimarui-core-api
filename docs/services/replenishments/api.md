# API - REPOSIÇÕES

## Base URL
{provável domínio Imaruí}/logistics/logimarui/replenishments/

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

#### [ Tela 1 ] - login


#### [ Tela 2 ] Tela Lançamentos
- ***GET*** → **/replenishments/** (busca de reposições, abaixo cabeçalhos e DTO corpo)
  - @Authentication DriverAccessToken
  - @Valid driver.id @obrigatorio
  - @Valid **pos.id** @opcional
  - @Valid **route.id** @opcional
  - @Valid **replenishment.id** @opcional
  - @Valid pageValue
  - 
#### [ Tela 3 ] Ticket Retroativo
- ***GET*** → **/replenishments/**
- @Authentication DriverAccessToken
- @Valid replenishment.id
- @ReturnBody replenishment.body

#### [ Tela 4 ] Tela Lançamento Reposição

##### capturando dados
- ***GET*** → **/auth/me/**
  - Puxa a assinatura do motorista e os mapas atrelados a ele
  - @Authentication DriverAccessToken
  - @Parametro DriverId

- ***GET*** → **/replenishments/line/lookup/**
- traz um tabelão produtos por nota, notas por clientes clientes por mapa
  - @Authentication DriverAccessToken
  - @Valid RouteId @opcional

- ***GET*** → **/replenishments/line/lookup/reason/**
  - @Authentication DriverAccessToken

##### registrando reposições
- ***POST*** → **/replenishments/**
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


- ***POST*** → **/replenishments/line/**
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


- ***PATCH*** → **/replenishments/**
- rec chamada após a criação da replenishmentLine para registrar continuação da escrita do processo de reposição
  - @Authentication DriverAccessToken
  -
  - @RequestBody Replenishment.id
  - @RequestBody lastUpdateData
  - @RequestBody lastUpdateTime

- ***PATCH*** → **/replenishments/**
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

- ***PATCH*** → **/replenishments/**
- deleta a linha de reposição,
  - @Authentication DriverAccessToken
  - 
  - @Valid replenishment.id
  - @RequestBody canceledData
  - @RequestBody canceledTime
  - @Service replenishmentStatus = "CANCELADO"

- ***PATCH*** → **/replenishments/line/**
- alteração da linha reposição, cancelamento apenas a reposição completa
  - @Authentication DriverAccessToken
  - @Valid replenishmentLine.id
  - @RequestBody replenishmentLine.body (pega os dados que n foram alterados e manda o body novo com as alterações)
  - @RequestBody lastUpdateDate
  - @RequestBody lastUpdateTime

##### [ Tela 5 ] - Histórico reposições cliente

- ***GET*** → **/replenishments/history-pos/**
- busca o historico de reposições do cliente ultimas 10
- @Authentication DriverAccessToken
- @Valid pos.id










###### Rotas improváveis (caso as rotas planejadas fiquem pesadas no mobile)
- ***GET*** → **/replenishments/line/lookup/pos/** (encontrar os clientes disponiveis na rota/rotas motorista com base nos filtros)
  - @Authentication DriverAccessToken
  - @Valid route.id @opcional
  - @Valid invoice.number @opcional
  - @Valid invoice.series @opcional

- ***GET*** → **/replenishments/line/lookup/invoice/**
  - @Authentication DriverAccessToken
  - @Valid route.id
  - @Valid pos.id

- ***GET*** → **/replenishments/line/lookup/product/**
  - @Authentication DriverAccessToken
  - @Valid route.id @opcional
  - @Valid pos.id @opcional
  - @Valid invoice.number @opcional
  - @Valid invoice.series @opcional

- ***GET*** → **/replenishments/line/lookup/uom/**
  - @Authentication DriverAccessToken
  - @Valid invoice.number
  - @Valid invoice.series
  - @Valid product.id

