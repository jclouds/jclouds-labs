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
package org.jclouds.abiquo.domain.enterprise;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWithLimitsWrapper;
import org.jclouds.abiquo.domain.builder.LimitsBuilder;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.enterprise.DatacenterLimitsDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

/**
 * Adds high level functionality to {@link DatacenterLimitsDto}.
 * 
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/Datacenter+Limits+Resource"
 *      >
 *      http://community.abiquo.com/display/ABI20/Datacenter+Limits+Resource</a>
 */
public class Limits extends DomainWithLimitsWrapper<DatacenterLimitsDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected Limits(final ApiContext<AbiquoApi> context, final DatacenterLimitsDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Datacenter+Limits+Resource#DatacenterLimitsResource-UpdatesanexistingLimitforanenterpriseinadatacenter"
    *      >
    *      http://community.abiquo.com/display/ABI20/Datacenter+Limits+Resource
    *      #DatacenterLimitsResource
    *      -UpdatesanexistingLimitforanenterpriseinadatacenter</a>
    */
   public void update() {
      target = context.getApi().getEnterpriseApi().updateLimits(target);
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context, Datacenter datacenter) {
      return new Builder(context, datacenter);
   }

   public static class Builder extends LimitsBuilder<Builder> {
      private ApiContext<AbiquoApi> context;

      protected Long repositorySoft = Long.valueOf(DEFAULT_LIMITS);

      protected Long repositoryHard = Long.valueOf(DEFAULT_LIMITS);

      protected Datacenter datacenter;

      public Builder(final ApiContext<AbiquoApi> context, final Datacenter datacenter) {
         this.context = checkNotNull(context, "missing context object");
         this.datacenter = checkNotNull(datacenter, "missing datacenter object");
      }

      public Builder repositoryLimits(final long soft, final long hard) {
         this.repositorySoft = soft;
         this.repositoryHard = hard;
         return this;
      }

      public Limits build() {
         DatacenterLimitsDto dto = new DatacenterLimitsDto();
         dto.setRamLimitsInMb(ramSoftLimitInMb, ramHardLimitInMb);
         dto.setCpuCountLimits(cpuCountSoftLimit, cpuCountHardLimit);
         dto.setHdLimitsInMb(hdSoftLimitInMb, hdHardLimitInMb);
         dto.setStorageLimits(storageSoft, storageHard);
         dto.setVlansLimits(vlansSoft, vlansHard);
         dto.setPublicIPLimits(publicIpsSoft, publicIpsHard);
         dto.setRepositoryHardLimitsInMb(repositoryHard);
         dto.setRepositorySoftLimitsInMb(repositorySoft);

         // Establish the relation with the physical datacenter
         dto.addLink(new RESTLink(ParentLinkName.DATACENTER, checkNotNull(datacenter.unwrap().getEditLink(),
                 "missing edit link").getHref()));

         Limits limits = new Limits(context, dto);

         return limits;
      }

      public static Builder fromEnterprise(final Limits in) {
         return Limits.builder(in.context, in.getDatacenter())
               .ramLimits(in.getRamSoftLimitInMb(), in.getRamHardLimitInMb())
               .cpuCountLimits(in.getCpuCountSoftLimit(), in.getCpuCountHardLimit())
               .hdLimitsInMb(in.getHdSoftLimitInBytes(), in.getHdHardLimitInBytes())
               .storageLimits(in.getStorageSoft(), in.getStorageHard())
               .vlansLimits(in.getVlansSoft(), in.getVlansHard())
               .publicIpsLimits(in.getPublicIpsSoft(), in.getPublicIpsHard())
               .repositoryLimits(in.getRepositorySoft(), in.getRepositoryHard());
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public long getRepositoryHard() {
      return target.getRepositoryHardLimitsInMb();
   }

   public long getRepositorySoft() {
      return target.getRepositorySoftLimitsInMb();
   }

   public void setRepositoryHard(final long repositoryHard) {
      target.setRepositoryHardLimitsInMb(repositoryHard);
   }

   public void setRepositoryLimits(final long soft, final long hard) {
      target.setRepositoryHardLimitsInMb(hard);
      target.setRepositorySoftLimitsInMb(soft);
   }

   public void setRepositorySoft(final long repositorySoft) {
      target.setRepositorySoftLimitsInMb(repositorySoft);
   }

   public Datacenter getDatacenter() {
      Integer datacenterId = target.getIdFromLink(ParentLinkName.DATACENTER);
      checkNotNull(datacenterId, ValidationErrors.MISSING_REQUIRED_LINK);
      DatacenterDto dto = context.getApi().getInfrastructureApi().getDatacenter(datacenterId);
      return wrap(context, Datacenter.class, dto);
   }

   @Override
   public String toString() {
      return "Limits [id=" + getId() + ", repositoryHard=" + getRepositoryHard() + ", repositorySoft="
            + getRepositorySoft() + ", cpuCountHard=" + getCpuCountHardLimit() + ", cpuCountSoft="
            + getCpuCountSoftLimit() + ", hdHardInBytes=" + getHdHardLimitInBytes() + ", hdSoftInBytes=" + getHdSoftLimitInBytes()
            + ", publicIPsHard=" + getPublicIpsHard() + ", publicIpsSoft=" + getPublicIpsSoft() + ", ramHardInMB="
            + getRamHardLimitInMb() + ", ramSoftInMB=" + getRamSoftLimitInMb() + ", storageHard=" + getStorageHard()
            + ", storageSoft=" + getStorageSoft() + ", vlansHard=" + getVlansHard() + ", vlansSoft=" + getVlansSoft()
            + "]";
   }

}
