package top.todu.hivemq.extensions.tdengine.dao;

import static top.todu.hivemq.extensions.tdengine.util.SqlUtil.buildInsertSql;

import coder.PayloadCoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.RestConfig;
import top.todu.hivemq.extensions.tdengine.util.HttpUtil;

/**
 * rest api dao <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 15:27 <br>
 */
public class RestDao implements TdEngineDao {
  private static final Logger log = LoggerFactory.getLogger(RestDao.class);
  private final RestConfig config;
  private final PayloadCoder payloadCoder;
  private HttpUtil httpUtil;

  public RestDao(RestConfig config) {
    this.config = config;
    this.payloadCoder = config.getPayloadCoder();
  }

  @Override
  public String getPayloadCoder() {
    return payloadCoder.name();
  }

  @Override
  public void init() {
    try {
      httpUtil =
          HttpUtil.newInstance(
              config.getUrl(),
              Base64.getEncoder()
                  .encodeToString(
                      (config.getUsername() + ":" + config.getPassword())
                          .getBytes(StandardCharsets.UTF_8)));
      createDB();
      createTable();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void createDB() {
    boolean success = httpUtil.post(String.format(JdbcDao.SQL_CREATE_DB, config.getDatabase()));
    if (success) {
      log.info("rest created db: {}", config.getDatabase());
    }
  }

  private void createTable() {
    boolean success =
        httpUtil.post(
            String.format(JdbcDao.SQL_CREATE_TABLE, config.getDatabase(), config.getTable()));
    if (success) {
      log.info("rest created table: {}.{}", config.getDatabase(), config.getTable());
    }
  }

  @Override
  public void save(
      String clientId, String topic, int qos, String ip, long timestamp, byte[] payload) {
    String sql =
        buildInsertSql(
            config.getDatabase(),
            config.getTable(),
            clientId,
            topic,
            qos,
            ip,
            timestamp,
            payload,
            config.getPayloadCoder());
    if (log.isDebugEnabled()) {
      log.debug("rest build sql:{}", sql);
    }

    httpUtil.post(sql);
  }

  @NotNull
  @Override
  public void close() {
    httpUtil.close();
    log.info("rest closed");
  }
}
