# AOP Logger Example

This project demonstrates logging implementation using Aspect-Oriented Programming (AOP) in a Spring Boot application. It utilizes custom annotations and aspects to log method invocations and request details. 
It is a library, so you don't need to run it, just build it

## Overview

The project includes the following components:

- **LoggingConfig**: Spring configuration class for defining logging beans.
- **ControllerLogger**: Aspect to log controller method invocations and their parameters.
- **MethodLogger**: Aspect to log methods annotated with `@Loggable`.
- **RequestLogger**: Utility class to log HTTP request headers and details.

## Table of Contents

- [Requirements](#requirements)
- [Setup](#setup)
- [Usage](#usage)
- [Customization](#customization)
- [Contributing](#contributing)

## Requirements

- Java 8 or higher
- Maven 
- Spring Boot 3+

## Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/your-username/aop-logger.git
   cd aop-logger
2. **Build and run the application:**
   ```bash
   mvn clean install
3. **Add the dependency to another project**
   ```bash
   <dependency>
            <groupId>com.example</groupId>
            <artifactId>aop-logger</artifactId>
            <version>0.0.1-SNAPSHOT</version>
   </dependency>

## Usage
- Perform HTTP requests to the defined endpoints.
- Check the console logs to see the method invocations and request details logged by AOP.

## Customization
- Modify the application.properties file to enable/disable specific logging features (controller-logging-enabled, method-logging-enabled).
- Adjust logging levels and configurations in log4j2.xml as needed for your environment.

## Contributing
Contributions are welcome! If you have any ideas or improvements, feel free to fork the repository and submit a pull request.
   

