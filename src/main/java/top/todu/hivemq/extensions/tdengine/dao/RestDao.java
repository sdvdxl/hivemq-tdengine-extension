package top.todu.hivemq.extensions.tdengine.dao;

import static com.fasterxml.jackson.databind.util.StdDateFormat.DATE_FORMAT_STR_ISO8601;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.TimeZone;
import org.apache.commons.lang3.time.FastDateFormat;
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
  private final FastDateFormat DATE_FORMAT;
  private final RestConfig config;
  private HttpUtil httpUtil;

  public RestDao(RestConfig config) {
    this.config = config;
    DATE_FORMAT =
        FastDateFormat.getInstance(DATE_FORMAT_STR_ISO8601, TimeZone.getTimeZone("GMT+8"));
  }

  private static String escape(String content) {
    return content.replace("'", "\\'");
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
    httpUtil.post(String.format(JdbcDao.SQL_CREATE_DB, config.getDatabase()));
  }

  private void createTable() {
    httpUtil.post(String.format(JdbcDao.SQL_CREATE_TABLE, config.getDatabase(), config.getTable()));
  }

  @Override
  public void save(
      String clientId, String topic, int qos, String ip, long timestamp, String payload) {
    StringBuilder sqlBuilder =
        new StringBuilder("insert into ")
            .append(config.getDatabase())
            .append(".")
            .append(config.getTable())
            .append(" values ('")
            .append(DATE_FORMAT.format(timestamp))
            .append("', '")
            .append(escape(clientId))
            .append("', '")
            .append(escape(topic))
            .append("', ")
            .append(qos)
            .append(", '")
            .append(ip)
            .append("', '")
            .append(escape(payload))
            .append("');");

    if (log.isDebugEnabled()) {
      log.debug("rest dao save sql:{}", sqlBuilder.toString());
    }

    httpUtil.post(sqlBuilder.toString());
  }

  @Override
  public void close() {
    httpUtil.close();
    log.info("rest closed");
  }
}
