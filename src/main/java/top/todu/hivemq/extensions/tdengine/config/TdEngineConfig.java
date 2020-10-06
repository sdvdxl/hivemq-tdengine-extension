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
  private String table;
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

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
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

    if (mode==null){
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
}
