# Contributing to AndroidTrueTime

Thank you for your interest in contributing to AndroidTrueTime! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Issue Guidelines](#issue-guidelines)
- [Coding Standards](#coding-standards)
- [Testing](#testing)

## Code of Conduct

This project and everyone participating in it is governed by our commitment to providing a welcoming and inspiring community for all. By participating, you agree to uphold this code of conduct.

## Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally
3. Create a new branch for your feature or bugfix
4. Make your changes
5. Test your changes thoroughly
6. Submit a pull request

## How to Contribute

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When you create a bug report, please include as many details as possible:

- **Use a clear and descriptive title**
- **Describe the exact steps to reproduce the problem**
- **Provide specific examples to demonstrate the steps**
- **Describe the behavior you observed after following the steps**
- **Explain which behavior you expected to see instead and why**
- **Include screenshots and animated GIFs if applicable**
- **Include your environment details (Android version, device, etc.)**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

- **Use a clear and descriptive title**
- **Provide a step-by-step description of the suggested enhancement**
- **Provide specific examples to demonstrate the steps**
- **Describe the current behavior and explain which behavior you expected to see instead**
- **Explain why this enhancement would be useful**
- **List some other applications where this enhancement exists, if applicable**

## Development Setup

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK API level 21+
- Java 8 or later
- Git

### Setup Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/hk1089/AndroidTrueTime.git
   cd AndroidTrueTime
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Run the sample app to verify everything works

### Project Structure

```
AndroidTrueTime/
├── app/                    # Sample application
├── TrueTime/              # Core library module
├── build.gradle           # Root build configuration
├── settings.gradle        # Project settings
└── README.md             # Project documentation
```

## Pull Request Process

1. **Fork and Clone**: Fork the repository and clone your fork
2. **Create Branch**: Create a feature branch from `main`
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Make Changes**: Implement your changes with proper tests
4. **Test**: Ensure all tests pass and the sample app works
5. **Commit**: Write clear commit messages
6. **Push**: Push your branch to your fork
7. **Pull Request**: Create a pull request with a clear description

### Pull Request Guidelines

- **Use a clear and descriptive title**
- **Provide a detailed description of the changes**
- **Reference any related issues**
- **Include screenshots or GIFs for UI changes**
- **Ensure all CI checks pass**
- **Keep the PR focused on a single feature or bugfix**

### Commit Message Format

Use clear and descriptive commit messages:

```
feat: add support for custom NTP servers
fix: resolve memory leak in SntpClient
docs: update README with new usage examples
test: add unit tests for TrueTimeRx
```

## Issue Guidelines

### Before Creating an Issue

1. Check if the issue already exists
2. Search through closed issues
3. Check the documentation

### Issue Templates

Use the provided issue templates for:
- Bug reports
- Feature requests
- Documentation improvements

## Coding Standards

### Java/Kotlin Style

- Follow Android's official style guide
- Use meaningful variable and method names
- Add Javadoc comments for public APIs
- Keep methods focused and small
- Use proper exception handling

### Code Formatting

- Use 4 spaces for indentation
- Use camelCase for variables and methods
- Use PascalCase for classes
- Use UPPER_CASE for constants

### Example

```java
/**
 * Gets the current accurate time from NTP servers.
 * 
 * @return Date object representing the current accurate time
 * @throws IllegalStateException if TrueTime is not initialized
 */
public static Date now() {
    if (!isInitialized()) {
        throw new IllegalStateException("You need to call init() on TrueTime at least once.");
    }
    // Implementation...
}
```

## Testing

### Unit Tests

- Write unit tests for new functionality
- Maintain or improve test coverage
- Use descriptive test method names
- Test both success and failure scenarios

### Integration Tests

- Test the library with the sample app
- Verify NTP functionality works correctly
- Test on different Android versions and devices

### Test Structure

```java
@Test
public void testTrueTimeInitialization_WithValidServer_ShouldSucceed() {
    // Given
    String ntpServer = "time.google.com";
    
    // When
    TrueTime.build().initialize(ntpServer);
    
    // Then
    assertTrue(TrueTime.isInitialized());
    assertNotNull(TrueTime.now());
}
```

## Release Process

Releases are managed through GitHub releases and JitPack:

1. Update version numbers in build.gradle files
2. Update CHANGELOG.md
3. Create a release tag
4. JitPack automatically builds and publishes the release

## Documentation

- Keep README.md updated with new features
- Add Javadoc comments for public APIs
- Update sample app to demonstrate new features
- Include code examples in documentation

## Questions?

If you have questions about contributing, please:

1. Check existing issues and discussions
2. Create a new issue with the "question" label
3. Join our community discussions

Thank you for contributing to AndroidTrueTime! 🚀
