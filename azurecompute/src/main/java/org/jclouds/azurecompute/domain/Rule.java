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

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Rule {

   public enum Action {

      Allow,
      Deny,
      UNRECOGNIZED;

      public static Action fromString(final String text) {
         if (text != null) {
            for (Action action : Action.values()) {
               if (text.equalsIgnoreCase(action.name())) {
                  return action;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   public enum Type {

      Inbound,
      Outbound,
      UNRECOGNIZED;

      public static Type fromString(final String text) {
         if (text != null) {
            for (Type type : Type.values()) {
               if (text.equalsIgnoreCase(type.name())) {
                  return type;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   public enum Protocol {

      TCP("TCP"),
      UDP("UDP"),
      ALL("*"),
      UNRECOGNIZED("");

      private final String value;

      Protocol(final String value) {
         this.value = value;
      }

      public static Protocol fromString(final String text) {
         if (text != null) {
            for (Protocol protocol : Protocol.values()) {
               if (text.equalsIgnoreCase(protocol.value)) {
                  return protocol;
               }
            }
         }
         return UNRECOGNIZED;
      }

      public String getValue() {
         return value;
      }
   }

   public abstract String name();

   public abstract Type type();

   public abstract String priority();

   public abstract Action action();

   public abstract String sourceAddressPrefix();

   public abstract String sourcePortRange();

   public abstract String destinationAddressPrefix();

   public abstract String destinationPortRange();

   public abstract Protocol protocol();

   public abstract String state();

   @Nullable
   public abstract Boolean isDefault();

   Rule() {
   } // For AutoValue only!

   /**
    * Use this method to create a new rule to be added to a network security group.
    * @param name
    * @param type
    * @param priority
    * @param action
    * @param sourceAddressPrefix
    * @param sourcePortRange
    * @param destinationAddressPrefix
    * @param destinationPortRange
    * @param protocol
    * @return 
    */
   public static Rule create(final String name, final Type type, final String priority, final Action action,
           final String sourceAddressPrefix, final String sourcePortRange, final String destinationAddressPrefix,
           final String destinationPortRange, final Protocol protocol) {

      return new AutoValue_Rule(name, type, priority, action, sourceAddressPrefix, sourcePortRange,
              destinationAddressPrefix, destinationPortRange, protocol, "Active", null);
   }

   public static Rule create(final String name, final Type type, final String priority, final Action action,
           final String sourceAddressPrefix, final String sourcePortRange, final String destinationAddressPrefix,
           final String destinationPortRange, final Protocol protocol, final String state, final Boolean isDefault) {

      return new AutoValue_Rule(name, type, priority, action, sourceAddressPrefix, sourcePortRange,
              destinationAddressPrefix, destinationPortRange, protocol, state, isDefault);
   }

}
