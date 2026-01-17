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
  - @Valid codigoMotorista @obrigatorio
  - @Valid **codigoCliente** @opcional
  - @Valid **codigoMapa** @opcional
  - @Valid **codigoReposicao** @opcional
  - @Valid valorPaginacao

#### Tela Lançamento Reposição

- ***GET*** → **/line-replenishment/deliver-route/** (encontra os mapas atrelados ao motorista)
  - @Authentication AccessTokenMotorista
  - @Parametro codigoMotorista

- ***GET*** → **/line-replenishment/pos/** (encontrar os clientes disponiveis na rota/rotas motorista com base nos filtros)
  - @Authentication AccessTokenMotorista
  - @Valid codigosMapa @opcional
  - @Valid codigoNotaFiscal @opcional
  - @Valid codigoSerieNotaFiscal @opcional

- ***GET*** → **/line-replenishment/invoice/**
    - @Authentication AccessTokenMotorista
    - @Valid codigoMapa
    - @Valid codigoCliente

- ***GET*** → **/line-replenishment/product/**
  - @Authentication AccessTokenMotorista
  - @Valid codigosMapa @opcional
  - @Valid codigoCliente @opcional
  - @Valid notaFiscal @opcional
  - @Valid serieNotaFiscal @opcional

- ***GET*** → **/line-replenishment/uom/**
  - @Authentication AccessTokenMotorista
  - @Valid notaFiscal
  - @Valid serieNotaFiscal
  - @Valid codigoProduto

- ***GET*** → **/line-replenishment/motivos/**
  - @Authentication AccessTokenMotorista

- ***POST*** → **/line-replenishment/**
  - @Authentication AccessTokenMotorista
  - @RequestBody codigosMapa @obrigatorio
  - @RequestBody codigoCliente @obrigatorio
  - @RequestBody codigoNotaFiscal @obrigatorio
  - @RequestBody codigoSErieNotaFiscal @obrigatorio
  - @RequestBody codigoProduto @obrigatorio
  - @RequestBody motivo @obrigatorio
  - @RequestBody quantidade @obrigatorio
  - @RequestBody unidade-medida @obrigatorio
  - @RequestBody imagem-avaria @obrigatorio 

- ***PUT*** → **/line-replenishment/**
    - @Authentication AccessTokenMotorista
    - @RequestBody codigosMapa @obrigatorio
    - @RequestBody codigoCliente @obrigatorio
    - @RequestBody codigoNotaFiscal @obrigatorio
    - @RequestBody codigoSErieNotaFiscal @obrigatorio
    - @RequestBody codigoProduto @obrigatorio
    - @RequestBody motivo @obrigatorio
    - @RequestBody quantidade @obrigatorio
    - @RequestBody unidade-medida @obrigatorio
    - @RequestBody imagem-avaria @obrigatorio 

- ***POST*** → **/replenishment/**
- essa req é chamada quando o motorista manda um post com apenas 1 objeto de reposição, ele cria a reposição, e atualiza o status para escrevendo
  - @Authentication AccessTokenMotorista
  - @Valid finalizacao = 0
  - @RequestBody codigoCliente
  - @RequestBody codigoMapa
  - @RequestBody codigoNotaFiscal
  - @RequestBody codigoSerieNotaFiscal
  - @RequestBody dataOcorrencia
  - @RequestBody horarioOcorrencia
  - @RequestBody statusReposicao = "MOTORISTA_ESCREVENDO"
  - @RequestBody line-replenishment
  - @ReturnBody codigoReposicao (codigo gerado ao iniciar o processo de reposição pelo motorista)

- ***POST*** → **/replenishment/**
- rec chamada para continuação da escrita do processo de reposição
    - @Authentication AccessTokenMotorista
    - @Valid finalizacao = false
    - @RequestBody codigoReposicao
    - @RequestBody codigoCliente
    - @RequestBody codigoMapa
    - @RequestBody codigoNotaFiscal
    - @RequestBody codigoSerieNotaFiscal
    - @RequestBody dataOcorrencia
    - @RequestBody horarioOcorrencia
    - @RequestBody statusReposicao = "MOTORISTA_ESCREVENDO"
    - @RequestBody line-replenishment

- ***PATCH*** → **/replenishment/**
- rec chamada para finalização da escrita do processo
    - @Authentication AccessTokenMotorista
    - @RequestBody codigoReposicao
    - @RequestBody dataOcorrenciaFinalizadaMotorista
    - @RequestBody horarioOcorrenciaFinalizadoMotorista
    - @RequestBody statusReposicao = "REGISTRADO"
    - @ReturnBody Replenishment(objeto json completo com o List<line-replenishment>)