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
package org.jclouds.azurecompute.features;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.binders.ServiceCertificateParamsToXML;
import org.jclouds.azurecompute.domain.ServiceCertificate;
import org.jclouds.azurecompute.domain.ServiceCertificateParams;
import org.jclouds.azurecompute.functions.ParseRequestIdHeader;
import org.jclouds.azurecompute.xml.ListServiceCertificatesHandler;
import org.jclouds.azurecompute.xml.ServiceCertificateHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * The Service Management API includes operations for managing service certificates in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee795178.aspx">docs</a>
 */
@Path("/services/hostedservices")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(APPLICATION_XML)
@Produces(APPLICATION_XML)
public interface ServiceCertificatesApi {

   /**
    * The List Service Certificates operation lists all of the service certificates associated with the specified cloud
    * service.
    *
    * @param service service name.
    * @return list of cloud service certificates.
    */
   @Named("ListServiceCertificates")
   @GET
   @Path("/{service}/certificates")
   @XMLResponseParser(ListServiceCertificatesHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<ServiceCertificate> list(@PathParam("service") String service);

   /**
    * The Get Service Certificate operation returns the public data for the specified X.509 certificate associated with
    * a cloud service.
    *
    * @param service service name.
    * @param thumbprintAlgorithm thumbprint algorithm.
    * @param thumbprintInHexadecimal thumbprint hexadecimal format.
    * @return service certificate including data only.
    */
   @Named("GetServiceCertificate")
   @GET
   @Path("/{service}/certificates/{thumbprintAlgorithm}-{thumbprintInHexadecimal}")
   @XMLResponseParser(ServiceCertificateHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ServiceCertificate get(
           @PathParam("service") String service,
           @PathParam("thumbprintAlgorithm") String thumbprintAlgorithm,
           @PathParam("thumbprintInHexadecimal") String thumbprintInHexadecimal);

   /**
    * The Delete Service Certificate asynchronous operation deletes a service certificate from the certificate store of
    * a cloud service.
    *
    * @param service service name.
    * @param thumbprintAlgorithm thumbprint algorithm.
    * @param thumbprintInHexadecimal thumbprint hexadecimal format.
    * @return request id.
    */
   @Named("DeleteServiceCertificate")
   @DELETE
   @Path("/{service}/certificates/{thumbprintAlgorithm}-{thumbprintInHexadecimal}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(
           @PathParam("service") String service,
           @PathParam("thumbprintAlgorithm") String thumbprintAlgorithm,
           @PathParam("thumbprintInHexadecimal") String thumbprintInHexadecimal);

   /**
    * The Add Service Certificate asynchronous operation adds a certificate to a cloud service.
    *
    * @param service service name.
    * @param params service certificate details to be sent as request body.
    * @return request id.
    */
   @Named("AddServiceCertificate")
   @POST
   @Path("/{service}/certificates")
   @ResponseParser(ParseRequestIdHeader.class)
   String add(
           @PathParam("service") String service,
           @BinderParam(ServiceCertificateParamsToXML.class) ServiceCertificateParams params);
}
