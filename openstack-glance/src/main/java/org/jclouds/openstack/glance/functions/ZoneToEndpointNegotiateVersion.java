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

package org.jclouds.openstack.glance.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.location.Zone;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.HttpClient;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

@Singleton
public class ZoneToEndpointNegotiateVersion implements Function<Object, URI> {

   public static final String VERSION_NEGOTIATION_HEADER = "Is-Version-Negotiation-Request";

   private static final Pattern versionRegex = Pattern.compile("v[0-9]+(\\.[0-9])?[0-9]*");

   private static class VersionsJsonResponse{
      public static class Version {
         public static class Link {
            public String href;
            public String rel;
         }
         public String status;
         public String id;
         public List<Link> links;
      }
      public List<Version> versions;
   }

   private final Supplier<Map<String, Supplier<URI>>> zoneToEndpointSupplier;
   private final String apiVersion;
   private final LoadingCache<URI, URI> endpointCache;

   @Inject
   public ZoneToEndpointNegotiateVersion(@Zone Supplier<Map<String, Supplier<URI>>> zoneToEndpointSupplier,
         @ApiVersion String rawApiVersionString, final HttpClient client, final Json json) {
      this.zoneToEndpointSupplier = checkNotNull(zoneToEndpointSupplier, "zoneToEndpointSupplier");
      if (!rawApiVersionString.startsWith("v")) {
         this.apiVersion = "v" + rawApiVersionString;
      } else {
         this.apiVersion = rawApiVersionString;
      }
      this.endpointCache = CacheBuilder.newBuilder()
         .build(
            new CacheLoader<URI, URI>() {
               public URI load(URI baseEndpointUri) {
                  try {
                     List<String> baseEndpointPathParts = Splitter.on('/').omitEmptyStrings().splitToList(baseEndpointUri.getPath());
                     if (!baseEndpointPathParts.isEmpty()
                           && versionRegex.matcher(baseEndpointPathParts.get(baseEndpointPathParts.size() - 1)).matches()) {
                        // Constructs a base URI Glance endpoint by stripping the version from the received URI
                        baseEndpointUri = new URI(baseEndpointUri.getScheme(), baseEndpointUri.getUserInfo(),
                           baseEndpointUri.getHost(), baseEndpointUri.getPort(),
                           Joiner.on('/').join(baseEndpointPathParts.subList(0, baseEndpointPathParts.size() - 1)) + "/",
                           baseEndpointUri.getQuery(), baseEndpointUri.getFragment());
                     }

                     HttpRequest negotiationRequest = HttpRequest.builder()
                        .method("GET").endpoint(baseEndpointUri)
                        .addHeader(VERSION_NEGOTIATION_HEADER, "true").build();
                     InputStream response = client.invoke(negotiationRequest).getPayload().openStream();
                     VersionsJsonResponse versions = json.fromJson(Strings2.toStringAndClose(response), VersionsJsonResponse.class);
                     for (VersionsJsonResponse.Version version : versions.versions) {
                        if (apiVersion.equals(version.id)) {
                           // We only expect one element here, we'll get an exception here if that changes
                           URI versionedEndpointUri = new URI(Iterables.getOnlyElement(version.links).href);
                           return new URI(baseEndpointUri.getScheme(), versionedEndpointUri.getUserInfo(),
                              versionedEndpointUri.getHost(), versionedEndpointUri.getPort(),
                              versionedEndpointUri.getPath(), versionedEndpointUri.getQuery(),
                              versionedEndpointUri.getFragment());
                        }
                     }
                  } catch (URISyntaxException ex) {
                     throw Throwables.propagate(ex);
                  } catch (IOException ex) {
                     throw Throwables.propagate(ex);
                  }
                  throw new UnsupportedOperationException("Glance endpoint does not support API version: " + apiVersion);
              }
         });
   }

   @Override
   public URI apply(Object from) {
      checkArgument(from instanceof String, "you must specify a zone, as a String argument");
      Map<String, Supplier<URI>> zoneToEndpoint = zoneToEndpointSupplier.get();
      checkState(!zoneToEndpoint.isEmpty(), "no zone name to endpoint mappings configured!");
      checkArgument(zoneToEndpoint.containsKey(from),
               "requested location %s, which is not in the configured locations: %s", from, zoneToEndpoint);
      URI uri = zoneToEndpointSupplier.get().get(from).get();

      try {
         return endpointCache.get(uri);
      } catch (ExecutionException ex) {
         throw Throwables.propagate(ex);
      }
    }
}
