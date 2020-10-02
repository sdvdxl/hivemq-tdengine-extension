package top.todu.hivemq.extensions.tdengine.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.TimeZone;
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

  static {
    TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    TimeZone.setDefault(timeZone);
  }

  private JdbcConfig jdbc;
  private RestConfig rest;
  private ThreadPoolConfig threadPool;

  public static Logger getLog() {
    return log;
  }

  public ThreadPoolConfig getThreadPool() {
    return threadPool;
  }

  public void setThreadPool(ThreadPoolConfig threadPool) {
    this.threadPool = threadPool;
  }

  public JdbcConfig getJdbc() {
    return jdbc;
  }

  public void setJdbc(JdbcConfig jdbc) {
    this.jdbc = jdbc;
  }

  public RestConfig getRest() {
    return rest;
  }

  public void setRest(RestConfig rest) {
    this.rest = rest;
  }

  @Override
  public String toString() {
    return "TdEngineConfig{" + "jdbc=" + jdbc + ", rest=" + rest + '}';
  }

  public void init() {
    if (jdbc == null && rest == null) {
      throw new RuntimeException("jdbc or rest is need at least one config");
    }

    if (jdbc != null && rest != null && !jdbc.isEnable() && !rest.isEnable()) {
      throw new RuntimeException("jdbc or rest is need at least one config enabled");
    }

    if (jdbc != null && jdbc.isEnable()) {
      log.info("jdbc config enabled");
    }

    if (rest != null && rest.isEnable()) {
      log.info("rest config enabled");
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
  }

  @JsonIgnore
  public boolean isJdbcEnable() {
    return this.jdbc != null && jdbc.isEnable();
  }

  @JsonIgnore
  public boolean isRestEnable() {
    return this.rest != null && rest.isEnable();
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
