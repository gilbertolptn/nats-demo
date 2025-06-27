package dev.gibatech.nats.demo.infra;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
public class NatsClient implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(NatsClient.class);
    private Connection connection;

    @Value("${nats.host:localhost}")
    private String natsHost;

    @Value("${nats.port:4222}")
    private int natsPort;

    @PostConstruct
    public void connect() throws Exception {
        String url = String.format("nats://%s:%d", natsHost, natsPort);
        logger.info("Conectando ao servidor NATS em {}...", url);
        connection = Nats.connect(url);
        logger.info("Conexão com NATS estabelecida.");
    }

    public void publish(String subject, String payload) {
        try {
            connection.publish(subject, payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Erro ao publicar no subject '{}': {}", subject, e.getMessage(), e);
        }
    }

    public Dispatcher subscribe(String subject, Consumer<byte[]> handler) {
        Dispatcher dispatcher = connection.createDispatcher(msg -> handler.accept(msg.getData()));
        dispatcher.subscribe(subject);
        return dispatcher;
    }

    @Override
    public void destroy() throws Exception {
        if (connection != null) {
            logger.info("Fechando conexão com NATS...");
            connection.close();
            logger.info("Conexão com NATS fechada.");
        }
    }
}