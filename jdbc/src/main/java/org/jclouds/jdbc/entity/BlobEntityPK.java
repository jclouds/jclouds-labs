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

import java.io.Serializable;

public class BlobEntityPK implements Serializable {

   private Long containerEntity;
   private String key;

   public Long getContainerEntity() {
      return containerEntity;
   }

   public String getKey() {
      return key;
   }

   public BlobEntityPK() {
   }

   public BlobEntityPK(Long containerEntity, String key) {
      this.containerEntity = containerEntity;
      this.key = key;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;

      BlobEntityPK blobEntityPK = (BlobEntityPK) o;

      return containerEntity.equals(blobEntityPK.containerEntity) && key.equals(blobEntityPK.key);
   }

   @Override
   public int hashCode() {
      int result = containerEntity.hashCode();
      result = 31 * result + key.hashCode();
      return result;
   }

}
