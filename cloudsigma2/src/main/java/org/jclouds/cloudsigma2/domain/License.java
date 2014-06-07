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
package org.jclouds.cloudsigma2.domain;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.net.URI;

public class License {

   public static class Builder {
      private boolean burstable;
      private String longName;
      private String name;
      private URI resourceUri;
      private String type;
      private String userMetric;

      public Builder isBurstable(boolean burstable) {
         this.burstable = burstable;
         return this;
      }

      public Builder longName(String longName) {
         this.longName = longName;
         return this;
      }

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public Builder resourceUri(URI resourceUri) {
         this.resourceUri = resourceUri;
         return this;
      }

      public Builder type(String type) {
         this.type = type;
         return this;
      }

      public Builder userMetric(String userMetric) {
         this.userMetric = userMetric;
         return this;
      }

      public License build() {
         return new License(burstable, longName, name, resourceUri, type, userMetric);
      }
   }

   private final boolean burstable;
   @Named("long_name")
   private final String longName;
   private final String name;
   @Named("resource_uri")
   private final URI resourceUri;
   private final String type;
   @Named("user_metric")
   private final String userMetric;

   @ConstructorProperties({
         "burstable", "long_name", "name", "resource_uri", "type", "user_metric"
   })
   public License(boolean burstable, String longName, String name, URI resourceUri, String type, String userMetric) {
      this.burstable = burstable;
      this.longName = longName;
      this.name = name;
      this.resourceUri = resourceUri;
      this.type = type;
      this.userMetric = userMetric;
   }

   /**
    * @return Whether this resource can be used on burst
    */
   public boolean isBurstable() {
      return burstable;
   }

   /**
    * @return A human readable name for the resource.
    */
   public String getLongName() {
      return longName;
   }

   /**
    * @return Name that should be used when purchasing
    */
   public String getName() {
      return name;
   }

   /**
    * @return Unique resource uri
    */
   public URI getResourceUri() {
      return resourceUri;
   }

   /**
    * @return Type of billing
    */
   public String getType() {
      return type;
   }

   /**
    * @return The metric that the user is charged for
    */
   public String getUserMetric() {
      return userMetric;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof License)) return false;

      License that = (License) o;

      if (burstable != that.burstable) return false;
      if (longName != null ? !longName.equals(that.longName) : that.longName != null) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;
      if (resourceUri != null ? !resourceUri.equals(that.resourceUri) : that.resourceUri != null) return false;
      if (type != null ? !type.equals(that.type) : that.type != null) return false;
      if (userMetric != null ? !userMetric.equals(that.userMetric) : that.userMetric != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = burstable ? 1 : 0;
      result = 31 * result + (longName != null ? longName.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (resourceUri != null ? resourceUri.hashCode() : 0);
      result = 31 * result + (type != null ? type.hashCode() : 0);
      result = 31 * result + (userMetric != null ? userMetric.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "burstable=" + burstable +
            ", longName='" + longName + '\'' +
            ", name='" + name + '\'' +
            ", resourceUri='" + resourceUri + '\'' +
            ", type='" + type + '\'' +
            ", userMetric='" + userMetric + '\'' +
            "]";
   }
}
