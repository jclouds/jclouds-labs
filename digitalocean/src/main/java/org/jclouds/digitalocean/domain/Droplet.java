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
package org.jclouds.digitalocean.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * A droplet.
 */
public class Droplet {

   public enum Status {
      NEW, ACTIVE, ARCHIVE, OFF;

      public static Status fromValue(String value) {
         Optional<Status> status = Enums.getIfPresent(Status.class, value.toUpperCase());
         checkArgument(status.isPresent(), "Expected one of %s but was %s", Joiner.on(',').join(Status.values()), value);
         return status.get();
      }
   }

   private final int id;
   private final String name;
   @Named("image_id")
   private final int imageId;
   @Named("size_id")
   private final int sizeId;
   @Named("region_id")
   private final int regionId;
   @Named("backups_active")
   private final boolean backupsActive;
   private final List<Object> backups;
   private final List<Object> snapshots;
   @Named("ip_address")
   private final String ip;
   @Named("private_ip_address")
   private final String privateIp;
   private final boolean locked;
   private final Status status;
   @Named("created_at")
   private final Date creationDate;

   @ConstructorProperties({ "id", "name", "image_id", "size_id", "region_id", "backups_active", "backups", "snapshots",
         "ip_address", "private_ip_address", "locked", "status", "created_at" })
   public Droplet(int id, String name, int imageId, int sizeId, int regionId, boolean backupsActive,
         @Nullable List<Object> backups, @Nullable List<Object> snapshots, String ip, @Nullable String privateIp,
         boolean locked, Status status, @Nullable Date creationDate) throws ParseException {
      this.id = id;
      this.name = checkNotNull(name, "name cannot be null");
      this.imageId = imageId;
      this.sizeId = sizeId;
      this.regionId = regionId;
      this.backupsActive = backupsActive;
      this.backups = backups != null ? ImmutableList.copyOf(backups) : ImmutableList.of();
      this.snapshots = snapshots != null ? ImmutableList.copyOf(snapshots) : ImmutableList.of();
      this.ip = ip;
      this.privateIp = privateIp;
      this.locked = locked;
      this.status = checkNotNull(status, "status cannot be null");
      this.creationDate = creationDate;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public int getImageId() {
      return imageId;
   }

   public int getSizeId() {
      return sizeId;
   }

   public int getRegionId() {
      return regionId;
   }

   public boolean isBackupsActive() {
      return backupsActive;
   }

   public List<Object> getBackups() {
      return backups;
   }

   public List<Object> getSnapshots() {
      return snapshots;
   }

   public String getIp() {
      return ip;
   }

   public String getPrivateIp() {
      return privateIp;
   }

   public boolean isLocked() {
      return locked;
   }

   public Status getStatus() {
      return status;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (backups == null ? 0 : backups.hashCode());
      result = prime * result + (backupsActive ? 1231 : 1237);
      result = prime * result + (creationDate == null ? 0 : creationDate.hashCode());
      result = prime * result + id;
      result = prime * result + imageId;
      result = prime * result + (ip == null ? 0 : ip.hashCode());
      result = prime * result + (locked ? 1231 : 1237);
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (privateIp == null ? 0 : privateIp.hashCode());
      result = prime * result + regionId;
      result = prime * result + sizeId;
      result = prime * result + (snapshots == null ? 0 : snapshots.hashCode());
      result = prime * result + (status == null ? 0 : status.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      Droplet other = (Droplet) obj;
      if (backups == null) {
         if (other.backups != null) {
            return false;
         }
      } else if (!backups.equals(other.backups)) {
         return false;
      }
      if (backupsActive != other.backupsActive) {
         return false;
      }
      if (creationDate == null) {
         if (other.creationDate != null) {
            return false;
         }
      } else if (!creationDate.equals(other.creationDate)) {
         return false;
      }
      if (id != other.id) {
         return false;
      }
      if (imageId != other.imageId) {
         return false;
      }
      if (ip == null) {
         if (other.ip != null) {
            return false;
         }
      } else if (!ip.equals(other.ip)) {
         return false;
      }
      if (locked != other.locked) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (privateIp == null) {
         if (other.privateIp != null) {
            return false;
         }
      } else if (!privateIp.equals(other.privateIp)) {
         return false;
      }
      if (regionId != other.regionId) {
         return false;
      }
      if (sizeId != other.sizeId) {
         return false;
      }
      if (snapshots == null) {
         if (other.snapshots != null) {
            return false;
         }
      } else if (!snapshots.equals(other.snapshots)) {
         return false;
      }
      if (status != other.status) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return "Droplet [id=" + id + ", name=" + name + ", imageId=" + imageId + ", sizeId=" + sizeId + ", regionId="
            + regionId + ", backupsActive=" + backupsActive + ", backups=" + backups + ", snapshots=" + snapshots
            + ", ip=" + ip + ", privateIp=" + privateIp + ", locked=" + locked + ", status=" + status
            + ", creationDate=" + creationDate + "]";
   }

}
