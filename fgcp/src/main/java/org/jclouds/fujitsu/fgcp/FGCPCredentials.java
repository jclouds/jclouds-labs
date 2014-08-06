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
package org.jclouds.fujitsu.fgcp;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.jclouds.domain.Credentials;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Credentials for the FGCP that include a private key to sign the access key id
 * and its chain of certificates for HTTPS client authentication.
 */
public class FGCPCredentials extends Credentials {
   public final PrivateKey privateKey;
   public final Collection<X509Certificate> certificates;

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromFGCPCredentials(this);
   }

   public FGCPCredentials(String identity, String credential, PrivateKey privateKey, Collection<X509Certificate> certs) {
      super(identity, credential);
      this.privateKey = privateKey;
      certificates = certs;
   }

   public static class Builder extends Credentials.Builder<FGCPCredentials> {
      protected PrivateKey privateKey;
      protected Collection<X509Certificate> certificates;

      public Builder identity(String identity) {
         this.identity = identity;
         return this;
      }

      public Builder credential(String credential) {
         this.credential = Preconditions.checkNotNull(credential, "credential");
         return this;
      }

      public Builder privateKey(PrivateKey privateKey) {
         this.privateKey = Preconditions.checkNotNull(privateKey);
         return this;
      }

      public Builder certificates(Collection<X509Certificate> certs) {
         certificates = Preconditions.checkNotNull(certs);
         return this;
      }

      public FGCPCredentials build() {
         return new FGCPCredentials(identity, Preconditions.checkNotNull(credential), privateKey, certificates);
      }

      public Builder fromFGCPCredentials(FGCPCredentials credentials) {
         return (new Builder()).certificates(credentials.certificates).privateKey(credentials.privateKey)
               .identity(credentials.identity).credential(credentials.credential);
      }
   }

   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass()) {
         return false;
      } else {
         FGCPCredentials other = (FGCPCredentials) obj;
         return Objects.equal(identity, other.identity) && Objects.equal(credential, other.credential)
               && Objects.equal(privateKey, other.privateKey) && Objects.equal(certificates, other.certificates);
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[] { identity, credential, privateKey, certificates });
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).omitNullValues().add("identity", identity)
            .add("credential", credential == null ? null : ((Object) (Integer.valueOf(credential.hashCode()))))
            .add("privateKey", privateKey.hashCode()).add("certificates", certificates.hashCode()).toString();
   }
}
