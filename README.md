# PB Tp1

Projeto monolítico simples com Spring Boot no backend e Vue.js com Vuetify no frontend.

## Arquitetura orientada a eventos

O backend publica eventos no RabbitMQ e o `preparo-service` os consome de forma assíncrona. A exchange utilizada é `pedidos.events` e a fila do microsserviço é `preparo-service.pedidos`.

Eventos implementados:

- `pedido.criado` - cria o registro de preparo com status `RECEBIDO`;
- `pedido.finalizado` - atualiza o preparo para `ENTREGUE`;
- `preparo.status-atualizado` - altera o status de preparo informado pela interface.

Essa abordagem reduz o acoplamento entre os serviços e permite que o preparo processe mensagens mesmo que o backend não precise aguardar uma chamada de escrita. Em contrapartida, o processamento é eventual e exige monitoramento, tratamento de mensagens inválidas e disponibilidade do broker.

### Como iniciar o RabbitMQ

Na raiz do projeto, com o Docker em execução:

```bash
docker compose up -d
```

- **AMQP:** `localhost:5672`
- **Painel:** `http://localhost:15672`
- **Usuário:** `guest`
- **Senha:** `guest`

Depois, inicie o backend, o `preparo-service` e o frontend. Os dois serviços Spring usam `localhost:5672` por padrão e aceitam configuração pelos parâmetros `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME` e `RABBITMQ_PASSWORD`.

## O que a aplicação faz

- Cadastra pedidos
- Lista pedidos
- Marca pedidos como finalizados

## Estrutura

- `backend/` - API REST com Spring Boot, Spring Data JPA e H2
- `frontend/` - interface em Vue.js com Vuetify para consumir a API

## Como rodar o backend

```bash
cd backend
mvn spring-boot:run
```

O backend sobe em `http://localhost:8081`.

### Acesso ao banco H2 do backend

Console: `http://localhost:8081/h2-console`

- **JDBC URL:** `jdbc:h2:file:C:/Users/Dwith/Desktop/Cadu/PB_Tp1/backend/data/pbtp1db`
- **Usuário:** `sa`
- **Senha:** deixe em branco

Tabela principal: `PEDIDOS`

Tabela de histórico: `PEDIDO_HISTORICO`

## Como rodar o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe em `http://localhost:5173`.

### Acesso ao banco H2 do microsserviço de preparo

Console: `http://localhost:8082/h2-console`

- **JDBC URL:** `jdbc:h2:file:C:/Users/Dwith/Desktop/Cadu/PB_Tp1/preparo-service/data/preparo-db`
- **Usuário:** `sa`
- **Senha:** deixe em branco

Tabela principal: `PEDIDOS_PREPARO`

## API

- `GET /api/pedidos` - lista pedidos
- `POST /api/pedidos` - cria pedido
- `PATCH /api/pedidos/{id}/finalizar` - marca pedido como finalizado


## Painel RabbitMQ

http://localhost:15672

- **Usuário:** `guest`
- **Senha:** guest