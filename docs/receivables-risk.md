# Atenção preventiva — risco comportamental v1

Implementação em 31/10/2026. Modelo `BEHAVIOURAL_V1`, somente leitura, por unidade confirmada + cliente.
O nível 1–5 é uma faixa operacional de comportamento observado, não uma probabilidade de inadimplência,
rating regulatório, estimativa de perda esperada ou decisão automática de concessão de crédito.

## Fundamento e limites

O [BIS, General credit risk management](https://www.bis.org/committees/bcbs/basel-consolidated-guidelines/module/cri/10)
recomenda considerar histórico de pagamento, capacidade de pagamento e deterioração da exposição.
O [BIS, Expected credit losses](https://www.bis.org/committees/bcbs/basel-consolidated-guidelines/module/pap/20)
orienta identificar deterioração antes da inadimplência e validar modelos e classificações.
Esses princípios motivam as dimensões abaixo; **não prescrevem os pesos, cortes ou fórmula desta implementação**.
Sem renda, fluxo de caixa, perdas efetivas, calendário bancário ou histórico externo, o sistema mede apenas
comportamento nas cargas disponíveis. Valores maiores associados a atrasos são associação descritiva, não causalidade.

Os pesos e cortes iniciais precisam de homologação operacional e posterior validação temporal com dados mais completos.
Para calibração futura: definir desfecho (ex.: atraso >30 dias nos próximos 60 dias), reconstruir as informações
que realmente estavam disponíveis em cada data, separar treino e validação cronologicamente, medir precisão,
recall, falsos alertas e taxa de desfecho por faixa, revisar estabilidade por unidade/volume e versionar mudanças.
Não reconstruir carteira histórica a partir da carteira atual: faltam snapshots e isso introduziria viés temporal.

## Fontes, ano e contagem

- Carteira `12_06_01`: mantém saldo positivo e CA/PRZ vazio/NULL, excluindo A/I.
- Histórico `12_06_06`: consulta adicional por **vencimento de 01/01 até hoje**, com pagamento até hoje.
  Inclui adiantamentos feitos antes de janeiro para vencimentos do ano. Vencimentos futuros não entram no denominador.
- O filtro `historyFrom/historyTo` continua controlando os eventos mostrados no detalhe e os indicadores antigos;
  não altera a análise anual. Filtros de carteira também não alteram o risco calculado para o cliente.
- O usuário confirmou em 31/10/2026 a correspondência **UNB histórica 0 → UNB carteira 1**.
  Configuração: `app.receivables.history-unit-mappings=${RECEIVABLES_HISTORY_UNIT_MAPPINGS:0=1}`;
  variável vazia desativa o alias; outras associações precisam de confirmação própria.
- A identidade da observação é título + parcela normalizada dentro do cliente/unidade.
  Pagamentos parciais são somados para valores e agrupados em uma única observação para frequência.
  Mantém a deduplicação por payload completo do repositório; não confunde parcelas distintas.
- Somam-se as parcelas monetárias dos eventos válidos e, quando ainda vencido, o saldo pendente atual do mesmo título.
  Isso é **valor observado**, não valor original contratado nem comprovação de quitação integral.
- Um título conta como atrasado se há qualquer parcela monetária observada paga após vencimento ou saldo ainda vencido.
  Conta uma vez mesmo que haja vários eventos/anos de pagamento. O ano se refere ao **vencimento**, não ao pagamento.
- Atrasos de títulos vencidos antes de janeiro não entram na taxa anual, mas o saldo e o maior atraso atual
  continuam visíveis e podem elevar o nível mínimo.
- Dias corridos, referência America/Sao_Paulo; sem tolerância, calendário de feriados ou ajuste de finais de semana.
- Sem datas válidas, com valor não positivo ou pagamento futuro: evento excluído dos cálculos.

## Métricas e fórmula

Cada componente limita sua fração ao intervalo [0,1] e arredonda os pontos ao inteiro mais próximo.
O índice é a soma dos seis componentes, no intervalo 0–100.

| Componente | Máximo | Fração utilizada |
|---|---:|---|
| Frequência | 30 | títulos com atraso / títulos observados maduros |
| Severidade | 20 | média de dias ponderada pelo valor atrasado / 30 |
| Recência | 15 | títulos atrasados / títulos observados com vencimento na janela recente |
| Valor atrasado | 15 | valor das parcelas atrasadas / todo valor observado |
| Sensibilidade ao valor | 10 | taxa de atraso dos títulos altos menos taxa dos demais |
| Exposição | 10 | (saldo pendente total / mediana em dia − 1) / 2 |

A severidade usa `SUM(valor_da_parcela_atrasada × dias_atraso) / SUM(valor_da_parcela_atrasada)`.
Não dilui dias com pagamentos pontuais. Não havendo atraso, o componente vale zero e a média fica ausente.
A janela recente usa `recent-days` (90 por padrão), relativa ao vencimento, dentro da coorte anual.
Mediana em dia: mediana da soma dos pagamentos observados de cada título sem atraso e sem saldo vencido associado;
não é capacidade financeira comprovada. Título alto: valor observado **>1,5×** essa mediana.
Sensibilidade só pontua se há ao menos três observações em dia e três em cada grupo (alto/demais),
e a taxa dos altos é maior. As taxas e tamanhos dos grupos ficam expostos mesmo quando não pontuam.
Exposição só pontua com ao menos três títulos em dia e evidência de atraso. Valor alto sozinho não gera alerta.

Níveis pelo índice: 1 = 0–19; 2 = 20–39; 3 = 40–59; 4 = 60–79; 5 = 80–100.
Com histórico suficiente, atraso atual máximo estabelece pisos operacionais:
8–29 dias → nível mínimo 3; 30–89 → mínimo 4; 90+ → mínimo 5.
O piso altera o nível, preservando o índice bruto e seu motivo: por exemplo, índice 10 e nível 4 por atraso atual.
Esses pisos também são parâmetros operacionais do modelo v1, não declaração de default regulatório.

Exige `max(3, minimum-history-titles)` títulos distintos com pagamentos válidos no ano (padrão 5).
Títulos abertos aumentam exposição e frequência, mas não substituem essa amostra mínima de pagamentos.
Falha da fonte, ausência de dados, unidade sem associação ou amostra insuficiente resultam em nível/índice NULL;
nunca viram baixo risco. Mantém valores e contagens observadas para consulta, com status explicativo.

## Uso e contrato

Cada item da lista/detalhe e da exportação recebe `risk`, com versão, janela, status, nível, índice,
contagens, taxas, valores, componentes e motivos. Os indicadores de eventos em `history` são preservados.
Sem campo `risk`, o frontend mostra que o servidor ainda não oferece a análise anual (compatibilidade na promoção).
`riskLevel=1..5` filtra antes do resumo/paginação; `sort=RISK_DESC` ordena níveis decrescentes, desconhecidos ao fim.
CSV usa os mesmos filtros e inclui os indicadores de risco. Métricas do cliente se repetem por título;
não devem ser somadas entre linhas como clientes diferentes.

Prioridade: vencido → `OVERDUE`; sem vencimento → `DATA_REVIEW`; vencendo em até `upcoming-days`
e nível >=3 → `PREVENTIVE`; demais próximos → `DUE_SOON`; outros → `MONITOR`.
Na ordenação PRIORITY, usa nível decrescente dentro de cada prioridade, depois vencimento, saldo e chave.
O parâmetro antigo `recurring-late-titles` permanece no contrato por compatibilidade, mas não decide a nova preventiva.

## Verificação e promoção

Testes cobrem pagamentos parciais, ponderação monetária, valores altos com/sem amostra,
histórico ausente, anos/datas futuras, pisos por atraso, independência de filtros e exportação.
Integração opcional roda MySQL com sessão `TRANSACTION READ ONLY`, reconcilia carteira e consulta anual.
Frontend verifica contrato anterior/novo, rejeição de risco inválido, renderização de métricas/ausência de histórico,
transporte autenticado e filtros da exportação. Build em ambos os projetos antes da promoção.

Em 31/10/2026, leitura do banco local encontrou 7.109 eventos históricos (pagamentos de 30/06 a 29/10),
468 títulos elegíveis e 252 clientes. Auditoria da versão inicial: 246 clientes com amostra suficiente,
6 insuficientes; níveis 1=220, 2=18, 3=6, 4=2, 5=0; 9 títulos preventivos.
É uma descrição da carga nessa data, **não backtest preditivo nem evidência de cobertura anual completa**.
Sem data de carga confiável, `sourceUpdatedAt` continua NULL. A tela sinaliza cobertura parcial.

Para PROD: puxar os commits de backend e frontend de `origin/master` e executar o `refresh.bat` habitual.
O launcher integrado agora compila o backend e o frontend em pastas isoladas antes de parar o servidor.
Falha de compilação de qualquer um preserva os processos e artefatos ativos. Depois da parada dos processos
confirmados, instala o novo JAR, guarda o anterior para recuperação e inicia as duas versões atualizadas.
O diretório de build do Maven pode ser isolado com `-Dlogimarui.build.directory=target/<diretorio>`.
Conferir lista/detalhe/CSV com sessão autorizada após a atualização.
Nenhuma migração, alteração de saldo, índice SQL ou carga de dados é necessária.

## Correção de atualização parcial em 31/10/2026

O PROD estava com frontend novo e JAR de 30/10, sem a análise anual, o filtro `riskLevel` e a associação UNB 0=1.
O antigo launcher compilava apenas o frontend durante o reinício. Reiniciar o JAR antigo mantinha a inconsistência.
O contrato agora inclui `metadata.riskModel=BEHAVIOURAL_V1`, inclusive para consultas vazias.
O frontend avisa quando essa capacidade está ausente, desabilita os controles de risco e rejeita
respostas incompatíveis com o nível solicitado, em vez de mostrar um filtro aparentemente aplicado.

A conciliação é por cliente + unidade confirmada. A carteira é o snapshot de títulos abertos, enquanto
o histórico busca os pagamentos de todos os títulos desse cliente/unidade, sem exigir que o título pago
continue na carteira. A união por título/parcela é usada somente para consolidar as observações de risco
e os pagamentos parciais, preservando as duas partes monetárias: pago observado e saldo aberto atual.
Na carga consultada às 15:52, a integração READ reconciliou 506 títulos e 275 clientes, 273 com amostra
suficiente e 2 insuficientes; identificou 3 títulos preventivos. O teste verificou também detalhe e filtro
por cada nível presente na carga, incluindo totais de saldo, sem alterar as tabelas.
