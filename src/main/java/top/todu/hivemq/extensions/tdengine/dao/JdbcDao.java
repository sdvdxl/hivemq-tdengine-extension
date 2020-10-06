package top.todu.hivemq.extensions.tdengine.dao;

import static top.todu.hivemq.extensions.tdengine.util.SqlUtil.buildInsertSql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 14:33 <br>
 */
public class JdbcDao extends AbstractDao {
  protected static final String DRIVER_CLASS_NAME = "com.taosdata.jdbc.TSDBDriver";
  private static final Logger log = LoggerFactory.getLogger(JdbcDao.class);

  protected HikariDataSource dataSource;

  public JdbcDao(TdEngineConfig config) {
    super(config);
  }

  @Override
  public void init() {
    initDatasource();
    createDB();
    createTable();
  }

  public void initDatasource() {

    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(config.getUrl());
    hikariConfig.setUsername(config.getUsername());
    hikariConfig.setPassword(config.getPassword());
    hikariConfig.setMinimumIdle(1);
    hikariConfig.setMaximumPoolSize(config.getMaxConnections());
    hikariConfig.setConnectionTimeout(config.getTimeout());
    hikariConfig.setIdleTimeout(TimeUnit.MINUTES.toMillis(1));
    hikariConfig.setDriverClassName(DRIVER_CLASS_NAME);
    // 报错,不支持空闲连接检测
    //    hikariConfig.setConnectionTestQuery(config.getPool().getValidationQuery());
    dataSource = new HikariDataSource(hikariConfig);
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
            buildInsertSql(
                config.getDatabase(),
                config.getTable().getName(),
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

  public void createTable() {
    log.info("jdbc create table: {}", config.getTable());
    try (Connection conn = dataSource.getConnection()) {
      try (Statement stmt = conn.createStatement()) {
        stmt.execute(String.format(SQL_CREATE_TABLE, config.getDatabase(), config.getTable()));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void createDB() {

    log.info("jdbc create database: {}", config.getDatabase());
    try (Connection conn = dataSource.getConnection()) {

      try (Statement stmt = conn.createStatement()) {
        stmt.executeUpdate(String.format(SQL_CREATE_DB, config.getDatabase()));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
