package com.argonathsystems.framework.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for obtaining ConfigurationManager instances.
 * Ensures that each mod uses the correct mutualized path structure.
 */
public class ConfigFactory {
    
    private static final Map<String, ConfigurationManager> managers = new ConcurrentHashMap<>();

    private ConfigFactory() {
        // Prevent instantiation
    }

    /**
     * Gets or creates the configuration manager for the specified mod.
     * Use this method to ensure you are accessing the mutualized configuration location.
     * The configuration is loaded automatically upon creation.
     * 
     * @param modId The unique identifier of the mod (e.g. "quest-framework").
     * @return The ConfigurationManager instance.
     */
    public static ConfigurationManager getManager(String modId) {
        return managers.computeIfAbsent(modId, id -> {
            YamlConfigurationManager manager = new YamlConfigurationManager(id);
            manager.load();
            return manager;
        });
    }

    /**
     * Unloads modules config from memory.
     * @param modId The unique identifier of the mod.
     */
    public static void unloadManager(String modId) {
        managers.remove(modId);
    }
}
