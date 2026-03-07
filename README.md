# Personal Maven/Gradle Repository Collection

This repository serves as a collection of personal Maven/Gradle artifacts (libraries, plugins, etc.) that I can reuse across various Maven and Gradle projects.

The main goal is to provide a centralized and versioned location for custom-built or locally-modified dependencies, allowing for easier integration and management in other projects.

---

## checkstyle-rule

The `checkstyle-rule` module provides a collection of custom Checkstyle rules and checks for Java projects.
It includes various checks for code style, potential bugs, and best practices.

For detailed usage instructions, including Maven and Gradle configurations, and a list of all available custom checks, please refer to the dedicated [checkstyle-rule/README.md](checkstyle-rule/README.md) file.

## bootstrap

The `bootstrap` module provides a shared Spring Boot library with reusable infrastructure components:

- **Database configuration** - HikariCP DataSource setup with configurable properties
- **Logging aspects** - AOP-based method execution logging with metrics, MDC context, and optional Spring Security user extraction
- **Metrics** - Micrometer metrics configuration
- **Tracing** - OpenTelemetry distributed tracing with MDC propagation
- **OpenAPI** - SpringDoc OpenAPI/Swagger configuration
- **Exception handling** - Global exception handler with standardized API error responses
- **Common utilities** - Generic service interfaces, repository helpers, validation helpers, and service operation executor

### Usage

**Maven:**
```xml
<dependency>
    <groupId>com.weehong.maven</groupId>
    <artifactId>bootstrap</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle:**
```kotlin
implementation("com.weehong.maven:bootstrap:1.0.0")
```
