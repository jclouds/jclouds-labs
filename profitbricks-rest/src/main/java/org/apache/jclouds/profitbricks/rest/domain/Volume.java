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
package org.apache.jclouds.profitbricks.rest.domain;

import com.google.auto.value.AutoValue;
import com.google.common.base.Enums;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Volume {

    public abstract String id();

    public abstract String type();

    public abstract String href();

    @Nullable
    public abstract Metadata metadata();

    @Nullable
    public abstract Properties properties();

    @SerializedNames({"id", "type", "href", "metadata", "properties"})
    public static Volume create(String id, String type, String href, Metadata metadata, Properties properties) {
        return new AutoValue_Volume(id, type, href, metadata, properties);
    }

    @AutoValue
    public abstract static class Properties {

        public enum BusType {

            IDE, SCSI, VIRTIO, UNRECOGNIZED;

            public static BusType fromValue(String value) {
                return Enums.getIfPresent(BusType.class, value).or(UNRECOGNIZED);
            }
        }

        public abstract String name();

        public abstract String type();

        public abstract int size();

        @Nullable
        public abstract String image();

        @Nullable
        public abstract String imagePassword();

        public abstract BusType bus();

        public abstract LicenceType licenceType();

        public abstract boolean cpuHotPlug();

        public abstract boolean cpuHotUnplug();

        public abstract boolean ramHotPlug();

        public abstract boolean ramHotUnplug();

        public abstract boolean nicHotPlug();

        public abstract boolean nicHotUnplug();

        public abstract boolean discVirtioHotPlug();

        public abstract boolean discVirtioHotUnplug();

        public abstract boolean discScsiHotPlug();

        public abstract boolean discScsiHotUnplug();

        public abstract int deviceNumber();

        @SerializedNames({"name", "type", "size", "image", "imagePassword", "bus", "licenceType", "cpuHotPlug", "cpuHotUnplug", "ramHotPlug", "ramHotUnplug", "nicHotPlug", "nicHotUnplug", "discVirtioHotPlug", "discVirtioHotUnplug", "discScsiHotPlug", "discScsiHotUnplug", "deviceNumber"})
        public static Volume.Properties create(String name, String type, int size, String image, String imagePassword, BusType bus, LicenceType licenceType,
                boolean cpuHotPlug, boolean cpuHotUnplug, boolean ramHotPlug, boolean ramHotUnplug, boolean nicHotPlug, boolean nicHotUnplug, boolean discVirtioHotPlug,
                boolean discVirtioHotUnplug, boolean discScsiHotPlug, boolean discScsiHotUnplug, int deviceNumber) {

            return new AutoValue_Volume_Properties(name, type, size, image, imagePassword, bus, licenceType, cpuHotPlug, cpuHotUnplug, ramHotPlug, ramHotUnplug, nicHotPlug, nicHotUnplug, discVirtioHotPlug, discVirtioHotUnplug, discScsiHotPlug, discScsiHotUnplug, deviceNumber);

        }
    }

}
