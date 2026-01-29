package com.argonathsystems.framework.config;

import com.argonathsystems.framework.accessorapi.data.DataValue;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Interface for configuration management.
 * Provides type-safe methods to load, save, and access configuration values.
 * 
 * <p>All configuration values are stored internally and can be accessed via
 * dot-separated paths (e.g., "server.port" or "database.connection.timeout").</p>
 * 
 * <p><b>Zero Hytale Imports Policy:</b> This is a platform-agnostic library.
 * No direct Hytale API dependencies allowed.</p>
 */
public interface ConfigurationManager {

    /**
     * Loads the configuration from disk.
     */
    void load();

    /**
     * Saves the current configuration to disk.
     */
    void save();

    /**
     * Gets a typed value from the configuration.
     * @param path The path to the value (dot-separated).
     * @param type The class of the type to retrieve.
     * @param <T> The type.
     * @return An Optional containing the value, or empty if not found or type mismatch.
     */
    <T> Optional<T> get(String path, Class<T> type);

    /**
     * Gets a typed value from the configuration with a default.
     * @param path The path to the value.
     * @param type The class of the type to retrieve.
     * @param defaultValue The default value to return if not found.
     * @param <T> The type.
     * @return The value, or defaultValue if not found.
     */
    <T> T get(String path, Class<T> type, T defaultValue);
    
    /**
     * Gets a value as a DataValue for interoperability with other modules.
     * @param path The path to the value.
     * @return An Optional containing the DataValue, or empty if not found.
     */
    Optional<DataValue> getAsDataValue(String path);

    /**
     * Sets a value in the configuration.
     * @param path The path to set.
     * @param value The value to set.
     */
    void set(String path, DataValue value);

    /**
     * Checks if a path exists in the configuration.
     * @param path The path to check.
     * @return True if exists, false otherwise.
     */
    boolean contains(String path);
    
    /**
     * Reloads the configuration from disk, discarding unsaved changes.
     */
    void reload();

    /**
     * Loads default values from a resource stream (e.g., from the JAR).
     * Missing keys in the actual config will be populated from these defaults.
     * Note: This does not automatically save to disk. Call save() to persist.
     * 
     * @param resourceStream The input stream of the default YAML file.
     */
    void loadDefaults(java.io.InputStream resourceStream);

    /**
     * Gets the entire configuration map as DataValues.
     * @return The configuration map.
     */
    Map<String, DataValue> getValues();
    
    /**
     * Registers a listener for configuration changes.
     * @param pathPrefix The path prefix to watch (e.g., "database" watches "database.*").
     * @param listener The callback to invoke on changes.
     */
    void addChangeListener(String pathPrefix, Consumer<ConfigChangeEvent> listener);
    
    /**
     * Removes a change listener.
     * @param listener The listener to remove.
     */
    void removeChangeListener(Consumer<ConfigChangeEvent> listener);
}

