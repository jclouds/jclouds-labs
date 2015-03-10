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
package org.jclouds.rackspace.autoscale.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Autoscale Personality. Part of the launch configuration.
 * You can inject data into the file system of the cloud server instance.
 * For example, you might want to insert ssh keys, set configuration files,
 * or store data that you want to retrieve from inside the instance.
 * This feature provides a minimal amount of launch-time personalization.
 * If you require significant customization, create a custom image.
 *
 * @see LaunchConfiguration#getPersonalities()
 * @see <a href="http://docs.rackspace.com/servers/api/v2/cs-devguide/content/Server_Personality-d1e2543.html">
 *    Server Personality
 *    </a>
 */
public class Personality {
   private final String path;
   private final String contents;

   @ConstructorProperties({ "path", "contents" })
   protected Personality(String path, String contents) {
      this.path = checkNotNull(path, "path should not be null");
      this.contents = checkNotNull(contents, "contents should not be null");
   }

   /**
    * @return the path of this Personality.
    * @see Personality.Builder#path(String)
    */
   public String getPath() {
      return this.path;
   }

   /**
    * @return the contents for this Personality.
    * @see Personality.Builder#contents(String)
    */
   public String getContents() {
      return this.contents;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(path, contents);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Personality that = Personality.class.cast(obj);
      return Objects.equal(this.path, that.path) && Objects.equal(this.contents, that.contents);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("path", path).add("contents", contents);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromPersonality(this);
   }

   public static class Builder {
      protected String path;
      protected String contents;

      /**
       * @param path The path of this Personality.
       * @return The builder object.
       * @see Personality#getPath()
       */
      public Builder path(String path) {
         this.path = path;
         return this;
      }

      /**
       * @param contents The contents of this Personality.
       * @return The builder object.
       * @see Personality#getContents()
       */
      public Builder contents(String contents) {
         this.contents = contents;
         return this;
      }

      /**
       * @return A new Personality object.
       */
      public Personality build() {
         return new Personality(path, contents);
      }

      public Builder fromPersonality(Personality in) {
         return this.path(in.getPath()).contents(in.getContents());
      }
   }
}
