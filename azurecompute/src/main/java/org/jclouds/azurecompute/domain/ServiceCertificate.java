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
import java.net.URI;
import org.jclouds.javax.annotation.Nullable;

/**
 * Cloud service certifcate.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee795178.aspx" >ServiceCertificate</a>
 */
@AutoValue
public abstract class ServiceCertificate {

   ServiceCertificate() {
   } // For AutoValue only!

   /**
    * The Service Management API request URI used to perform Get Service Certificate requests against the certificate
    * store.
    *
    * @return service certificate URL.
    */
   @Nullable
   public abstract URI url();

   /**
    * The X509 certificate thumb print property of the service certificate.
    *
    * @return thumbprint of the service certificate.
    */
   @Nullable
   public abstract String thumbprint();

   /**
    * The algorithm that was used to hash the service certificate. Currently SHA-1 is the only supported algorithm.
    *
    * @return thumbprint algorithm of the service certificate.
    */
   @Nullable
   public abstract String thumbprintAlgorithm();

   /**
    * The public part of the service certificate as a base-64 encoded .cer file.
    *
    * @return base-64 encoded certificate.
    */
   public abstract String data();

   public static ServiceCertificate create(
           final URI url,
           final String thumbprint,
           final String thumbprintAlgorithm,
           final String data) {

      return new AutoValue_ServiceCertificate(url, thumbprint, thumbprintAlgorithm, data);
   }
}
