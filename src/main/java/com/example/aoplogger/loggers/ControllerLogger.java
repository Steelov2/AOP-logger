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

@Aspect
public class ControllerLogger {
  private static final Logger log = LogManager.getLogger(ControllerLogger.class);
  private final ObjectMapper mapper;
  private final RequestLogger requestLogger;

  @Pointcut("within(com.example.aoplogger..*.controllers..*) || within(com.example.aoplogger..*.controller..*) || within(com.example.aoplogger..*..*..*.controllers..*) || within(com.example.aoplogger..*..*..*.controller..*) || within(com.example.aoplogger..*..*.rest..*) || within(com.example.aoplogger..*..*.controllers..*) || within(com.example.aoplogger..*..*.controller..*)")
  public void methodsRestController() {
  }

  @Around("methodsRestController()")
  public Object applicationLogger(ProceedingJoinPoint pjp) throws Throwable {
    StringBuilder logMessage = new StringBuilder();
    this.requestLogger.logRequestHeaders();
    String requestUrl = this.requestLogger.getRequestUrl();
    logMessage.append("requestUrl: ").append(requestUrl).append("\n");
    String methodName = pjp.getSignature().getName();
    String className = pjp.getTarget().getClass().toString();
    Object[] array = pjp.getArgs();
    Object[] filteredArray = Arrays.stream(array).filter((objectx) -> {
      return !(objectx instanceof MultipartFile);
    }).toArray();
    logMessage.append("method invoked ").append(className).append(" : ").append(methodName).append("()").append("arguments : ")
        .append(this.mapper.writeValueAsString(filteredArray)).append("\n");
    Object object = pjp.proceed();
    logMessage.append(className).append(" : ").append(methodName).append("()").append("Response : ")
        .append(this.getReturnedObjectAsString(object));
    log.debug(logMessage.toString());
    return object;
  }

  private String getReturnedObjectAsString(Object object) throws JsonProcessingException {
    if (object instanceof ResponseEntity<?> e) {
      Object body = e.getBody();
      return body instanceof byte[] ? "" : this.mapper.writeValueAsString(body);
    } else {
      return this.mapper.writeValueAsString(object);
    }
  }

  public ControllerLogger(ObjectMapper mapper, RequestLogger requestLogger) {
    this.mapper = mapper;
    this.requestLogger = requestLogger;
  }
}
