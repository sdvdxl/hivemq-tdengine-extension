package top.todu.hivemq.extensions.tdengine.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * JsonUtil <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/30 3:40 下午 <br>
 */
public class JsonUtil {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper(new JsonFactory());
    objectMapper
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);
  }

  public static <T> T fromBytes(byte[] payload, Class<T> tClass) {
    try {
      return objectMapper.readValue(payload, tClass);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
