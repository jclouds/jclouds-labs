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
package org.jclouds.abiquo.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.network.NicDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.google.common.base.Predicate;

/**
 * Container for {@link RESTLink} filters.
 */
public class LinkPredicates {

   private static final Pattern IS_NIC_REL_PATTERN = Pattern.compile("^" + NicDto.REL_PREFIX + "[0-9]+$");
   private static final Pattern IS_DISK_REL_PATTERN = Pattern.compile("^" + VolumeManagementDto.REL_PREFIX + "[0-9]+$");

   public static Predicate<RESTLink> rel(final String rel) {
      checkNotNull(rel, "rel must be defined");
      return new Predicate<RESTLink>() {
         @Override
         public boolean apply(final RESTLink link) {
            return link.getRel().equals(rel);
         }
      };
   }

   public static Predicate<RESTLink> isNic() {
      return new Predicate<RESTLink>() {
         @Override
         public boolean apply(final RESTLink link) {
            return IS_NIC_REL_PATTERN.matcher(link.getRel()).matches();
         }
      };
   }

   public static Predicate<RESTLink> isDisk() {
      return new Predicate<RESTLink>() {
         @Override
         public boolean apply(RESTLink link) {
            return IS_DISK_REL_PATTERN.matcher(link.getRel()).matches();
         }
      };
   }

}
