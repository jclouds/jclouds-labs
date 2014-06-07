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

public class IPConfiguration {

   public static class Builder {
      private IPConfigurationType configurationType;
      private IP ip;

      public Builder configurationType(IPConfigurationType configurationType) {
         this.configurationType = configurationType;
         return this;
      }

      public Builder ip(IP ip) {
         this.ip = ip;
         return this;
      }

      public IPConfiguration build() {
         return new IPConfiguration(configurationType, ip);
      }
   }

   @Named("conf")
   private final IPConfigurationType configurationType;
   @Named("ip")
   private final IP ip;

   @ConstructorProperties({
         "conf", "ip"
   })
   public IPConfiguration(IPConfigurationType configurationType, IP ip) {
      this.configurationType = configurationType;
      this.ip = ip;
   }

   /**
    * @return configuration type
    */
   public IPConfigurationType getConfigurationType() {
      return configurationType;
   }

   /**
    * @return An IP address reference. Only used in ’static’ IP configuration.
    */
   public IP getIp() {
      return ip;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof IPConfiguration)) return false;

      IPConfiguration that = (IPConfiguration) o;

      if (configurationType != that.configurationType) return false;
      if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = configurationType != null ? configurationType.hashCode() : 0;
      result = 31 * result + (ip != null ? ip.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "configurationType=" + configurationType +
            ", ip=" + ip +
            "]";
   }
}
