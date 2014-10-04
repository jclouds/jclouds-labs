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
package org.jclouds.joyent.cloudapi.v6_5.features;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.Fallbacks.VoidOnNotFoundOr404;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.joyent.cloudapi.v6_5.domain.Machine;
import org.jclouds.joyent.cloudapi.v6_5.options.CreateMachineOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;

@Headers(keys = "X-Api-Version", values = "{jclouds.api-version}")
@RequestFilters(BasicAuthentication.class)
@Consumes(APPLICATION_JSON)
@Path("/my/machines")
public interface MachineApi {

   /**
    * Lists all machines we have on record for your account.
    * 
    * @return an account's associated machine objects.
    */
   @Named("ListMachines")
   @GET
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Machine> list();

   @Named("GetMachine")
   @GET
   @Path("/{id}")
   @Fallback(NullOnNotFoundOr404.class)
   Machine get(@PathParam("id") String id);

   /**
    * Allows you to provision a machine. Note that if you do not specify a
    * package, you'll get the datacenter defaults for it. If
    * you do not specify a name, CloudAPI will generate a random one for you.
    * Your machine will initially be not available for login (SmartDataCenter
    * must provision and boot it). You can poll {@link #get} for status. When the
    * state field is equal to running, you can login.
    * 
    * <p/>
    * With regards to login, if the machine is of type smartmachine, you can use
    * any of the SSH keys managed under the keys section of CloudAPI to login,
    * as any POSIX user on the OS. You can add/remove them over time, and the
    * machine will automatically work with that set.
    * 
    * <p/>
    * If the the machine is a virtualmachine, and of a UNIX-derived OS (e.g.
    * Linux), you must have keys uploaded before provisioning; that entire set
    * of keys will be written out to /root/.ssh/authorized_keys, and you can ssh
    * in using one of those. Changing the keys over time under your account will
    * not affect the running virtual machine in any way; those keys are
    * statically written at provisioning-time only, and you will need to
    * manually manage them.
    * 
    * <p/>
    * If the dataset you create a machine from is set to generate passwords for
    * you, the username/password pairs will be returned in the metadata response
    * as a nested object, like:
    * 
    * @param datasetURN urn of the dataset to install
    * 
    * @return the newly created machine
    */
   @Named("CreateMachine")
   @POST
   Machine createWithDataset(@QueryParam("dataset") String datasetURN, CreateMachineOptions options);
   
   /**
    * 
    * @see #createWithDataset(String, CreateMachineOptions)
    */
   @Named("CreateMachine")
   @POST
   Machine createWithDataset(@QueryParam("dataset") String datasetURN);

   /**
    * Allows you to shut down a machine.
    * 
    * @param id
    *           the id of the machine to stop
    */
   @Named("StopMachine")
   @POST
   @Produces(APPLICATION_FORM_URLENCODED)
   @Path("/{id}")
   @Payload("action=stop")
   void stop(@PathParam("id") String id);

   /**
    * Allows you to boot up a machine.
    * 
    * @param id
    *           the id of the machine to start
    */
   @Named("StartMachine")
   @POST
   @Produces(APPLICATION_FORM_URLENCODED)
   @Path("/{id}")
   @Payload("action=start")
   void start(@PathParam("id") String id);

   /**
    * Allows you to reboot a machine.
    * 
    * @param id
    *           the id of the machine to reboot
    */
   @Named("RestartMachine")
   @POST
   @Produces(APPLICATION_FORM_URLENCODED)
   @Path("/{id}")
   @Payload("action=reboot")
   void reboot(@PathParam("id") String id);

   /**
    * Allows you to resize a machine. (Works only for smart machines)
    * 
    * @param id
    *           the id of the machine to resize
    * @param packageJoyentCloud
    *           the package to use for the machine
    */
   @Named("ResizeMachine")
   @POST
   @Produces(APPLICATION_FORM_URLENCODED)
   @Path("/{id}")
   @Payload("action=resize&package={package}")
   void resize(@PathParam("id") String id, @PayloadParam("package") String packageJoyentCloud);

   /**
    * Allows you to delete a machine (the machine must be stopped before it can
    * be deleted).
    * 
    * @param id
    *           the id of the machine to delete
    */
   @Named("DeleteMachine")
   @DELETE
   @Path("/{id}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("id") String id);
}
