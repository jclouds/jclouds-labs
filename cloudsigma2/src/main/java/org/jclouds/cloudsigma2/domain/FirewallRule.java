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

public class FirewallRule {

   public static class Builder {
      private FirewallAction action;
      private String comment;
      private FirewallDirection direction;
      private String destinationIp;
      private String destinationPort;
      private FirewallIpProtocol ipProtocol;
      private String sourceIp;
      private String sourcePort;

      /**
       * @param action Action to be taken
       * @return FirewallRule Builder
       */
      public Builder action(FirewallAction action) {
         this.action = action;
         return this;
      }

      /**
       * @param comment Optional rule comment
       * @return FirewallRule Builder
       */
      public Builder comment(String comment) {
         this.comment = comment;
         return this;
      }

      /**
       * @param direction Packet direction
       * @return FirewallRule Builder
       */
      public Builder direction(FirewallDirection direction) {
         this.direction = direction;
         return this;
      }

      /**
       * @param destinationIp Destination IP address
       * @return FirewallRule Builder
       */
      public Builder destinationIp(String destinationIp) {
         this.destinationIp = destinationIp;
         return this;
      }

      /**
       * @param destinationPort Destination port
       * @return FirewallRule Builder
       */
      public Builder destinationPort(String destinationPort) {
         this.destinationPort = destinationPort;
         return this;
      }

      /**
       * @param ipProtocol IP protocol
       * @return FirewallRule Builder
       */
      public Builder ipProtocol(FirewallIpProtocol ipProtocol) {
         this.ipProtocol = ipProtocol;
         return this;
      }

      /**
       * @param sourceIp Source IP address
       * @return FirewallRule Builder
       */
      public Builder sourceIp(String sourceIp) {
         this.sourceIp = sourceIp;
         return this;
      }

      /**
       * @param sourcePort Source port
       * @return FirewallRule Builder
       */
      public Builder sourcePort(String sourcePort) {
         this.sourcePort = sourcePort;
         return this;
      }

      public FirewallRule build() {
         return new FirewallRule(action, comment, direction, destinationIp, destinationPort, ipProtocol, sourceIp,
               sourcePort);
      }
   }

   private final FirewallAction action;
   private final String comment;
   private final FirewallDirection direction;
   @Named("dst_ip")
   private final String destinationIp;
   @Named("dst_port")
   private final String destinationPort;
   @Named("ip_proto")
   private final FirewallIpProtocol ipProtocol;
   @Named("src_ip")
   private final String sourceIp;
   @Named("src_port")
   private final String sourcePort;

   @ConstructorProperties({
         "action", "comment", "direction", "dst_ip",
         "dst_port", "ip_proto", "src_ip", "src_port"
   })
   public FirewallRule(FirewallAction action, String comment, FirewallDirection direction, String destinationIp,
                       String destinationPort, FirewallIpProtocol ipProtocol, String sourceIp, String sourcePort) {
      this.action = action;
      this.comment = comment;
      this.direction = direction;
      this.destinationIp = destinationIp;
      this.destinationPort = destinationPort;
      this.ipProtocol = ipProtocol;
      this.sourceIp = sourceIp;
      this.sourcePort = sourcePort;
   }

   /**
    * @return Action to be taken
    */
   public FirewallAction getAction() {
      return action;
   }

   /**
    * @return Optional rule comment
    */
   public String getComment() {
      return comment;
   }

   /**
    * @return Packet direction
    */
   public FirewallDirection getDirection() {
      return direction;
   }

   /**
    * @return Destination IP address
    */
   public String getDestinationIp() {
      return destinationIp;
   }

   /**
    * @return Destination port
    */
   public String getDestinationPort() {
      return destinationPort;
   }

   /**
    * @return IP protocol
    */
   public FirewallIpProtocol getIpProtocol() {
      return ipProtocol;
   }

   /**
    * @return Source IP address
    */
   public String getSourceIp() {
      return sourceIp;
   }

   /**
    * @return Source port
    */
   public String getSourcePort() {
      return sourcePort;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof FirewallRule)) return false;

      FirewallRule that = (FirewallRule) o;

      if (action != that.action) return false;
      if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
      if (destinationIp != null ? !destinationIp.equals(that.destinationIp) : that.destinationIp != null)
         return false;
      if (destinationPort != null ? !destinationPort.equals(that.destinationPort) : that.destinationPort != null)
         return false;
      if (direction != that.direction) return false;
      if (ipProtocol != that.ipProtocol) return false;
      if (sourceIp != null ? !sourceIp.equals(that.sourceIp) : that.sourceIp != null) return false;
      if (sourcePort != null ? !sourcePort.equals(that.sourcePort) : that.sourcePort != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = action != null ? action.hashCode() : 0;
      result = 31 * result + (comment != null ? comment.hashCode() : 0);
      result = 31 * result + (direction != null ? direction.hashCode() : 0);
      result = 31 * result + (destinationIp != null ? destinationIp.hashCode() : 0);
      result = 31 * result + (destinationPort != null ? destinationPort.hashCode() : 0);
      result = 31 * result + (ipProtocol != null ? ipProtocol.hashCode() : 0);
      result = 31 * result + (sourceIp != null ? sourceIp.hashCode() : 0);
      result = 31 * result + (sourcePort != null ? sourcePort.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "action=" + action +
            ", comment='" + comment + '\'' +
            ", direction=" + direction +
            ", destinationIp='" + destinationIp + '\'' +
            ", destinationPort='" + destinationPort + '\'' +
            ", ipProtocol=" + ipProtocol +
            ", sourceIp='" + sourceIp + '\'' +
            ", sourcePort='" + sourcePort + '\'' +
            "]";
   }
}
