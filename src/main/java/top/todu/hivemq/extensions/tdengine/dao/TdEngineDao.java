package top.todu.hivemq.extensions.tdengine.dao;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public interface TdEngineDao {
  String SQL_CREATE_DB = "create database if not exists %s ;";

  String getPayloadCoder();

  /** init */
  void init();

  /**
   * save data to database
   *
   * @param clientId
   * @param username
   * @param topic
   * @param qos
   * @param ip
   * @param timestamp
   * @param payload
   */
  void save(
      String clientId,
      String username,
      String topic,
      int qos,
      String ip,
      long timestamp,
      byte[] payload);

  void close();
}
