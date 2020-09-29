package top.todu.hivemq.extensions.tdengine.util;

import org.junit.Assert;
import org.junit.Test;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 10:57 <br>
 */
public class ConfigUtilTest {
  @Test
  public void testParseFromFile() {
    TdEngineConfig config = ConfigUtil.parseFromFile("src/main/resources");
    System.out.println(config);
    Assert.assertNotNull(config);
  }
}
