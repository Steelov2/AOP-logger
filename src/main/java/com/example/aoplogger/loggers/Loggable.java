package com.example.aoplogger.loggers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking methods or fields to be logged.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Loggable {

  /**
   * Optional value for additional description or context.
   *
   * @return Description value.
   */
  String value() default "";

  /**
   * Flag to indicate whether to log data parameters.
   *
   * @return True if data should be logged, false otherwise.
   */
  boolean showData() default false;
}