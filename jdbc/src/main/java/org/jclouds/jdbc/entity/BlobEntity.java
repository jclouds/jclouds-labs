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
package org.jclouds.jdbc.entity;

import org.jclouds.blobstore.domain.BlobAccess;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

@Entity
@Table
@IdClass(value = BlobEntityPK.class)
public class BlobEntity {

   @Id
   @ManyToOne
   @JoinColumn(name = "id")
   private ContainerEntity containerEntity;

   @Id
   private String key;

   @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   private PayloadEntity payload;

   @ElementCollection(fetch = FetchType.EAGER)
   public Map<String, String> userMetadata;

   private Date creationDate;
   private Date lastModified;
   private BlobAccess blobAccess;
   private Long size;
   private String etag;
   private boolean directory;

   @PrePersist
   private void defaults() {
      this.lastModified = new Date();
      if (this.creationDate == null) {
         this.creationDate = new Date();
      }
      if (blobAccess == null) {
         this.blobAccess = BlobAccess.PRIVATE;
      }
   }

   public BlobEntity() {
   }

   public BlobEntity(ContainerEntity containerEntity, String key, PayloadEntity payload, Date creationDate, Date lastModified,
         BlobAccess blobAccess, Map<String, String> userMetadata, Long size, String etag, boolean directory) {
      this.containerEntity = containerEntity;
      this.key = key;
      this.creationDate = creationDate;
      this.lastModified = lastModified;
      this.payload = payload;
      this.blobAccess = blobAccess;
      this.userMetadata = userMetadata;
      this.size = size;
      this.etag = etag;
      this.directory = directory;
   }

   public ContainerEntity getContainerEntity() {
      return containerEntity;
   }

   public boolean isDirectory() {
      return directory;
   }

   public void setDirectory(boolean directory) {
      this.directory = directory;
   }

   public void setContainerEntity(ContainerEntity containerEntity) {
      this.containerEntity = containerEntity;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public PayloadEntity getPayload() {
      return payload;
   }

   public void setPayload(PayloadEntity payload) {
      this.payload = payload;
   }

   public Date getLastModified() {
      return lastModified;
   }

   public void setLastModified(Date lastModified) {
      this.lastModified = lastModified;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   public BlobAccess getBlobAccess() {
      return blobAccess;
   }

   public void setBlobAccess(BlobAccess blobAccess) {
      this.blobAccess = blobAccess;
   }

   public Map<String, String> getUserMetadata() {
      return userMetadata;
   }

   public void setUserMetadata(Map<String, String> userMetadata) {
      this.userMetadata = userMetadata;
   }

   public Long getSize() {
      return size;
   }

   public void setSize(Long size) {
      this.size = size;
   }

   public String getEtag() {
      return etag;
   }

   public void setEtag(String etag) {
      this.etag = etag;
   }

   public static Builder builder(ContainerEntity containerEntity, String key) {
      return new Builder(containerEntity, key);
   }

   public static class Builder {
      private ContainerEntity containerEntity;
      private String key;
      private PayloadEntity payload;
      private BlobAccess blobAccess;
      private Long size;
      private String etag;
      private Map<String, String> userMetadata;
      private boolean directory;

      public Builder(ContainerEntity containerEntity, String key) {
         this.containerEntity = containerEntity;
         this.key = key;
         this.directory = false;
      }

      public Builder blobAccess(BlobAccess blobAccess) {
         this.blobAccess = blobAccess;
         return this;
      }

      public Builder payload(PayloadEntity payload) {
         this.payload = payload;
         return this;
      }

      public Builder userMetadata(Map<String, String> userMetadata) {
         this.userMetadata = userMetadata;
         return this;
      }

      public Builder size(Long size) {
         this.size = size;
         return this;
      }

      public Builder etag(String etag) {
         this.etag = etag;
         return this;
      }

      public Builder directory(boolean directory) {
         this.directory = directory;
         return this;
      }

      public BlobEntity build() {
         return new BlobEntity(containerEntity, key, payload, null, null, blobAccess, userMetadata, size, etag, directory);
      }
   }

}
