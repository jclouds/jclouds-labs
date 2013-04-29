/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.elb.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.elb.binders.BindAvailabilityZonesToIndexedFormParams;
import org.jclouds.elb.xml.AvailabilityZonesResultHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"
 *      >doc</a>
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AvailabilityZoneApi {

   /**
    * Adds one or more EC2 Availability Zones to the LoadBalancer.
    * 
    * The LoadBalancer evenly distributes requests across all its registered Availability Zones that
    * contain instances. As a result, the api must ensure that its LoadBalancer is appropriately
    * scaled for each registered Availability Zone.
    * 
    * <h4>Note</h4>
    * 
    * The new EC2 Availability Zones to be added must be in the same EC2 Region as the Availability
    * Zones for which the LoadBalancer was created.
    * 
    * @param zones
    *           A list of new Availability Zones for the LoadBalancer. Each Availability Zone must
    *           be in the same Region as the LoadBalancer.
    * 
    * @param loadBalancerName
    *           The name associated with the LoadBalancer. The name must be unique within the api
    *           AWS account.
    * 
    * @return An updated list of Availability Zones for the LoadBalancer.
    */
   @Named("EnableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "EnableAvailabilityZonesForLoadBalancer")
   Set<String> addAvailabilityZonesToLoadBalancer(
            @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) Iterable<String> zones,
            @FormParam("LoadBalancerName") String loadBalancerName);
   

   /**
    * @see AvailabilityZoneApi#addAvailabilityZoneToLoadBalancer
    */
   @Named("EnableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "EnableAvailabilityZonesForLoadBalancer")
   Set<String> addAvailabilityZoneToLoadBalancer(
            @FormParam("AvailabilityZones.member.1") String zone,
            @FormParam("LoadBalancerName") String loadBalancerName);


   /**
    * Removes the specified EC2 Availability Zones from the set of configured Availability Zones for
    * the LoadBalancer.
    * 
    * 
    * There must be at least one Availability Zone registered with a LoadBalancer at all times. A
    * api cannot remove all the Availability Zones from a LoadBalancer. Once an Availability Zone
    * is removed, all the instances registered with the LoadBalancer that are in the removed
    * Availability Zone go into the OutOfService state. Upon Availability Zone removal, the
    * LoadBalancer attempts to equally balance the traffic among its remaining usable Availability
    * Zones. Trying to remove an Availability Zone that was not associated with the LoadBalancer
    * does nothing.
    * 
    * <h4>Note</h4>
    * 
    * In order for this call to be successful, the api must have created the LoadBalancer. The
    * api must provide the same account credentials as those that were used to create the
    * LoadBalancer.
    * 
    * @param zones
    *           A list of Availability Zones to be removed from the LoadBalancer.
    * 
    *           <h4>Note</h4>
    * 
    *           There must be at least one Availability Zone registered with a LoadBalancer at all
    *           times. The api cannot remove all the Availability Zones from a LoadBalancer.
    *           Specified Availability Zones must be in the same Region.
    * 
    * 
    * @param loadBalancerName
    *           The name associated with the LoadBalancer. The name must be unique within the api
    *           AWS account.
    * 
    * @return A list of updated Availability Zones for the LoadBalancer.
    */
   @Named("DisableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "DisableAvailabilityZonesForLoadBalancer")
   Set<String> removeAvailabilityZonesFromLoadBalancer(
            @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) Iterable<String> zones,
            @FormParam("LoadBalancerName") String loadBalancerName);

   /**
    * @see AvailabilityZoneApi#removeAvailabilityZoneFromLoadBalancer
    */
   @Named("DisableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "DisableAvailabilityZonesForLoadBalancer")
   Set<String> removeAvailabilityZoneFromLoadBalancer(
            @FormParam("AvailabilityZones.member.1") String zone,
            @FormParam("LoadBalancerName") String loadBalancerName);
}
