package org.jclouds.openstack.reddwarf.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.reddwarf.v1.domain.Flavor;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.SkipEncoding;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volumes.
 * 
 * This API strictly handles creating and managing Volumes. To attach a Flavor to a Server you need to use the
 * @see VolumeAttachmentApi
 * 
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Zack Shoylev
 */
@SkipEncoding({'/', '='})
@RequestFilters(AuthenticateRequest.class)
public interface FlavorApi {
   /**
    * Returns a summary list of Flavors.
    *
    * @return The list of Flavors
    */
   @Named("flavor:list")
   @GET
   @Path("/flavors")
   @SelectJson("flavors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Flavor> list();
   
   /**
    * Returns a Flavor by id
    *
    * @return Flavor
    */
   @Named("flavors:get/{id}")
   @GET
   @Path("/flavors/{id}")
   @SelectJson("flavor")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Flavor get(@PathParam("id") int flavorId);
   
   /**
    * Returns a list of Flavors by Account ID (Tenant Id)
    *
    * @return The list of Flavors for Account/Tenant Id
    */
   @Named("flavors:get/{id}")
   @GET
   @Path("/flavors/{id}")
   @SelectJson("flavors")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Flavor> list(@PathParam("id") String accountId);
}
