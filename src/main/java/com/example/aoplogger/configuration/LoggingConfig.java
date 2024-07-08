package com.example.aoplogger.configuration;

import com.example.aoplogger.loggers.ControllerLogger;
import com.example.aoplogger.loggers.MethodLogger;
import com.example.aoplogger.loggers.RequestLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {
  public LoggingConfig() {
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "logging",
      name = {"controller-logging-enabled"},
      havingValue = "true",
      matchIfMissing = true
  )
  public ControllerLogger controllerLogger(ObjectMapper objectMapper, RequestLogger requestLogger) {
    return new ControllerLogger(objectMapper, requestLogger);
  }

  @Bean
  @ConditionalOnProperty(
      prefix = "logging",
      name = {"method-logging-enabled"},
      havingValue = "true",
      matchIfMissing = true
  )
  public MethodLogger methodLogger(ObjectMapper objectMapper) {
    return new MethodLogger(objectMapper);
  }

  @Bean
  public RequestLogger requestLogger(HttpServletRequest request, ObjectMapper objectMapper) {
    return new RequestLogger(request, objectMapper);
  }
}

