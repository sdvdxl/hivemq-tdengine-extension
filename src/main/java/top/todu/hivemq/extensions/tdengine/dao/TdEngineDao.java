package top.todu.hivemq.extensions.tdengine.dao;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public interface TdEngineDao {
  String SQL_CREATE_TABLE =
      "create table if not exists %s.%s(ts timestamp, client_id nchar(1024), topic nchar(1024), qos tinyint, ip nchar(512), payload nchar(1024) );";
  String SQL_CREATE_DB = "create database if not exists %s ;";
  String getPayloadCoder();

  /** init */
  void init();

  /**
   * save data to database
   * @param clientId
   * @param username
   * @param topic
   * @param qos
   * @param ip
   * @param timestamp
   * @param payload
   */
  void save(String clientId, String username, String topic, int qos, String ip,
      long timestamp, byte[] payload);

  void close();
}
