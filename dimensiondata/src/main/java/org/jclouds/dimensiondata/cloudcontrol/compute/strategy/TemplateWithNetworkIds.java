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
package org.jclouds.dimensiondata.cloudcontrol.compute.strategy;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

/**
 * Extend the default {@link Template} object with extra identifier information about where the nodes must be created.
 */
public class TemplateWithNetworkIds implements Template {

   private final Template delegate;
   private final String networkDomainId;
   private final String vlanId;

   public TemplateWithNetworkIds(Template template, String networkDomainId, String vlanId) {
      this.delegate = template;
      this.networkDomainId = networkDomainId;
      this.vlanId = vlanId;
   }

   @Override
   public Image getImage() {
      return delegate.getImage();
   }

   @Override
   public Hardware getHardware() {
      return delegate.getHardware();
   }

   @Override
   public Location getLocation() {
      return delegate.getLocation();
   }

   @Override
   public TemplateOptions getOptions() {
      return delegate.getOptions();
   }

   @Override
   public Template clone() {
      return new TemplateWithNetworkIds(delegate.clone(), networkDomainId, vlanId);
   }
}
