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
import com.hivemq.extension.sdk.api.interceptor.disconnect.DisconnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectInboundOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.util.MqttSession;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public class TdEngineDisconnectInboundInterceptor implements DisconnectInboundInterceptor {

  private static final Logger log =
      LoggerFactory.getLogger(TdEngineDisconnectInboundInterceptor.class);

  @Override
  public void onInboundDisconnect(
      @NotNull DisconnectInboundInput disconnectInboundInput,
      @NotNull DisconnectInboundOutput disconnectInboundOutput) {
    String clientId = disconnectInboundInput.getClientInformation().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("disconnect,clientId:{}", clientId);
    }
    MqttSession.clean(clientId);
  }
}
