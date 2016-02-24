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

import com.google.common.collect.ImmutableList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
public class PayloadEntity {

   @Id
   @GeneratedValue
   private Long id;

   @ElementCollection(fetch = FetchType.EAGER)
   private List<Long> chunks;

   private String cacheControl;
   private String contentType;
   private Long contentLength;
   private byte[] contentMD5;
   private String contentDisposition;
   private String contentLanguage;
   private String contentEncoding;
   private Date expires;

   public PayloadEntity(List<Long> chunks, String cacheControl, String contentType, Long contentLength, byte[] contentMD5,
         String contentDisposition, String contentLanguage, String contentEncoding, Date expires) {
      this.chunks = chunks;
      this.cacheControl = cacheControl;
      this.contentType = contentType;
      this.contentLength = contentLength;
      this.contentMD5 = contentMD5;
      this.contentDisposition = contentDisposition;
      this.contentLanguage = contentLanguage;
      this.contentEncoding = contentEncoding;
      this.expires = expires;
   }

   public PayloadEntity() {
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public List<Long> getChunks() {
      return chunks;
   }

   public void setChunks(List<Long> chunks) {
      this.chunks = chunks;
   }

   public String getCacheControl() {
      return cacheControl;
   }

   public void setCacheControl(String cacheControl) {
      this.cacheControl = cacheControl;
   }

   public String getContentType() {
      return contentType;
   }

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public Long getContentLength() {
      return contentLength;
   }

   public void setContentLength(Long contentLength) {
      this.contentLength = contentLength;
   }

   public byte[] getContentMD5() {
      return contentMD5;
   }

   public void setContentMD5(byte[] contentMD5) {
      this.contentMD5 = contentMD5;
   }

   public String getContentDisposition() {
      return contentDisposition;
   }

   public void setContentDisposition(String contentDisposition) {
      this.contentDisposition = contentDisposition;
   }

   public String getContentLanguage() {
      return contentLanguage;
   }

   public void setContentLanguage(String contentLanguage) {
      this.contentLanguage = contentLanguage;
   }

   public String getContentEncoding() {
      return contentEncoding;
   }

   public void setContentEncoding(String contentEncoding) {
      this.contentEncoding = contentEncoding;
   }

   public Date getExpires() {
      return expires;
   }

   public void setExpires(Date expires) {
      this.expires = expires;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private List<Long> chunks;
      private String cacheControl;
      private String contentType;
      private Long contentLength;
      private byte[] contentMD5;
      private String contentDisposition;
      private String contentLanguage;
      private String contentEncoding;
      private Date expires;

      public Builder() {
         this.chunks = ImmutableList.of();
      }

      public Builder chunks(List<Long> chunks) {
         this.chunks = chunks;
         return this;
      }

      public Builder cacheControl(String cacheControl) {
         this.cacheControl = cacheControl;
         return this;
      }

      public Builder contentType(String contentType) {
         this.contentType = contentType;
         return this;
      }

      public Builder contentLength(Long contentLength) {
         this.contentLength = contentLength;
         return this;
      }

      public Builder contentMD5(byte[] contentMD5) {
         this.contentMD5 = contentMD5;
         return this;
      }

      public Builder contentDisposition(String contentDisposition) {
         this.contentDisposition = contentDisposition;
         return this;
      }

      public Builder contentLanguage(String contentLanguage) {
         this.contentLanguage = contentLanguage;
         return this;
      }

      public Builder contentEncoding(String contentEncoding) {
         this.contentEncoding = contentEncoding;
         return this;
      }

      public Builder expires(Date expires) {
         this.expires = expires;
         return this;
      }

      public PayloadEntity build() {
         return new PayloadEntity(chunks, cacheControl, contentType, contentLength, contentMD5, contentDisposition, contentLanguage, contentEncoding, expires);
      }
   }

}
