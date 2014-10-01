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

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.util.Map;
import org.jclouds.azurecompute.domain.HostedService.Status;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailedHostedServiceProperties extends HostedServiceProperties {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromDetailedHostedServiceProperties(this);
   }

   public static class Builder extends HostedServiceProperties.Builder<Builder> {

      protected String rawStatus;
      protected Status status;
      protected Date created;
      protected Date lastModified;
      protected ImmutableMap.Builder<String, String> extendedProperties = ImmutableMap.<String, String>builder();

      /**
       * @see DetailedHostedServiceProperties#getRawStatus()
       */
      public Builder rawStatus(String rawStatus) {
         this.rawStatus = rawStatus;
         return this;
      }

      /**
       * @see DetailedHostedServiceProperties#getStatus()
       */
      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      /**
       * @see DetailedHostedServiceProperties#getCreated()
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      /**
       * @see DetailedHostedServiceProperties#getLastModified()
       */
      public Builder lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return this;
      }

      /**
       * @see DetailedHostedServiceProperties#getExtendedProperties()
       */
      public Builder extendedProperties(Map<String, String> extendedProperties) {
         this.extendedProperties.putAll(checkNotNull(extendedProperties, "extendedProperties"));
         return this;
      }

      /**
       * @see DetailedHostedServiceProperties#getExtendedProperties()
       */
      public Builder addExtendedProperty(String name, String value) {
         this.extendedProperties.put(checkNotNull(name, "name"), checkNotNull(value, "value"));
         return this;
      }

      @Override protected Builder self() {
         return this;
      }

      public DetailedHostedServiceProperties build() {
         return new DetailedHostedServiceProperties(description, location, affinityGroup, label, rawStatus, status,
               created, lastModified, extendedProperties.build());
      }

      public Builder fromDetailedHostedServiceProperties(DetailedHostedServiceProperties in) {
         return fromHostedServiceProperties(in).rawStatus(in.getRawStatus()).status(in.getStatus())
               .created(in.getCreated()).lastModified(in.getLastModified())
               .extendedProperties(in.getExtendedProperties());
      }
   }

   protected final String rawStatus;
   protected final Status status;
   protected final Date created;
   protected final Date lastModified;
   protected final Map<String, String> extendedProperties;

   protected DetailedHostedServiceProperties(Optional<String> description, Optional<String> location,
         Optional<String> affinityGroup, String label, String rawStatus, Status status, Date created, Date lastModified,
         Map<String, String> extendedProperties) {
      super(description, location, affinityGroup, label);
      this.rawStatus = checkNotNull(rawStatus, "rawStatus of %s", description);
      this.status = checkNotNull(status, "status of %s", description);
      this.created = checkNotNull(created, "created of %s", description);
      this.lastModified = checkNotNull(lastModified, "lastModified of %s", description);
      this.extendedProperties = ImmutableMap
            .copyOf(checkNotNull(extendedProperties, "extendedProperties of %s", description));
   }

   /**
    * The status of the hosted service.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * The status of the hosted service unparsed.
    */
   public String getRawStatus() {
      return rawStatus;
   }

   /**
    * The date that the hosted service was created.
    */
   public Date getCreated() {
      return created;
   }

   /**
    * The date that the hosted service was last updated.
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * Represents the name of an extended hosted service property. Each extended property must have
    * both a defined name and value. You can have a maximum of 50 extended property name/value
    * pairs.
    *
    * The maximum length of the Name element is 64 characters, only alphanumeric characters and
    * underscores are valid in the Name, and the name must start with a letter. Each extended
    * property value has a maximum length of 255 characters.
    */
   public Map<String, String> getExtendedProperties() {
      return extendedProperties;
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("status", rawStatus).add("created", created).add("lastModified", lastModified)
            .add("extendedProperties", extendedProperties);
   }
}
