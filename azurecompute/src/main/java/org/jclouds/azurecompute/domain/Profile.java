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
import java.util.Map;
import org.jclouds.javax.annotation.Nullable;

/**
 * Traffic Maager profile.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758255.aspx" >Traffic Manager operations</a>
 */
@AutoValue
public abstract class Profile {

   Profile() {
   } // For AutoValue only!

   /**
    * Specifies the domain name, as specified during the creation of the profile.
    *
    * @return domain nae.
    */
   public abstract String domain();

   /**
    * Specifies the profile name.
    *
    * @return profile name.
    */
   public abstract String name();

   /**
    * Indicates whether a definition of the specified profile is enabled or disabled in Azure Traffic Manager. Possible
    * values are: Enabled, Disabled.
    *
    * @return profile definition status.
    */
   public abstract ProfileDefinition.Status status();

   /**
    * Specifies the version of the enabled definition. This value is always 1.
    *
    * @return version.
    */
   @Nullable
   public abstract String version();

   /**
    * Specifies the definition for the specified profile, along with the status. Only one definition version exists for
    * a profile.
    *
    * @return profile definitions in terms of version-status pairs;
    */
   public abstract Map<String, ProfileDefinition.Status> definitions();

   public static Profile create(
           final String domain,
           final String name,
           final ProfileDefinition.Status status,
           final String version,
           final Map<String, ProfileDefinition.Status> definitions) {

      return new AutoValue_Profile(domain, name, status, version, definitions);
   }
}
