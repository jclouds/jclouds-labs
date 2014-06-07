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

public class NICStats {

   @Named("interface_type")
   private final InterfaceType interfaceType;
   @Named("io")
   private final IOStats ioStats;
   @Named("ip_v4")
   private final IP ipV4;
   @Named("ip_v6")
   private final IP ipV6;
   private final String mac;

   @ConstructorProperties({
         "interface_type", "io", "ip_v4", "ip_v6", "mac"
   })
   public NICStats(InterfaceType interfaceType, IOStats ioStats, IP ipV4, IP ipV6, String mac) {
      this.interfaceType = interfaceType;
      this.ioStats = ioStats;
      this.ipV4 = ipV4;
      this.ipV6 = ipV6;
      this.mac = mac;
   }

   /**
    * @return Type of interface
    */
   public InterfaceType getInterfaceType() {
      return interfaceType;
   }

   /**
    * @return NIC runtime Input and Output data"
    */
   public IOStats getIoStats() {
      return ioStats;
   }

   /**
    * @return Public IPv4 configuration
    */
   public IP getIpV4() {
      return ipV4;
   }

   /**
    * @return Public IPv6 configuration
    */
   public IP getIpV6() {
      return ipV6;
   }

   /**
    * @return MAC address of this NIC
    */
   public String getMac() {
      return mac;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof NICStats)) return false;

      NICStats nicStats = (NICStats) o;

      if (interfaceType != nicStats.interfaceType) return false;
      if (ioStats != null ? !ioStats.equals(nicStats.ioStats) : nicStats.ioStats != null) return false;
      if (ipV4 != null ? !ipV4.equals(nicStats.ipV4) : nicStats.ipV4 != null) return false;
      if (ipV6 != null ? !ipV6.equals(nicStats.ipV6) : nicStats.ipV6 != null) return false;
      if (mac != null ? !mac.equals(nicStats.mac) : nicStats.mac != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = interfaceType != null ? interfaceType.hashCode() : 0;
      result = 31 * result + (ioStats != null ? ioStats.hashCode() : 0);
      result = 31 * result + (ipV4 != null ? ipV4.hashCode() : 0);
      result = 31 * result + (ipV6 != null ? ipV6.hashCode() : 0);
      result = 31 * result + (mac != null ? mac.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "interfaceType=" + interfaceType +
            ", ioStats=" + ioStats +
            ", ipV4='" + ipV4 + '\'' +
            ", ipV6='" + ipV6 + '\'' +
            ", mac='" + mac + '\'' +
            "]";
   }
}
