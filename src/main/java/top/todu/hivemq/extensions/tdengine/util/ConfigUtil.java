package top.todu.hivemq.extensions.tdengine.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;

/**
 * config util <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/28 23:49 <br>
 * @since 1.0.0
 */
public class ConfigUtil {
  private static final String CONFIG_FILE = "config.yml";

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper(new YAMLFactory());
    objectMapper
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);
  }

  public static TdEngineConfig parseFromFile(String configFilePath) {
    try {
      File file = new File(configFilePath, CONFIG_FILE);
      if (!file.canRead()) {
        throw new RuntimeException("can not read file: " + file.getAbsolutePath());
      }
      return objectMapper.readValue(new File(configFilePath, CONFIG_FILE), TdEngineConfig.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
