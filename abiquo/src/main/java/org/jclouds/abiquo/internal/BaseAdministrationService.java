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
package org.jclouds.abiquo.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getFirst;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.PaginatedCollection;
import org.jclouds.abiquo.domain.config.Category;
import org.jclouds.abiquo.domain.config.License;
import org.jclouds.abiquo.domain.config.Privilege;
import org.jclouds.abiquo.domain.config.SystemProperty;
import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.EnterpriseProperties;
import org.jclouds.abiquo.domain.enterprise.Role;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.Machine;
import org.jclouds.abiquo.features.services.AdministrationService;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.strategy.infrastructure.ListMachines;
import org.jclouds.collect.Memoized;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.ApiContext;

import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.config.LicensesDto;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.EnterprisePropertiesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.abiquo.server.core.enterprise.RoleDto;
import com.abiquo.server.core.enterprise.RolesDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;

/**
 * Provides high level Abiquo administration operations.
 */
@Singleton
public class BaseAdministrationService implements AdministrationService {
   @VisibleForTesting
   protected ApiContext<AbiquoApi> context;

   @VisibleForTesting
   protected final ListMachines listMachines;

   @VisibleForTesting
   protected final Supplier<User> currentUser;

   @VisibleForTesting
   protected final Supplier<Enterprise> currentEnterprise;

   @Inject
   protected BaseAdministrationService(final ApiContext<AbiquoApi> context, final ListMachines listMachines,
         @Memoized final Supplier<User> currentUser, @Memoized final Supplier<Enterprise> currentEnterprise) {
      this.context = checkNotNull(context, "context");
      this.listMachines = checkNotNull(listMachines, "listMachines");
      this.currentUser = checkNotNull(currentUser, "currentUser");
      this.currentEnterprise = checkNotNull(currentEnterprise, "currentEnterprise");
   }

   /*********************** Datacenter ********************** */

   @Override
   public Iterable<Datacenter> listDatacenters() {
      DatacentersDto result = context.getApi().getInfrastructureApi().listDatacenters();
      return wrap(context, Datacenter.class, result.getCollection());
   }

   @Override
   public Datacenter getDatacenter(final Integer datacenterId) {
      DatacenterDto datacenter = context.getApi().getInfrastructureApi().getDatacenter(datacenterId);
      return wrap(context, Datacenter.class, datacenter);
   }

   /*********************** Machine ***********************/

   @Override
   public Iterable<Machine> listMachines() {
      return listMachines.execute();
   }

   /*********************** Enterprise ***********************/

   @Override
   public Iterable<Enterprise> listEnterprises() {
      PagedIterable<EnterpriseDto> result = context.getApi().getEnterpriseApi().listEnterprises();
      return wrap(context, Enterprise.class, result.concat());
   }

   @Override
   public Iterable<Enterprise> listEnterprises(EnterpriseOptions options) {
      PaginatedCollection<EnterpriseDto, EnterprisesDto> result = context.getApi().getEnterpriseApi()
            .listEnterprises(options);
      return wrap(context, Enterprise.class, result.toPagedIterable().concat());
   }

   @Override
   public Enterprise getEnterprise(final Integer enterpriseId) {
      EnterpriseDto enterprise = context.getApi().getEnterpriseApi().getEnterprise(enterpriseId);
      return wrap(context, Enterprise.class, enterprise);
   }

   /*********************** Enterprise Properties ***********************/

   @Override
   public EnterpriseProperties getEnterpriseProperties(final Enterprise enterprise) {
      checkNotNull(enterprise.getId(), ValidationErrors.MISSING_REQUIRED_FIELD + " id in " + Enterprise.class);

      EnterprisePropertiesDto properties = context.getApi().getEnterpriseApi()
            .getEnterpriseProperties(enterprise.unwrap());
      return wrap(context, EnterpriseProperties.class, properties);
   }

   /*********************** Role ********************** */

   @Override
   public Iterable<Role> listRoles() {
      RolesDto result = context.getApi().getAdminApi().listRoles();
      return wrap(context, Role.class, result.getCollection());
   }

   @Override
   public Role getRole(final Integer roleId) {
      RoleDto role = context.getApi().getAdminApi().getRole(roleId);
      return wrap(context, Role.class, role);
   }

   /*********************** Privilege ***********************/

   @Override
   public Iterable<Privilege> listPrivileges() {
      PrivilegesDto result = context.getApi().getConfigApi().listPrivileges();
      return wrap(context, Privilege.class, result.getCollection());
   }

   @Override
   public Privilege getPrivilege(Integer privilegeId) {
      PrivilegeDto result = context.getApi().getConfigApi().getPrivilege(privilegeId);
      return wrap(context, Privilege.class, result);
   }

   /*********************** User ***********************/

   @Override
   public User getCurrentUser() {
      return currentUser.get();
   }

   @Override
   public Enterprise getCurrentEnterprise() {
      return currentEnterprise.get();
   }

   /*********************** License ***********************/

   @Override
   public Iterable<License> listLicenses() {
      LicensesDto result = context.getApi().getConfigApi().listLicenses();
      return wrap(context, License.class, result.getCollection());
   }

   @Override
   public Iterable<License> listLicenses(final boolean active) {
      LicenseOptions options = LicenseOptions.builder().active(active).build();
      LicensesDto result = context.getApi().getConfigApi().listLicenses(options);
      return wrap(context, License.class, result.getCollection());
   }

   /*********************** System Properties ***********************/

   @Override
   public Iterable<SystemProperty> listSystemProperties() {
      SystemPropertiesDto result = context.getApi().getConfigApi().listSystemProperties();
      return wrap(context, SystemProperty.class, result.getCollection());
   }

   @Override
   public SystemProperty getSystemProperty(final String name) {
      PropertyOptions options = PropertyOptions.builder().name(name).build();
      SystemPropertiesDto result = context.getApi().getConfigApi().listSystemProperties(options);
      return getFirst(wrap(context, SystemProperty.class, result.getCollection()), null);
   }

   @Override
   public Iterable<SystemProperty> listSystemProperties(final String component) {
      PropertyOptions options = PropertyOptions.builder().component(component).build();
      SystemPropertiesDto result = context.getApi().getConfigApi().listSystemProperties(options);
      return wrap(context, SystemProperty.class, result.getCollection());
   }

   @Override
   public Iterable<Category> listCategories() {
      CategoriesDto result = context.getApi().getConfigApi().listCategories();
      return wrap(context, Category.class, result.getCollection());
   }

   @Override
   public Category getCategory(Integer categoryId) {
      CategoryDto result = context.getApi().getConfigApi().getCategory(categoryId);
      return wrap(context, Category.class, result);
   }
}
