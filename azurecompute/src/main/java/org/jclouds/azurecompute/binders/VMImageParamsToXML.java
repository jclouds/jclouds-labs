/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azurecompute.binders;

import com.google.common.base.CaseFormat;
import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.azurecompute.domain.RoleSize;
import org.jclouds.azurecompute.domain.VMImageParams;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import java.net.URI;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.Throwables.propagate;

public final class VMImageParamsToXML implements Binder {

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object input) {
        VMImageParams params = VMImageParams.class.cast(input);

        try {
            XMLBuilder builder = XMLBuilder.create("VMImage", "http://schemas.microsoft.com/windowsazure");
            add(builder, "Name", params.name());
            add(builder, "Label", params.label());
            add(builder, "Description", params.description());
            //OSConfig
            VMImageParams.OSDiskConfigurationParams osDiskConfig = params.osDiskConfiguration();
            if (osDiskConfig != null) {
                String cache = CaseFormat.UPPER_UNDERSCORE.to(UPPER_CAMEL, osDiskConfig.hostCaching().toString());
                XMLBuilder osConfigBuilder = builder.e("OSDiskConfiguration");
                osConfigBuilder
                        .e("HostCaching").t(cache).up()
                        .e("OSState").t(osDiskConfig.osState().toString()).up()
                        .e("OS").t(osDiskConfig.os().toString()).up()
                        .e("MediaLink").t(osDiskConfig.mediaLink().toASCIIString()).up()
                        .up(); //OSDiskConfiguration
            }
            builder.up();
            builder.e("DataDiskConfigurations").up();
            add(builder, "Language", params.language());
            add(builder, "ImageFamily", params.imageFamily());

            RoleSize.Type vmSize = params.recommendedVMSize();
            if (vmSize != null) {
                String vmSizeText = params.recommendedVMSize().getText();
                builder.e("RecommendedVMSize").t(vmSizeText).up();
            }

            add(builder, "Eula", params.eula());
            add(builder, "IconUri", params.iconUri());
            add(builder, "SmallIconUri", params.smallIconUri());
            add(builder, "PrivacyUri", params.privacyUri());

            if (params.showGui() != null) {
                String showGuiText = params.showGui().toString();
                builder.e("ShowGui").t(showGuiText).up();
            }
            builder.up();

            return (R) request.toBuilder().payload(builder.asString()).build();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    private XMLBuilder add(XMLBuilder xmlBuilder, String entity, String text) {
        if (text != null) {
            return xmlBuilder.e(entity).t(text).up();
        } else {
            return xmlBuilder.e(entity).up();
        }
    }

    private XMLBuilder add(XMLBuilder xmlBuilder, String entity, URI text) {
        if (text != null) {
            return xmlBuilder.e(entity).t(text.toASCIIString()).up();
        } else {
            return xmlBuilder.e(entity).up();
        }
    }
}
