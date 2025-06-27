# Getting Started com NATS no Spring Boot

Este guia mostra como criar um projeto de exemplo para publicar e consumir mensagens usando NATS como broker de mensagens, utilizando Spring Boot.

## 1. Criação do Projeto

Crie um novo projeto Spring Boot com as dependências básicas:

- Spring Boot Starter Web
- NATS (biblioteca Java)
- Datafaker (para geração de dados de exemplo)

No `build.gradle`:

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'io.nats:jnats:2.19.0'
    implementation 'net.datafaker:datafaker:2.2.2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

## 2. Configuração do NATS

Adicione um arquivo `docker-compose.yml` para subir o NATS localmente:

```yaml
version: '3.8'
services:
  nats:
    image: nats:2.10
    ports:
      - "4222:4222"
```

Inicie o NATS com:

```sh
docker compose up -d
```

## 3. Configuração de Host e Porta

No seu código Java, configure o host e a porta do NATS (por padrão, `localhost:4222`):

```java
String natsUrl = "nats://localhost:4222";
Connection natsConnection = Nats.connect(natsUrl);
```

## 4. Publicando Mensagens

Exemplo de publicação de mensagem:

```java
import io.nats.client.Connection;
import io.nats.client.Nats;

public void publishMessage(String subject, String message) throws Exception {
    try (Connection nc = Nats.connect("nats://localhost:4222")) {
        nc.publish(subject, message.getBytes(StandardCharsets.UTF_8));
    }
}
```

## 5. Consumindo Mensagens

Exemplo de assinatura de mensagens:

```java
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;

public void subscribe(String subject) throws Exception {
    Connection nc = Nats.connect("nats://localhost:4222");
    Dispatcher d = nc.createDispatcher((msg) -> {
        System.out.println("Recebido: " + new String(msg.getData(), StandardCharsets.UTF_8));
    });
    d.subscribe(subject);
}
```

## 6. Integração com Spring Boot

Você pode criar um `@Service` para publicar e consumir mensagens, injetando as dependências conforme necessário.

## 7. Testando

Execute a aplicação Spring Boot:

```sh
./gradlew bootRun
```

Dispare requisições para publicar ou consumir mensagens conforme sua API REST.

---

Para mais detalhes, consulte a [documentação oficial do NATS Java](https://docs.nats.io/using-nats/developer/connecting/java)