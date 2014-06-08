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
package org.jclouds.rackspace.autoscale.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Autoscale WebhookResponse. Extends Webhook with id and links.
 * 
 * @see CreateWebhook#getWebhooks()
 */
public class Webhook extends CreateWebhook {
   private final ImmutableList<Link> links;
   private final String id;

   @ConstructorProperties({
      "name", "metadata", "links", "id"
   })
   public Webhook(String name, @Nullable Map<String, Object> metadata, List<Link> links, String id) {
      super(name, metadata);
      this.id = checkNotNull(id, "id required");
      this.links = ImmutableList.copyOf(checkNotNull(links, "links required"));
   }

   /**
    * @return the unique id of this ScalingPolicy.
    * @see ScalingPolicyResponse.Builder#id(String)
    */
   public String getId() {
      return this.id;
   }   

   /**
    * The capability Link for the webhook can be called with a POST request to execute the webhook anonymously.
    * @return the links to this ScalingPolicy.
    * @see ScalingPolicyResponse.Builder#links(String)
    */
   public ImmutableList<Link> getLinks() {
      return this.links;
   }
   
   public Optional<URI> getAnonymousExecutionURI() {
      // TODO: Add Relation.CAPABILITY to openstack Link
      for (Link l : this.links) {
         if (l.getRelation() == Relation.UNRECOGNIZED) {
            return Optional.of(l.getHref());
         }
      }
      return Optional.absent();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), links, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Webhook that = Webhook.class.cast(obj);
      return Objects.equal(this.id, that.id) && 
            Objects.equal(this.links, that.links) &&
            super.equals(obj);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("links", links)
            .add("id", id);
   }

   @Override
   public String toString() {
      return string().toString();
   }   
}
