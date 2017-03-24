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
package org.jclouds.dimensiondata.cloudcontrol.domain.internal;

import com.google.auto.value.AutoValue;
import org.jclouds.dimensiondata.cloudcontrol.domain.Server;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class ServerWithExternalIp {

   ServerWithExternalIp() {
   }

   public static Builder builder() {
      return new AutoValue_ServerWithExternalIp.Builder();
   }

   public abstract Server server();

   @Nullable
   public abstract String externalIp();

   @SerializedNames({ "server", "externalIp" })
   public static ServerWithExternalIp create(Server server, String externalIp) {
      return builder().server(server).externalIp(externalIp).build();
   }

   public abstract Builder toBuilder();

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder server(Server server);

      public abstract Builder externalIp(String externalIp);

      public abstract ServerWithExternalIp build();
   }
}
