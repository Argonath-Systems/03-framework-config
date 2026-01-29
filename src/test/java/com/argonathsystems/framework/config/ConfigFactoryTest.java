package com.argonathsystems.framework.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for ConfigFactory.
 */
class ConfigFactoryTest {

    @Test
    @DisplayName("Should create and cache configuration manager")
    void testGetManager() {
        ConfigurationManager config1 = ConfigFactory.getManager("test-mod");
        ConfigurationManager config2 = ConfigFactory.getManager("test-mod");
        
        assertThat(config1).isNotNull();
        assertThat(config1).isSameAs(config2); // Should return cached instance
    }

    @Test
    @DisplayName("Should create different managers for different mod IDs")
    void testDifferentModIds() {
        ConfigurationManager config1 = ConfigFactory.getManager("mod-a");
        ConfigurationManager config2 = ConfigFactory.getManager("mod-b");
        
        assertThat(config1).isNotSameAs(config2);
    }

    @Test
    @DisplayName("Should unload manager")
    void testUnloadManager() {
        ConfigurationManager config1 = ConfigFactory.getManager("temp-mod");
        ConfigFactory.unloadManager("temp-mod");
        ConfigurationManager config2 = ConfigFactory.getManager("temp-mod");
        
        assertThat(config1).isNotSameAs(config2); // Should be new instance
    }
}
