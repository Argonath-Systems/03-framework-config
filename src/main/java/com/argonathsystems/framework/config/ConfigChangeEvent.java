package com.argonathsystems.framework.config;

import com.argonathsystems.framework.accessorapi.data.DataValue;

import java.util.Optional;

/**
 * Event fired when a configuration value changes.
 */
public record ConfigChangeEvent(
    String path,
    Optional<DataValue> oldValue,
    Optional<DataValue> newValue
) {
    /**
     * Creates a new change event.
     * @param path The configuration path that changed
     * @param oldValue The previous value (empty if newly created)
     * @param newValue The new value (empty if deleted)
     */
    public ConfigChangeEvent {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
    }
    
    /**
     * Checks if this was a creation (no old value).
     * @return true if newly created
     */
    public boolean isCreation() {
        return oldValue.isEmpty() && newValue.isPresent();
    }
    
    /**
     * Checks if this was a deletion (no new value).
     * @return true if deleted
     */
    public boolean isDeletion() {
        return oldValue.isPresent() && newValue.isEmpty();
    }
    
    /**
     * Checks if this was a modification (both values present).
     * @return true if modified
     */
    public boolean isModification() {
        return oldValue.isPresent() && newValue.isPresent();
    }
}
