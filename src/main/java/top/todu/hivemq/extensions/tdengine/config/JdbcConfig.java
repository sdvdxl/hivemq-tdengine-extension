package top.todu.hivemq.extensions.tdengine.config;

import coder.PayloadCoder;

/**
 * jdbc config <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 00:05 <br>
 * @since 1.0.0
 */
public class JdbcConfig {

  private boolean enable;
  private Pool pool;
  /**
   * driver class full name
   *
   * <p>com.taosdata.jdbc.TSDBDriver
   */
  private String driver;

  private String url;
  private String username;
  private String password;
  private String table;
  private String database;
  private PayloadCoder payloadCoder;

  public PayloadCoder getPayloadCoder() {
    return payloadCoder;
  }

  public void setPayloadCoder(PayloadCoder payloadCoder) {
    this.payloadCoder = payloadCoder;
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public Pool getPool() {
    return pool;
  }

  public void setPool(Pool pool) {
    this.pool = pool;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String toString() {
    return "JdbcConfig{"
        + "enable="
        + enable
        + ", driver='"
        + driver
        + '\''
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public static class Pool {

    private int maxActive;
    /** initial number of connection */
    private int initialSize;
    /** maximum wait milliseconds for get connection from pool */
    private int maxWait;
    /** minimum number of connection in the pool */
    private int minIdle;

    private String validationQuery;

    public int getMaxActive() {
      return maxActive;
    }

    public void setMaxActive(int maxActive) {
      this.maxActive = maxActive;
    }

    public int getInitialSize() {
      return initialSize;
    }

    public void setInitialSize(int initialSize) {
      this.initialSize = initialSize;
    }

    public int getMaxWait() {
      return maxWait;
    }

    public void setMaxWait(int maxWait) {
      this.maxWait = maxWait;
    }

    public int getMinIdle() {
      return minIdle;
    }

    public void setMinIdle(int minIdle) {
      this.minIdle = minIdle;
    }

    public String getValidationQuery() {
      return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
      this.validationQuery = validationQuery;
    }

    @Override
    public String toString() {
      return "Pool{"
          + "maxActive="
          + maxActive
          + ", initialSize="
          + initialSize
          + ", maxWait="
          + maxWait
          + ", minIdle="
          + minIdle
          + ", validationQuery='"
          + validationQuery
          + '\''
          + '}';
    }
  }
}
