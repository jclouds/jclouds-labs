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
package org.jclouds.snia.cdmi.v1.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.snia.cdmi.v1.binders.BindQueryParmsToSuffix;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;
import org.jclouds.snia.cdmi.v1.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;

/** Data Object Resource Operations */
@RequestFilters({ BasicAuthenticationAndTenantId.class, StripExtraAcceptHeader.class })
public interface DataNonCDMIContentTypeApi {
   /**
    * get CDMI Data object
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @return DataObjectNonCDMIContentType
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject");
    *  dataObject = get("parentContainer/childContainer/","myDataObject");
    * }
    * 
    * <pre>
    */
   @GET
   @Consumes
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   Payload getValue(@PathParam("dataObjectName") String dataObjectName);

   /**
    * get CDMI Data object
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param range
    *           a valid ranges-specifier (see RFC2616 Section 14.35.1)
    * @return DataObjectNonCDMIContentType
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject","bytes=0-10");
    * }
    * 
    *         <pre>
    */
   @GET
   @Consumes
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   Payload getValue(@PathParam("dataObjectName") String dataObjectName, @HeaderParam("Range") String range);

   /**
    * get CDMI Data object
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param queryParams
    *           enables getting only certain fields, metadata, value range
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject",ContainerQueryParams.Builder.field("parentURI").field("objectName"));
    *  dataObject = get("myDataObject",ContainerQueryParams.Builder.value(0,10));
    * }
    * 
    *         <pre>
    */
   @GET
   @Consumes(APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   DataObject get(@PathParam("dataObjectName") String dataObjectName,
         @BinderParam(BindQueryParmsToSuffix.class) DataObjectQueryParams queryParams);

   /**
    * create CDMI Data object Non CDMI Content Type
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param payload
    *           enables defining the body's payload i.e. file, inputStream, String, ByteArray
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  create("myDataObject",new StringPayload("value");
    *  create("myDataObject",new ByteArrayPayload(bytes);
    *  create("myDataObject",new FilePayload(myFileIn);
    *  create("myDataObject",new InputStreamPayload(is);
    *  
    *  File f = new File("yellow-flowers.jpg");
    *  payloadIn = new InputStreamPayload(new FileInputStream(f));
    *  payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(
    *            payloadIn.getContentMetadata().toBuilder()
    *            .contentType(MediaType.JPEG.toString())
    *            .contentLength(new Long(inFile.length()))
    *            .build()));
    *  dataNonCDMIContentTypeApi.create(containerName, f.getName(),
    * 					payloadIn);
    * }
    * 
    *           <pre>
    */
   @PUT
   @Consumes
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   void create(@PathParam("dataObjectName") String dataObjectName, Payload payload);

   /**
    * create CDMI Data object partial Non CDMI Content Type Only part of the object is contained in
    * the payload and the X-CDMI-Partial header flag is set to true
    * 
    * 
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param payload
    *           enables defining the body's payload i.e. file, inputStream, String, ByteArray
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  createPartial("myDataObject",new StringPayload("value");
    *  createPartial("myDataObject",new ByteArrayPayload(bytes);
    *  createPartial("myDataObject",new FilePayload(myFileIn);
    *  createPartial("myDataObject",new InputStreamPayload(is);
    * }
    * 
    *           <pre>
    */
   @PUT
   @Consumes
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   @Headers(keys = "X-CDMI-Partial", values = "true")
   void createPartial(@PathParam("dataObjectName") String dataObjectName, Payload payload);

   /**
    * create CDMI Data object Non CDMI Content Type
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param input
    *           simple string input
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  create("myDataObject",new String("value");
    * }
    * 
    *           <pre>
    */
   @PUT
   @Consumes
   @Produces(TEXT_PLAIN)
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   @org.jclouds.rest.annotations.Payload("{input}")
   void create(@PathParam("dataObjectName") String dataObjectName, @PayloadParam("input") String input);

   /**
    * delete CDMI Data object
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * 
    *           <pre>
    *  Examples: 
    *  {@code
    *  delete("myDataObject");
    * }
    * 
    *           <pre>
    */
   @DELETE
   @Consumes
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   void delete(@PathParam("dataObjectName") String dataObjectName);
}
