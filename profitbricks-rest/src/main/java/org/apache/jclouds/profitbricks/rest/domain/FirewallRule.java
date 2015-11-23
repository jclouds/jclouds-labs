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
public abstract class FirewallRule {

    public abstract String id();

    public abstract String type();

    public abstract String href();

    public abstract Metadata metadata();

    public abstract Properties properties();

    public enum Protocol {

        TCP, UDP, ICMP, ANY, UNRECOGNIZED;

        public static Protocol fromValue(String value) {
           return Enums
                   .getIfPresent(FirewallRule.Protocol.class, value)
                   .or(UNRECOGNIZED);
        }
    }   

    @SerializedNames({"id", "type", "href", "metadata", "properties"})
    public static FirewallRule create(String id, String type, String href, Metadata metadata, Properties properties) {
        return new AutoValue_FirewallRule(id, type, href, metadata, properties);
    }

    @AutoValue
    public abstract static class Properties {

        public abstract String name();

        public abstract Protocol protocol();

        @Nullable
        public abstract String sourceMac();

        @Nullable
        public abstract String sourceIp();

        @Nullable
        public abstract String targetIp();

        @Nullable
        public abstract String icmpCode();

        @Nullable
        public abstract String icmpType();

        public abstract int portRangeStart();

        public abstract int portRangeEnd();

        @SerializedNames({"name", "protocol", "sourceMac", "sourceIp", "targetIp", "icmpCode", "icmpType", "portRangeStart", "portRangeEnd"})
        public static Properties create(String name, Protocol protocol, String sourceMac, String sourceIp, String targetIp, String icmpCode, String icmpType, int portRangeStart, int portRangeEnd) {
            return new AutoValue_FirewallRule_Properties(name, protocol, sourceMac, sourceIp, targetIp, icmpCode, icmpType, portRangeStart, portRangeEnd);
        }
    }
}
