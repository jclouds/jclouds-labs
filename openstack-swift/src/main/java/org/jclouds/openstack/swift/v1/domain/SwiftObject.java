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
package org.jclouds.openstack.swift.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.blobstore.options.CreateContainerOptions;
import org.jclouds.io.Payload;
import org.jclouds.openstack.swift.v1.features.ObjectApi;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/retrieve-object.html">api
 *      doc</a>
 */
public class SwiftObject implements Comparable<SwiftObject> {

   private final String name;
   private final URI uri;
   private final String etag;
   private final Date lastModified;
   private final Map<String, String> metadata;
   private final Payload payload;

   protected SwiftObject(String name, URI uri, String etag, Date lastModified, Map<String, String> metadata,
         Payload payload) {
      this.name = checkNotNull(name, "name");
      this.uri = checkNotNull(uri, "uri of %s", uri);
      this.etag = checkNotNull(etag, "etag of %s", name).replace("\"", "");
      this.lastModified = checkNotNull(lastModified, "lastModified of %s", name);
      this.metadata = metadata == null ? ImmutableMap.<String, String> of() : metadata;
      this.payload = checkNotNull(payload, "payload of %s", name);
   }

   public String name() {
      return name;
   }

   /**
    * http location of the object, accessible if its container is
    * {@link CreateContainerOptions#publicRead}.
    */
   public URI uri() {
      return uri;
   }

   /**
    * Corresponds to the {@code ETag} header of the response, and is usually the
    * MD5 checksum of the object
    */
   public String etag() {
      return etag;
   }

   public Date lastModified() {
      return lastModified;
   }

   /**
    * Empty except in {@link ObjectApi#head(String) GetObjectMetadata} or
    * {@link ObjectApi#get(String) GetObject} commands.
    * 
    * <h3>Note</h3>
    * 
    * In current swift implementations, headers keys are lower-cased. This means
    * characters such as turkish will probably not work out well.
    */
   public Map<String, String> metadata() {
      return metadata;
   }

   /**
    * Only has a {@link Payload#getInput()} when retrieved via the
    * {@link ObjectApi#get(String) GetObject} command.
    */
   public Payload payload() {
      return payload;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (object instanceof SwiftObject) {
         final SwiftObject that = SwiftObject.class.cast(object);
         return equal(name(), that.name()) //
               && equal(uri(), that.uri()) //
               && equal(etag(), that.etag());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name(), uri(), etag());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return toStringHelper("") //
            .add("name", name()) //
            .add("uri", uri()) //
            .add("etag", etag()) //
            .add("lastModified", lastModified()) //
            .add("metadata", metadata());
   }

   @Override
   public int compareTo(SwiftObject that) {
      if (that == null)
         return 1;
      if (this == that)
         return 0;
      return this.name().compareTo(that.name());
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromObject(this);
   }

   public static class Builder {
      protected String name;
      protected URI uri;
      protected String etag;
      protected Date lastModified;
      protected Payload payload;
      protected Map<String, String> metadata = ImmutableMap.of();

      /**
       * @see SwiftObject#name()
       */
      public Builder name(String name) {
         this.name = checkNotNull(name, "name");
         return this;
      }

      /**
       * @see SwiftObject#uri()
       */
      public Builder uri(URI uri) {
         this.uri = checkNotNull(uri, "uri");
         return this;
      }

      /**
       * @see SwiftObject#etag()
       */
      public Builder etag(String etag) {
         this.etag = etag;
         return this;
      }

      /**
       * @see SwiftObject#lastModified()
       */
      public Builder lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return this;
      }

      /**
       * @see SwiftObject#payload()
       */
      public Builder payload(Payload payload) {
         this.payload = payload;
         return this;
      }

      /**
       * Will lower-case all metadata keys due to a swift implementation
       * decision.
       * 
       * @see SwiftObject#metadata()
       */
      public Builder metadata(Map<String, String> metadata) {
         ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
         for (Entry<String, String> entry : checkNotNull(metadata, "metadata").entrySet()) {
            builder.put(entry.getKey().toLowerCase(), entry.getValue());
         }
         this.metadata = builder.build();
         return this;
      }

      public SwiftObject build() {
         return new SwiftObject(name, uri, etag, lastModified, metadata, payload);
      }

      public Builder fromObject(SwiftObject from) {
         return name(from.name()) //
               .uri(from.uri()) //
               .etag(from.etag()) //
               .lastModified(from.lastModified()) //
               .metadata(from.metadata()) //
               .payload(from.payload());
      }
   }
}
