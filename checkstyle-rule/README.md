# Checkstyle Rule

Reusable Checkstyle rules and custom checks for Java projects.
This project provides a collection of strict code quality rules and custom Checkstyle checks.

## Prerequisites

- Java 21+
- Checkstyle 10.21.1

## Installation

This artifact is published to GitHub Packages.

```bash
mvn clean deploy
```

## Maven Usage

Add the GitHub Packages repository to your project:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>github</id>
        <url>https://maven.pkg.github.com/weehong-1/personal-maven-repository-collection</url>
    </pluginRepository>
</pluginRepositories>
```

Then, configure the `maven-checkstyle-plugin` to include this artifact as a dependency:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.6.0</version>
    <dependencies>
        <dependency>
            <groupId>com.weehong.maven</groupId>
            <artifactId>checkstyle-rule</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
        <consoleOutput>true</consoleOutput>
        <failsOnError>true</failsOnError>
        <linkXRef>false</linkXRef>
    </configuration>
    <executions>
        <execution>
            <id>validate</id>
            <phase>validate</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Gradle Usage

Add the GitHub Packages repository and configure the `checkstyle` dependency:

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/weehong-1/personal-maven-repository-collection")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

configurations {
    checkstyleRule
}

dependencies {
    checkstyle 'com.weehong.maven:checkstyle-rule:1.0.0'
    checkstyleRule 'com.weehong.maven:checkstyle-rule:1.0.0'
}

checkstyle {
    toolVersion = "10.21.1"
    config = resources.text.fromArchiveEntry(configurations.checkstyleRule, 'checkstyle.xml')
}
```

## Available Custom Checks

This library includes the following custom checks:

### Spacing Checks

- **ForSpacingCheck**: Enforces blank lines before and after `for` loops (unless first/last in block).
- **IfSpacingCheck**: Enforces blank lines around standalone `if` statements (excludes `else if` chains).
- **WhileSpacingCheck**: Enforces blank lines around standalone `while` loops (excludes `do-while`).
- **SwitchSpacingCheck**: Enforces blank lines around `switch` statements and between case groups.
- **ReturnStatementSpacingCheck**: Enforces a blank line before `return` statements (unless first in block).

### Formatting Checks

- **MethodParameterLineBreakCheck**: Enforces one parameter per line for methods with more than 4 parameters.
- **TernaryOperatorLineBreakCheck**: Enforces ternary operators (`?`, `:`) to be at the start of new lines.
- **SwaggerAnnotationLineBreakCheck**: Enforces OpenAPI/Swagger annotation properties on separate lines when exceeding a configurable threshold (default: 2). Supported annotations: `@Schema`, `@Operation`, `@ApiResponse`, `@ApiResponses`, `@Parameter`, `@RequestBody`, `@Header`, `@Content`, `@ExampleObject`, `@ArraySchema`.
- **NoMultipleBlankLinesCheck**: Prohibits more than one consecutive blank line (AST-based).
- **NoMultipleBlankLinesFileCheck**: Prohibits more than one consecutive blank line (file-based, works for all file types).

### Code Quality Checks

- **UnusedImportCheck**: Detects unused and duplicate imports (supports static imports).
- **UnusedMethodCheck**: Detects unused private methods. Excludes `main`, serialization methods, and methods annotated with framework annotations (`@Test`, `@Bean`, `@Scheduled`, `@PostConstruct`, etc.).
- **UnusedVariableCheck**: Detects unused local variables and parameters. Excludes catch parameters, `@Override` method parameters, and serialization method parameters.

### Best Practice Checks

- **NoHardcodedStringCheck**: Prohibits hardcoded string literals. Allows strings in constants (`static final`), annotations, enums, exception constructors, and empty strings.
- **NoForbiddenLombokAnnotationsCheck**: Forbids all Lombok annotations except `@Getter` and `@Setter`.
- **NoSuppressWarningsCheck**: Prohibits the use of `@SuppressWarnings`.
- **NoTypeCastCheck**: Prohibits explicit type casting. Encourages generics, polymorphism, or pattern matching.
- **NoVarKeywordCheck**: Prohibits the `var` keyword; requires explicit type declarations.

## Bundled Configuration

The JAR includes a `checkstyle.xml` with all custom checks enabled and a strict set of standard Checkstyle rules:

- **File limits**: Max 2000 lines per file, 120 characters per line
- **Complexity**: Cyclomatic complexity max 10, method length max 80 lines, nested if depth max 3
- **Naming**: Standard Java naming conventions enforced
- **Imports**: No star imports, no unused/redundant imports, no fully qualified class names in code
- **Style**: No field injection (`@Autowired private`), no `System.out`/`printStackTrace`, no magic numbers (except 0, 1, -1)
- **Test suppressions**: `MethodName`, `NoHardcodedString`, `UnusedMethod`, and `UnusedVariable` checks are suppressed for test files (`*Test.java`)

An optional `checkstyle-suppressions.xml` file in your project root can be used for additional suppressions.
