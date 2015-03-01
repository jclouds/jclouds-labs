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

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.azurecompute.domain.CaptureVMImageParams;
import org.jclouds.azurecompute.domain.RoleSize;

import static org.jclouds.azurecompute.domain.VMImage.OSDiskConfiguration.OSState.GENERALIZED;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import static com.google.common.base.Throwables.propagate;

public final class CaptureVMImageParamsToXML implements Binder {

    @Override
    public <R extends HttpRequest> R bindToRequest(R request, Object input) {
        CaptureVMImageParams params = CaptureVMImageParams.class.cast(input);

        try {
            XMLBuilder builder = XMLBuilder.create("CaptureRoleAsVMImageOperation", "http://schemas.microsoft.com/windowsazure")
                    .namespace("i", "http://www.w3.org/2001/XMLSchema-instance")
                    .e("OperationType").t("CaptureRoleAsVMImageOperation").up()
                    .e("OSState").t(params.osState() == GENERALIZED ? "Generalized" : "Specialized").up()
                    .e("VMImageName").t(params.name()).up()
                    .e("VMImageLabel").t(params.label()).up();
            add(builder, "Description", params.description());
            add(builder, "Language", params.language());
            add(builder, "ImageFamily", params.imageFamily());

            RoleSize.Type vmSize = params.recommendedVMSize();
            if (vmSize != null) {
                String vmSizeText = params.recommendedVMSize().getText();
                builder.e("RecommendedVMSize").t(vmSizeText).up();
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
}
