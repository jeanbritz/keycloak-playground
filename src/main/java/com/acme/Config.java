package com.acme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static Properties properties;
    private static final Config INSTANCE;

    static {
        INSTANCE = new Config();
        INSTANCE.init();
    }

    public interface K {

        /**
         * The name of the property.
         * @return String of the property name
         */
        String getPropertyName();

        /**
         * The default value of the property if not found.
         * @return the default value
         */
        Object getDefaultValue();
    }

    public enum Key implements K {
        SERVER_HOSTNAME ("acme.server.hostname", "localhost"),
        SERVER_PORT ("acme.server.port", 8080),
        SERVER_BASE_CONTEXT("acme.server.base.url", "/"),
        OIDC_ISSUER_URL                         ("acme.oidc.issuer.url", null),
        OIDC_CLIENT_ID                          ("acme.oidc.client.id",               null),
        OIDC_CLIENT_SECRET                      ("acme.oidc.client.secret", null),
        OIDC_AUTH_CALLBACK_URL                  ("acme.oidc.auth.callback.url", null),
        FRONTEND_BASE_URL                       ("acme.frontend.base.url", "http://localhost:3000"),
        SESSION_COOKIE_NAME                     ("acme.session.cookie.name", "SESSION_ID"),
        SWAGGER_ENABLED ("acme.swagger.enabled", true);

        private final String propertyName;
        private final Object defaultValue;
        Key(String item, Object defaultValue) {
            this.propertyName = item;
            this.defaultValue = defaultValue;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    public static Config get() {
        return INSTANCE;
    }

    public void init() {
        if (properties != null) {
            return;
        }

        logger.info("Initializing Configuration");
        properties = new Properties();
        final Path appPropPath = Paths.get(System.getProperty("user.dir"), "application.properties");
        if (Files.exists(appPropPath)) {
            logger.info("Loading application properties from {}", appPropPath);
            try (InputStream fis = Files.newInputStream(appPropPath)) {
                properties.load(fis);
            } catch (FileNotFoundException e) {
                logger.error("Could not find property file {}", appPropPath);
            } catch (IOException e) {
                logger.error("Unable to load {}", appPropPath);
            }
        }
    }

    public static String getProperty(Key key) {
        final String envVariable = getPropertyFromEnvironment(key);
        if (envVariable != null) {
            return envVariable;
        }
        if (key.getDefaultValue() == null) {
            return properties.getProperty(key.getPropertyName());
        } else {
            return properties.getProperty(key.getPropertyName(), String.valueOf(key.getDefaultValue()));
        }
    }

    public static int getIntProperty(Key key) {
        String val = String.valueOf(getProperty(key));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            logger.error("error parsing config property [{}], expected integer value", key.getPropertyName(), e);
            return -1;
        }
    }

    public static boolean getBoolProperty(Key key) {
        String val = String.valueOf(getProperty(key));
        try {
            return Boolean.parseBoolean(val);
        } catch (NumberFormatException e) {
            logger.error("error parsing config property [{}], expected boolean value", key.getPropertyName(), e);
            return false;
        }
    }

    private static String getPropertyFromEnvironment(Key key) {
        final String envVariable = key.getPropertyName().toUpperCase().replace(".", "_");
        try {
            return (System.getenv(envVariable)).trim();
        } catch (SecurityException e) {
            logger.warn("A security exception prevented access to the environment variable. Using defaults.");
        } catch (NullPointerException e) {
            // Do nothing. The key was not specified in an environment variable. Continue along.
        }
        return null;
    }
}
