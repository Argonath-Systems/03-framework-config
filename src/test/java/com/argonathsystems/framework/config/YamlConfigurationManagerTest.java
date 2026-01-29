package com.argonathsystems.framework.config;

import com.argonathsystems.framework.accessorapi.data.DataValue;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for YamlConfigurationManager.
 */
class YamlConfigurationManagerTest {

    @TempDir
    Path tempDir;

    private YamlConfigurationManager config;

    @BeforeEach
    void setUp() {
        // Override config path to use temp directory
        System.setProperty("user.dir", tempDir.toString());
        config = new YamlConfigurationManager("test-mod");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("user.dir");
    }

    @Nested
    @DisplayName("Basic Operations")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should save and reload configuration")
        void testLoadAndSave() throws IOException {
            // Set values
            config.set("server.port", DataValue.of(8080));
            config.set("server.host", DataValue.of("localhost"));
            
            // Save
            config.save();
            
            // Verify values in memory
            assertThat(config.get("server.port", Integer.class)).contains(8080);
            assertThat(config.get("server.host", String.class)).contains("localhost");
            
            // Reload from disk
            config.reload();
            
            // Verify values persisted
            assertThat(config.get("server.port", Integer.class)).contains(8080);
            assertThat(config.get("server.host", String.class)).contains("localhost");
        }

        @Test
        @DisplayName("Should handle missing configuration gracefully")
        void testMissingConfig() {
            config.load();
            assertThat(config.get("nonexistent.key", String.class)).isEmpty();
        }

