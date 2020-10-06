package top.todu.hivemq.extensions.tdengine.config;

import coder.PayloadCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * config <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/28 23:58 <br>
 */
public class TdEngineConfig {
  private static final Logger log = LoggerFactory.getLogger(TdEngineConfig.class);
  private static final int DEFAULT_MAX_CONNECTIONS = 4;
  private static final int DEFAULT_TIMEOUT = 3000;
  private Mode mode;
  private ThreadPoolConfig threadPool;
  private String url;
  private String username;
  private String password;
  private TableInfo table;
  private String database;
  private PayloadCoder payloadCoder;
  private int maxConnections;
  private int timeout;

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
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

  public TableInfo getTable() {
    return table;
  }

  public void setTable(TableInfo table) {
    this.table = table;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public PayloadCoder getPayloadCoder() {
    return payloadCoder;
  }

  public void setPayloadCoder(PayloadCoder payloadCoder) {
    this.payloadCoder = payloadCoder;
  }

  public ThreadPoolConfig getThreadPool() {
    return threadPool;
  }

  public void setThreadPool(ThreadPoolConfig threadPool) {
    this.threadPool = threadPool;
  }

  public void init() {

    if (mode == null) {
      throw new RuntimeException("mode type is required , support HTTP or JDBC");
    }

    if (threadPool == null) {
      threadPool = new ThreadPoolConfig();
    }
    if (threadPool.getCore() < 1) {
      threadPool.setCore(ThreadPoolConfig.DEFAULT_CORE);
    }
    if (threadPool.getMax() < threadPool.getCore()) {
      threadPool.setMax(ThreadPoolConfig.DEFAULT_MAX);
    }
    if (threadPool.getQueue() < 1) {
      threadPool.setCore(ThreadPoolConfig.DEFAULT_QUEUE);
    }

    if (maxConnections <= 0) {
      maxConnections = DEFAULT_MAX_CONNECTIONS;
    }

    if (timeout < 1000) {
      timeout = DEFAULT_TIMEOUT;
    }
  }

  public enum Mode {
    /** http */
    HTTP,
    /** JDBC sdk */
    JDBC
  }

  public enum TableMode {
    SINGLE_TABLE,
    SUPER_TABLE
  }

  public enum TableNameUseType {
    USERNAME,
    CLIENT_ID
  }

  public enum TableNameFormat {
    FIXED,
    MD5
  }

  public static class ConnectPool {

    private int size;

    /** maximum wait milliseconds for get connection from pool */
    private int timeout;
  }

  public static class ThreadPoolConfig {
    public static final int DEFAULT_CORE = Runtime.getRuntime().availableProcessors();
    public static final int DEFAULT_MAX = DEFAULT_CORE * 2;
    public static final int DEFAULT_QUEUE = 1000;

    private int core = DEFAULT_CORE;
    private int max = DEFAULT_MAX;
    private int queue = DEFAULT_QUEUE;

    public int getCore() {
      return core;
    }

    public void setCore(int core) {
      this.core = core;
    }

    public int getMax() {
      return max;
    }

    public void setMax(int max) {
      this.max = max;
    }

    public int getQueue() {
      return queue;
    }

    public void setQueue(int queue) {
      this.queue = queue;
    }
  }

  public static class TableInfo {
    private String name;
    private TableMode mode;
    private TableNameUseType use;
    private TableNameFormat format;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public TableMode getMode() {
      return mode;
    }

    public void setMode(TableMode mode) {
      this.mode = mode;
    }

    public TableNameUseType getUse() {
      return use;
    }

    public void setUse(TableNameUseType use) {
      this.use = use;
    }

    public TableNameFormat getFormat() {
      return format;
    }

    public void setFormat(TableNameFormat format) {
      this.format = format;
    }

    @Override
    public String toString() {
      return "TableInfo{"
          + "name='"
          + name
          + '\''
          + ", mode="
          + mode
          + ", use="
          + use
          + ", format="
          + format
          + '}';
    }
  }
}
