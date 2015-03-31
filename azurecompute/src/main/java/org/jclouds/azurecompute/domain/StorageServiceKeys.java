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

import com.google.auto.value.AutoValue;
import java.net.URL;

@AutoValue
public abstract class StorageServiceKeys {

   public enum KeyType {

      Primary,
      Secondary;

   }

   StorageServiceKeys() {
   } // For AutoValue only!

   /**
    * The Service Management API request URI used to perform Get Storage Account Properties requests against the storage
    * account.
    */
   public abstract URL url();

   /**
    * The primary access key for the storage account.
    */
   public abstract String primary();

   /**
    * The secondary access key for the storage account.
    */
   public abstract String secondary();

   public static StorageServiceKeys create(final URL url, final String primary, final String secondary) {
      return new AutoValue_StorageServiceKeys(url, primary, secondary);
   }
}
