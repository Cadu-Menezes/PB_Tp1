# Implantacao com Docker e Kubernetes

## Docker Compose

O Compose executa RabbitMQ, backend, `preparo-service` e frontend na mesma rede Docker. O backend usa `preparo-service:8082` para chamadas internas e ambos os servicos usam `rabbitmq:5672`.

```bash
docker compose build
docker compose up -d
```

Acessos locais:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8081`
- Preparo-service: `http://localhost:8082`
- RabbitMQ: `http://localhost:15672`

Credenciais locais:

- RabbitMQ: usuário `guest`, senha `guest`;
- Grafana: usuário `admin`, senha inicial `admin`;
- H2 do backend e do `preparo-service`: usuário `sa`, senha em branco.

Para acompanhar os eventos:

```bash
docker compose logs -f backend preparo-service
```

Para encerrar os containers:

```bash
docker compose down
```

Os bancos H2 ficam nos volumes `backend-data` e `preparo-data`.

## Kubernetes

Os manifests ficam em `k8s/` e devem ser aplicados em um cluster com suporte a Deployments, Services e metrics-server para uso do HPA.

As imagens locais precisam ser construidas com os mesmos nomes usados pelos manifests:

```bash
docker build -t pb-tp1/backend:latest ./backend
docker build -t pb-tp1/preparo-service:latest ./preparo-service
docker build -t pb-tp1/frontend:latest ./frontend
```

Em clusters que nao compartilham o daemon Docker local, publique as imagens em um registry e substitua os campos `image` dos manifests.

Aplicacao:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/rabbitmq.yaml
kubectl apply -f k8s/preparo-service.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/hpa.yaml
```

Verificacao:

```bash
kubectl get pods -n pb-tp1
kubectl get services -n pb-tp1
kubectl get hpa -n pb-tp1
```

A aplicacao web fica disponivel no NodePort `30080`. Em Minikube, por exemplo:

```bash
minikube service frontend -n pb-tp1
```

Escala manual:

```bash
kubectl scale deployment backend --replicas=3 -n pb-tp1
kubectl scale deployment preparo-service --replicas=3 -n pb-tp1
```

O HPA mantem entre 2 e 5 replicas do backend e do `preparo-service` conforme o uso de CPU.

## GitHub Actions

O workflow `.github/workflows/ci-cd.yml` automatiza a integração e a entrega:

- em pull requests e pushes na `main`, executa os testes Maven do backend e do `preparo-service` e o build do frontend;
- em pushes na `main`, depois do CI passar, constrói e publica as imagens Docker do backend, `preparo-service` e frontend no GitHub Container Registry;
- pode ser executado manualmente pela opção `workflow_dispatch` do GitHub Actions.

O workflow usa `GITHUB_TOKEN` para publicar no GHCR, sem armazenar credenciais no código. O deploy Kubernetes continua protegido pelos recursos do cluster e pode consumir as imagens publicadas usando as tags `latest` ou o SHA do commit.

