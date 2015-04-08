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
 * To create a new service certifcate.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee460817.aspx">docs</a>
 */
@AutoValue
public abstract class ServiceCertificateParams {

   ServiceCertificateParams() {
   } // For AutoValue only!

   /**
    * The public part of the service certificate as a base-64 encoded .cer file.
    *
    * @return base-64 encoded certificate.
    */
   public abstract String data();

   /**
    * Required. Specifies the format of the service certificate. Possible values are: pfx, cer.
    *
    * @return certificate format.
    */
   public abstract String format();

   /**
    * Specifies the password for a .pfx certificate. A .cer certificate does not require a password.
    *
    * @return certificate password.
    */
   @Nullable
   public abstract String password();

   public Builder toBuilder() {
      return builder().fromServiceCertificateParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String data;

      private String format;

      private String password;

      public Builder data(final String data) {
         this.data = data;
         return this;
      }

      public Builder format(final String format) {
         this.format = format;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public ServiceCertificateParams build() {
         return ServiceCertificateParams.create(data, format, password);
      }

      public Builder fromServiceCertificateParams(final ServiceCertificateParams in) {
         return data(in.data())
                 .format(in.format())
                 .password(in.password());
      }
   }

   private static ServiceCertificateParams create(final String data, final String format, final String password) {
      return new AutoValue_ServiceCertificateParams(data, format, password);
   }
}
