package org.example.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        String env = System.getProperty("env", "dev");
        String path = "config/" + env + ".properties";
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found: " + path);
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load config: " + path, e);
        }
    }

    private Config() {}

    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override;
        }
        return PROPS.getProperty(key);
    }
}