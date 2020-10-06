package top.todu.hivemq.extensions.tdengine.service;

import com.hivemq.extension.sdk.api.packets.general.Qos;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;
import top.todu.hivemq.extensions.tdengine.dao.HttpDao;
import top.todu.hivemq.extensions.tdengine.dao.JdbcDao;
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
  private final ThreadPoolExecutor threadPoolExecutor;
  private final TdEngineDao tdEngineDao;

  public MqttPayloadService(TdEngineConfig config) {
    threadPoolExecutor =
        new ThreadPoolUtil.Builder()
            .setCore(config.getThreadPool().getCore())
            .setMax(config.getThreadPool().getMax())
            .setUncaughtExceptionHandler(
                new UncaughtExceptionHandler() {
                  @Override
                  public void uncaughtException(Thread t, Throwable e) {
                    if (e instanceof RejectedExecutionException) {
                      log.warn(
                          "save mqtt payload queue full, reject, queue info:{}",
                          threadPoolExecutor);
                    } else {
                      log.error(e.getMessage(), e);
                    }
                  }
                })
            .setPrefix("hivemq-tdengine-")
            .setKeepAliveTime(TimeUnit.MINUTES.toMillis(1))
            .setQueueSize(config.getThreadPool().getQueue())
            .build();

    switch (config.getMode()) {
      case HTTP:
        tdEngineDao = new JdbcDao(config);
        break;
      case JDBC:
        tdEngineDao = new HttpDao(config);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + config.getMode());
    }
  }

  public void close() {
    try {
      tdEngineDao.close();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    ThreadPoolUtil.close();
  }

  public void save(
      String clientId,
      String topic,
      Qos qos,
      InetAddress inetAddress,
      long timestamp,
      byte[] payload) {
    threadPoolExecutor.execute(() -> doSave(clientId, topic, qos, inetAddress, timestamp, payload));
  }

  private void doSave(
      String clientId,
      String topic,
      Qos qos,
      InetAddress inetAddress,
      long timestamp,
      byte[] payload) {
    tdEngineDao.save(
        clientId,
        topic,
        qos.getQosNumber(),
        inetAddress == null ? "" : inetAddress.getHostAddress(),
        timestamp,
        payload);
  }
}
