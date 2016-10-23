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
package org.jclouds.azurecompute.xml;

import org.jclouds.azurecompute.domain.OSImage;
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.http.functions.ParseSax;

import java.net.URI;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.jclouds.util.SaxUtils.currentOrNull;

/**
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/jj157193.aspx#DataVirtualHardDisks" >api</a>
 */
final class OSConfigHandler extends ParseSax.HandlerForGeneratedRequestWithResult<VMImage.OSDiskConfiguration> {

    private VMImage.OSDiskConfiguration.Caching hostCaching;
    private String name;
    private VMImage.OSDiskConfiguration.OSState osState;
    private OSImage.Type os;
    private Integer logicalDiskSizeInGB;
    private URI mediaLink;
    private String ioType;

    private final StringBuilder currentText = new StringBuilder();

    @Override
    public VMImage.OSDiskConfiguration getResult() {
        return VMImage.OSDiskConfiguration
                .create(name, hostCaching, osState, os, mediaLink, logicalDiskSizeInGB, ioType);
    }

    @Override
    public void endElement(String ignoredUri, String ignoredName, String qName) {

        if (qName.equals("HostCaching")) {
            String hostCachingText = currentOrNull(currentText);
            if (hostCachingText != null) {
                hostCaching = parseHostCache(hostCachingText);
            }
        } else if (qName.equals("OSState")) {
            String osStateText = currentOrNull(currentText);
            if (osStateText != null) {
                osState = VMImage.OSDiskConfiguration.OSState.valueOf(osStateText.toUpperCase());
            }
        } else if (qName.equals("OS")) {
            String osText = currentOrNull(currentText);
            if (osText != null) {
                os = OSImage.Type.valueOf(osText.toUpperCase());
            }
        } else if (qName.equals("Name")) {
            name = currentOrNull(currentText);
        } else if (qName.equals("LogicalDiskSizeInGB")) {
            String gb = currentOrNull(currentText);
            if (gb != null) {
                logicalDiskSizeInGB = Integer.parseInt(gb);
            }
        } else if (qName.equals("MediaLink")) {
            String link = currentOrNull(currentText);
            if (link != null) {
                mediaLink = URI.create(link);
            }
        } else if (qName.equals("IOType")) {
            ioType = currentOrNull(currentText);
        }
        currentText.setLength(0);
    }

    private static VMImage.OSDiskConfiguration.Caching parseHostCache(String hostCaching) {
        try {
            return VMImage.OSDiskConfiguration.Caching.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, hostCaching));
        } catch (IllegalArgumentException e) {
            return VMImage.OSDiskConfiguration.Caching.NONE;
        }
    }

    private static VMImage.OSDiskConfiguration.OSState parseOSState(String osState) {
        try {
            return VMImage.OSDiskConfiguration.OSState.valueOf(UPPER_CAMEL.to(UPPER_UNDERSCORE, osState));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        currentText.append(ch, start, length);
    }
}
