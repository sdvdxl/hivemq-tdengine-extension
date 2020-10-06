/*
 * Copyright 2018-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.todu.hivemq.extensions.tdengine;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extension.sdk.api.packets.publish.ModifiablePublishPacket;
import java.nio.ByteBuffer;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.service.MqttPayloadService;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public class TdEngineMessageInBoundInterceptor implements PublishInboundInterceptor {

  private static final Logger log =
      LoggerFactory.getLogger(TdEngineMessageInBoundInterceptor.class);

  private final MqttPayloadService mqttPayloadService;

  public TdEngineMessageInBoundInterceptor(MqttPayloadService mqttPayloadService) {

    this.mqttPayloadService = mqttPayloadService;
  }

  @Override
  public void onInboundPublish(
      final @NotNull PublishInboundInput publishInboundInput,
      final @NotNull PublishInboundOutput publishInboundOutput) {
    final ModifiablePublishPacket publishPacket = publishInboundOutput.getPublishPacket();
    try {
      doSave(publishInboundInput, publishPacket);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void doSave(
      PublishInboundInput publishInboundInput, ModifiablePublishPacket publishPacket) {
    Optional<ByteBuffer> optionalByteBuffer = publishInboundInput.getPublishPacket().getPayload();
    if (optionalByteBuffer.isPresent()) {
      ByteBuffer payload = optionalByteBuffer.get();
      byte[] buf = new byte[payload.limit()];
      payload.get(buf);
      mqttPayloadService.save(
          publishInboundInput.getClientInformation().getClientId(),
          null,
          publishPacket.getTopic(),
          publishPacket.getQos(),
          publishInboundInput.getConnectionInformation().getInetAddress().orElseGet(null),
          publishPacket.getTimestamp(),
          buf);
    }
  }
}
