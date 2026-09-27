# Observabilidade

A stack local de observabilidade é composta por:

- Actuator: saúde e métricas dos microsserviços;
- Micrometer Tracing + OpenTelemetry: `traceId` e `spanId` nos logs e traces;
- Jaeger: rastreamento de transações distribuídas;
- Loki: armazenamento e consulta de logs;
- Grafana Alloy: coleta dos logs dos containers Docker;
- Grafana: interface para logs e traces.

Suba a stack com:

```powershell
docker compose up -d --build
```

Acessos:

- Grafana: `http://localhost:3000`
	- Usuário: `admin`
	- Senha inicial: `admin`
- Jaeger: `http://localhost:16686`
- Actuator backend: `http://localhost:8081/actuator`
- Métricas backend: `http://localhost:8081/actuator/prometheus`
- Actuator preparo-service: `http://localhost:8082/actuator`
- Métricas preparo-service: `http://localhost:8082/actuator/prometheus`

### Credenciais

| Interface | Endereço | Usuário | Senha |
| --- | --- | --- | --- |
| Grafana | `http://localhost:3000` | `admin` | `admin` |
| RabbitMQ | `http://localhost:15672` | `guest` | `guest` |
| H2 backend | `http://localhost:8081/h2-console` | `sa` | deixe em branco |
| H2 preparo-service | `http://localhost:8082/h2-console` | `sa` | deixe em branco |

O Jaeger, o frontend, os endpoints Actuator e as métricas Prometheus não exigem login nesta configuração local.

No Grafana, use o Explore com a fonte Loki e consulte:

```logql
{service="backend"}
{service="preparo-service"}
{service=~"backend|preparo-service"} |= "pedidoId"
```

No Jaeger, pesquise pelos serviços `pb-tp1` e `preparo-service`. Os logs dos dois serviços exibem `traceId` e `spanId`, permitindo relacionar uma requisição com o processamento posterior.
