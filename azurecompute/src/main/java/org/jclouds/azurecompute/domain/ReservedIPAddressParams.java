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
package org.jclouds.azurecompute.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

/**
 * The Create Reserved IP Address operation reserves an IPv4 address for the specified subscription. For more
 * information, see Reserved IP Addresses. You can use a small number of reserved IP addresses for free, but unused
 * reserved IP addresses and a number of addresses above the limit will incur charges.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn722413.aspx">Create Reserved IP Address</a>
 */
@AutoValue
public abstract class ReservedIPAddressParams {

   ReservedIPAddressParams() {
   } // For AutoValue only!

   /**
    * Specifies the name for the reserved IP address.
    *
    * @return name.
    */
   public abstract String name();

   /**
    * Specifies a label for the reserved IP address. The label can be up to 100 characters long and can be used for your
    * tracking purposes.
    *
    * @return label.
    */
   @Nullable
   public abstract String label();

   /**
    * Specifies the location of the reserved IP address. This should be the same location that is assigned to the cloud
    * service containing the deployment that will use the reserved IP address.
    *
    * @return location.
    */
   public abstract String location();

   public Builder toBuilder() {
      return builder().fromReservedIPAddressParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String name;

      private String label;

      private String location;

      public Builder name(final String data) {
         this.name = data;
         return this;
      }

      public Builder label(final String format) {
         this.label = format;
         return this;
      }

      public Builder location(final String password) {
         this.location = password;
         return this;
      }

      public ReservedIPAddressParams build() {
         return ReservedIPAddressParams.create(name, label, location);
      }

      public Builder fromReservedIPAddressParams(final ReservedIPAddressParams in) {
         return name(in.name())
                 .label(in.label())
                 .location(in.location());
      }
   }

   private static ReservedIPAddressParams create(final String name, final String label, final String location) {
      return new AutoValue_ReservedIPAddressParams(name, label, location);
   }
}
