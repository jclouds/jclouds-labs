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
package org.jclouds.aliyun.ecs.features;

import org.jclouds.Constants;
import org.jclouds.Fallbacks;
import org.jclouds.aliyun.ecs.domain.Region;
import org.jclouds.aliyun.ecs.domain.Zone;
import org.jclouds.aliyun.ecs.filters.FormSign;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * https://www.alibabacloud.com/help/doc-detail/25609.htm?spm=a2c63.p38356.a1.4.7dd43c1aeoTmzO
 */
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(FormSign.class)
@QueryParams(keys = { "Version", "Format", "SignatureVersion", "ServiceCode", "SignatureMethod" },
        values = {"{" + Constants.PROPERTY_API_VERSION + "}", "JSON", "1.0", "ecs", "HMAC-SHA1"})
public interface RegionAndZoneApi {

   @Named("region:list")
   @GET
   @SelectJson("Region")
   @QueryParams(keys = "Action", values = "DescribeRegions")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Region> describeRegions();

   @Named("zone:list")
   @GET
   @SelectJson("Zone")
   @QueryParams(keys = "Action", values = "DescribeZones")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<Zone> describeZones(@QueryParam("RegionId") String region);
}
