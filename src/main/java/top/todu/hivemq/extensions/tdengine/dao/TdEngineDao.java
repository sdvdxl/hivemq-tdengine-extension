package top.todu.hivemq.extensions.tdengine.dao;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public interface TdEngineDao {

  /** init */
  void init();

  /**
   * save data to database
   * @param clientId
   * @param topic
   * @param qos
   * @param ip
   * @param timestamp
   * @param payload
   */
  void save(String clientId, String topic, int qos, String ip, long timestamp, String payload);

  void close();
}
