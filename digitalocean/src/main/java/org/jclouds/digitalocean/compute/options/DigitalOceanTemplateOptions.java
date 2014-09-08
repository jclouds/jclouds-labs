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
package org.jclouds.digitalocean.compute.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Custom options for the DigitalOcean API.
 */
public class DigitalOceanTemplateOptions extends TemplateOptions implements Cloneable {

   private Set<Integer> sshKeyIds = ImmutableSet.of();
   private Boolean privateNetworking;
   private Boolean backupsEnabled;
   private boolean autoCreateKeyPair = true;

   /**
    * Enables a private network interface if the region supports private networking.
    */
   public DigitalOceanTemplateOptions privateNetworking(boolean privateNetworking) {
      this.privateNetworking = privateNetworking;
      return this;
   }

   /**
    * Enabled backups for the droplet.
    */
   public DigitalOceanTemplateOptions backupsEnabled(boolean backupsEnabled) {
      this.backupsEnabled = backupsEnabled;
      return this;
   }

   /**
    * Sets the ssh key ids to be added to the droplet.
    */
   public DigitalOceanTemplateOptions sshKeyIds(Iterable<Integer> sshKeyIds) {
      this.sshKeyIds = ImmutableSet.copyOf(checkNotNull(sshKeyIds, "sshKeyIds cannot be null"));
      return this;
   }

   /**
    * Sets whether an SSH key pair should be created automatically.
    */
   public DigitalOceanTemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
      this.autoCreateKeyPair = autoCreateKeyPair;
      return this;
   }

   public Set<Integer> getSshKeyIds() {
      return sshKeyIds;
   }

   public Boolean getPrivateNetworking() {
      return privateNetworking;
   }

   public Boolean getBackupsEnabled() {
      return backupsEnabled;
   }

   public boolean getAutoCreateKeyPair() {
      return autoCreateKeyPair;
   }

   @Override
   public DigitalOceanTemplateOptions clone() {
      DigitalOceanTemplateOptions options = new DigitalOceanTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof DigitalOceanTemplateOptions) {
         DigitalOceanTemplateOptions eTo = DigitalOceanTemplateOptions.class.cast(to);
         if (privateNetworking != null) {
            eTo.privateNetworking(privateNetworking);
         }
         if (backupsEnabled != null) {
            eTo.backupsEnabled(backupsEnabled);
         }
         eTo.autoCreateKeyPair(autoCreateKeyPair);
         eTo.sshKeyIds(sshKeyIds);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), backupsEnabled, privateNetworking, autoCreateKeyPair, sshKeyIds);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      DigitalOceanTemplateOptions other = (DigitalOceanTemplateOptions) obj;
      return super.equals(other) && equal(this.backupsEnabled, other.backupsEnabled)
            && equal(this.privateNetworking, other.privateNetworking)
            && equal(this.autoCreateKeyPair, other.autoCreateKeyPair) && equal(this.sshKeyIds, other.sshKeyIds);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string().omitNullValues();
      toString.add("privateNetworking", privateNetworking);
      toString.add("backupsEnabled", backupsEnabled);
      if (!sshKeyIds.isEmpty()) {
         toString.add("sshKeyIds", sshKeyIds);
      }
      toString.add("autoCreateKeyPair", autoCreateKeyPair);
      return toString;
   }

   public static class Builder {

      /**
       * @see DigitalOceanTemplateOptions#privateNetworking
       */
      public static DigitalOceanTemplateOptions privateNetworking(boolean privateNetworking) {
         DigitalOceanTemplateOptions options = new DigitalOceanTemplateOptions();
         return options.privateNetworking(privateNetworking);
      }

      /**
       * @see DigitalOceanTemplateOptions#backupsEnabled
       */
      public static DigitalOceanTemplateOptions backupsEnabled(boolean backupsEnabled) {
         DigitalOceanTemplateOptions options = new DigitalOceanTemplateOptions();
         return options.backupsEnabled(backupsEnabled);
      }

      /**
       * @see DigitalOceanTemplateOptions#sshKeyIds
       */
      public static DigitalOceanTemplateOptions sshKeyIds(Iterable<Integer> sshKeyIds) {
         DigitalOceanTemplateOptions options = new DigitalOceanTemplateOptions();
         return options.sshKeyIds(sshKeyIds);
      }

      /**
       * @see DigitalOceanTemplateOptions#autoCreateKeyPair
       */
      public static DigitalOceanTemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
         DigitalOceanTemplateOptions options = new DigitalOceanTemplateOptions();
         return options.autoCreateKeyPair(autoCreateKeyPair);
      }
   }
}
