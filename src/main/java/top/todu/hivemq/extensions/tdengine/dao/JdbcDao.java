package top.todu.hivemq.extensions.tdengine.dao;

import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_DRIVERCLASSNAME;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_INITIALSIZE;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_MAXACTIVE;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_MAXWAIT;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_MINEVICTABLEIDLETIMEMILLIS;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_MINIDLE;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_PASSWORD;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_TESTONBORROW;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_TESTONRETURN;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_TESTWHILEIDLE;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_TIMEBETWEENEVICTIONRUNSMILLIS;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_URL;
import static com.alibaba.druid.pool.DruidDataSourceFactory.PROP_USERNAME;
import static top.todu.hivemq.extensions.tdengine.util.SqlUtil.buildInsertSql;

import coder.PayloadCoder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.JdbcConfig;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 14:33 <br>
 */
public class JdbcDao implements TdEngineDao {
  public static final String SQL_CREATE_DB = "create database if not exists %s ;";
  public static final String SQL_CREATE_TABLE =
      "create table if not exists %s.%s(ts timestamp, client_id nchar(1024), topic nchar(1024), qos tinyint, ip nchar(512), payload nchar(1024) );";
  private static final Logger log = LoggerFactory.getLogger(JdbcDao.class);
  private static final String SQL_INSERT =
      "insert into %s.%s(ts, client_id, topic, qos, ip, payload) values(?, ?, ?, ?, ?, ?)";
  private final JdbcConfig config;
  private final PayloadCoder payloadCoder;
  private DruidDataSource dataSource;

  public JdbcDao(JdbcConfig config) {
    this.config = config;
    this.payloadCoder = config.getPayloadCoder();
  }

  @Override
  public String getPayloadCoder() {
    return payloadCoder.name();
  }

  @Override
  public void init() {
    initDatasource();
    createDB();
    createTable();
  }

  public void initDatasource() {
    Properties properties = new Properties();
    properties.put(PROP_DRIVERCLASSNAME, config.getDriver());
    properties.put(PROP_URL, config.getUrl());
    properties.put(PROP_USERNAME, config.getUsername());
    properties.put(PROP_PASSWORD, config.getPassword());
    properties.put(PROP_MAXACTIVE, String.valueOf(config.getPool().getMaxActive()));
    properties.put(PROP_INITIALSIZE, String.valueOf(config.getPool().getInitialSize()));
    properties.put(PROP_MAXWAIT, String.valueOf(config.getPool().getMaxWait()));
    properties.put(PROP_MINIDLE, String.valueOf(config.getPool().getMinIdle()));
    // the interval milliseconds to test connection
    properties.put(PROP_TIMEBETWEENEVICTIONRUNSMILLIS, "3000");
    // the minimum milliseconds to keep idle
    properties.put(PROP_MINEVICTABLEIDLETIMEMILLIS, "60000");
    // validation query
    //    properties.put(PROP_VALIDATIONQUERY, config.getPool().getValidationQuery());
    // test connection while idle
    properties.put(PROP_TESTWHILEIDLE, "false");
    // don't need while testWhileIdle is true
    properties.put(PROP_TESTONBORROW, "false");
    // don't need while testWhileIdle is true
    properties.put(PROP_TESTONRETURN, "false");
    log.info("data source config: {}", properties);
    // create druid datasource
    try {
      dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
      dataSource.init();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void save(
      String clientId, String topic, int qos, String ip, long timestamp, byte[] payload) {
    // todo 这个地方taos驱动 PreparedStatement有个bug，如果设置的字符串含有单引号（'）就会插入失败，也不会报错,
    // 所以这里先用statement拼接方式

    try (Connection conn = dataSource.getConnection()) {

      try (Statement stmt = conn.createStatement()) {
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

  private void createTable() {
    log.info("create table: {}", config.getTable());
    try (Connection conn = dataSource.getConnection()) {
      try (Statement stmt = conn.createStatement()) {
        stmt.execute(String.format(SQL_CREATE_TABLE, config.getDatabase(), config.getTable()));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void createDB() {

    log.info("create database: {}", config.getDatabase());
    try (Connection conn = dataSource.getConnection()) {

      try (Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("use " + config.getDatabase());
        stmt.executeUpdate(String.format(SQL_CREATE_DB, config.getDatabase()));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
