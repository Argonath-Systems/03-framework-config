package com.argonathsystems.framework.config.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a configuration holder to be managed by the configuration system.
 * 
 * <p>Classes annotated with {@code @Configurable} can be automatically loaded from
 * and saved to configuration files. The configuration manager will handle serialization
 * and deserialization based on field annotations.
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * @Configurable(file = "my-config.yml", path = "settings")
 * public class MyConfig {
 *     private boolean enabled = true;
 *     private int maxItems = 100;
 *     
 *     // getters and setters...
 * }
 * }</pre>
 * 
 * @author Argonath Systems Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configurable {
    
    /**
     * The configuration file name.
     * 
     * <p>Relative to the plugin's configuration directory.
     * 
     * @return The file name (e.g., "config.yml", "settings/advanced.yml")
     */
    String file();
    
    /**
     * The path within the configuration file where this config's values are stored.
     * 
     * <p>Uses dot notation for nested paths (e.g., "settings.advanced.features").
     * An empty string indicates root level.
     * 
     * @return The configuration path
     */
    String path() default "";
    
    /**
     * Whether to automatically reload when the file changes.
     * 
     * @return true if hot-reload is enabled
     */
    boolean hotReload() default false;
}
