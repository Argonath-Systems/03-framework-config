package com.argonathsystems.framework.config;

import com.argonathsystems.framework.accessorapi.data.DataValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * YAML implementation of ConfigurationManager.
 * Enforces the structure: config/Argonath/{modId}/config.yml
 * 
 * <p>This implementation is thread-safe and supports change listeners.</p>
 */
public class YamlConfigurationManager implements ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(YamlConfigurationManager.class);

    private final String modId;
    private final Path configPath;
    private final Map<String, DataValue> configData;
    private final Yaml yaml;
    private final List<ChangeListenerEntry> changeListeners;

    public YamlConfigurationManager(String modId) {
        this.modId = modId;
        // Defined common path: config/Argonath/{modId}/config.yml
        this.configPath = Paths.get("config", "Argonath", modId, "config.yml");
        
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        this.yaml = new Yaml(options);
        
        this.configData = new ConcurrentHashMap<>();
        this.changeListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void load() {
        if (Files.exists(configPath)) {
            try (InputStream in = Files.newInputStream(configPath)) {
                Map<String, Object> loaded = yaml.load(in);
                if (loaded != null) {
                    this.configData.clear();
                    convertAndStore(loaded, this.configData);
                } else {
                    this.configData.clear();
                }
                logger.info("Loaded configuration for {} from {}", modId, configPath);
            } catch (IOException e) {
                logger.error("Failed to load configuration for {}: {}", modId, e.getMessage(), e);
            }
        } else {
            // File doesn't exist, start with empty
            this.configData.clear();
            logger.info("No existing configuration found for {} at {}", modId, configPath);
        }
    }

    @Override
    public void save() {
        try {
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }
            
            // Convert DataValues back to raw objects for YAML serialization
            Map<String, Object> rawData = convertToRawMap(configData);
            
            try (Writer writer = new FileWriter(configPath.toFile())) {
                yaml.dump(rawData, writer);
            }
            logger.info("Saved configuration for {} to {}", modId, configPath);
        } catch (IOException e) {
            logger.error("Failed to save configuration for {}: {}", modId, e.getMessage(), e);
        }
    }

    @Override
    public <T> Optional<T> get(String path, Class<T> type) {
        if (path == null || path.isEmpty()) {
            return Optional.empty();
        }
        
        Optional<DataValue> dataValue = getByPath(path);
        if (dataValue.isEmpty()) {
            return Optional.empty();
        }
        
        return convertDataValueToType(dataValue.get(), type);
    }

    @Override
    public <T> T get(String path, Class<T> type, T defaultValue) {
        return get(path, type).orElse(defaultValue);
    }
    
    @Override
    public Optional<DataValue> getAsDataValue(String path) {
        return getByPath(path);
    }

    @Override
    public void loadDefaults(InputStream resourceStream) {
        if (resourceStream == null) {
            return;
        }
        
        try {
            Map<String, Object> defaults = yaml.load(resourceStream);
            if (defaults != null) {
                Map<String, DataValue> defaultsData = new HashMap<>();
                convertAndStore(defaults, defaultsData);
                mergeDefaults(this.configData, defaultsData);
                logger.info("Loaded default configuration for {}", modId);
            }
        } catch (Exception e) {
            logger.error("Failed to load defaults for {}: {}", modId, e.getMessage(), e);
        }
    }

    private void mergeDefaults(Map<String, DataValue> target, Map<String, DataValue> source) {
        for (Map.Entry<String, DataValue> entry : source.entrySet()) {
            String key = entry.getKey();
            DataValue value = entry.getValue();

            if (value instanceof DataValue.MapValue mapValue) {
                DataValue targetValue = target.get(key);
                if (targetValue instanceof DataValue.MapValue targetMap) {
                    // Recurse into nested maps
                    mergeDefaults(targetMap.value(), mapValue.value());
                } else if (!target.containsKey(key)) {
                    // Target missing this section, copy it
                    target.put(key, value);
                }
                // If target has a non-map value here, treat as valid user override
            } else {
                // Primitive/List - only set if missing
                if (!target.containsKey(key)) {
                    target.put(key, value);
                }
            }
        }
    }

    @Override
    public void set(String path, DataValue value) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        
        Optional<DataValue> oldValue = getByPath(path);
        setByPath(path, value);
        
        // Fire change listeners
        ConfigChangeEvent event = new ConfigChangeEvent(path, oldValue, Optional.of(value));
        fireChangeEvent(event);
    }

    @Override
    public boolean contains(String path) {
        return getByPath(path).isPresent();
    }

    @Override
    public void reload() {
        load();
    }

    @Override
    public Map<String, DataValue> getValues() {
        return new HashMap<>(configData);
    }
    
    @Override
    public void addChangeListener(String pathPrefix, Consumer<ConfigChangeEvent> listener) {
        if (pathPrefix == null || listener == null) {
            throw new IllegalArgumentException("pathPrefix and listener cannot be null");
        }
        changeListeners.add(new ChangeListenerEntry(pathPrefix, listener));
        logger.debug("Added change listener for path prefix: {}", pathPrefix);
    }
    
    @Override
    public void removeChangeListener(Consumer<ConfigChangeEvent> listener) {
        changeListeners.removeIf(entry -> entry.listener().equals(listener));
    }

    // ==================== Private Helper Methods ====================

    private Optional<DataValue> getByPath(String path) {
        if (path == null || path.isEmpty()) {
            return Optional.empty();
        }
        
        String[] parts = path.split("\\.");
        Map<String, DataValue> current = configData;
        
        for (int i = 0; i < parts.length - 1; i++) {
            DataValue dv = current.get(parts[i]);
            if (dv instanceof DataValue.MapValue mapValue) {
                current = mapValue.value();
            } else {
                return Optional.empty();
            }
        }
        
        return Optional.ofNullable(current.get(parts[parts.length - 1]));
    }

    private void setByPath(String path, DataValue value) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        
        String[] parts = path.split("\\.");
        Map<String, DataValue> current = configData;
        
        for (int i = 0; i < parts.length - 1; i++) {
            DataValue dv = current.get(parts[i]);
            if (dv == null) {
                Map<String, DataValue> newMap = new HashMap<>();
                current.put(parts[i], DataValue.of(newMap));
                current = newMap;
            } else if (dv instanceof DataValue.MapValue mapValue) {
                current = mapValue.value();
            } else {
                // Overwrite non-map with map
                Map<String, DataValue> newMap = new HashMap<>();
                current.put(parts[i], DataValue.of(newMap));
                current = newMap;
            }
        }
        
        current.put(parts[parts.length - 1], value);
    }

    @SuppressWarnings("unchecked")
    private void convertAndStore(Map<String, Object> source, Map<String, DataValue> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map<String, DataValue> nested = new HashMap<>();
                convertAndStore((Map<String, Object>) value, nested);
                target.put(entry.getKey(), DataValue.of(nested));
            } else if (value instanceof List) {
                List<DataValue> list = new ArrayList<>();
                for (Object item : (List<?>) value) {
                    list.add(convertToDataValue(item));
                }
                target.put(entry.getKey(), DataValue.of(list));
            } else {
                target.put(entry.getKey(), convertToDataValue(value));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private DataValue convertToDataValue(Object value) {
        if (value == null) {
            return DataValue.of("null");
        }
        if (value instanceof String s) {
            return DataValue.of(s);
        }
        if (value instanceof Integer i) {
            return DataValue.of(i);
        }
        if (value instanceof Long l) {
            return DataValue.of(l);
        }
        if (value instanceof Double d) {
            return DataValue.of(d);
        }
        if (value instanceof Boolean b) {
            return DataValue.of(b);
        }
        if (value instanceof Map) {
            Map<String, DataValue> map = new HashMap<>();
            convertAndStore((Map<String, Object>) value, map);
            return DataValue.of(map);
        }
        if (value instanceof List) {
            List<DataValue> list = new ArrayList<>();
            for (Object item : (List<?>) value) {
                list.add(convertToDataValue(item));
            }
            return DataValue.of(list);
        }
        // Fallback: convert to string
        return DataValue.of(value.toString());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToRawMap(Map<String, DataValue> source) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, DataValue> entry : source.entrySet()) {
            result.put(entry.getKey(), convertDataValueToRaw(entry.getValue()));
        }
        return result;
    }

    private Object convertDataValueToRaw(DataValue dv) {
        return switch (dv) {
            case DataValue.StringValue sv -> sv.value();
            case DataValue.IntValue iv -> iv.value();
            case DataValue.LongValue lv -> lv.value();
            case DataValue.DoubleValue dov -> dov.value();
            case DataValue.BoolValue bv -> bv.value();
            case DataValue.ListValue lv -> {
                List<Object> list = new ArrayList<>();
                for (DataValue item : lv.value()) {
                    list.add(convertDataValueToRaw(item));
                }
                yield list;
            }
            case DataValue.MapValue mv -> convertToRawMap(mv.value());
        };
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> convertDataValueToType(DataValue dv, Class<T> type) {
        try {
            if (type == String.class) {
                return dv.asString().map(s -> (T) s);
            }
            if (type == Integer.class || type == int.class) {
                return dv.asInt().map(i -> (T) i);
            }
            if (type == Long.class || type == long.class) {
                return dv.asLong().map(l -> (T) l);
            }
            if (type == Double.class || type == double.class) {
                return dv.asDouble().map(d -> (T) d);
            }
            if (type == Boolean.class || type == boolean.class) {
                return dv.asBool().map(b -> (T) b);
            }
            if (type == List.class) {
                return dv.asList().map(l -> (T) l);
            }
            if (type == Map.class) {
                return dv.asMap().map(m -> (T) m);
            }
            
            logger.warn("Unsupported type conversion for path: {}, type: {}", type.getName(), dv.getClass().getSimpleName());
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Type conversion error for type {}: {}", type.getName(), e.getMessage());
            return Optional.empty();
        }
    }

    private void fireChangeEvent(ConfigChangeEvent event) {
        for (ChangeListenerEntry entry : changeListeners) {
            if (event.path().startsWith(entry.pathPrefix())) {
                try {
                    entry.listener().accept(event);
                } catch (Exception e) {
                    logger.error("Error in change listener for path {}: {}", event.path(), e.getMessage(), e);
                }
            }
        }
    }

    private record ChangeListenerEntry(
        String pathPrefix,
        Consumer<ConfigChangeEvent> listener
    ) {}
}
