# Config Framework

> **Configuration management and serialization**

[![GitHub](https://img.shields.io/badge/GitHub-Argonath--Systems-181717?logo=github)](https://github.com/Argonath-Systems/03-framework-config)
[![Maven](https://img.shields.io/badge/Maven-Central-C71A36?logo=apache-maven)](https://maven.apache.org/)
[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](../LICENSE)
[![Website](https://img.shields.io/badge/Docs-argonath--systems.github.io-blue)](https://argonath-systems.github.io/00-Argonath-Wiki)

---

## 📋 Overview

Configuration management and serialization

## 🏗️ C4 Component Diagram

> [!NOTE]
> Detailed architecture diagrams can be found in the [Argonath Wiki C4 Documentation](https://argonath-systems.github.io/00-Argonath-Wiki/diagrams/).

## ✨ Features

- Feature 1
- Feature 2
- Feature 3

## 📦 Installation

### Maven

```xml
<dependency>
    <groupId>com.argonathsystems.framework</groupId>
    <artifactId>argonath-rivendell-config</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Build from Source

```bash
# Clone the repository
git clone https://github.com/Argonath-Systems/03-framework-config.git
cd 03-framework-config

# Build with justfile
just compile

# Or build with Maven
mvn clean install
```

## 🚀 Usage

```java
// Example usage code
```

## 🛠️ Development

### Prerequisites

- Java 25 or higher
- Maven 3.9+
- just (command runner)

### Building

```bash
# Compile the project
just compile

# Run tests
just test

# Deploy to local Maven repository
just deploy
```

## 🏛️ Architecture

### Zero Hytale Imports Policy

This framework strictly adheres to the **Platform Agnostic** architecture principle:
- **NO** `hytale.*` imports allowed in this module
- All game engine interactions go through the Accessor API
- Business logic remains portable and testable

### Dependencies

- **See pom.xml for current dependencies**

## 📖 Documentation

- [Argonath Systems Wiki](https://argonath-systems.github.io/00-Argonath-Wiki)
- [API Documentation](./docs/api)
- [Architecture Documentation](../design/C4)

## 🤝 Contributing

Please read [CONTRIBUTING.md](../CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## 🔗 Related Projects

- [Argonath Systems](https://github.com/Argonath-Systems)
- [LordOfTheTales](https://github.com/K1ntus/LordOfTheTales)
- [HyUI](https://github.com/Argonath-Systems/HyUI)

---

**Built with ❤️ by the Argonath Systems Team**
