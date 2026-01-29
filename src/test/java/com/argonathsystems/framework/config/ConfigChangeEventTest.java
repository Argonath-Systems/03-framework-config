package com.argonathsystems.framework.config;

import com.argonathsystems.framework.accessorapi.data.DataValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for ConfigChangeEvent.
 */
class ConfigChangeEventTest {

    @Test
    @DisplayName("Should create event with valid data")
    void testCreate() {
        ConfigChangeEvent event = new ConfigChangeEvent(
            "server.port",
            Optional.of(DataValue.of(8080)),
            Optional.of(DataValue.of(9090))
        );
        
        assertThat(event.path()).isEqualTo("server.port");
        assertThat(event.oldValue()).contains(DataValue.of(8080));
        assertThat(event.newValue()).contains(DataValue.of(9090));
    }

    @Test
    @DisplayName("Should detect creation")
    void testIsCreation() {
        ConfigChangeEvent event = new ConfigChangeEvent(
            "new.key",
            Optional.empty(),
            Optional.of(DataValue.of("value"))
        );
        
        assertThat(event.isCreation()).isTrue();
        assertThat(event.isModification()).isFalse();
        assertThat(event.isDeletion()).isFalse();
    }

    @Test
    @DisplayName("Should detect deletion")
    void testIsDeletion() {
        ConfigChangeEvent event = new ConfigChangeEvent(
            "old.key",
            Optional.of(DataValue.of("value")),
            Optional.empty()
        );
        
        assertThat(event.isDeletion()).isTrue();
        assertThat(event.isCreation()).isFalse();
        assertThat(event.isModification()).isFalse();
    }

    @Test
    @DisplayName("Should detect modification")
    void testIsModification() {
        ConfigChangeEvent event = new ConfigChangeEvent(
            "key",
            Optional.of(DataValue.of(1)),
            Optional.of(DataValue.of(2))
        );
        
        assertThat(event.isModification()).isTrue();
        assertThat(event.isCreation()).isFalse();
        assertThat(event.isDeletion()).isFalse();
    }

    @Test
    @DisplayName("Should throw on null path")
    void testNullPath() {
        assertThatThrownBy(() -> new ConfigChangeEvent(
            null,
            Optional.empty(),
            Optional.of(DataValue.of("value"))
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw on empty path")
    void testEmptyPath() {
        assertThatThrownBy(() -> new ConfigChangeEvent(
            "",
            Optional.empty(),
            Optional.of(DataValue.of("value"))
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
