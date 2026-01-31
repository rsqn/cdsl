# CDSL Java Development Container

This devcontainer provides a complete Java development environment for the CDSL project using Amazon Corretto 21 and Maven.

## Features

- **Amazon Corretto 21 (LTS)** - Java runtime environment
- **Maven** - Build tool and dependency management
- **VS Code Extensions**: Java Extension Pack, Maven for Java, Spring tools
- **Development Tools**: Git, GitHub CLI, Make, Curl, JQ
- **Pre-configured**: Java settings, formatting, and testing setup

## Quick Start

1. Open this project in VS Code
2. When prompted, click "Reopen in Container"
3. Wait for the container to build and start
4. Run `./dev_helpers.sh status` to verify everything is working

## Available Commands

- `./dev_helpers.sh build [args]` - Build Maven project
- `./dev_helpers.sh test [args]` - Run all tests
- `./dev_helpers.sh run [args]` - Run application (if Spring Boot)
- `./dev_helpers.sh clean [args]` - Clean project
- `./dev_helpers.sh format` - Format code (if formatter plugin configured)
- `./dev_helpers.sh status` - Show project status

## Development Workflow

1. Make your changes
2. Run `./dev_helpers.sh format` to format code (if configured)
3. Run `./dev_helpers.sh build` to build the project
4. Run `./dev_helpers.sh test` to run tests
5. Run `./dev_helpers.sh run` to test the application

## Maven Commands

You can also use Maven directly:

- `mvn clean install` - Build and install project
- `mvn test` - Run tests
- `mvn clean` - Clean build artifacts
- `mvn spring-boot:run` - Run Spring Boot application (if applicable)

## Troubleshooting

If you encounter issues:

1. Check the environment: `./test_environment.sh`
2. Rebuild the container: Command Palette → "Dev Containers: Rebuild Container"
3. Check logs: `docker logs <container-name>`
4. Verify Java: `java -version`
5. Verify Maven: `mvn -version`

## Project Structure

The devcontainer expects a standard Maven project structure:

```
src/
  main/
    java/          # Main Java source files
    resources/     # Main resources
  test/
    java/          # Test Java source files
    resources/     # Test resources
pom.xml            # Maven configuration
```
