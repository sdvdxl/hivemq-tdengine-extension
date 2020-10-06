package top.todu.hivemq.extensions.tdengine.dao;

import coder.PayloadCoder;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;

/**
 * <br>
 *
 * @author sdvdxl <杜龙少> <br>
 * @date 2020/10/6 20:08 <br>
 */
public abstract class AbstractDao implements TdEngineDao {
  protected final TdEngineConfig config;
  protected final PayloadCoder payloadCoder;

  public AbstractDao(TdEngineConfig config) {
    this.config = config;
    this.payloadCoder = config.getPayloadCoder();
  }

  @Override
  public String getPayloadCoder() {
    return payloadCoder.name();
  }
}
