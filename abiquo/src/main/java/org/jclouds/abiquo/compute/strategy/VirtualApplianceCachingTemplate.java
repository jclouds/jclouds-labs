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
package org.jclouds.abiquo.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.domain.cloud.VirtualAppliance;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;

import com.google.common.base.Objects;

/**
 * A {@link Template} implementation that caches the {@link VirtualAppliance}
 * and the {@link VirtualDatacenter} where the nodes will be deployed.
 * <p>
 * When deploying multiple nodes at the same time, all go to the same virtual
 * appliance and virtual datacenter. Having both cached in the template saves a
 * couple extra api calls for each deployed node.
 * <p>
 * This class is not public as it is intended to be used internally.
 * 
 * 
 * @see CreateGroupBeforeCreatingNodes
 * @see AbiquoComputeServiceAdapter
 */
class VirtualApplianceCachingTemplate implements Template {

   private final Template delegate;
   private final VirtualDatacenter virtualDatacenter;
   private final VirtualAppliance virtualAppliance;

   private VirtualApplianceCachingTemplate(Template delegate, VirtualDatacenter virtualDatacenter,
         VirtualAppliance virtualAppliance) {
      this.delegate = checkNotNull(delegate, "delegate");
      this.virtualDatacenter = checkNotNull(virtualDatacenter, "virtualDatacenter");
      this.virtualAppliance = checkNotNull(virtualAppliance, "virtualAppliance");
   }

   public VirtualDatacenter getVirtualDatacenter() {
      return virtualDatacenter;
   }

   public VirtualAppliance getVirtualAppliance() {
      return virtualAppliance;
   }

   // Delegate methods

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
   public VirtualApplianceCachingTemplate clone() {
      return new VirtualApplianceCachingTemplate(delegate.clone(), virtualDatacenter, virtualAppliance);
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      VirtualApplianceCachingTemplate that = VirtualApplianceCachingTemplate.class.cast(o);
      return delegate.equals(that) && virtualAppliance.getId().equals(that.virtualAppliance.getId())
            && virtualDatacenter.getId().equals(that.virtualDatacenter.getId());
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(getImage(), getHardware(), getLocation(), getOptions(), virtualDatacenter,
            virtualAppliance);
   }

   static Builder from(Template template) {
      return new Builder(template);
   }

   static class Builder {
      private Template template;
      private VirtualDatacenter virtualDatacenter;
      private VirtualAppliance virtualAppliance;

      public Builder(Template template) {
         this.template = template;
      }

      public Builder withVirtualDatacenter(VirtualDatacenter virtualDatacenter) {
         this.virtualDatacenter = virtualDatacenter;
         return this;
      }

      public Builder withVirtualAppliance(VirtualAppliance virtualAppliance) {
         this.virtualAppliance = virtualAppliance;
         return this;
      }

      public VirtualApplianceCachingTemplate build() {
         return new VirtualApplianceCachingTemplate(template, virtualDatacenter, virtualAppliance);
      }
   }

}
