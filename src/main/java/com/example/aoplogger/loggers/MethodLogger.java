package com.example.aoplogger.loggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.multipart.MultipartFile;

@Aspect
public class MethodLogger {
  private static final Logger log = LogManager.getLogger(MethodLogger.class);
  private final ObjectMapper objectMapper;

  @Pointcut("@annotation(loggable)")
  public void loggableMethod(Loggable loggable) {
  }

  @Before(
      value = "loggableMethod(loggable)",
      argNames = "joinPoint,loggable"
  )
  public void beforeLoggableMethod(JoinPoint joinPoint, Loggable loggable) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    StringBuilder logMessage = (new StringBuilder()).append("Entering method: ").append(className).append(".").append(methodName);
    if (loggable.showData()) {
      Object[] args = joinPoint.getArgs();

      for (int i = 0; i < args.length && !(args[i] instanceof MultipartFile); ++i) {
        logMessage.append(". ").append("Argument ").append(i).append(": ");

        try {
          String argJson = this.objectMapper.writeValueAsString(args[i]);
          logMessage.append(argJson);
        } catch (JsonProcessingException var9) {
          logMessage.append("[Unable to serialize to JSON]");
        }
      }
    }

    log.debug(logMessage.toString());
  }

  @AfterReturning(
      value = "loggableMethod(loggable)",
      returning = "result",
      argNames = "joinPoint,loggable,result"
  )
  public void afterReturningLoggableMethod(JoinPoint joinPoint, Loggable loggable, Object result) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    StringBuilder logMessage =
        (new StringBuilder()).append("Exiting method: ").append(className).append(".").append(methodName).append(". Result: ");
    if (loggable.showData()) {
      try {
        String resultJson = this.objectMapper.writeValueAsString(result);
        logMessage.append(resultJson);
      } catch (JsonProcessingException var8) {
        logMessage.append("[Unable to serialize to JSON]");
      }
    }

    log.debug(logMessage);
  }

  public MethodLogger(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
}
