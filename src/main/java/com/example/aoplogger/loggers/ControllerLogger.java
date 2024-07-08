package com.example.aoplogger.loggers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Aspect for logging controller method invocations.
 */
@Aspect
public class ControllerLogger {
  private static final Logger log = LogManager.getLogger(ControllerLogger.class);
  private final ObjectMapper mapper;
  private final RequestLogger requestLogger;

  /**
   * Pointcut definition for methods in controllers package.
   */
  @Pointcut("within(com.example.aoplogger..*.controllers..*) || within(com.example.aoplogger..*.controller..*)")
  public void methodsRestController() {}

  /**
   * Around advice for logging method invocation.
   *
   * @param pjp ProceedingJoinPoint instance.
   * @return Object returned by the method.
   * @throws Throwable if an error occurs during method invocation.
   */
  @Around("methodsRestController()")
  public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {
    StringBuilder logMessage = new StringBuilder();
    this.requestLogger.logRequestHeaders();
    String requestUrl = this.requestLogger.getRequestUrl();
    logMessage.append("requestUrl: ").append(requestUrl).append("\n");
    String methodName = pjp.getSignature().getName();
    String className = pjp.getTarget().getClass().toString();
    Object[] array = pjp.getArgs();
    Object[] filteredArray = Arrays.stream(array)
        .filter(objectx -> !(objectx instanceof MultipartFile))
        .toArray();
    logMessage.append("method invoked ").append(className).append(" : ").append(methodName).append("()").append("arguments : ")
        .append(this.mapper.writeValueAsString(filteredArray)).append("\n");
    Object object = pjp.proceed();
    logMessage.append(className).append(" : ").append(methodName).append("()").append("Response : ")
        .append(this.getReturnedObjectAsString(object));
    log.debug(logMessage.toString());
    return object;
  }

  /**
   * Convert returned object to JSON string.
   *
   * @param object Object returned by the method.
   * @return JSON representation of the object.
   * @throws JsonProcessingException if object serialization fails.
   */
  private String getReturnedObjectAsString(Object object) throws JsonProcessingException {
    if (object instanceof ResponseEntity<?>) {
      Object body = ((ResponseEntity<?>) object).getBody();
      return body instanceof byte[] ? "" : this.mapper.writeValueAsString(body);
    } else {
      return this.mapper.writeValueAsString(object);
    }
  }

  /**
   * Constructor for ControllerLogger.
   *
   * @param mapper       ObjectMapper instance.
   * @param requestLogger RequestLogger instance.
   */
  public ControllerLogger(ObjectMapper mapper, RequestLogger requestLogger) {
    this.mapper = mapper;
    this.requestLogger = requestLogger;
  }
}
