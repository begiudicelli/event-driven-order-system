APP_NAME=event-driven-order-system
SPRING_PROFILE?=dev

.PHONY: dev docker build test test-coverage infra down clean logs rebuild ps help

## Rodar ambiente de desenvolvimento
dev:
	docker compose up -d postgres rabbitmq
	@echo "Aguardando Postgres ficar healthy..."
	@until [ "$$(docker inspect --format='{{.State.Health.Status}}' order-postgres 2>/dev/null)" = "healthy" ]; do \
		printf '.'; sleep 2; \
	done
	@echo " Postgres pronto."
	@echo "Aguardando RabbitMQ ficar healthy..."
	@until [ "$$(docker inspect --format='{{.State.Health.Status}}' order-rabbitmq 2>/dev/null)" = "healthy" ]; do \
		printf '.'; sleep 2; \
	done
	@echo " RabbitMQ pronto."
	@echo "Iniciando aplicacao com profile=$(SPRING_PROFILE)"
	mvn spring-boot:run -Dspring-boot.run.profiles=$(SPRING_PROFILE)

## Subir tudo via Docker
docker: build
	docker compose up --build

## Build do projeto (sem testes)
build:
	mvn clean package -DskipTests

## Rodar testes
test:
	mvn clean test

## Rodar testes com relatorio de cobertura (JaCoCo)
test-coverage:
	mvn clean verify
	@echo ""
	@echo "Relatorio disponivel em: target/site/jacoco/index.html"

## Subir apenas banco e RabbitMQ
infra:
	docker compose up -d postgres rabbitmq

## Parar containers
down:
	docker compose down

## Limpar containers + volumes (reset total)
clean:
	docker compose down -v
	mvn clean

## Ver logs dos containers em tempo real
logs:
	docker compose logs -f

## Rebuild completo (para quando mudar o Dockerfile)
rebuild:
	docker compose down
	docker compose up --build

## Status dos containers
ps:
	docker compose ps

## Ajuda
help:
	@echo ""
	@echo "  make dev            -> infra no Docker + app local (profile=$(SPRING_PROFILE))"
	@echo "  make docker         -> tudo via Docker"
	@echo "  make build          -> build Maven sem testes"
	@echo "  make test           -> roda testes"
	@echo "  make test-coverage  -> testes + relatorio JaCoCo"
	@echo "  make infra          -> sobe apenas postgres + rabbitmq"
	@echo "  make down           -> para containers"
	@echo "  make clean          -> limpa tudo (containers + volumes + build)"
	@echo "  make logs           -> logs dos containers"
	@echo "  make rebuild        -> rebuild docker completo"
	@echo "  make ps             -> status dos containers"
	@echo ""
	@echo "  Variaveis:"
	@echo "    SPRING_PROFILE    -> perfil Spring ativo (default: dev)"
	@echo ""
