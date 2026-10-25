package com.rvillalba.samples.async;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    @Qualifier(AsyncExecutorConfiguration.REPORT_EXECUTOR)
    private ThreadPoolTaskExecutor reportExecutor;

    @Test
    void executorIsConfiguredFromProperties() {
        assertThat(reportExecutor.getCorePoolSize()).isEqualTo(4);
        assertThat(reportExecutor.getMaxPoolSize()).isEqualTo(8);
        assertThat(reportExecutor.getQueueCapacity()).isEqualTo(100);
        assertThat(reportExecutor.getThreadNamePrefix()).isEqualTo("report-");
    }

    @Test
    void generateRunsInTheReportExecutor() {
        var report = reportService.generate("ventas").join();

        assertThat(report.name()).isEqualTo("ventas");
        assertThat(report.generatedBy())
                .startsWith("report-")
                .isNotEqualTo(Thread.currentThread().getName());
    }

    @Test
    void severalReportsRunInParallel() {
        var futures = IntStream.range(0, 10)
                .mapToObj(i -> reportService.generate("informe-" + i))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        assertThat(futures).allSatisfy(f -> assertThat(f.join().generatedBy()).startsWith("report-"));
    }

    @Test
    void exceptionsArePropagatedThroughTheFuture() {
        var future = reportService.generateFailing("roto");

        assertThatThrownBy(future::join)
                .isInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se puede generar el informe roto");
    }
}
