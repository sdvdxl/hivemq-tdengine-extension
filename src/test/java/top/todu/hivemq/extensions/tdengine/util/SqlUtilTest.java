package top.todu.hivemq.extensions.tdengine.util;

import coder.PayloadCoder;
import org.junit.Test;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/10/6 21:04 <br>
 */
public class SqlUtilTest {
  @Test
  public void testBuildSuperTableInsertSql() {
    System.out.println(
        SqlUtil.buildSuperTableInsertSql(
            "db",
            "st",
            "t",
            "client_id",
            "topic",
            1,
            "1.1.1.1",
            System.currentTimeMillis(),
            "payload".getBytes(),
            PayloadCoder.RAW));
  }
}
