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
package org.jclouds.digitalocean.features;

import java.io.Closeable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.http.filters.AuthenticationFilter;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.inject.name.Named;

/**
 * Provides access to the SSH key pair management features.
 */
@RequestFilters(AuthenticationFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/ssh_keys")
public interface KeyPairApi extends Closeable {

   /**
    * Lists all existing SSH key pairs.
    * 
    * @return The list of all existing SSH key pairs.
    */
   @Named("key:list")
   @GET
   @SelectJson("ssh_keys")
   List<SshKey> list();

   /**
    * Gets the details of an existing SSH key pair.
    * 
    * @param id The id of the SSH key pair.
    * @return The details of the SSH key pair or <code>null</code> if no key exists with the given id.
    */
   @Named("key:get")
   @GET
   @Path("/{id}")
   @SelectJson("ssh_key")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   SshKey get(@PathParam("id") int id);

   /**
    * Creates a new SSH key pair.
    * 
    * @param name The name of the key pair.
    * @param publicKey The public key.
    * @return The details of the created key pair.
    */
   @Named("key:create")
   @GET
   @Path("/new")
   @SelectJson("ssh_key")
   SshKey create(@QueryParam("name") String name, @QueryParam("ssh_pub_key") String publicKey);

   /**
    * Changes the SSH key for the given key pair.
    * 
    * @param id The id of the key pair.
    * @param newPublicKey The new public key.
    * @return The details of the modified key pair.
    */
   @Named("key:edit")
   @GET
   @Path("/{id}/edit")
   @SelectJson("ssh_key")
   SshKey edit(@PathParam("id") int id, @QueryParam("ssh_pub_key") String newPublicKey);

   /**
    * Deletes an existing SSH key pair.
    * 
    * @param id The id of the key pair.
    */
   @Named("key:delete")
   @GET
   @Path("/{id}/destroy")
   void delete(@PathParam("id") int id);
}
