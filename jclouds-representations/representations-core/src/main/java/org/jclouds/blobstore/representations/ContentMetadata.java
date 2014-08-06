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
package org.jclouds.blobstore.representations;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Date;

import static org.jclouds.representations.Representations.dateFormat;


public class ContentMetadata implements Serializable {

   private static final long serialVersionUID = 4047812866269918734L;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Long length;
      private String disposition;
      private String encoding;
      private String type;
      private byte[] md5;
      private String language;
      private String expires;

      public Builder length(final Long length) {
         this.length = length;
         return this;
      }

      public Builder disposition(final String disposition) {
         this.disposition = disposition;
         return this;
      }

      public Builder encoding(final String encoding) {
         this.encoding = encoding;
         return this;
      }

      public Builder type(final String type) {
         this.type = type;
         return this;
      }

      public Builder md5(final byte[] md5) {
         this.md5 = md5;
         return this;
      }

      public Builder language(final String language) {
         this.language = language;
         return this;
      }

      public Builder expires(final String expires) {
         this.expires = expires;
         return this;
      }

      public Builder expires(final Date expires) {
         this.expires = dateFormat(expires);
         return this;
      }

      public ContentMetadata build() {
         return new ContentMetadata(length, disposition, encoding, type, md5, language, expires);
      }
   }

   private final Long length;
   private final String disposition;
   private final String encoding;
   private final String type;
   private final byte[] md5;
   private final String language;
   private final String expires;

   public ContentMetadata(Long length, String disposition, String encoding, String type, byte[] md5, String language, String expires) {
      this.length = length;
      this.disposition = disposition;
      this.encoding = encoding;
      this.type = type;
      this.md5 = md5;
      this.language = language;
      this.expires = expires;
   }

   public Long getLength() {
      return length;
   }

   public String getDisposition() {
      return disposition;
   }

   public String getEncoding() {
      return encoding;
   }

   public String getType() {
      return type;
   }

   public byte[] getMd5() {
      return md5;
   }

   public String getLanguage() {
      return language;
   }

   public String getExpires() {
      return expires;
   }

   public int hashCode() {
      return Objects.hashCode(length, disposition, encoding, type, md5, language, expires);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("length", language).add("disposition", disposition).add("encoding", encoding)
              .add("type", type).add("md5", md5).add("language", language)
              .add("expires", expires).toString();
   }
}
