package org.gooru.route0.infra.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish.
 */
public final class AppConfiguration implements Initializer {
    private static final String APP_CONFIG_KEY = "app.configuration";
    private JsonObject configuration;
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfiguration.class);

    public static AppConfiguration getInstance() {
        return Holder.INSTANCE;
    }

    private volatile boolean initialized = false;

    private AppConfiguration() {
    }

    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        if (!initialized) {
            synchronized (Holder.INSTANCE) {
                if (!initialized) {
                    JsonObject appConfiguration = config.getJsonObject(APP_CONFIG_KEY);
                    if (appConfiguration == null || appConfiguration.isEmpty()) {
                        LOGGER.warn("App configuration is not available");
                    } else {
                        configuration = appConfiguration.copy();
                        initialized = true;
                    }
                }
            }
        }
    }

    public String getRoute0ApplicableCourseVersion() {
        return configuration.getString("route0.applicable.course.version");
    }

    public int getConfigAsInt(String key) {
        return configuration.getInteger(key);
    }

    public boolean getConfigAsBoolean(String key) {
        return configuration.getBoolean(key);
    }

    public String getConfigAsString(String key) {
        return configuration.getString(key);
    }

    public Object getConfigAsRawObject(String key) {
        return configuration.getValue(key);
    }

    private static final class Holder {
        private static final AppConfiguration INSTANCE = new AppConfiguration();
    }

}
