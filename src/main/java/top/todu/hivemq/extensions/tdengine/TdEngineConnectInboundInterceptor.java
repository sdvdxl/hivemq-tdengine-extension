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
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundOutput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.util.MqttSession;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public class TdEngineConnectInboundInterceptor implements ConnectInboundInterceptor {

  private static final Logger log =
      LoggerFactory.getLogger(TdEngineConnectInboundInterceptor.class);

  @Override
  public void onConnect(
      @NotNull ConnectInboundInput connectInboundInput,
      @NotNull ConnectInboundOutput connectInboundOutput) {
    ConnectPacket connectPacket = connectInboundInput.getConnectPacket();
    String username = connectPacket.getUserName().orElse("");
    String clientId = connectPacket.getClientId();
    MqttSession.set(clientId, username);
  }
}
