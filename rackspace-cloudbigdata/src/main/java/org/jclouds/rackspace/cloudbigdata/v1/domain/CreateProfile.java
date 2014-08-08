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

import javax.inject.Named;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Cloud Big Data CreateProfile. 
 * This class is used when creating a new Profile.
 * You must create a profile before you can manage clusters for an account.
 * @see ProfileApi#create
 */
public class CreateProfile implements Comparable<CreateProfile> {
   private final String username;
   private final String password;
   @Named("sshkeys")
   private final ImmutableList<ProfileSSHKey> sshKeys;
   private final CloudCredentials cloudCredentials;

   @ConstructorProperties({
      "username", "password", "sshkeys", "cloudCredentials"
   })
   protected CreateProfile(String username, String password, ImmutableList<ProfileSSHKey> sshKeys, CloudCredentials cloudCredentials) {
      this.username = checkNotNull(username, "username required");
      this.password = checkNotNull(password, "password required");
      this.sshKeys = sshKeys;
      this.cloudCredentials = cloudCredentials;
   }

   /**
    * @return the username for this profile.
    * @see CreateProfile.Builder#username(String)
    */
   public String getUsername() {
      return username;
   }
   
   /**
    * The profile password.
    * @return the password for this CreateProfile object; empty in the response.
    * @see CreateProfile.Builder#username(String)
    */
   public String getPassword() {
      return password;
   }
   
   /**
    * @return the username for this profile's cloud credentials
    * @see CreateProfile.Builder#credentialsUsername(String)
    */
   public String getCredentialsUsername() {
      return cloudCredentials.username;
   }
   
   /**
    * @return the api key for this profile's cloud credentials
    * @see CreateProfile.Builder#credentialsApiKey(String)
    */
   public String getCredentialsApiKey() {
      return cloudCredentials.apikey;
   }
   
   /**
    * @return the list of ssh keys
    */
   public ImmutableList<ProfileSSHKey> getSSHKeys() {
      return sshKeys;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(username, password, cloudCredentials.username, cloudCredentials.apikey, sshKeys);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      CreateProfile that = CreateProfile.class.cast(obj);
      return Objects.equal(this.username, that.username) && 
            Objects.equal(this.password, that.password) &&
            Objects.equal(this.cloudCredentials.username, that.cloudCredentials.username) &&
            Objects.equal(this.cloudCredentials.apikey, that.cloudCredentials.apikey) &&
            Objects.equal(this.sshKeys, that.sshKeys);
   }

   protected ToStringHelper string() {
      return MoreObjects.toStringHelper(this)
            .add("username", username)
            .add("password", password)
            .add("credentialsUsername", cloudCredentials.username)
            .add("credentialsApikey", cloudCredentials.apikey)
            .add("sshkeys", sshKeys);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   /**
    * @return The builder object for this class
    */
   public static Builder builder() { 
      return new Builder();
   }

   /**
    * @return The Builder object
    */
   public Builder toBuilder() { 
      return new Builder().fromCreateProfile(this);
   }

   /**
    * Implements the Builder pattern
    */
   public static class Builder {
      protected String username;
      protected String password;
      protected ImmutableList<ProfileSSHKey> sshKeys;
      protected String credentialsUsername;
      protected String credentialsApikey;

      /** 
       * @param username The name of this Profile.
       * @return The builder object.
       * @see CreateProfile#getUsername()
       */
      public Builder username(String username) {
         this.username = username;
         return this;
      }

      /** 
       * @param password The password for this Profile.
       * @return The builder object.
       * @see CreateProfile#getPassword()
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }
      
      /** 
       * @param credentialsUsername The login name of this Profile.
       * @return The builder object.
       * @see CreateProfile#getCredentialsUsername()
       */
      public Builder credentialsUsername(String credentialsUsername) {
         this.credentialsUsername = credentialsUsername;
         return this;
      }
      
      /** 
       * @param credentialsApikey The name of this Profile.
       * @return The builder object.
       * @see CreateProfile#getCredentialsApiKey()
       */
      public Builder credentialsApiKey(String credentialsApikey) {
         this.credentialsApikey = credentialsApikey;
         return this;
      }
      
      /** 
       * @param sshKeys The list of SSH keys for this Profile.
       * @return The builder object.
       * @see CreateProfile#getSSHKeys()
       */
      public Builder sshKeys(ImmutableList<ProfileSSHKey> sshKeys) {
         this.sshKeys = sshKeys;
         return this;
      }

      /**
       * @return A new CreateProfile object.
       */
      public CreateProfile build() {
         return new CreateProfile(username, password, sshKeys, new CloudCredentials(credentialsUsername, credentialsApikey));
      }

      /**
       * @param in The target CreateProfile
       * @return The CreateProfile Builder
       */
      public Builder fromCreateProfile(CreateProfile in) {
         return this
               .username(in.getUsername())
               .password(in.getPassword())
               .sshKeys(in.getSSHKeys())
               .credentialsUsername(in.getCredentialsUsername())
               .credentialsApiKey(in.getCredentialsApiKey());
      }        
   }

   @Override
   public int compareTo(CreateProfile that) {
      return this.getUsername().compareTo(that.getUsername());
   }

   protected static class CloudCredentials {
      String username;
      String apikey;
      
      @ConstructorProperties({
         "username", "apikey"
      })
      protected CloudCredentials(String username, String apikey) {
         this.username = checkNotNull(username, "username required");
         this.apikey = apikey; // Optional in response.
      }
   }
}
