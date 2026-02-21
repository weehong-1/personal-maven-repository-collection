# Checkstyle Rule

Reusable Checkstyle rules and custom checks for Java projects.
This project provides a collection of strict code quality rules and custom Checkstyle checks.

## Installation

To use this library, you can install it into a specific local directory (acting as a repository). This is useful if you want to keep it separate from your main local cache or share it via a file path.

Run the following command to deploy the artifact to `~/.maven-repo`:

```bash
mvn clean deploy -DaltDeploymentRepository=local-maven-repo::default::file://${user.home}/.maven-repo
```

## Maven Usage

To use these checks in a Maven project, first configure the `pluginRepositories` to point to the location where you deployed the artifact (as done in the Installation step).

```xml
<pluginRepositories>
    <pluginRepository>
        <id>local-maven-repo</id>
        <url>file://${user.home}/.maven-repo</url>
    </pluginRepository>
</pluginRepositories>
```

Then, configure the `maven-checkstyle-plugin` to include this artifact as a dependency.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.6.0</version>
    <dependencies>
        <!-- Add the custom checkstyle rule artifact -->
        <dependency>
            <groupId>com.weehong</groupId>
            <artifactId>checkstyle-rule</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    <configuration>
        <!-- Reference the configuration file inside the JAR -->
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

To use these checks in a Gradle project, you need to add the dependency to the `checkstyle` configuration and extract the configuration file.

```groovy
configurations {
    checkstyleRule
}

dependencies {
    // Add to checkstyle classpath so custom checks can be loaded
    checkstyle 'com.weehong:checkstyle-rule:1.0.0'
    
    // Add to a separate config to extract the XML (optional, or reuse same config)
    checkstyleRule 'com.weehong:checkstyle-rule:1.0.0'
}

checkstyle {
    toolVersion = "10.21.1"
    // Extract checkstyle.xml from the jar
    config = resources.text.fromArchiveEntry(configurations.checkstyleRule, 'checkstyle.xml')
}
```

## Available Custom Checks

This library includes the following custom checks:

- **ForSpacingCheck**: Enforces blank lines around `for` loops.
- **IfSpacingCheck**: Enforces blank lines around standalone `if` statements.
- **WhileSpacingCheck**: Enforces blank lines around `while` loops.
- **SwitchSpacingCheck**: Enforces blank lines around `switch` statements and case groups.
- **MethodParameterLineBreakCheck**: Enforces one parameter per line for methods with > 4 parameters.
- **NoForbiddenLombokAnnotationsCheck**: Forbids Lombok annotations other than `@Getter` and `@Setter`.
- **NoMultipleBlankLinesCheck**: Prohibits more than one consecutive blank line (AST based).
- **NoMultipleBlankLinesFileCheck**: Prohibits more than one consecutive blank line (File based, works for non-Java files).
- **NoSuppressWarningsCheck**: Prohibits the use of `@SuppressWarnings`.
- **NoTypeCastCheck**: Prohibits explicit type casting.
- **NoVarKeywordCheck**: Prohibits the use of `var` keyword; requires explicit types.
- **TernaryOperatorLineBreakCheck**: Enforces ternary operators (`?`, `:`) to be at the start of new lines.
- **UnusedImportCheck**: Detects unused imports (supports static imports and better detection than standard check).
- **UnusedMethodCheck**: Detects unused private methods.
- **UnusedVariableCheck**: Detects unused local variables and parameters.