        @Test
        @DisplayName("Should check if path exists")
        void testContains() {
            config.set("database.url", DataValue.of("jdbc:mysql://localhost"));
            
            assertThat(config.contains("database.url")).isTrue();
            assertThat(config.contains("database.password")).isFalse();
        }
    }

    @Nested
    @DisplayName("Type-Safe Access")
    class TypeSafeAccessTests {

        @Test
        @DisplayName("Should get String values")
        void testGetString() {
            config.set("app.name", DataValue.of("MyApp"));
            
            Optional<String> value = config.get("app.name", String.class);
            assertThat(value).contains("MyApp");
        }

        @Test
        @DisplayName("Should get Integer values")
        void testGetInteger() {
            config.set("server.timeout", DataValue.of(30));
            
            Optional<Integer> value = config.get("server.timeout", Integer.class);
            assertThat(value).contains(30);
        }

        @Test
        @DisplayName("Should get Long values")
        void testGetLong() {
            config.set("limits.max-size", DataValue.of(9999999999L));
            
            Optional<Long> value = config.get("limits.max-size", Long.class);
            assertThat(value).contains(9999999999L);
        }

        @Test
        @DisplayName("Should get Double values")
        void testGetDouble() {
            config.set("rates.multiplier", DataValue.of(1.5));
            
            Optional<Double> value = config.get("rates.multiplier", Double.class);
            assertThat(value).contains(1.5);
        }

        @Test
        @DisplayName("Should get Boolean values")
        void testGetBoolean() {
            config.set("features.pvp", DataValue.of(true));
            
            Optional<Boolean> value = config.get("features.pvp", Boolean.class);
            assertThat(value).contains(true);
        }

        @Test
        @DisplayName("Should get List values")
        void testGetList() {
            List<DataValue> items = List.of(
                DataValue.of("sword"),
                DataValue.of("shield"),
                DataValue.of("helmet")
            );
            config.set("items.allowed", DataValue.of(items));
            
            Optional<List> value = config.get("items.allowed", List.class);
            assertThat(value).isPresent();
        }

        @Test
        @DisplayName("Should return default value when key not found")
        void testGetWithDefault() {
            String value = config.get("missing.key", String.class, "default-value");
            assertThat(value).isEqualTo("default-value");
        }

        @Test
        @DisplayName("Should return empty Optional for type mismatch")
        void testTypeMismatch() {
            config.set("value", DataValue.of("string-value"));
            
            Optional<Integer> value = config.get("value", Integer.class);
            assertThat(value).isEmpty();
        }
    }

    @Nested
    @DisplayName("DataValue Integration")
    class DataValueIntegrationTests {

        @Test
        @DisplayName("Should get values as DataValue")
        void testGetAsDataValue() {
            config.set("quest.title", DataValue.of("The First Quest"));
            
            Optional<DataValue> dataValue = config.getAsDataValue("quest.title");
            assertThat(dataValue).isPresent();
            assertThat(dataValue.get().asString()).contains("The First Quest");
        }

        @Test
        @DisplayName("Should set complex DataValue structures")
        void testSetComplexDataValue() {
            Map<String, DataValue> questData = Map.of(
                "id", DataValue.of("quest-001"),
                "title", DataValue.of("Tutorial Quest"),
                "rewards", DataValue.of(List.of(DataValue.of(100), DataValue.of(200)))
            );
            
            config.set("quests.tutorial", DataValue.of(questData));
            
            Optional<DataValue> retrieved = config.getAsDataValue("quests.tutorial.title");
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().asString()).contains("Tutorial Quest");
        }

        @Test
        @DisplayName("Should return all values as DataValue map")
        void testGetValues() {
            config.set("key1", DataValue.of("value1"));
            config.set("key2", DataValue.of(42));
            
            Map<String, DataValue> values = config.getValues();
            assertThat(values).containsKeys("key1", "key2");
        }
    }

    @Nested
    @DisplayName("Nested Paths")
    class NestedPathsTests {

        @Test
        @DisplayName("Should handle deeply nested paths")
        void testDeeplyNestedPaths() {
            config.set("level1.level2.level3.value", DataValue.of("deep-value"));
            
            Optional<String> value = config.get("level1.level2.level3.value", String.class);
            assertThat(value).contains("deep-value");
        }

        @Test
        @DisplayName("Should create intermediate maps automatically")
        void testAutoCreateIntermediateMaps() {
            config.set("a.b.c.d.e", DataValue.of(123));
            
            assertThat(config.contains("a.b.c.d.e")).isTrue();
            Optional<Integer> value = config.get("a.b.c.d.e", Integer.class);
            assertThat(value).contains(123);
        }

        @Test
        @DisplayName("Should return empty for incomplete nested path")
        void testIncompleteNestedPath() {
            config.set("root.leaf", DataValue.of("value"));
            
            Optional<String> value = config.get("root.leaf.extra", String.class);
            assertThat(value).isEmpty();
        }
    }

    @Nested
    @DisplayName("Default Values")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should load and merge defaults")
        void testLoadDefaults() {
            String defaultYaml = """
                server:
                  host: localhost
                  port: 8080
                features:
                  pvp: true
                """;
            
            config.loadDefaults(new ByteArrayInputStream(defaultYaml.getBytes()));
            
            assertThat(config.get("server.host", String.class)).contains("localhost");
            assertThat(config.get("server.port", Integer.class)).contains(8080);
            assertThat(config.get("features.pvp", Boolean.class)).contains(true);
        }

        @Test
        @DisplayName("Should not override existing values with defaults")
        void testDefaultsDontOverride() {
            // Set existing value
            config.set("server.port", DataValue.of(9090));
            
            String defaultYaml = """
                server:
                  port: 8080
                """;
            
            config.loadDefaults(new ByteArrayInputStream(defaultYaml.getBytes()));
            
            // Existing value should not be overridden
            assertThat(config.get("server.port", Integer.class)).contains(9090);
        }

        @Test
        @DisplayName("Should merge nested defaults")
        void testMergeNestedDefaults() {
            // Set partial config
            config.set("database.host", DataValue.of("custom-host"));
            
            String defaultYaml = """
                database:
                  host: localhost
                  port: 3306
                  timeout: 30
                """;
            
            config.loadDefaults(new ByteArrayInputStream(defaultYaml.getBytes()));
            
            // Custom value preserved
            assertThat(config.get("database.host", String.class)).contains("custom-host");
            // Defaults added
            assertThat(config.get("database.port", Integer.class)).contains(3306);
            assertThat(config.get("database.timeout", Integer.class)).contains(30);
        }
    }

    @Nested
    @DisplayName("Change Listeners")
    class ChangeListenerTests {

        @Test
        @DisplayName("Should fire change event on set")
        void testChangeEventOnSet() {
            AtomicInteger callCount = new AtomicInteger(0);
            ConfigChangeEvent[] capturedEvent = new ConfigChangeEvent[1];
            
            config.addChangeListener("server", event -> {
                callCount.incrementAndGet();
                capturedEvent[0] = event;
            });
            
            config.set("server.port", DataValue.of(8080));
            
            assertThat(callCount.get()).isEqualTo(1);
            assertThat(capturedEvent[0]).isNotNull();
            assertThat(capturedEvent[0].path()).isEqualTo("server.port");
            assertThat(capturedEvent[0].isCreation()).isTrue();
        }

        @Test
        @DisplayName("Should fire change event on modification")
        void testChangeEventOnModification() {
            config.set("value", DataValue.of(10));
            
            ConfigChangeEvent[] capturedEvent = new ConfigChangeEvent[1];
            config.addChangeListener("value", event -> {
                capturedEvent[0] = event;
            });
            
            config.set("value", DataValue.of(20));
            
            assertThat(capturedEvent[0]).isNotNull();
            assertThat(capturedEvent[0].isModification()).isTrue();
            assertThat(capturedEvent[0].oldValue()).isPresent();
            assertThat(capturedEvent[0].newValue()).isPresent();
        }

        @Test
        @DisplayName("Should only fire for matching path prefix")
        void testPathPrefixMatching() {
            AtomicInteger serverCallCount = new AtomicInteger(0);
            AtomicInteger databaseCallCount = new AtomicInteger(0);
            
            config.addChangeListener("server", event -> serverCallCount.incrementAndGet());
            config.addChangeListener("database", event -> databaseCallCount.incrementAndGet());
            
            config.set("server.port", DataValue.of(8080));
            config.set("database.url", DataValue.of("jdbc:mysql://localhost"));
            
            assertThat(serverCallCount.get()).isEqualTo(1);
            assertThat(databaseCallCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should remove listener")
        void testRemoveListener() {
            AtomicInteger callCount = new AtomicInteger(0);
            var listener = (java.util.function.Consumer<ConfigChangeEvent>) event -> callCount.incrementAndGet();
            
            config.addChangeListener("test", listener);
            config.set("test.value", DataValue.of(1));
            assertThat(callCount.get()).isEqualTo(1);
            
            config.removeChangeListener(listener);
            config.set("test.value", DataValue.of(2));
            assertThat(callCount.get()).isEqualTo(1); // Should not increment
        }
    }

    @Nested
    @DisplayName("Reload")
    class ReloadTests {

        @Test
        @DisplayName("Should reload configuration from disk")
        void testReload() throws IOException {
            // Save initial config
            config.set("version", DataValue.of(1));
            config.save();
            
            // Modify config in memory
            config.set("version", DataValue.of(2));
            assertThat(config.get("version", Integer.class)).contains(2);
            
            // Reload from disk
            config.reload();
            assertThat(config.get("version", Integer.class)).contains(1);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null path gracefully")
        void testNullPath() {
            assertThat(config.get(null, String.class)).isEmpty();
            assertThat(config.contains(null)).isFalse();
        }

        @Test
        @DisplayName("Should handle empty path gracefully")
        void testEmptyPath() {
            assertThat(config.get("", String.class)).isEmpty();
            assertThat(config.contains("")).isFalse();
        }

        @Test
        @DisplayName("Should throw on set with null path")
        void testSetNullPath() {
            assertThatThrownBy(() -> config.set(null, DataValue.of("value")))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should throw on set with empty path")
        void testSetEmptyPath() {
            assertThatThrownBy(() -> config.set("", DataValue.of("value")))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should handle null defaults gracefully")
        void testNullDefaults() {
            assertThatCode(() -> config.loadDefaults(null))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Thread Safety")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent reads and writes")
        void testConcurrentAccess() throws InterruptedException {
            Thread writer = new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    config.set("counter", DataValue.of(i));
                }
            });
            
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    config.get("counter", Integer.class);
                }
            });
            
            writer.start();
            reader.start();
            
            writer.join();
            reader.join();
            
            // Should not crash or throw exceptions
            assertThat(config.get("counter", Integer.class)).isPresent();
        }
    }
}
