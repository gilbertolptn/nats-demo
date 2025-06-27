package dev.gibatech.nats.demo.api;

import org.springframework.web.bind.annotation.*;

import dev.gibatech.nats.demo.usercase.BenchmarkUserCase;

@RestController
@RequestMapping("/v1")
public class BenchmarkApi {

    private final BenchmarkUserCase benchmarkUserCase;

    public BenchmarkApi(BenchmarkUserCase benchmarkUserCase) {
        this.benchmarkUserCase = benchmarkUserCase;
    }

    /**
     * Dispara o benchmark de publicação e recebimento de mensagens.
     * @param subject subject NATS a ser utilizado
     * @param messageCount quantidade de mensagens
     */
    @GetMapping("/benchmark")
    public String runBenchmark(
            @RequestParam(defaultValue = "benchmark.subject") String subject,
            @RequestParam(defaultValue = "1000000") int messageCount
    ) {
        try {
            benchmarkUserCase.benchmark(subject, messageCount);
            return "Benchmark concluído com sucesso.";
        } catch (Exception e) {
            return "Erro ao executar benchmark: " + e.getMessage();
        }
    }
}