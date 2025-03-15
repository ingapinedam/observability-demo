package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    static {
        // Disable metrics and monitoring
        System.setProperty("spring.jmx.enabled", "false");
        System.setProperty("management.metrics.enabled", "false");
        System.setProperty("management.metrics.web.server.auto-time-requests", "false");
        System.setProperty("management.endpoint.metrics.enabled", "false");
        System.setProperty("spring.main.banner-mode", "off");
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setRegisterShutdownHook(true);
        app.run(args);
    }
}