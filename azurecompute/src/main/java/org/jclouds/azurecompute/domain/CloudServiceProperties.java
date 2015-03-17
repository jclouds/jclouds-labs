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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.ImmutableList.copyOf;

/**
 * System properties for the specified cloud service. These properties include the service name and
 * service type; the name of the affinity group to which the service belongs, or its location if it
 * is not part of an affinity group.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee460806.aspx" >CloudService</a>
 */
@AutoValue
public abstract class CloudServiceProperties {

   public enum Status {
      CREATING, CREATED, DELETING, DELETED, CHANGING, RESOLVING_DNS,
      UNRECOGNIZED;
   }

   CloudServiceProperties() {
   } // For AutoValue only!

   /**
    * The name of the cloud service. This name is the DNS prefix name and can be used to access the
    * cloud service.
    * <p/>
    * <p/>For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public abstract String serviceName();

   public abstract URI url();

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

   @Nullable public abstract Status status();

   @Nullable public abstract Date created();

   @Nullable public abstract Date lastModified();

   public abstract Map<String, String> extendedProperties();

   public abstract List<Deployment> deployments();

   public static CloudServiceProperties create(String name, URI url, String location, String affinityGroup,
         String label, String description, Status status, Date created, Date lastModified, Map<String, String> extendedProperties,
         List<Deployment> deployments) {
      return new AutoValue_CloudServiceProperties(name, url, location, affinityGroup, label, description, status,
            created, lastModified, copyOf(extendedProperties), copyOf(deployments));
   }
}
