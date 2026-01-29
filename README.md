# Config Framework

> **Type-safe configuration management with hot-reload support**

[![GitHub](https://img.shields.io/badge/GitHub-Argonath--Systems-181717?logo=github)](https://github.com/Argonath-Systems/03-framework-config)
[![Maven](https://img.shields.io/badge/Maven-Central-C71A36?logo=apache-maven)](https://maven.apache.org/)
[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](../LICENSE)
[![Website](https://img.shields.io/badge/Docs-argonath--systems.github.io-blue)](https://argonath-systems.github.io/00-Argonath-Wiki)

---

## 📋 Overview

The Configuration Framework provides type-safe, hierarchical configuration management for all Argonath modules. It supports YAML configuration files, hot-reloading, change listeners, and automatic default value population.

## ✨ Features

- **Type-Safe API**: Generic methods with `Optional` return types eliminate null-related bugs
- **DataValue Integration**: Seamless interoperability with accessor v2.0.0 type system
- **Hierarchical Configuration**: Dot-separated paths for nested values (e.g., `database.connection.timeout`)
- **Hot-Reload**: Runtime configuration changes without server restart
- **Change Listeners**: React to configuration changes programmatically
- **Default Merging**: Automatically populate missing keys from embedded defaults
- **Thread-Safe**: Concurrent access supported via ConcurrentHashMap

## 📦 Installation

### Maven

```xml
<dependency>
    <groupId>com.argonathsystems.framework</groupId>
    <artifactId>argonath-rivendell-config</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

### Build from Source

```bash
# Clone the repository
git clone https://github.com/Argonath-Systems/03-framework-config.git
cd 03-framework-config

# Build with Maven
mvn clean install
```

## 🚀 Usage

### Basic Configuration Access

```java
import com.argonathsystems.framework.config.ConfigFactory;
import com.argonathsystems.framework.config.ConfigurationManager;

// Get configuration manager for your mod
ConfigurationManager config = ConfigFactory.getManager("my-mod");

// Type-safe access with Optional
int maxPlayers = config.get("server.max-players", Integer.class).orElse(20);
String motd = config.get("server.motd", String.class, "Welcome!");

// Check if key exists
if (config.contains("features.pvp.enabled")) {
    boolean pvpEnabled = config.get("features.pvp.enabled", Boolean.class, false);
}
```

### Change Listeners

```java
// React to configuration changes
config.addChangeListener("database", event -> {
    if (event.isModification()) {
        logger.info("Database config changed: {} -> {}", 
            event.oldValue(), event.newValue());
        // Reconnect to database...
    }
});

// Update configuration (triggers listeners)
config.set("database.host", DataValue.of("localhost"));
config.save();
```

### Loading Defaults

```java
// Load defaults from embedded resource
try (InputStream defaults = getClass().getResourceAsStream("/defaults.yml")) {
    config.loadDefaults(defaults);
    config.save(); // Persist merged configuration
}
```

### DataValue Interoperability

```java
import com.argonathsystems.framework.accessor.data.DataValue;

// Get configuration as DataValue for passing to other modules
Optional<DataValue> questConfig = config.getAsDataValue("quests.tutorial");
questConfig.ifPresent(dv -> questManager.loadQuestData(dv));

// Set values using DataValue
config.set("stats.strength", DataValue.of(10));
config.set("items.allowed", DataValue.of(List.of("sword", "shield")));
```

## 🛠️ Development

### Prerequisites

- Java 25 or higher
- Maven 3.9+

### Building

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Deploy to local Maven repository
mvn install
```

## 🏛️ Architecture

### Zero Hytale Imports Policy

This framework strictly adheres to the **Platform Agnostic** architecture principle:
- **NO** `hytale.*` imports allowed in this module
- All game engine interactions go through the Accessor API
- Business logic remains portable and testable

### Dependencies

- `02-framework-accessor` - DataValue types for type-safe data exchange
- `02-framework-core` - Common utilities
- `snakeyaml` - YAML parsing
- `slf4j-api` - Logging abstraction

### Configuration Path Convention

All configuration files follow the mutualized directory structure:

```
config/Argonath/{mod-id}/config.yml
```

**Examples:**
- `config/Argonath/quest-framework/config.yml`
- `config/Argonath/combat-mod/config.yml`
- `config/Argonath/npc-framework/config.yml`

## 📖 Documentation

- [Argonath Systems Wiki](https://argonath-systems.github.io/00-Argonath-Wiki)
- [Specification: CL-L1-001](../00-Argonath-Specifications/HLR-ARCHITECTURE-002-lib-core-infrastructure.md)
- [Library Catalog](../00-Argonath-Specifications/SF-ARCHITECTURE-000-library-catalog.md#LIB-001)

## 🤝 Contributing

Please read [CONTRIBUTING.md](../CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## 🔗 Related Projects

- [Argonath Systems](https://github.com/Argonath-Systems)
- [02-framework-accessor](https://github.com/Argonath-Systems/02-framework-accessor)
- [02-framework-core](https://github.com/Argonath-Systems/02-framework-core)

---

**Built with ❤️ by the Argonath Systems Team**
