package com.example.aoplogger.loggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Aspect for logging annotated methods.
 */
@Aspect
public class MethodLogger {
  private static final Logger log = LogManager.getLogger(MethodLogger.class);
  private final ObjectMapper objectMapper;

  /**
   * Before advice for logging method entry.
   *
   * @param joinPoint JoinPoint instance.
   * @param loggable  Loggable annotation instance.
   */
  @Before("execution(* *(..)) && @annotation(loggable)")
  public void beforeLoggableMethod(JoinPoint joinPoint, Loggable loggable) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    StringBuilder logMessage = new StringBuilder()
        .append("Entering method: ")
        .append(className)
        .append(".")
        .append(methodName);

    if (loggable.showData()) {
      Object[] args = joinPoint.getArgs();

      for (int i = 0; i < args.length; ++i) {
        logMessage.append(". Argument ").append(i).append(": ");
        try {
          String argJson = this.objectMapper.writeValueAsString(args[i]);
          logMessage.append(argJson);
        } catch (JsonProcessingException e) {
          logMessage.append("[Unable to serialize to JSON]");
        }
      }
    }

    log.debug(logMessage.toString());
  }

  /**
   * AfterReturning advice for logging method return.
   *
   * @param joinPoint JoinPoint instance.
   * @param loggable  Loggable annotation instance.
   * @param result    Object returned by the method.
   */
  @AfterReturning(
      value = "execution(* *(..)) && @annotation(loggable)",
      returning = "result"
  )
  public void afterReturningLoggableMethod(JoinPoint joinPoint, Loggable loggable, Object result) {
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
    StringBuilder logMessage = new StringBuilder()
        .append("Exiting method: ")
        .append(className)
        .append(".")
        .append(methodName)
        .append(". Result: ");

    if (loggable.showData()) {
      try {
        String resultJson = this.objectMapper.writeValueAsString(result);
        logMessage.append(resultJson);
      } catch (JsonProcessingException e) {
        logMessage.append("[Unable to serialize to JSON]");
      }
    }

    log.debug(logMessage.toString());
  }

  /**
   * Constructor for MethodLogger.
   *
   * @param objectMapper ObjectMapper instance.
   */
  public MethodLogger(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
}
