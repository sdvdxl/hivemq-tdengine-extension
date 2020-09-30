package top.todu.hivemq.extensions.tdengine.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author du */
public class ThreadPoolUtil {
  private static final Logger log = LoggerFactory.getLogger(ThreadPoolUtil.class);
  public static final UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER =
      (t, e) ->
          log.error(
              "[thread:" + t.getName() + "] priority:" + t.getPriority() + ", " + e.getMessage(),
              e);

  private static final int CORES = Runtime.getRuntime().availableProcessors();
  private static final ConcurrentHashMap<String, ThreadPoolExecutor>
      THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

  static {
    final StringBuilder message = new StringBuilder();
    // 设置每分钟打印一次线程池状态
    ScheduledThreadPoolExecutor timerExecutor =
        (ScheduledThreadPoolExecutor)
            new Builder()
                .setCore(1)
                .setPrefix("thread-pool-util-monitor")
                .setScheduled(true)
                .build();
    timerExecutor.scheduleAtFixedRate(
        () -> {
          message.delete(0, message.length());
          THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.forEach(
              (k, v) -> message.append(k).append(", ").append(v.toString()).append("\n"));
          log.info(
              "\nall thread pools(count: {}) status:\n{}",
              THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.size(),
              message.toString());
        },
        0,
        1,
        TimeUnit.MINUTES);
  }

  static {
    log.info("enable all thread exception log");
    Thread.setDefaultUncaughtExceptionHandler(DEFAULT_UNCAUGHT_EXCEPTION_HANDLER);
  }

  public static ThreadPoolExecutor get(String name) {
    return THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.get(name);
  }

  /**
   * 是否开启记录所有的线程异常信息，建议开启，默认开启
   *
   * @param enable true 开启，false 关闭
   */
  public static void logAllThreadException(boolean enable) {
    Thread.setDefaultUncaughtExceptionHandler(enable ? DEFAULT_UNCAUGHT_EXCEPTION_HANDLER : null);
  }

  public static void close() {
    try {
      THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP
          .values()
          .forEach(
              pool -> {
                pool.shutdownNow();
                log.info("thread pool {} closed", pool);
              });
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static class Builder {

    /** 核心数量,默认1 */
    private int core = 1;
    /** 最大数量，默认cpu最大数量2倍 */
    private int max = core * 2;

    private int queueSize = -1;
    /** 存活时间，默认0,永久 */
    private long keepAliveTime = 0;
    /**
     * 存活时间 默认毫秒数，永久
     *
     * @see #keepAliveTime
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    /** 拒绝策略，默认 AbortPolicy */
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
    /** 线城池名字前缀，并且唯一标识线城池 */
    private String format;

    private UncaughtExceptionHandler uncaughtExceptionHandler = DEFAULT_UNCAUGHT_EXCEPTION_HANDLER;
    private boolean scheduled;

    public Builder setCore(int core) {
      this.core = core;
      return this;
    }

    public Builder setMax(int max) {
      this.max = max;
      return this;
    }

    public Builder setQueueSize(int queueSize) {
      this.queueSize = queueSize;
      return this;
    }

    public long getKeepAliveTime() {
      return keepAliveTime;
    }

    public Builder setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
      return this;
    }

    public Builder setScheduled(boolean scheduled) {
      this.scheduled = scheduled;
      return this;
    }

    public Builder setPrefix(String prefix) {
      this.format = prefix + "-%d";
      return this;
    }

    /**
     * 如果名字一样，则返回已经存在的
     *
     * @return ThreadPoolExecutor
     */
    public synchronized ThreadPoolExecutor build() {

      String poolName = StringUtils.defaultString(format, "sm-default-pool-thread-%s");
      return THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.computeIfAbsent(
          poolName,
          name -> {
            ThreadFactoryBuilder threadFactoryBuilder =
                new ThreadFactoryBuilder().setNameFormat(poolName);
            if (uncaughtExceptionHandler != null) {
              threadFactoryBuilder.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }

            if (core < 0) {
              log.warn("thread pool {} core must be gte 0, will set to cores: {}", poolName, CORES);
              core = CORES;
            }

            if (max < core) {
              log.warn(
                  "thread pool {} max must be gte core, core: {}, max: {}, will set max eq core",
                  poolName,
                  core,
                  max);
            }

            if (scheduled) {
              return new ScheduledThreadPoolExecutor(core, threadFactoryBuilder.build(), handler);
            }

            return new ThreadPoolExecutor(
                core,
                max,
                keepAliveTime,
                timeUnit,
                new LinkedBlockingQueue<>(queueSize == -1 ? max * 2 : queueSize),
                threadFactoryBuilder.build(),
                handler);
          });
    }
  }
}
