/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.codec;

import com.google.common.collect.ImmutableSet;
import org.jclouds.apis.Apis;
import org.jclouds.representations.ApiMetadata;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.collect.Iterables.transform;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test
public class ToApiMetadataTest {

   @Test
   void testConversion() {
      assertNull(ToApiMetadata.INSTANCE.apply(null));
      org.jclouds.apis.ApiMetadata stub = Apis.withId("stub");
      assertNotNull(stub);
      ApiMetadata dto = ToApiMetadata.INSTANCE.apply(stub);
      assertNotNull(dto);
      assertEquals("stub", dto.getId());
   }

   @Test
   void testIterableTransformation() {
      Set<ApiMetadata> representations = ImmutableSet.<ApiMetadata>builder()
                                          .addAll(transform(Apis.all(), ToApiMetadata.INSTANCE))
                                          .build();
      assertFalse(representations.isEmpty());
   }
}
