package top.todu.hivemq.extensions.tdengine.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.RestConfig;

/**
 * rest api dao <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/9/29 15:27 <br>
 */
public class RestDao implements TdEngineDao {
  private static final Logger log = LoggerFactory.getLogger(RestDao.class);

  private final RestConfig config;

  public RestDao(RestConfig config) {
    this.config = config;
  }

  @Override
  public void init() {}

  @Override
  public void save(String clientId, String topic, int qos, String ip, long timestamp,
      String payload) {
    log.info("rest dao: save");
  }

  @Override
  public void close() {
    log.info("rest closed");
  }
}
