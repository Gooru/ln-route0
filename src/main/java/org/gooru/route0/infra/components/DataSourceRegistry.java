package org.gooru.route0.infra.components;

import com.zaxxer.hikari.HikariDataSource;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ashish.
 */
public final class DataSourceRegistry implements Initializer, Finalizer {

  private static final String CONFIG_DS_KEY = "datasources";
  private static final String DEFAULT_DATA_SOURCE = "defaultDataSource";
  private static final String DSDB_DATA_SOURCE = "dsdbDataSource";
  private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRegistry.class);

  private final List<String> datasources = Arrays.asList(DEFAULT_DATA_SOURCE, DSDB_DATA_SOURCE);
  private final Map<String, DataSource> registry = new HashMap<>();
  private volatile boolean initialized = false;

  private DataSourceRegistry() {
  }

  public static DataSourceRegistry getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public void initializeComponent(Vertx vertx, JsonObject dsConfig) {
    // Skip if we are already initialized
    LOGGER.debug("Initialization called upon.");
    if (!initialized) {
      LOGGER.debug("May have to do initialization");
      // We need to do initialization, however, we are running it via
      // verticle instance which is going to run in
      // multiple threads hence we need to be safe for this operation
      synchronized (Holder.INSTANCE) {
        LOGGER.debug("Will initialize after double checking");
        if (!initialized) {
          LOGGER.debug("Initializing now");
          JsonObject config = dsConfig.getJsonObject(CONFIG_DS_KEY);
          if (config == null || config.isEmpty()) {
            throw new IllegalStateException("No configuaration for datasouces found");
          }
          for (String datasource : datasources) {
            JsonObject dbConfig = config.getJsonObject(datasource);
            if (dbConfig != null) {
              DataSource ds = DatasourceHelper.initializeDataSource(dbConfig);
              registry.put(datasource, ds);
            }
          }
          initialized = true;
        }
      }
    }
  }

  public DataSource getDefaultDataSource() {
    return registry.get(DEFAULT_DATA_SOURCE);
  }

  public DataSource getDsdbDataSource() {
    return registry.get(DSDB_DATA_SOURCE);
  }

  public DataSource getDataSourceByName(String name) {
    if (name != null) {
      return registry.get(name);
    }
    return null;
  }

  @Override
  public void finalizeComponent() {
    for (String datasource : datasources) {
      DataSource ds = registry.get(datasource);
      if (ds != null) {
        if (ds instanceof HikariDataSource) {
          ((HikariDataSource) ds).close();
        }
      }
    }
  }

  private static final class Holder {

    private static final DataSourceRegistry INSTANCE = new DataSourceRegistry();
  }

}
