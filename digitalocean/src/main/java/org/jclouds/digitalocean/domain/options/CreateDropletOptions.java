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
package org.jclouds.digitalocean.domain.options;

import java.util.Set;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

/**
 * Options to customize droplet creation.
 */
public class CreateDropletOptions extends BaseHttpRequestOptions {

   private final Set<Integer> sshKeyIds;
   private final Boolean privateNetworking;
   private final Boolean backupsEnabled;

   public CreateDropletOptions(Set<Integer> sshKeyIds, Boolean privateNetworking, Boolean backupsEnabled) {
      this.sshKeyIds = sshKeyIds;
      this.privateNetworking = privateNetworking;
      this.backupsEnabled = backupsEnabled;

      if (!sshKeyIds.isEmpty()) {
         queryParameters.put("ssh_key_ids", Joiner.on(',').join(sshKeyIds));
      }
      if (privateNetworking != null) {
         queryParameters.put("private_networking", privateNetworking.toString());
      }
      if (backupsEnabled != null) {
         queryParameters.put("backups_enabled", backupsEnabled.toString());
      }
   }

   public Iterable<Integer> getSshKeyIds() {
      return sshKeyIds;
   }

   public Boolean getPrivateNetworking() {
      return privateNetworking;
   }

   public Boolean getBackupsEnabled() {
      return backupsEnabled;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (backupsEnabled == null ? 0 : backupsEnabled.hashCode());
      result = prime * result + (privateNetworking == null ? 0 : privateNetworking.hashCode());
      result = prime * result + (sshKeyIds == null ? 0 : sshKeyIds.hashCode());
      return result;
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
      CreateDropletOptions other = (CreateDropletOptions) obj;
      if (backupsEnabled == null) {
         if (other.backupsEnabled != null) {
            return false;
         }
      } else if (!backupsEnabled.equals(other.backupsEnabled)) {
         return false;
      }
      if (privateNetworking == null) {
         if (other.privateNetworking != null) {
            return false;
         }
      } else if (!privateNetworking.equals(other.privateNetworking)) {
         return false;
      }
      if (sshKeyIds == null) {
         if (other.sshKeyIds != null) {
            return false;
         }
      } else if (!sshKeyIds.equals(other.sshKeyIds)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "CreateDropletOptions [sshKeyIds=" + sshKeyIds + ", privateNetworking=" + privateNetworking
            + ", backupsEnabled=" + backupsEnabled + "]";
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private ImmutableSet.Builder<Integer> sshKeyIds = ImmutableSet.builder();
      private Boolean privateNetworking;
      private Boolean backupsEnabled;

      /**
       * Adds a set of ssh key ids to be added to the droplet.
       */
      public Builder addSshKeyIds(Iterable<Integer> sshKeyIds) {
         this.sshKeyIds.addAll(sshKeyIds);
         return this;
      }

      /**
       * Adds an ssh key id to be added to the droplet.
       */
      public Builder addSshKeyId(int sshKeyId) {
         this.sshKeyIds.add(sshKeyId);
         return this;
      }

      /**
       * Enables a private network interface if the region supports private
       * networking.
       */
      public Builder privateNetworking(boolean privateNetworking) {
         this.privateNetworking = privateNetworking;
         return this;
      }

      /**
       * Enabled backups for the droplet.
       */
      public Builder backupsEnabled(boolean backupsEnabled) {
         this.backupsEnabled = backupsEnabled;
         return this;
      }

      public CreateDropletOptions build() {
         return new CreateDropletOptions(sshKeyIds.build(), privateNetworking, backupsEnabled);
      }
   }
}
