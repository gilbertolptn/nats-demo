package dev.gibatech.nats.demo.usercase;

import com.google.gson.Gson;

import dev.gibatech.nats.demo.infra.NatsClient;
import dev.gibatech.nats.demo.model.Pessoa;
import dev.gibatech.nats.demo.model.PessoaFactory;
import dev.gibatech.nats.demo.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class BenchmarkUserCase {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkUserCase.class);    
    private static final int LATCH_TIMEOUT_SECONDS = 30;

    private final Gson gson = new Gson();
    private final NatsClient natsClient;

    public BenchmarkUserCase(NatsClient natsClient) {
        this.natsClient = natsClient;
    }

    public void benchmark(String subject, int messageCount) throws Exception {
        logger.info("Iniciando benchmark: enviando e recebendo {} mensagens no subject '{}'", messageCount, subject);

        CountDownLatch latch = new CountDownLatch(messageCount);

        subscribe(subject, latch);

        List<Pessoa> payloads = gerarPayloads(messageCount);

        double sendSec = publicarMensagens(subject, payloads);

        double recvSec = receberMensagens(subject, latch);

        latch.await(LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (latch.getCount() == 0) {
            logger.info("Benchmark concluído: geração={}s, envio={}s, recebimento={}s, total={}s",
                    String.format("%.2f", recvSec),
                    String.format("%.2f", sendSec),
                    String.format("%.2f", recvSec),
                    String.format("%.2f", (recvSec + sendSec + recvSec)));
        } else {
            logger.warn("Nem todas as mensagens ({}/{}) foram recebidas no tempo limite.",
                    messageCount - latch.getCount(), messageCount);
        }
    }

    private List<Pessoa> gerarPayloads(int messageCount) {
        Timer timer = new Timer();
        timer.start();
        List<Pessoa> payloads = new ArrayList<>(messageCount);
        for (int i = 0; i < messageCount; i++) {
            payloads.add(PessoaFactory.criarPessoa(i));
        }
        double genSec = timer.stopAndGetSeconds();
        logger.info("Tempo para gerar {} payloads: {} segundos", messageCount, String.format("%.2f", genSec));
        return payloads;
    }

    private double publicarMensagens(String subject, List<Pessoa> payloads) {
        Timer timer = new Timer();
        timer.start();
        for (Pessoa payload : payloads) {
            String json = gson.toJson(payload);
            natsClient.publish(subject, json);
        }
        double sendSec = timer.stopAndGetSeconds();
        logger.info("Tempo para enviar {} mensagens: {} segundos", payloads.size(), String.format("%.2f", sendSec));
        return sendSec;
    }

    private double receberMensagens(String subject, CountDownLatch latch) {
        Timer timer = new Timer();
        timer.start();
        // O await agora é feito no método benchmark
        double recvSec = timer.stopAndGetSeconds();
        return recvSec;
    }

    private void subscribe(String subject, CountDownLatch latch) {
        natsClient.subscribe(subject, data -> {
            try {
                String json = new String(data, StandardCharsets.UTF_8);
                Pessoa payload = gson.fromJson(json, Pessoa.class);
                logger.trace("Payload recebido: {}", payload);
            } catch (Exception e) {
                logger.error("Erro ao desserializar payload recebido", e);
            }
            latch.countDown();
        });
        logger.info("Subscribe registrado");
    }
}