package rgo.tt.authentication.boot;

import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceNaming;
import com.linecorp.armeria.server.cors.CorsService;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.server.metric.PrometheusExpositionService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rgo.tt.common.armeria.ProbeService;
import rgo.tt.common.headers.HeadersDecorator;
import rgo.tt.common.logger.LoggingDecorator;

import java.util.function.Function;

@Configuration
public class ArmeriaConfig {

    @Autowired private ProbeService probeService;

    @Autowired private Function<? super HttpService, CorsService> corsDecorator;
    @Autowired private Function<? super HttpService, HeadersDecorator> headersDecorator;
    @Autowired private Function<? super HttpService, MetricCollectingService> metricsDecorator;
    @Autowired private Function<? super HttpService, LoggingDecorator> loggingDecorator;

    @Autowired private PrometheusMeterRegistry registry;

    @Bean
    public ArmeriaServerConfigurator armeriaConfigurator() {
        return serverBuilder ->
                serverBuilder
                        .defaultServiceNaming(ServiceNaming.simpleTypeName())
                        .annotatedService("/internal", probeService)
                        .serviceUnder("/internal/metrics", PrometheusExpositionService.of(registry.getPrometheusRegistry()))
                        .serviceUnder("/docs", docService())
                        .decorator(loggingDecorator)
                        .decorator(metricsDecorator)
                        .decorator(headersDecorator)
                        .decorator(corsDecorator);
    }

    private DocService docService() {
        return DocService.builder().build();
    }
}
