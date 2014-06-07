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

import java.beans.ConstructorProperties;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import com.google.common.collect.ImmutableList;

public class LibraryDrive extends DriveInfo {

   public static class Builder extends DriveInfo.Builder {
      private String arch;
      private List<String> category;
      private String description;
      private boolean isFavorite;
      private String imageType;
      private String installNotes;
      private String os;
      private boolean isPaid;
      private String url;

      public Builder arch(String arch) {
         this.arch = arch;
         return this;
      }

      public Builder category(List<String> category) {
         this.category = ImmutableList.copyOf(category);
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder isFavorite(boolean isFavorite) {
         this.isFavorite = isFavorite;
         return this;
      }

      public Builder imageType(String imageType) {
         this.imageType = imageType;
         return this;
      }

      public Builder installNotes(String installNotes) {
         this.installNotes = installNotes;
         return this;
      }

      public Builder isPaid(boolean isPaid) {
         this.isPaid = isPaid;
         return this;
      }

      public Builder os(String os) {
         this.os = os;
         return this;
      }

      public Builder url(String url) {
         this.url = url;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder size(BigInteger size) {
         this.size = size;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder allowMultimount(boolean allowMultimount) {
         this.allowMultimount = allowMultimount;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder affinities(List<String> affinities) {
         this.affinities = ImmutableList.copyOf(affinities);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder jobs(List<Job> jobs) {
         this.jobs = ImmutableList.copyOf(jobs);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder licenses(List<DriveLicense> licenses) {
         this.licenses = licenses;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder media(MediaType media) {
         this.media = media;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder meta(Map<String, String> meta) {
         this.meta = meta;
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder mountedOn(List<Server> mountedOn) {
         this.mountedOn = ImmutableList.copyOf(mountedOn);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder tags(List<String> tags) {
         this.tags = ImmutableList.copyOf(tags);
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder resourceUri(URI resourceUri) {
         return Builder.class.cast(super.resourceUri(resourceUri));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder owner(Owner owner) {
         return Builder.class.cast(super.owner(owner));
      }

      /**
       * {@inheritDoc}
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

      public LibraryDrive build() {
         return new LibraryDrive(uuid, name, resourceUri, size, owner, status, allowMultimount, affinities, jobs,
               licenses, media, meta, mountedOn, tags, arch, category, description, isFavorite, imageType, installNotes,
               os, isPaid, url);
      }
   }

   private final String arch;
   private final List<String> category;
   private final String description;
   @Named("favourite")
   private final boolean isFavorite;
   @Named("image_type")
   private final String imageType;
   @Named("install_notes")
   private final String installNotes;
   private final String os;
   @Named("paid")
   private final boolean isPaid;
   private final String url;

   @ConstructorProperties({
         "uuid", "name", "resource_uri", "size", "owner", "status",
         "allow_multimount", "affinities", "jobs", "licenses",
         "media", "meta", "mounted_on", "tags", "arch", "category",
         "description", "favourite", "image_type", "install_notes", "os", "paid", "url"
   })
   public LibraryDrive(String uuid, String name, URI resourceUri, BigInteger size, Owner owner, DriveStatus status,
                       boolean allowMultimount, List<String> affinities, List<Job> jobs, List<DriveLicense> licenses,
                       MediaType media, Map<String, String> meta, List<Server> mountedOn, List<String> tags,
                       String arch, List<String> category, String description, boolean favorite, String imageType,
                       String installNotes, String os, boolean paid, String url) {
      super(uuid, name, resourceUri, size, owner, status, allowMultimount, affinities, jobs, licenses, media, meta,
            mountedOn, tags);
      this.arch = arch;
      this.category = category;
      this.description = description;
      this.isFavorite = favorite;
      this.imageType = imageType;
      this.installNotes = installNotes;
      this.os = os;
      this.isPaid = paid;
      this.url = url;
   }

   /**
    * @return Operating system bit architecture the drive.
    */
   public String getArch() {
      return arch;
   }

   /**
    * @return Category of the drive.
    */
   public List<String> getCategory() {
      return category;
   }

   /**
    * @return Description of drive image.
    */
   public String getDescription() {
      return description;
   }

   /**
    * @return Type of drive image
    */
   public String getImageType() {
      return imageType;
   }

   /**
    * @return Install notes for the drive image.
    */
   public String getInstallNotes() {
      return installNotes;
   }

   /**
    * @return Favourite drive image for user.
    */
   public boolean isFavorite() {
      return isFavorite;
   }

   /**
    * @return Paid or free.
    */
   public boolean isPaid() {
      return isPaid;
   }

   /**
    * @return Operating system of the drive.
    */
   public String getOs() {
      return os;
   }

   /**
    * @return Operating system bit architecture the drive.
    */
   public String getUrl() {
      return url;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof LibraryDrive)) return false;
      if (!super.equals(o)) return false;

      LibraryDrive that = (LibraryDrive) o;

      if (isFavorite != that.isFavorite) return false;
      if (isPaid != that.isPaid) return false;
      if (arch != null ? !arch.equals(that.arch) : that.arch != null) return false;
      if (category != null ? !category.equals(that.category) : that.category != null) return false;
      if (description != null ? !description.equals(that.description) : that.description != null) return false;
      if (imageType != null ? !imageType.equals(that.imageType) : that.imageType != null) return false;
      if (installNotes != null ? !installNotes.equals(that.installNotes) : that.installNotes != null) return false;
      if (os != null ? !os.equals(that.os) : that.os != null) return false;
      if (url != null ? !url.equals(that.url) : that.url != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (arch != null ? arch.hashCode() : 0);
      result = 31 * result + (category != null ? category.hashCode() : 0);
      result = 31 * result + (description != null ? description.hashCode() : 0);
      result = 31 * result + (isFavorite ? 1 : 0);
      result = 31 * result + (imageType != null ? imageType.hashCode() : 0);
      result = 31 * result + (installNotes != null ? installNotes.hashCode() : 0);
      result = 31 * result + (os != null ? os.hashCode() : 0);
      result = 31 * result + (isPaid ? 1 : 0);
      result = 31 * result + (url != null ? url.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", size=" + size + ", owner=" + owner + ", status=" + status
            + ", affinities=" + affinities + ", jobs=" + jobs + ", licenses=" + licenses + ", media=" + media
            + ", meta=" + meta + ", mountedOn=" + mountedOn + ", tags=" + tags +
            ", arch='" + arch + '\'' +
            ", category=" + category +
            ", description='" + description + '\'' +
            ", isFavorite=" + isFavorite +
            ", imageType='" + imageType + '\'' +
            ", installNotes='" + installNotes + '\'' +
            ", os='" + os + '\'' +
            ", isPaid=" + isPaid +
            ", url='" + url + '\'' +
            "]";
   }
}
