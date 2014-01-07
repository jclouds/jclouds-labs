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

   String USER_METADATA_PREFIX = "X-Object-Meta-"; 
	
   String ACCOUNT_TEMPORARY_URL_KEY = "X-Account-Meta-Temp-Url-Key";
   String ACCOUNT_BYTES_USED = "X-Account-Bytes-Used";
   String ACCOUNT_CONTAINER_COUNT = "X-Account-Container-Count";

   String CONTAINER_BYTES_USED = "X-Container-Bytes-Used";
   String CONTAINER_OBJECT_COUNT = "X-Container-Object-Count";
   String CONTAINER_METADATA_PREFIX = "X-Container-Meta-";
   String CONTAINER_DELETE_METADATA_PREFIX = "X-Remove-Container-Meta-";

   String CONTAINER_READ = "X-Container-Read";
   String CONTAINER_WRITE = "X-Container-Write";
   
   String CONTAINER_WEB_INDEX = "X-Container-Meta-Web-Index"; 
   String CONTAINER_WEB_ERROR = "X-Container-Meta-Web-Error"; 
   String CONTAINER_WEB_LISTINGS = "X-Container-Meta-Web-Listings";
   String CONTAINER_WEB_LISTINGS_CSS = "X-Container-Meta-Web-Listings-CSS";    
   
   String OBJECT_COPY_FROM = "X-Copy-From";
   String OBJECT_DELETE_AFTER = "X-Delete-After";
   String OBJECT_DELETE_AT = "X-Delete-At";
   /** Get the newest version of the object for GET and HEAD requests */
   String OBJECT_NEWEST = "X-Newest";
   String OBJECT_VERSIONS_LOCATION = "X-Versions-Location";
}
