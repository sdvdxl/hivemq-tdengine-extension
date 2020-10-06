package top.todu.hivemq.extensions.tdengine.dao;

import static top.todu.hivemq.extensions.tdengine.util.SqlUtil.buildSuperTableInsertSql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 14:33 <br>
 */
public class JdbcSuperTableDao extends JdbcDao {
  public static final String SQL_CREATE_TABLE =
      "create table if not exists %s.%s(ts timestamp, client_id nchar(1024), topic nchar(1024), qos tinyint, ip nchar(512), payload nchar(1024) );";
  public static final String SQL_CREATE_SUPER_TABLE =
      "create table if not exists %s.%s(ts timestamp, payload nchar(1024) ) tags (client_id nchar(1024), topic nchar(1024), qos tinyint, ip nchar(512));";
  private static final String SQL_CREATE_DB = "create database if not exists %s ;";
  private static final String DRIVER_CLASS_NAME = "com.taosdata.jdbc.TSDBDriver";
  private static final Logger log = LoggerFactory.getLogger(JdbcSuperTableDao.class);
  private static final String SQL_INSERT =
      "insert into %s.%s(ts, client_id, topic, qos, ip, payload) values(?, ?, ?, ?, ?, ?)";

  public JdbcSuperTableDao(TdEngineConfig config) {
    super(config);
  }

  @Override
  public String getPayloadCoder() {
    return payloadCoder.name();
  }

  @Override
  public void save(
      String clientId, String username, String topic, int qos, String ip, long timestamp,
      byte[] payload) {
    // todo 这个地方taos驱动 PreparedStatement有个bug，如果设置的字符串含有单引号（'）就会插入失败，也不会报错,
    // 所以这里先用statement拼接方式

    try (Connection conn = dataSource.getConnection()) {

      try (Statement stmt = conn.createStatement()) {
        String sql =
            buildSuperTableInsertSql(
                config.getDatabase(),
                config.getTable().getName(),
                config.getTable().getFormat(),
                config.getTable().getUse(),
                clientId,
                topic,
                qos,
                ip,
                timestamp,
                payload,
                payloadCoder);
        int count = stmt.executeUpdate(sql);

        if (log.isDebugEnabled()) {
          log.debug("jdbc build sql:{}", sql);
        }
        if (count == 0) {
          log.error("use jdbc insert to tdengine failed, data:{}", payloadCoder.encode(payload));
          return;
        }
        if (log.isDebugEnabled()) {
          log.debug("jdbc save to tdengine, count: {}", count);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    dataSource.close();
    log.info("druid datasource closed");
  }

  @Override
  public void createTable() {
    log.info("jdbc create super table: {}", config.getTable());
    try (Connection conn = dataSource.getConnection()) {
      try (Statement stmt = conn.createStatement()) {
        stmt.execute(
            String.format(SQL_CREATE_SUPER_TABLE, config.getDatabase(), config.getTable()));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
