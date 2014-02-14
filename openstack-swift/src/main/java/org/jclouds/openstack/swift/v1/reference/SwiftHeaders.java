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
package org.jclouds.openstack.swift.v1.reference;

/**
 * Common headers in Swift.
 * 
 * @author Jeremy Daggett
 */
public interface SwiftHeaders {
   
   // Common Metadata Prefixes
   String ACCOUNT_METADATA_PREFIX = "X-Account-Meta-";
   String CONTAINER_METADATA_PREFIX = "X-Container-Meta-";
   String OBJECT_METADATA_PREFIX = "X-Object-Meta-";
   String USER_METADATA_PREFIX = OBJECT_METADATA_PREFIX;
   
   // Metadata Removal Prefixes
   String ACCOUNT_REMOVE_METADATA_PREFIX = "X-Remove-Account-Meta-";
   String CONTAINER_REMOVE_METADATA_PREFIX = "X-Remove-Container-Meta-";
   String OBJECT_REMOVE_METADATA_PREFIX = "X-Remove-Object-Meta-";
   
   // TempURL
   String ACCOUNT_TEMPORARY_URL_KEY = ACCOUNT_METADATA_PREFIX + "Temp-Url-Key";
   String ACCOUNT_TEMPORARY_URL_KEY_2 = ACCOUNT_TEMPORARY_URL_KEY + "-2";

   // Account Headers
   String ACCOUNT_BYTES_USED = "X-Account-Bytes-Used";
   String ACCOUNT_CONTAINER_COUNT = "X-Account-Container-Count";
   String ACCOUNT_OBJECT_COUNT = "X-Account-Object-Count";

   // Container Headers
   String CONTAINER_BYTES_USED = "X-Container-Bytes-Used";
   String CONTAINER_OBJECT_COUNT = "X-Container-Object-Count";

   // Public access - not supported in all Swift Impls
   String CONTAINER_READ = "X-Container-Read";
   String CONTAINER_WRITE = "X-Container-Write";
   String CONTAINER_ACL_ANYBODY_READ = ".r:*,.rlistings";
   
   // CORS
   String CONTAINER_ACCESS_CONTROL_ALLOW_ORIGIN = CONTAINER_METADATA_PREFIX + "Access-Control-Allow-Origin";
   String CONTAINER_ACCESS_CONTROL_MAX_AGE = CONTAINER_METADATA_PREFIX + "Access-Control-Max-Age";
   String CONTAINER_ACCESS_CONTROL_EXPOSE_HEADERS = CONTAINER_METADATA_PREFIX + "Access-Control-Expose-Headers";

   // Container Quota
   String CONTAINER_QUOTA_BYTES = CONTAINER_METADATA_PREFIX + "Quota-Bytes";
   String CONTAINER_QUOTA_COUNT = CONTAINER_METADATA_PREFIX + "Quota-Count";

   // Container Sync
   String CONTAINER_SYNC_KEY = "X-Container-Sync-Key";
   String CONTAINER_SYNC_TO = "X-Container-Sync-To";

   // Versioning
   String CONTAINER_VERSIONS_LOCATION = "X-Versions-Location";

   // Misc functionality
   String CONTAINER_WEB_MODE = "X-Web-Mode";

   String OBJECT_COPY_FROM = "X-Copy-From";
   String OBJECT_DELETE_AFTER = "X-Delete-After";
   String OBJECT_DELETE_AT = "X-Delete-At";
   String OBJECT_MANIFEST = "X-Object-Manifest";  
   /** Get the newest version of the object for GET and HEAD requests */
   String OBJECT_NEWEST = "X-Newest";

   // Static Large Object
   String STATIC_LARGE_OBJECT = "X-Static-Large-Object";
}
