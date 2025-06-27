## Streaming Demo

Este projeto é uma aplicação de demonstração para benchmarking de publicação e recebimento de mensagens usando NATS como broker de mensagens, desenvolvida com Spring Boot.

### Funcionalidades

- Geração de payloads do tipo `Pessoa`
- Publicação e assinatura de mensagens via NATS
- Benchmark de envio e recebimento de mensagens
- API REST para disparar o benchmark

### Requisitos

- Java 24
- Docker (necessário apenas instalado, o Spring Boot sobe o NATS automaticamente)
- Gradle

### Como rodar

1. **Execute a aplicação Spring Boot** (o NATS será iniciado automaticamente via Docker Compose):

   ```sh
   ./gradlew bootRun
   ```

2. **Dispare o benchmark via API REST:**

   Exemplo usando `curl`:
   ```sh
   curl -X GET http://localhost:8080/v1/benchmark
   ```

### Como rodar os testes

```sh
./gradlew test
```