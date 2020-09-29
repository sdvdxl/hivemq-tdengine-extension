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

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.parameter.ExtensionInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.InitializerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.todu.hivemq.extensions.tdengine.config.TdEngineConfig;
import top.todu.hivemq.extensions.tdengine.service.MqttPayloadService;
import top.todu.hivemq.extensions.tdengine.util.ConfigUtil;

/**
 * @author sdvdxl
 * @since 1.0.0
 */
public class TdEngineMain implements ExtensionMain {

  private static final @NotNull Logger log = LoggerFactory.getLogger(TdEngineMain.class);
  private volatile TdEngineConfig config;
  private volatile MqttPayloadService mqttPayloadService;

  @Override
  public void extensionStart(
      final @NotNull ExtensionStartInput extensionStartInput,
      final @NotNull ExtensionStartOutput extensionStartOutput) {
    try {
      extensionStartInput.getServerInformation();
    } catch (final NoSuchMethodError e) {
      // only a version that is not supported will throw this exception
      extensionStartOutput.preventExtensionStartup("The HiveMQ version is not supported");
      return;
    }

    init(extensionStartInput);

    final ExtensionInformation extensionInformation = extensionStartInput.getExtensionInformation();
    log.info("Started " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
  }

  @Override
  public void extensionStop(
      final @NotNull ExtensionStopInput extensionStopInput,
      final @NotNull ExtensionStopOutput extensionStopOutput) {

    final ExtensionInformation extensionInformation = extensionStopInput.getExtensionInformation();
    // clear resources
    mqttPayloadService.close();
    log.info("Stopped " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
  }

  private void init(@NotNull ExtensionStartInput extensionStartInput) {
    config =
        ConfigUtil.parseFromFile(
            extensionStartInput
                .getExtensionInformation()
                .getExtensionHomeFolder()
                .getAbsolutePath());
    config.init();

    final InitializerRegistry initializerRegistry = Services.initializerRegistry();
    mqttPayloadService = new MqttPayloadService(config);

    final TdEngineMessageInBoundInterceptor tdEngineMessageInBoundInterceptor =
        new TdEngineMessageInBoundInterceptor(mqttPayloadService);

    initializerRegistry.setClientInitializer(
        (initializerInput, clientContext) ->
            clientContext.addPublishInboundInterceptor(tdEngineMessageInBoundInterceptor));
  }
}
