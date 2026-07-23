# PB Tp1

Projeto monolítico simples com Spring Boot no backend e Vue.js com Vuetify no frontend.

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

## Como rodar o frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe em `http://localhost:5173`.

## API

- `GET /api/pedidos` - lista pedidos
- `POST /api/pedidos` - cria pedido
- `PATCH /api/pedidos/{id}/finalizar` - marca pedido como finalizado
