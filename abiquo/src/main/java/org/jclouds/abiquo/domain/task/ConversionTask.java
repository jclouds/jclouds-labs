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

package org.jclouds.abiquo.domain.task;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.cloud.Conversion;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.appslibrary.ConversionDto;
import com.abiquo.server.core.task.TaskDto;

/**
 * Task that produces a {@link Conversion}.
 * 
 * @author Ignasi Barrera
 */
public class ConversionTask extends AsyncTask<Conversion, ConversionDto> {
   protected ConversionTask(final ApiContext<AbiquoApi> context, final TaskDto target) {
      super(context, target, Conversion.class, ConversionDto.class);
   }

   @Override
   public String toString() {
      return "Conversion" + super.toString();
   }

}
