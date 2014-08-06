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
package org.jclouds.compute.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.net.URI;

public class LoginCredentials implements Serializable {

   private static final long serialVersionUID = -4665781183795990721L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String username;
      private String password;
      private String privateKey;
      private String credentialUrl;
      private boolean authenticateSudo;

      public Builder username(final String username) {
         this.username = username;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public Builder privateKey(final String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      public Builder credentialUrl(final URI credentialUrl) {
         if (credentialUrl != null) {
            this.credentialUrl = credentialUrl.toString();
         }
         return this;
      }

      public Builder credentialUrl(final String credentialUrl) {
         this.credentialUrl = credentialUrl;
         return this;
      }

      public Builder authenticateSudo(final boolean authenticateSudo) {
         this.authenticateSudo = authenticateSudo;
         return this;
      }

      public LoginCredentials build() {
         return new LoginCredentials(username, password, privateKey, credentialUrl, authenticateSudo);
      }

   }

   private final String username;
   private final String password;
   private final String privateKey;
   private final String credentialUrl;
   private final boolean authenticateSudo;

   public LoginCredentials(String username, String password, String privateKey, String credentialUrl, boolean authenticateSudo) {
      this.username = username;
      this.password = password;
      this.privateKey = privateKey;
      this.credentialUrl = credentialUrl;
      this.authenticateSudo = authenticateSudo;
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public String getCredentialUrl() {
      return credentialUrl;
   }

   public boolean isAuthenticatedSudo() {
      return authenticateSudo;
   }


   @Override
   public int hashCode() {
      return Objects.hashCode(username, password, credentialUrl, authenticateSudo);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
              .add("username", username)
              .add("hasPassword", password != null || credentialUrl != null)
              .add("hasPrivateKey", privateKey != null)
              .add("hasSudoPassword", authenticateSudo).toString();
   }
}
