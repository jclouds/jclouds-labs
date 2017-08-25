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

import org.jclouds.openstack.v2_0.domain.Link;

import com.google.common.base.Objects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ImmutableList;

/**
 * Cloud Big Data Profile. A Profile describes account settings for Cloud Big Data.
 * A Profile must be created before Clusters can be created.
 * 
 * @see ProfileApi#create
 */
public class Profile extends CreateProfile {
   private final String userId;
   private final String tenantId;
   private final ImmutableList<Link> links;

   @ConstructorProperties({
      "username", "password", "sshkeys", "cloudCredentials", "userId", "tenantId", "links"
   })
   protected Profile(String username, String password, ImmutableList<ProfileSSHKey> sshKeys, CloudCredentials cloudCredentials, String userId, String tenantId, ImmutableList<Link> links) {
      super(username, "", sshKeys, cloudCredentials); // Password not returned in response
      this.userId = checkNotNull(userId, "user id required");
      this.tenantId = checkNotNull(tenantId, "tenant id required");
      this.links = checkNotNull(links, "links required");
   }

   /**
    * @return the user id of this Profile.
    */
   public String getUserId() {
      return this.userId;
   }

   /**
    * @return the tenant id of this Profile.
    */
   public String getTenantId() {
      return this.tenantId;
   }

   /**
    * @return the links to this Profile.
    */
   public ImmutableList<Link> getLinks() {
      return this.links;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), links, userId, tenantId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Profile that = Profile.class.cast(obj);
      return Objects.equal(this.userId, that.userId) &&
            Objects.equal(this.tenantId, that.tenantId) &&
            Objects.equal(this.links, that.links) &&
            super.equals(obj);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("userId", userId)
            .add("tenantId", tenantId)
            .add("links", links);
   }

   @Override
   public String toString() {
      return string().toString();
   }
}
