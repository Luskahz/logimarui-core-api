# API - REPOSIÇÕES

## Base URL
{provável domínio Imaruí}/logistics/logimarui/replenishments/

## Authenticação
implementado, reponsábilidade do módulo Auth


### Rotas existentes (replenishment)
Não há rotas implementadas definidademente, apenas chamadas na pasta controller
gerenciando o service para deixar as rotas funcionais, abaixo segue as rotas funcionais:
 - não há rotas funcionais

### Rotas planejadas
    
#### [ Tela 1 ] Tela histórico reposições do motorista
- ***GET*** → **/replenishments/** (busca de reposições, abaixo cabeçalhos e DTO corpo)
  - @Authentication Authentication
  - @Valid driver.id @obrigatorio
  - @Valid **pos.id** @opcional
  - @Valid **route.id** @opcional
  - @Valid **replenishment.id** @opcional
  - @Valid pageValue
  - 
#### [ Tela 2 ] Ticket Reposição (retroativo e atual)
- ***GET*** → **/{id}/ticket**
- @Authentication Authentication
- @Valid replenishment.id
- @ReturnBody replenishment.body

#### [ Tela 3 ] Tela Lançamento Reposição

##### capturando dados
- ***GET*** → **/me**
  - Puxa a assinatura do motorista e os mapas atrelados a ele
  - @Authentication Authentication
  - @Parametro DriverId

- ***GET*** → **line/{deliver-router-id}/lookup**
- traz um tabelão produtos por nota, notas por clientes clientes por mapa
  - @Authentication Authentication
  - @Valid RouteId @opcional

##### registrando reposições
- ***POST*** → **/replenishments/**
  - Cria a replenishment e a replenishmentLine espera o body de ambos
  - @Authentication Authentication
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
  - @Authentication Authentication
  - 
  - @Valid replenishment.id (já existente)
  - @RequestBody insertionDate
  - @RequestBody insertionTime
  - @RequestBody replenishment.body
  - 
  - @ReturnBody replenishment.id
  - @ReturnBody replenishment.body


- ***PATCH*** → **/{id}/conclude**
- rec chamada para finalização da escrita do processo
  - @Authentication Authentication
  - 
  - @RequestBody Replenishment.id
  - @RequestBody lastUpdateDate
  - @RequestBody lastUpdateTime
  - @Service replenishmentStatus = "REGISTRADO" (garantir enum)
  - 
  - @ReturnBody replenishment.id
  - @ReturnBody replenishment.body (para criação do ticket)


- ***DELETE*** → **/replenishments/**
- cancela a reposição e todas as suas linhas incluídas,
  - @Authentication Authentication
  - 
  - @Valid replenishment.id
  - @RequestBody canceledData
  - @RequestBody canceledTime
  - @Service replenishmentStatus = "CANCELADO"

- ***PATCH*** → **/replenishments/line/**
- alteração da linha reposição, cancelamento apenas a reposição completa
  - @Authentication Authentication
  - @Valid replenishmentLine.id
  - @RequestBody replenishmentLine.body (pega os dados que n foram alterados e manda o body novo com as alterações)
  - @RequestBody lastUpdateDate
  - @RequestBody lastUpdateTime

##### [ Tela 4 ] - Histórico reposições cliente

- ***GET*** → **/replenishments/history-pos/**
- busca o historico de reposições do cliente ultimas 10
- @Authentication Authentication
- @Valid pos.id


###### Rotas improváveis (caso as rotas planejadas fiquem pesadas no mobile)
- ***GET*** → **/replenishments/line/lookup/pos/** (encontrar os clientes disponiveis na rota/rotas motorista com base nos filtros)
  - @Authentication Authentication
  - @Valid route.id @opcional
  - @Valid invoice.number @opcional
  - @Valid invoice.series @opcional

- ***GET*** → **/replenishments/line/lookup/invoice/**
  - @Authentication Authentication
  - @Valid route.id
  - @Valid pos.id

- ***GET*** → **/replenishments/line/lookup/product/**
  - @Authentication Authentication
  - @Valid route.id @opcional
  - @Valid pos.id @opcional
  - @Valid invoice.number @opcional
  - @Valid invoice.series @opcional

- ***GET*** → **/replenishments/line/lookup/uom/**
  - @Authentication Authentication
  - @Valid invoice.number
  - @Valid invoice.series
  - @Valid product.id

