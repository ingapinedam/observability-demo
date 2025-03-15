package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

@RestController
public class EjemploController {

    private static final Logger logger = LoggerFactory.getLogger(EjemploController.class);
    private final Tracer tracer;

    public EjemploController() {
        // Obtener el tracer desde la instancia global de OpenTelemetry
        this.tracer = GlobalOpenTelemetry.get().getTracer("com.example.demo.controller");
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "Andy") String name) {
        // Limitar la cantidad de datos añadidos al span
        Span span = tracer.spanBuilder("hello-endpoint")
                .setAttribute("user.name", name)
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            return "¡Hola, " + name + "!";
        } finally {
            span.end();
        }
    }
}