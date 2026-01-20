package com.argonathsystems.framework.config;

import java.util.Map;

/**
 * Interface for configuration management.
 * Provides methods to load, save, and access configuration values.
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
     * Gets a value from the configuration.
     * @param path The path to the value (dot-separated).
     * @return The value, or null if not found.
     */
    Object get(String path);

    /**
     * Gets a typed value from the configuration.
     * @param path The path to the value.
     * @param type The class of the type to retrieve.
     * @param <T> The type.
     * @return The value, or null if not found or type mismatch.
     */
    <T> T get(String path, Class<T> type);

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
     * Sets a value in the configuration.
     * @param path The path to set.
     * @param value The value to set.
     */
    void set(String path, Object value);

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
     * Gets the entire configuration map.
     * @return The raw map.
     */
    Map<String, Object> getValues();
}
