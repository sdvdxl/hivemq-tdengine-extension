package top.todu.hivemq.extensions.tdengine.util;

import static com.fasterxml.jackson.databind.util.StdDateFormat.DATE_FORMAT_STR_ISO8601;

import coder.PayloadCoder;
import java.util.TimeZone;
import org.apache.commons.lang3.time.FastDateFormat;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig.TableNameFormat;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig.TableNameUseType;

/**
 * sql util <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/30 20:43 <br>
 */
public class SqlUtil {
  private static final FastDateFormat DATE_FORMAT =
      FastDateFormat.getInstance(DATE_FORMAT_STR_ISO8601, TimeZone.getTimeZone("GMT+8"));
  public static String escape(String content) {
    return content.replace("'", "\\'");

  }

  public static String buildInsertSql(
      String database,
      String table,
      String clientId,
      String topic,
      int qos,
      String ip,
      long timestamp,
      byte[] payload,
      PayloadCoder payloadCoder) {
    String encodedPayload = payloadCoder.encode(payload);
    StringBuilder sqlBuilder =
        new StringBuilder("insert into ")
            .append(database)
            .append(".")
            .append(table)
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
            // raw need escape char "'", base64 not need
            .append(payloadCoder == PayloadCoder.RAW ? escape(encodedPayload) : encodedPayload)
            .append("');");
    return sqlBuilder.toString();
  }

  public static String buildSuperTableInsertSql(
      String database,
      String superTable,
      TableNameFormat tableNameFormat,
      TableNameUseType tableNameUseType,
      String clientId,
      String topic,
      int qos,
      String ip,
      long timestamp,
      byte[] payload,
      PayloadCoder payloadCoder) {

    String tableName = tableNameFormat.format(tableNameUseType.get(clientId,user));
    String encodedPayload = payloadCoder.encode(payload);
    StringBuilder sqlBuilder =
        new StringBuilder("insert into ")
            .append(database)
            .append(".")
            .append(table)
            .append(" using ").append(superTable).append(" tags ('")
            .append(escape(clientId))
            .append("', '")
            .append(escape(topic))
            .append("', ")
            .append(qos)
            .append(", '")
            .append(ip)
            .append("') ").append("values ('")
            .append(DATE_FORMAT.format(timestamp))
            .append("', '")
            .append(payloadCoder == PayloadCoder.RAW ? escape(encodedPayload) : encodedPayload)
            .append("');");
    return sqlBuilder.toString();
  }
}
