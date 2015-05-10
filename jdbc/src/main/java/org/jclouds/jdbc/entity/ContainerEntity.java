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

import org.jclouds.blobstore.domain.ContainerAccess;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table
public class ContainerEntity {

   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private Long id;

   @Column(unique = true)
   private String name;

   private Date creationDate;

   private ContainerAccess containerAccess;

   public ContainerEntity() {
   }

   public ContainerEntity(Long id, String name, Date creationDate, ContainerAccess containerAccess) {
      this.id = id;
      this.name = name;
      this.creationDate = creationDate;
      this.containerAccess = containerAccess;
   }

   @PrePersist
   private void defaults() {
      this.creationDate = new Date();
      if (containerAccess == null) {
         this.containerAccess = ContainerAccess.PRIVATE;
      }
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Date getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   public ContainerAccess getContainerAccess() {
      return containerAccess;
   }

   public void setContainerAccess(ContainerAccess containerAccess) {
      this.containerAccess = containerAccess;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;
      private ContainerAccess containerAccess;

      public Builder() {
      }

      public Builder name(String name){
         this.name = name;
         return this;
      }

      public Builder containerAccess(ContainerAccess containerAccess){
         this.containerAccess = containerAccess;
         return this;
      }

      public ContainerEntity build() {
         return new ContainerEntity(null, name, null, containerAccess);
      }
   }
}
