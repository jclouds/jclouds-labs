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
package org.jclouds.azurecompute.domain;

import java.net.URI;
import java.util.List;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * OS image from the image repository
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 */
@AutoValue
public abstract class OSImage {
   public enum Type {
      LINUX, WINDOWS;
   }

   public abstract String name();

   /** The geo-locations of the image, if the image is not associated with an affinity group. */
   @Nullable public abstract String location();

   /** The affinity group with which this image is associated, if any. */
   @Nullable public abstract String affinityGroup();

   /**
    * The name can be up to 100 characters in length. The name can be used identify the storage account for your
    * tracking purposes.
    */
   public abstract String label();

   @Nullable public abstract String description();

   /**
    * The repository classification of image. All user images have the category "User", but
    * categories for other images could be, for example "Canonical"
    */
   @Nullable public abstract String category();

   /** The operating system type of the OS image. */
   public abstract Type os();


   //The name of the publisher of the image. All user images have a publisher name of User.
   @Nullable public abstract String publisherName();
   /**
    * The locations of the blob in the blob store in which the media for the image is located. The
    * blob locations belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    *
    * Example:
    *
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   @Nullable public abstract URI mediaLink();

   public abstract int logicalSizeInGB();

   /** The eulas for the image, if available. */
   // Not URI as some providers put non-uri data in, such as riverbed.
   public abstract List<String> eula();


   public static OSImage create(String name, String location, String affinityGroup, String label,
         String description, String category, Type os, String publisherName, URI mediaLink, int logicalSizeInGB, List<String> eula) {
      return new AutoValue_OSImage(name, location, affinityGroup, label, description, category, os, publisherName, mediaLink,
            logicalSizeInGB, eula);
   }
}
