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
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.service.MqttPayloadService;
import top.todu.hivemq.extensions.tdengine.util.ThreadPoolUtil;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public class TdEngineMessageInBoundInterceptor implements PublishInboundInterceptor {

  private static final Logger log =
      LoggerFactory.getLogger(TdEngineMessageInBoundInterceptor.class);
  private static final ThreadPoolExecutor EXECUTOR =
      new ThreadPoolUtil.Builder()
          .setCore(Runtime.getRuntime().availableProcessors())
          .setMax(Runtime.getRuntime().availableProcessors() * 2)
          .setUncaughtExceptionHandler(
              new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                  if (e instanceof RejectedExecutionException) {
                    log.warn("save mqtt payload queue full, reject, queue info:{}", EXECUTOR);
                  } else {
                    log.error(e.getMessage(), e);
                  }
                }
              })
          .setPrefix("hivemq-tdengine-")
          .setKeepAliveTime(TimeUnit.MINUTES.toMillis(1))
          .setQueueSize(1000)
          .build();
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
    EXECUTOR.execute(
        () -> {
          publishPacket
              .getPayload()
              .ifPresent(
                  b -> {
                    byte[] buf = new byte[b.limit()];
                    b.get(buf);
                    mqttPayloadService.save(
                        publishInboundInput.getClientInformation().getClientId(),
                        publishPacket.getTopic(),
                        publishPacket.getQos(),
                        publishInboundInput
                            .getConnectionInformation()
                            .getInetAddress()
                            .orElseGet(null),
                        publishPacket.getTimestamp(),
                        buf);
                  });
        });
  }
}
