package com.example.demo.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OpenTelemetryConfig {

    @Value("${management.otlp.tracing.endpoint:http://otel-collector:4318}")
    private String tracingEndpoint;

    @Bean
    public OpenTelemetry openTelemetry() {
        // Configurar el exportador HTTP para trazas
        OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
                .setEndpoint("http://otel-collector:4318/v1/traces")
                .setTimeout(30, TimeUnit.SECONDS) // Aumentar timeout
                .build();

        // Configurar el recurso con nombre de servicio
        Resource resource = Resource.getDefault()
                .merge(Resource.create(Attributes.of(
                        ResourceAttributes.SERVICE_NAME, "mi-api-rest")));

        // Configurar el proveedor de trazas con un procesador de lotes más pequeño
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                        .setMaxExportBatchSize(512) // Limitar tamaño del lote
                        .setMaxQueueSize(2048)      // Limitar tamaño de la cola
                        .setScheduleDelay(100, TimeUnit.MILLISECONDS)
                        .build())
                .build();

        // Construir el SDK
        return OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }


}