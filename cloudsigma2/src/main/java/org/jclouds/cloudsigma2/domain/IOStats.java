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

public class IOStats {

   @Named("bytes_recv")
   private final String bytesReceived;
   @Named("bytes_sent")
   private final String bytesSent;
   @Named("packets_recv")
   private final String packetsReceived;
   @Named("packets_sent")
   private final String packetsSent;

   @ConstructorProperties({
         "bytes_recv", "bytes_sent", "packets_recv", "packets_sent"
   })
   public IOStats(String bytesReceived, String bytesSent, String packetsReceived, String packetsSent) {
      this.bytesReceived = bytesReceived;
      this.bytesSent = bytesSent;
      this.packetsReceived = packetsReceived;
      this.packetsSent = packetsSent;
   }

   /**
    * @return Bytes received on this interface
    */
   public String getBytesReceived() {
      return bytesReceived;
   }

   /**
    * @return Packets received on this interface
    */
   public String getBytesSent() {
      return bytesSent;
   }

   /**
    * @return Bytes sent from this interface
    */
   public String getPacketsReceived() {
      return packetsReceived;
   }

   /**
    * @return Packets sent from this interface
    */
   public String getPacketsSent() {
      return packetsSent;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((bytesReceived == null) ? 0 : bytesReceived.hashCode());
      result = prime * result + ((bytesSent == null) ? 0 : bytesSent.hashCode());
      result = prime * result + ((packetsReceived == null) ? 0 : packetsReceived.hashCode());
      result = prime * result + ((packetsSent == null) ? 0 : packetsSent.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      IOStats other = (IOStats) obj;
      if (bytesReceived == null) {
         if (other.bytesReceived != null)
            return false;
      } else if (!bytesReceived.equals(other.bytesReceived))
         return false;
      if (bytesSent == null) {
         if (other.bytesSent != null)
            return false;
      } else if (!bytesSent.equals(other.bytesSent))
         return false;
      if (packetsReceived == null) {
         if (other.packetsReceived != null)
            return false;
      } else if (!packetsReceived.equals(other.packetsReceived))
         return false;
      if (packetsSent == null) {
         if (other.packetsSent != null)
            return false;
      } else if (!packetsSent.equals(other.packetsSent))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[bytesReceived=" + bytesReceived + ", bytesSent=" + bytesSent + ", packetsReceived=" + packetsReceived
            + ", packetsSent=" + packetsSent + "]";
   }
}
