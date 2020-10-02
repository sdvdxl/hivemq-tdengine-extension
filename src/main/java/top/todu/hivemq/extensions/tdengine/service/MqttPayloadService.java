package top.todu.hivemq.extensions.tdengine.service;

import com.hivemq.extension.sdk.api.packets.general.Qos;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.TdEngine;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;
import top.todu.hivemq.extensions.tdengine.dao.JdbcDao;
import top.todu.hivemq.extensions.tdengine.dao.RestDao;
import top.todu.hivemq.extensions.tdengine.dao.TdEngineDao;
import top.todu.hivemq.extensions.tdengine.util.ThreadPoolUtil;

/**
 * mqtt pay load service <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 14:48 <br>
 */
public class MqttPayloadService {
  private static final Logger log = LoggerFactory.getLogger(MqttPayloadService.class);
  private static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolUtil.Builder()
          .setCore(Runtime.getRuntime().availableProcessors())
          .setMax(Runtime.getRuntime().availableProcessors() * 8)
          .setUncaughtExceptionHandler(
              new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                  if (e instanceof RejectedExecutionException) {
                    log.warn("save mqtt payload queue full, reject, queue info:{}", EXECUTOR);
                  } else {
                    log.error(e.getMessage(), e);
                  }
                }
              })
          .setPrefix("hivemq-tdengine-")
          .setKeepAliveTime(TimeUnit.MINUTES.toMillis(1))
          .setQueueSize(1000)
          .build();
  private final TdEngineConfig config;
  private final CopyOnWriteArrayList<TdEngineDao> daoList = new CopyOnWriteArrayList<>();

  public MqttPayloadService(TdEngineConfig config) {
    this.config = config;
    if (config.isJdbcEnable()) {
      try {
        register(new JdbcDao(this.config.getJdbc()));
      } catch (Throwable e) {
        log.error(e.getMessage(), e);
      }
    }

    if (config.isRestEnable()) {
      try {
        register(new RestDao(this.config.getRest()));
      } catch (Throwable e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * register dao
   *
   * @param dao
   */
  public synchronized void register(TdEngineDao dao) {
    if (daoList.stream().anyMatch(d -> d.getClass() == dao.getClass())) {
      log.warn("dao of class {} already register", dao.getClass());
      return;
    }

    dao.init();
    daoList.add(dao);
    log.info(
        "dao of class {} register success, use coder: {}", dao.getClass(), dao.getPayloadCoder());
  }

  /**
   * unregister dao
   *
   * @param clazz
   */
  public synchronized void unregister(Class<? extends TdEngine> clazz) {
    boolean success = daoList.removeIf(d -> d.getClass() == clazz);
    if (success) {
      log.info("dao of class {} unregister success", clazz);
    } else {
      log.warn("dao of class {} not register", clazz);
    }
  }

  public void close() {
    try {
      daoList.parallelStream().forEach(TdEngineDao::close);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    daoList.clear();
    ThreadPoolUtil.close();
  }

  public void save(
      String clientId,
      String topic,
      Qos qos,
      InetAddress inetAddress,
      long timestamp,
      byte[] payload) {
    EXECUTOR.execute(
        () ->
            daoList.parallelStream()
                .forEach(
                    dao -> doSave(clientId, topic, qos, inetAddress, timestamp, payload, dao)));
  }

  private void doSave(
      String clientId,
      String topic,
      Qos qos,
      InetAddress inetAddress,
      long timestamp,
      byte[] payload,
      TdEngineDao dao) {
    dao.save(
        clientId,
        topic,
        qos.getQosNumber(),
        inetAddress == null ? "" : inetAddress.getHostAddress(),
        timestamp,
        payload);
  }
}
