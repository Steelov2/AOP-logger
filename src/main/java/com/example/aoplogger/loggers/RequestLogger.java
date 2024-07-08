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

/**
 * Utility class for logging HTTP request details.
 */
public class RequestLogger {
  private static final Logger log = LoggerFactory.getLogger(RequestLogger.class);
  private final HttpServletRequest request;
  private final ObjectMapper objectMapper;

  /**
   * Log HTTP request headers.
   */
  public void logRequestHeaders() {
    List<Map<String, String>> headersList = Collections.list(this.request.getHeaderNames())
        .stream()
        .filter(headerName -> !"Cookie".equalsIgnoreCase(headerName))
        .map(headerName -> Map.of(headerName, this.getHeaderValue(headerName)))
        .toList();

    try {
      String headersJson = this.objectMapper.writeValueAsString(headersList);
      log.debug(headersJson);
    } catch (IOException e) {
      log.error("Error converting headers to JSON", e);
    }
  }

  /**
   * Get the request URL including query parameters.
   *
   * @return Full request URL as a String.
   */
  public String getRequestUrl() {
    String requestUri = this.request.getRequestURI();
    String queryString = this.request.getQueryString();
    return StringUtils.isNotBlank(queryString) ? requestUri + ":" + queryString : requestUri;
  }

  /**
   * Get header value for a given header name.
   *
   * @param headerName Name of the header.
   * @return Value of the header as a String.
   */
  private String getHeaderValue(String headerName) {
    String headerValue = this.request.getHeader(headerName);
    return "Authorization".equalsIgnoreCase(headerName) ? this.truncateAuthorizationHeader(headerValue) : headerValue;
  }

  /**
   * Truncate Authorization header value for security reasons.
   *
   * @param headerValue Authorization header value.
   * @return Truncated value of the Authorization header.
   */
  private String truncateAuthorizationHeader(String headerValue) {
    // Implement logic to truncate or mask sensitive information in Authorization header
    return headerValue.substring(0, Math.min(headerValue.length(), 10)) + "...";
  }

  /**
   * Constructor for RequestLogger.
   *
   * @param request      HttpServletRequest instance.
   * @param objectMapper ObjectMapper instance.
   */
  public RequestLogger(HttpServletRequest request, ObjectMapper objectMapper) {
    this.request = request;
    this.objectMapper = objectMapper;
  }
}
