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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * System properties for the specified cloud service. These properties include the service name and
 * service type; the name of the affinity group to which the service belongs, or its location if it
 * is not part of an affinity group.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 */
public final class CloudService {
   public enum Status {
      CREATING, CREATED, DELETING, DELETED, CHANGING, RESOLVING_DNS,
      UNRECOGNIZED;
   }

   /**
    * The name of the cloud service. This name is the DNS prefix name and can be used to access the
    * cloud service.
    *
    * <p/>For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public String name() {
      return name;
   }

   /**
    * The geo-location of the cloud service in Windows Azure, if the cloud service is not
    * associated with an affinity group. If a location has been specified, the AffinityGroup element
    * is not returned.
    */
   @Nullable public String location() {
      return location;
   }

   /**
    * The affinity group with which this cloud service is associated, if any. If the service is
    * associated with an affinity group, the Location element is not returned.
    */
   @Nullable public String affinityGroup() {
      return affinityGroup;
   }

   /**
    * The name can be up to 100 characters in length. The name can be used identify the storage account for your
    * tracking purposes.
    */
   public String label() {
      return label;
   }

   @Nullable public String description() {
      return description;
   }

   public Status status() {
      return status;
   }

   public Date created() {
      return created;
   }

   public Date lastModified() {
      return lastModified;
   }

   /**
    * Represents the name of an extended cloud service property. Each extended property must have
    * both a defined name and value. You can have a maximum of 50 extended property name/value
    * pairs.
    *
    * <p/>The maximum length of the Name element is 64 characters, only alphanumeric characters and
    * underscores are valid in the Name, and the name must start with a letter. Each extended
    * property value has a maximum length of 255 characters.
    */
   public Map<String, String> extendedProperties() {
      return extendedProperties;
   }

   public static CloudService create(String name, String location, String affinityGroup, String label,
         String description, Status status, Date created, Date lastModified, Map<String, String> extendedProperties) {
      return new CloudService(name, location, affinityGroup, label, description, status, created, lastModified,
            extendedProperties);
   }

   // TODO: Remove from here down with @AutoValue.
   private CloudService(String name, String location, String affinityGroup, String label, String description,
         Status status, Date created, Date lastModified, Map<String, String> extendedProperties) {
      this.name = checkNotNull(name, "name");
      this.location = location;
      this.affinityGroup = affinityGroup;
      this.label = checkNotNull(label, "label");
      this.description = description;
      this.status = checkNotNull(status, "status");
      this.created = checkNotNull(created, "created");
      this.lastModified = checkNotNull(lastModified, "lastModified");
      this.extendedProperties = checkNotNull(extendedProperties, "extendedProperties");
   }

   private final String name;
   private final String location;
   private final String affinityGroup;
   private final String label;
   private final String description;
   private final Status status;
   private final Date created;
   private final Date lastModified;
   private final Map<String, String> extendedProperties;

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof CloudService) {
         CloudService that = CloudService.class.cast(object);
         return equal(name, that.name)
               && equal(location, that.location)
               && equal(affinityGroup, that.affinityGroup)
               && equal(label, that.label)
               && equal(description, that.description)
               && equal(status, that.status)
               && equal(created, that.created)
               && equal(lastModified, that.lastModified)
               && equal(extendedProperties, that.extendedProperties);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, location, affinityGroup, label, description, status, created, lastModified,
            extendedProperties);
   }

   @Override
   public String toString() {
      return toStringHelper(this)
            .add("name", name)
            .add("location", location)
            .add("affinityGroup", affinityGroup)
            .add("label", label)
            .add("description", description)
            .add("status", status)
            .add("created", created)
            .add("lastModified", lastModified)
            .add("extendedProperties", extendedProperties).toString();
   }
}
