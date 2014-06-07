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

import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.net.URI;

public class Server extends Item {

   public static class Builder extends Item.Builder {
      protected Owner owner;
      protected ServerStatus status;
      protected ServerRuntime runtime;

      /**
       * @param owner server's owner.
       * @return Server Builder
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @param status Status of the guest.
       * @return Server Builder
       */
      public Builder status(ServerStatus status) {
         this.status = status;
         return this;
      }

      /**
       * @param runtime Runtime information of the guest
       * @return Server Builder
       */
      public Builder runtime(ServerRuntime runtime) {
         this.runtime = runtime;
         return this;
      }

      /**
       * {@inheritDoc}
       *
       * @return Server Builder
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * {@inheritDoc}
       *
       * @return Server Builder
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       *
       * @return Server Builder
       */
      @Override
      public Builder resourceUri(URI resourceUri) {
         return Builder.class.cast(super.resourceUri(resourceUri));
      }

      public Server build() {
         return new Server(uuid, name, resourceUri, owner, status, runtime);
      }

      public static Builder fromServer(Server in) {
         return new Builder()
               .uuid(in.getUuid())
               .name(in.getName())
               .resourceUri(in.getResourceUri())
               .owner(in.getOwner())
               .status(in.getStatus())
               .runtime(in.getRuntime());
      }
   }

   protected final Owner owner;
   protected final ServerStatus status;
   protected final ServerRuntime runtime;

   @ConstructorProperties({
         "uuid", "name", "resource_uri", "owner", "status", "runtime"
   })
   public Server(@Nullable String uuid, String name, URI resourceUri, Owner owner, ServerStatus status,
                 ServerRuntime runtime) {
      super(uuid, name, resourceUri);
      this.owner = owner;
      this.status = status;
      this.runtime = runtime;
   }

   /**
    * @return server's owner.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * @return Status of the guest.
    */
   public ServerStatus getStatus() {
      return status;
   }

   /**
    * @return Runtime information of the guest
    */
   public ServerRuntime getRuntime() {
      return runtime;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Server)) return false;
      if (!super.equals(o)) return false;

      Server server = (Server) o;

      if (owner != null ? !owner.equals(server.owner) : server.owner != null) return false;
      if (runtime != null ? !runtime.equals(server.runtime) : server.runtime != null) return false;
      if (status != server.status) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (owner != null ? owner.hashCode() : 0);
      result = 31 * result + (status != null ? status.hashCode() : 0);
      result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", resourceUri=" + resourceUri + ", owner=" + owner
            + ", status=" + status + ", runtime=" + runtime + "]";
   }

}
