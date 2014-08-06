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
package org.jclouds.virtualbox.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;


/**
 * An immutable representation of a MachineNameOrId and NIC port.
 *
 * <p>Example usage:
 * <pre>
 * MachineNameOrIdAndNicSlot mp = MachineNameOrIdAndNicSlot.fromString("myMachine:1");
 * hp.getMachineNameOrId();  // returns "myMachine"
 * hp.getSlot();      // returns 1
 * hp.toString();     // returns "myMachine:1"
 * </pre>
 */
public final class MachineNameOrIdAndNicSlot {

  private static final String SEPARATOR = ":";

/** IMachine name or id*/
  private final String machineNameOrId;

  /** Validated NIC slot number in the range [0..3] */
  private final long slot;

  private MachineNameOrIdAndNicSlot(String machineNameOrId, long slot) {
    this.machineNameOrId = machineNameOrId;
    this.slot = slot;
  }

  public String getMachineNameOrId() {
    return machineNameOrId;
  }

  public boolean hasSlot() {
    return slot >= 0;
  }

  public long getSlot() {
    checkState(hasSlot());
    return slot;
  }
  
  public String getSlotText() {
     checkState(hasSlot());
     return String.valueOf(slot);
   }  

  public static MachineNameOrIdAndNicSlot fromParts(String machineNameOrId, long slot) {
    checkArgument(isValidSlot(slot));
    return new MachineNameOrIdAndNicSlot(checkNotNull(machineNameOrId, "machineNameOrId"), slot);
  }

   public static MachineNameOrIdAndNicSlot fromString(String machineNameOrIdAndNicSlotString) {
      Iterable<String> splittedString = Splitter.on(SEPARATOR).split(machineNameOrIdAndNicSlotString);
      checkState(Iterables.size(splittedString) == 2);
      String machineNameOrId = Strings.nullToEmpty(Iterables.get(splittedString, 0));
      String nicSlotString = Strings.nullToEmpty(Iterables.get(splittedString, 1));
      checkArgument(!nicSlotString.startsWith("+"), "Unparseable slot number: %s", nicSlotString);
      try {
         long slot = Long.parseLong(nicSlotString);
         checkArgument(isValidSlot(slot), "Slot number out of range: %s", nicSlotString);
         return new MachineNameOrIdAndNicSlot(machineNameOrId, slot);
      } catch (NumberFormatException e) {
         throw new IllegalArgumentException("Unparseable slot number: " + nicSlotString);
      }
   }

  public MachineNameOrIdAndNicSlot withDefaultSlot(int defaultSlot) {
    checkArgument(isValidSlot(defaultSlot));
    if (hasSlot() || slot == defaultSlot) {
      return this;
    }
    return new MachineNameOrIdAndNicSlot(machineNameOrId, defaultSlot);
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof MachineNameOrIdAndNicSlot) {
       MachineNameOrIdAndNicSlot that = (MachineNameOrIdAndNicSlot) other;
      return Objects.equal(this.machineNameOrId, that.machineNameOrId)
          && this.slot == that.slot;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(machineNameOrId, slot);
  }

  @Override
  public String toString() {
     return MoreObjects.toStringHelper(this)
     .add("machineNameOrId", machineNameOrId)
     .add("nicSlot", slot)
     .toString();
  }

  private static boolean isValidSlot(long slot) {
    return slot >= 0l && slot <= 3l;
  }
}
