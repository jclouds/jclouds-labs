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

import static com.google.common.collect.ImmutableMap.copyOf;
import java.util.Date;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * System properties for the specified cloud service. These properties include the service name and
 * service type; the name of the affinity group to which the service belongs, or its location if it
 * is not part of an affinity group.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >CloudService</a>
 */
@AutoValue
public abstract class CloudService {

   public enum Status {
      CREATING, CREATED, DELETING, DELETED, CHANGING, RESOLVING_DNS,
      UNRECOGNIZED;
   }

   CloudService() {} // For AutoValue only!

   /**
    * The name of the cloud service. This name is the DNS prefix name and can be used to access the
    * cloud service.
    *
    * <p/>For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public abstract String name();

   /**
    * The geo-location of the cloud service in Windows Azure, if the cloud service is not
    * associated with an affinity group. If a location has been specified, the AffinityGroup element
    * is not returned.
    */
   @Nullable public abstract String location();

   /**
    * The affinity group with which this cloud service is associated, if any. If the service is
    * associated with an affinity group, the Location element is not returned.
    */
   @Nullable public abstract String affinityGroup();

   /**
    * The name can be up to 100 characters in length. The name can be used identify the storage account for your
    * tracking purposes.
    */
   public abstract String label();

   @Nullable public abstract String description();

   public abstract Status status();

   public abstract Date created();

   public abstract Date lastModified();

   /**
    * Represents the name of an extended cloud service property. Each extended property must have
    * both a defined name and value. You can have a maximum of 50 extended property name/value
    * pairs.
    *
    * <p/>The maximum length of the Name element is 64 characters, only alphanumeric characters and
    * underscores are valid in the Name, and the name must start with a letter. Each extended
    * property value has a maximum length of 255 characters.
    */
   public abstract Map<String, String> extendedProperties();

   public static CloudService create(String name, String location, String affinityGroup, String label,
         String description, Status status, Date created, Date lastModified, Map<String, String> extendedProperties) {
      return new AutoValue_CloudService(name, location, affinityGroup, label, description, status, created,
            lastModified, copyOf(extendedProperties));
   }
}
