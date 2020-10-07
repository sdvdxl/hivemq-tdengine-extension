package top.todu.hivemq.extensions.tdengine.util;

import coder.PayloadCoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig.TableNameFormat;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig.TableNameUseType;

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
        SqlUtil.buildInsertSql(
            "db",
            "st",
            "t",
            TableNameFormat.FIXED,
            TableNameUseType.CLIENT_ID,
            "client_id",
            "topic",
            1,
            "1.1.1.1",
            System.currentTimeMillis(),
            "payload".getBytes(),
            PayloadCoder.RAW));
    System.out.println(
        SqlUtil.buildInsertSql(
            "db",
            "st",
            "t",
            TableNameFormat.MD5,
            TableNameUseType.CLIENT_ID,
            "client_id",
            "topic",
            1,
            "1.1.1.1",
            System.currentTimeMillis(),
            "payload".getBytes(),
            PayloadCoder.RAW));
  }

  @Test
  public void testMd5() {
    Assert.assertEquals(
        "e10adc3949ba59abbe56e057f20f883e",
        DigestUtils.md5Hex("123456".getBytes(StandardCharsets.UTF_8)));
  }
}
