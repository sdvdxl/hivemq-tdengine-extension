/*
 * Copyright 2019 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package top.todu.hivemq.extensions.tdengine;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationFailedInput;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ClientInitiatedDisconnectInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionLostInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ServerInitiatedDisconnectInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.util.MqttSession;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class TdEngineConnectDisconnectEventListener implements ClientLifecycleEventListener {

  @NotNull
  private static final Logger log =
      LoggerFactory.getLogger(TdEngineConnectDisconnectEventListener.class);

  @Override
  public void onMqttConnectionStart(@NotNull final ConnectionStartInput connectionStartInput) {
    String username = connectionStartInput.getConnectPacket().getUserName().orElse(null);
    String clientId = connectionStartInput.getConnectPacket().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("onMqttConnectionStart,clientId:{}, username:{}", clientId, username);
    }
    MqttSession.set(clientId, username);
  }

  @Override
  public void onAuthenticationSuccessful(
      @NotNull final AuthenticationSuccessfulInput authenticationSuccessfulInput) {}

  @Override
  public void onDisconnect(final @NotNull DisconnectEventInput disconnectEventInput) {
    String clientId = disconnectEventInput.getClientInformation().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("onDisconnect,clientId:{}", clientId);
    }
    MqttSession.clean(clientId);
  }

  @Override
  public void onAuthenticationFailedDisconnect(
      @NotNull final AuthenticationFailedInput authenticationFailedInput) {
    String clientId = authenticationFailedInput.getClientInformation().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("onAuthenticationFailedDisconnect,clientId:{}", clientId);
    }
    MqttSession.clean(clientId);
  }

  @Override
  public void onConnectionLost(@NotNull final ConnectionLostInput connectionLostInput) {
    String clientId = connectionLostInput.getClientInformation().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("onConnectionLost,clientId:{}", clientId);
    }
    MqttSession.clean(clientId);
  }

  @Override
  public void onClientInitiatedDisconnect(
      @NotNull final ClientInitiatedDisconnectInput clientInitiatedDisconnectInput) {
    String clientId = clientInitiatedDisconnectInput.getClientInformation().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("onClientInitiatedDisconnect,clientId:{}", clientId);
    }
    MqttSession.clean(clientId);
  }

  @Override
  public void onServerInitiatedDisconnect(
      @NotNull final ServerInitiatedDisconnectInput serverInitiatedDisconnectInput) {
    String clientId = serverInitiatedDisconnectInput.getClientInformation().getClientId();
    if (log.isDebugEnabled()) {
      log.debug("onServerInitiatedDisconnect,clientId:{}", clientId);
    }
    MqttSession.clean(clientId);
  }
}
