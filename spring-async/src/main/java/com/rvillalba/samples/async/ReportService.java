package com.rvillalba.samples.async;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    /**
     * Se ejecuta en un hilo del pool {@code reportExecutor} y devuelve el resultado como
     * {@link CompletableFuture}. El método que lo llama sigue su ejecución sin esperar.
     */
    @Async(AsyncExecutorConfiguration.REPORT_EXECUTOR)
    public CompletableFuture<Report> generate(String name) {
        return CompletableFuture.completedFuture(new Report(name, Thread.currentThread().getName()));
    }

    /**
     * Las excepciones de un método asíncrono que devuelve CompletableFuture llegan al llamante
     * al hacer join()/get(), envueltas en CompletionException/ExecutionException.
     */
    @Async(AsyncExecutorConfiguration.REPORT_EXECUTOR)
    public CompletableFuture<Report> generateFailing(String name) {
        throw new IllegalArgumentException("No se puede generar el informe " + name);
    }

    public record Report(String name, String generatedBy) {
    }
}
