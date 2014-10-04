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
import static org.jclouds.Fallbacks.NullOnNotFoundOr404;
import static org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import static org.jclouds.snia.cdmi.v1.ObjectTypes.DATAOBJECT;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.snia.cdmi.v1.binders.BindQueryParmsToSuffix;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;

/** Data Object Resource Operations */
@RequestFilters({ BasicAuthenticationAndTenantId.class, StripExtraAcceptHeader.class })
@Headers(keys = "X-CDMI-Specification-Version", values = "{jclouds.api-version}")
public interface DataApi {
   /**
    * get CDMI Data object
    * 
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = get("myDataObject");
    *  dataObject = get("parentContainer/childContainer","myDataObject");
    * }
    * 
    *         <pre>
    */
   @GET
   @Consumes({ DATAOBJECT, APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   DataObject get(@PathParam("dataObjectName") String dataObjectName);

   /**
    * get CDMI Data object
    * 
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
   @Consumes({ DATAOBJECT, APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   DataObject get(@PathParam("dataObjectName") String dataObjectName,
         @BinderParam(BindQueryParmsToSuffix.class) DataObjectQueryParams queryParams);

   /**
    * create CDMI Data object
    * 
    * 
    * @param dataObjectName
    *           dataObjectName must not end with a forward slash, /.
    * @param options
    *           enables defining the body i.e. metadata, mimetype, value
    * @return DataObject
    * 
    *         <pre>
    *  Examples: 
    *  {@code
    *  dataObject = create(
    *                                "myDataObject",
    *                                CreateDataObjectOptions.Builder
    *                                                    .value(value)
    *                                                    .mimetype("text/plain")
    *                                                    .metadata(pDataObjectMetaDataIn);
    * }
    * 
    *         <pre>
    */
   @PUT
   @Consumes({ DATAOBJECT, APPLICATION_JSON })
   @Produces({ DATAOBJECT })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   DataObject create(@PathParam("dataObjectName") String dataObjectName,
         CreateDataObjectOptions... options);

   /**
    * delete CDMI Data object
    * 
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
   @Consumes(TEXT_PLAIN)
   // note: MediaType.APPLICATION_JSON work also, however without consumes
   // jclouds throws null exception
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   void delete(@PathParam("dataObjectName") String dataObjectName);
}
