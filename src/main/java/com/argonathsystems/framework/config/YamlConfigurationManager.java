package com.argonathsystems.framework.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * YAML implementation of ConfigurationManager.
 * Enforces the structure: config/Argonath/{modId}/config.yml
 */
public class YamlConfigurationManager implements ConfigurationManager {

    private final String modId;
    private final Path configPath;
    private Map<String, Object> configData;
    private final Yaml yaml;

    public YamlConfigurationManager(String modId) {
        this.modId = modId;
        // Defined common path: config/Argonath/{modId}/config.yml
        this.configPath = Paths.get("config", "Argonath", modId, "config.yml");
        
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        this.yaml = new Yaml(options);
        
        this.configData = new HashMap<>();
    }

    @Override
    public void load() {
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                Map<String, Object> loaded = yaml.load(in);
                if (loaded != null) {
                    this.configData = loaded;
                } else {
                    this.configData = new HashMap<>();
                }
            } catch (IOException e) {
                System.err.println("Failed to load configuration for " + modId + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // File doesn't exist, start with empty or default if provided later
            this.configData = new HashMap<>();
        }
    }

    @Override
    public void save() {
        try {
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }
            try (Writer writer = new FileWriter(configPath.toFile())) {
                yaml.dump(configData, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save configuration for " + modId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String path) {
        return getByPath(path);
    }

    @Override
    public <T> T get(String path, Class<T> type) {
        Object val = getByPath(path);
        if (val != null) {
             if (type.isInstance(val)) {
                return type.cast(val);
             } else {
                 // Type mismatch handling: Log warning and return null (which triggers default)
                 System.err.println("[Config Warning] Type mismatch for '" + path + "'. Expected " + type.getSimpleName() + ", got " + val.getClass().getSimpleName());
             }
        }
        return null;
    }

    @Override
    public <T> T get(String path, Class<T> type, T defaultValue) {
        T val = get(path, type);
        return val != null ? val : defaultValue;
    }

    @Override
    public void loadDefaults(InputStream resourceStream) {
        if (resourceStream == null) return;
        try {
            Map<String, Object> defaults = yaml.load(resourceStream);
            if (defaults != null) {
                mergeDefaults(this.configData, defaults);
            }
        } catch (Exception e) {
             System.err.println("Failed to load defaults for " + modId + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void mergeDefaults(Map<String, Object> target, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                Object targetValue = target.get(key);
                if (targetValue instanceof Map) {
                    // Recurse
                    mergeDefaults((Map<String, Object>) targetValue, (Map<String, Object>) value);
                } else if (!target.containsKey(key)) {
                    // Target missing this section, simply put deep copy/reference
                    // For safety, we should probably clone, but for simple config reference is okay usually 
                    // unless we modify defaults. Here we assumes defaults are static structure.
                    target.put(key, value); 
                }
                // If target has a non-map value here, we treat it as valid user override and do nothing
            } else {
                // Primitive/List
                if (!target.containsKey(key)) {
                    target.put(key, value);
                }
            }
        }
    }

    @Override
    public void set(String path, Object value) {
        setByPath(path, value);
    }

    @Override
    public boolean contains(String path) {
        return getByPath(path) != null;
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public Map<String, Object> getValues() {
        return configData;
    }

    // Helper to traverse nested maps "a.b.c"
    @SuppressWarnings("unchecked")
    private Object getByPath(String path) {
        if (path == null || path.isEmpty()) return null;
        String[] parts = path.split("\\.");
        Map<String, Object> current = configData;
        
        for (int i = 0; i < parts.length - 1; i++) {
            Object obj = current.get(parts[i]);
            if (obj instanceof Map) {
                current = (Map<String, Object>) obj;
            } else {
                return null;
            }
        }
        return current.get(parts[parts.length - 1]);
    }

    @SuppressWarnings("unchecked")
    private void setByPath(String path, Object value) {
        if (path == null || path.isEmpty()) return;
        String[] parts = path.split("\\.");
        Map<String, Object> current = configData;
        
        for (int i = 0; i < parts.length - 1; i++) {
            Object obj = current.get(parts[i]);
            if (obj == null) {
                Map<String, Object> newMap = new HashMap<>();
                current.put(parts[i], newMap);
                current = newMap;
            } else if (obj instanceof Map) {
                current = (Map<String, Object>) obj;
            } else {
                // If we encounter a non-map value where we strictly need a map, we overwrite it.
                // This is a design choice.
                Map<String, Object> newMap = new HashMap<>();
                current.put(parts[i], newMap);
                current = newMap;
            }
        }
        current.put(parts[parts.length - 1], value);
    }
}
