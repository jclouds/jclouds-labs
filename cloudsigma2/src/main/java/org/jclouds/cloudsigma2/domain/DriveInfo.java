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

import com.google.common.collect.ImmutableList;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class DriveInfo extends Drive {
   public static class Builder extends Drive.Builder {

      protected BigInteger size;
      protected boolean allowMultimount;
      protected List<String> affinities;
      protected List<Job> jobs;
      protected List<DriveLicense> licenses;
      protected MediaType media = MediaType.UNRECOGNIZED;
      protected Map<String, String> meta;
      protected List<Server> mountedOn;
      protected List<String> tags;

      /**
       * @param size Size of the drive in bytes
       * @return DriveInfo Builder
       */
      public Builder size(BigInteger size) {
         this.size = size;
         return this;
      }

      /**
       * @param allowMultimount Allow the drive to be mounted on multiple guests
       * @return DriveInfo Builder
       */
      public Builder allowMultimount(boolean allowMultimount) {
         this.allowMultimount = allowMultimount;
         return this;
      }

      /**
       * @param affinities A list of affinities this drive should belong to
       * @return DriveInfo Builder
       */
      public Builder affinities(List<String> affinities) {
         this.affinities = ImmutableList.copyOf(affinities);
         return this;
      }

      /**
       * @param jobs Background jobs related to this resource
       * @return DriveInfo Builder
       */
      public Builder jobs(List<Job> jobs) {
         this.jobs = ImmutableList.copyOf(jobs);
         return this;
      }

      /**
       * @param licenses A list of licences attached to this drive
       * @return DriveInfo Builder
       */
      public Builder licenses(List<DriveLicense> licenses) {
         this.licenses = licenses;
         return this;
      }

      /**
       * @param media Media representation type
       * @return DriveInfo Builder
       */
      public Builder media(MediaType media) {
         this.media = media;
         return this;
      }

      /**
       * @param meta User defined meta information
       * @return DriveInfo Builder
       */
      public Builder meta(Map<String, String> meta) {
         this.meta = meta;
         return this;
      }

      /**
       * @param mountedOn Servers on which this drive is mounted on
       * @return DriveInfo Builder
       */
      public Builder mountedOn(List<Server> mountedOn) {
         this.mountedOn = ImmutableList.copyOf(mountedOn);
         return this;
      }

      /**
       * @param tags Tags associated with this drive
       * @return DriveInfo Builder
       */
      public Builder tags(List<String> tags) {
         this.tags = ImmutableList.copyOf(tags);
         return this;
      }

      /**
       * {@inheritDoc}
       *
       * @return DriveInfo Builder
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * {@inheritDoc}
       *
       * @return DriveInfo Builder
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       *
       * @return DriveInfo Builder
       */
      @Override
      public Builder resourceUri(URI resourceUri) {
         return Builder.class.cast(super.resourceUri(resourceUri));
      }

      /**
       * {@inheritDoc}
       *
       * @return DriveInfo Builder
       */
      @Override
      public Builder owner(Owner owner) {
         return Builder.class.cast(super.owner(owner));
      }

      /**
       * {@inheritDoc}
       *
       * @return DriveInfo Builder
       */
      @Override
      public Builder status(DriveStatus status) {
         return Builder.class.cast(super.status(status));
      }

      public static Builder fromDriveInfo(DriveInfo in) {
         return new Builder()
               .uuid(in.getUuid())
               .name(in.getName())
               .resourceUri(in.getResourceUri())
               .owner(in.getOwner())
               .status(in.getStatus())
               .size(in.getSize())
               .allowMultimount(in.isAllowMultimount())
               .affinities(in.getAffinities())
               .jobs(in.getJobs())
               .licenses(in.getLicenses())
               .media(in.getMedia())
               .meta(in.getMeta())
               .mountedOn(in.getMountedOn())
               .tags(in.getTags());
      }

      /**
       * Warning: media, name & size should be specified
       *
       * @return DriveInfo instance
       */
      @Override
      public DriveInfo build() {
         return new DriveInfo(uuid, name, resourceUri, size, owner, status, allowMultimount, affinities, jobs, licenses,
               media, meta, mountedOn, tags);
      }

   }

   protected final BigInteger size;
   @Named("allow_multimount")
   protected final boolean allowMultimount;
   protected final List<String> affinities;
   protected final List<Job> jobs;
   protected final List<DriveLicense> licenses;
   protected final MediaType media;
   protected final Map<String, String> meta;
   @Named("mounted_on")
   protected final List<Server> mountedOn;
   protected final List<String> tags;

   @ConstructorProperties({
         "uuid", "name", "resource_uri", "size", "owner", "status",
         "allow_multimount", "affinities", "jobs", "licenses",
         "media", "meta", "mounted_on", "tags"
   })
   public DriveInfo(String uuid, String name, URI resourceUri,
                    BigInteger size, Owner owner, DriveStatus status,
                    boolean allowMultimount, List<String> affinities, List<Job> jobs, List<DriveLicense> licenses,
                    MediaType media, Map<String, String> meta, List<Server> mountedOn, List<String> tags) {
      super(uuid, name, resourceUri, owner, status);
      this.size = size;
      this.allowMultimount = allowMultimount;
      this.affinities = affinities;
      this.jobs = jobs;
      this.licenses = licenses;
      this.media = media;
      this.meta = meta;
      this.mountedOn = mountedOn;
      this.tags = tags;
   }

   /**
    * @return Size of the drive in bytes
    */
   public BigInteger getSize() {
      return size;
   }

   /**
    * @return A list of affinities this drive should belong to
    */
   public List<String> getAffinities() {
      return affinities;
   }

   /**
    * @return Allow the drive to be mounted on multiple guests
    */
   public boolean isAllowMultimount() {
      return allowMultimount;
   }

   /**
    * @return Background jobs related to this resource
    */
   public List<Job> getJobs() {
      return jobs;
   }

   /**
    * @return A list of licences attached to this drive
    */
   public List<DriveLicense> getLicenses() {
      return licenses;
   }

   /**
    * @return Media representation type
    */
   public MediaType getMedia() {
      return media;
   }

   /**
    * @return User defined meta information
    */
   public Map<String, String> getMeta() {
      return meta;
   }

   /**
    * @return Servers on which this drive is mounted on
    */
   public List<Server> getMountedOn() {
      return mountedOn;
   }

   /**
    * @return Tags associated with this drive
    */
   public List<String> getTags() {
      return tags;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (size != null ? size.hashCode() : 0);
      result = 31 * result + (allowMultimount ? 1 : 0);
      result = 31 * result + (affinities != null ? affinities.hashCode() : 0);
      result = 31 * result + (jobs != null ? jobs.hashCode() : 0);
      result = 31 * result + (licenses != null ? licenses.hashCode() : 0);
      result = 31 * result + (media != null ? media.hashCode() : 0);
      result = 31 * result + (meta != null ? meta.hashCode() : 0);
      result = 31 * result + (mountedOn != null ? mountedOn.hashCode() : 0);
      result = 31 * result + (tags != null ? tags.hashCode() : 0);
      return result;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof DriveInfo)) return false;
      if (!super.equals(o)) return false;

      DriveInfo driveInfo = (DriveInfo) o;

      if (allowMultimount != driveInfo.allowMultimount) return false;
      if (affinities != null ? !affinities.equals(driveInfo.affinities) : driveInfo.affinities != null) return false;
      if (jobs != null ? !jobs.equals(driveInfo.jobs) : driveInfo.jobs != null) return false;
      if (licenses != null ? !licenses.equals(driveInfo.licenses) : driveInfo.licenses != null) return false;
      if (media != driveInfo.media) return false;
      if (meta != null ? !meta.equals(driveInfo.meta) : driveInfo.meta != null) return false;
      if (mountedOn != null ? !mountedOn.equals(driveInfo.mountedOn) : driveInfo.mountedOn != null) return false;
      if (size != null ? !size.equals(driveInfo.size) : driveInfo.size != null) return false;
      if (tags != null ? !tags.equals(driveInfo.tags) : driveInfo.tags != null) return false;

      return true;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", size=" + size + ", owner=" + owner + ", status=" + status
            + ", affinities=" + affinities + ", jobs=" + jobs + ", licenses=" + licenses + ", media=" + media
            + ", meta=" + meta + ", mountedOn=" + mountedOn + ", tags=" + tags + "]";
   }

}
