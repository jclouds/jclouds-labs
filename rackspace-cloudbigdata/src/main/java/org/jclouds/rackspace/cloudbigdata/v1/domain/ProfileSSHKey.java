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
package org.jclouds.rackspace.cloudbigdata.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Cloud Big Data ProfileSSHKey.
 * Used to provide a key pair for a Profile.
 */
public class ProfileSSHKey implements Comparable<ProfileSSHKey> {
   private String name;
   private String publicKey;

   @ConstructorProperties({
      "name", "publicKey"
   })
   protected ProfileSSHKey(String name, String publicKey) {
      this.name = checkNotNull(name, "name required");
      this.publicKey = checkNotNull(publicKey, "public key required");
   }

   /**
    * @return the name of this SSH key
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the public SSH key
    */
   public String getPublicKey() {
      return this.publicKey;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, publicKey);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ProfileSSHKey that = ProfileSSHKey.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.publicKey, that.publicKey);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("publicKey", publicKey);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return A new builder object
    */
   public static Builder builder() { 
      return new Builder();
   }

   /**
    * @return A new Builder object from another ProfileSSHKey
    */
   public Builder toBuilder() { 
      return new Builder().fromProfileSSHKey(this);
   }

   /**
    * Implements the Builder pattern for this class
    */
   public static class Builder {
      protected String name;
      protected String publicKey;

      /** 
       * @param name The name of this ProfileSSHKey.
       * @return The builder object.
       * @see ProfileSSHKey#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param publicKey The publicKey for this ProfileSSHKey.
       * @return The builder object.
       */
      public Builder publicKey(String publicKey) {
         this.publicKey = publicKey;
         return this;
      }      

      /**
       * @return A new ProfileSSHKey object.
       */
      public ProfileSSHKey build() {
         return new ProfileSSHKey(name, publicKey);
      }

      /**
       * @param in The target ProfileSSHKey
       * @return A Builder from the provided ProfileSSHKey
       */
      public Builder fromProfileSSHKey(ProfileSSHKey in) {
         return this
               .name(in.getName())
               .publicKey(in.getPublicKey());
      }        
   }

   @Override
   public int compareTo(ProfileSSHKey that) {
      return this.getName().compareTo(that.getName());
   }
}
