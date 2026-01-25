# Config Framework Build Configuration

# Default recipe - list all available commands
default:
    @just --list

# Display module information
info:
    @echo "Module: Config Framework"
    @echo "GroupId: com.argonathsystems.framework"
    @echo "ArtifactId: argonath-rivendell-config"
    @echo "Version: 1.0.0-SNAPSHOT"
    @echo "Type: framework"

# Compile the project
compile:
    @echo "🔨 Compiling Config Framework..."
    mvn clean compile -DskipTests

# Run all tests
test:
    @echo "🧪 Running tests for Config Framework..."
    mvn test

# Package the artifact
package:
    @echo "📦 Packaging Config Framework..."
    mvn clean package

# Install to local Maven repository
install:
    @echo "📥 Installing Config Framework to local repository..."
    mvn clean install

# Deploy to configured repository
deploy:
    @echo "🚀 Deploying Config Framework..."
    mvn clean deploy

# Clean build artifacts
clean:
    @echo "🧹 Cleaning Config Framework..."
    mvn clean

# Validate POM file
validate:
    @echo "✅ Validating Config Framework POM..."
    mvn validate

# Run with detailed output
verbose-build:
    @echo "🔍 Building Config Framework (verbose)..."
    mvn clean install -X

# Generate project documentation
docs:
    @echo "📚 Generating documentation for Config Framework..."
    mvn javadoc:javadoc

# Check for dependency updates
dependency-check:
    @echo "🔍 Checking dependencies for Config Framework..."
    mvn versions:display-dependency-updates

# Format code (if spotless is configured)
format:
    @echo "✨ Formatting code for Config Framework..."
    @if grep -q "spotless" pom.xml 2>/dev/null; then \
        mvn spotless:apply; \
    else \
        echo "⚠️  Spotless not configured for this module"; \
    fi

# Run quick verification (compile + test)
verify:
    @echo "🔍 Verifying Config Framework..."
    mvn clean verify

# Show dependency tree
dep-tree:
    @echo "🌳 Dependency tree for Config Framework:"
    mvn dependency:tree

# Help - show all available commands with descriptions
help:
    @echo "Available commands for Config Framework:"
    @echo ""
    @echo "  compile          - Compile source code"
    @echo "  test             - Run all tests"
    @echo "  package          - Create JAR artifact"
    @echo "  install          - Install to local Maven repository"
    @echo "  deploy           - Deploy to remote repository"
    @echo "  clean            - Remove build artifacts"
    @echo "  validate         - Validate POM configuration"
    @echo "  verify           - Run full verification (compile + test)"
    @echo "  docs             - Generate Javadoc documentation"
    @echo "  dependency-check - Check for dependency updates"
    @echo "  dep-tree         - Show dependency tree"
    @echo "  format           - Format source code"
    @echo "  verbose-build    - Build with verbose output"
    @echo "  info             - Display module information"
    @echo "  help             - Show this help message"
