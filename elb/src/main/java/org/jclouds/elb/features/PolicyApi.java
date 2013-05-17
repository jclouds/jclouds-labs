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
package org.jclouds.elb.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.elb.binders.BindPolicyTypeNamesToIndexedFormParams;
import org.jclouds.elb.domain.Policy;
import org.jclouds.elb.domain.PolicyType;
import org.jclouds.elb.options.ListPoliciesOptions;
import org.jclouds.elb.xml.DescribeLoadBalancerPoliciesResultHandler;
import org.jclouds.elb.xml.DescribeLoadBalancerPolicyTypesResultHandler;
import org.jclouds.elb.xml.PolicyHandler;
import org.jclouds.elb.xml.PolicyTypeHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference" >doc</a>
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface PolicyApi {
   
   /**
    * Retrieves information about the specified policy.
    * 
    * @param name
    *           Name of the policy to get information about.
    * @return null if not found
    */
   @Named("DescribeLoadBalancerPolicies")
   @POST
   @Path("/")
   @XMLResponseParser(PolicyHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicies")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Policy get(@FormParam("PolicyNames.member.1") String name);
   
   /**
    * returns descriptions of the specified sample policies, or descriptions of all the sample
    * policies.
    * 
    * @return the response object
    */
   @Named("DescribeLoadBalancerPolicies")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPoliciesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicies")
   Set<Policy> list();

   /**
    * Returns detailed descriptions of the policies.
    * 
    * If you specify a LoadBalancer name, the operation returns either the descriptions of the
    * specified policies, or descriptions of all the policies created for the LoadBalancer. If you
    * don't specify a LoadBalancer name, the operation returns descriptions of the specified sample
    * policies, or descriptions of all the sample policies. The names of the sample policies have
    * the ELBSample- prefix.
    * 
    * @param options
    *           the options describing the policies query
    * 
    * @return the response object
    */
   @Named("DescribeLoadBalancerPolicies")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPoliciesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicies")
   Set<Policy> list(ListPoliciesOptions options);

   /**
    * Retrieves information about the specified policy type.
    * 
    * @param name
    *           Name of the policy type to get information about.
    * @return null if not found
    */
   @Named("DescribeLoadBalancerPolicyTypes")
   @POST
   @Path("/")
   @XMLResponseParser(PolicyTypeHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicyTypes")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   PolicyType getType(@FormParam("PolicyTypeNames.member.1") String name);
   
   /**
    * Returns meta-information on the specified LoadBalancer policies defined by the Elastic Load
    * Balancing service. The policy types that are returned from this action can be used in a
    * CreateLoadBalancerPolicy action to instantiate specific policy configurations that will be
    * applied to an Elastic LoadBalancer.
    * 
    * @return the response object
    */
   @Named("DescribeLoadBalancerPolicyTypes")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPolicyTypesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicyTypes")
   Set<PolicyType> listTypes();

   /**
    * @param names Specifies the name of the policy types. If no names are specified, returns the description of all the policy types defined by Elastic Load Balancing service.
    * 
    * @see #listTypes()
    */
   @Named("DescribeLoadBalancerPolicyTypes")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPolicyTypesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicyTypes")
   Set<PolicyType> listTypes(@BinderParam(BindPolicyTypeNamesToIndexedFormParams.class) Iterable<String> names);
   
}
