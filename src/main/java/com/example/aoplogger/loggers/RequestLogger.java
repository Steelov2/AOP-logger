package com.example.aoplogger.loggers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLogger {
  private static final Logger log = LoggerFactory.getLogger(RequestLogger.class);
  private final HttpServletRequest request;
  private final ObjectMapper objectMapper;

  public void logRequestHeaders() {
    List<Map<String, String>> headersList = Collections.list(this.request
        .getHeaderNames())
        .stream()
        .filter((headerName) -> !"Cookie".equalsIgnoreCase(headerName))
        .map((headerName) -> Map.of(headerName, this.getHeaderValue(headerName)))
        .toList();

    try {
      String headersJson = this.objectMapper.writeValueAsString(headersList);
      log.debug(headersJson);
    } catch (IOException var3) {
      log.error("Error converting headers to JSON", var3);
    }

  }

  private String getHeaderValue(String headerName) {
    String headerValue = this.request.getHeader(headerName);
    return "Authorization".equalsIgnoreCase(headerName) ? this.truncateAuthorizationHeader(headerValue) : headerValue;
  }

  public String getRequestUrl() {
    String requestUri = this.request.getRequestURI();
    String queryString = this.request.getQueryString();
    return StringUtils.isNotBlank(queryString) ? requestUri + ":" + queryString : requestUri;
  }

  private String truncateAuthorizationHeader(String authorizationHeader) {
    return StringUtils.isAlphaSpace(authorizationHeader) && authorizationHeader.length() > 10 ?
        authorizationHeader.substring(0, 10) : authorizationHeader;
  }

  public RequestLogger(HttpServletRequest request, ObjectMapper objectMapper) {
    this.request = request;
    this.objectMapper = objectMapper;
  }
}
