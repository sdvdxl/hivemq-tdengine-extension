package top.todu.hivemq.extensions.tdengine.util;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/10/6 21:36 <br>
 */
public class MqttSession {

  /** key: clientId value: username */
  private static final ConcurrentHashMap<String, String> CLIENT_MAP = new ConcurrentHashMap<>();

  public static void set(String clientId, String username) {
    CLIENT_MAP.put(clientId, StringUtils.defaultString(username, "default_user"));
  }

  public static String get(String clientId) {
    return CLIENT_MAP.get(clientId);
  }

  public static void clean(String clientId) {
    CLIENT_MAP.remove(clientId);
  }

  public static void cleanAll() {
    CLIENT_MAP.clear();
  }
}
