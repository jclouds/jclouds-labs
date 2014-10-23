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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A vApp can be in one of these states:
 * <ul>
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#FAILED_CREATION
 * FAILED_CREATION(-1)} - Transient entity state, e.g., model object is addd but the
 * corresponding VC backing does not exist yet. This is further sub-categorized in the respective
 * entities.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#UNRESOLVED
 * UNRESOLVED(0)} - Entity is whole, e.g., VM creation is complete and all the required model
 * objects and VC backings are created.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#RESOLVED
 * RESOLVED(1)} - Entity is resolved.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#DEPLOYED
 * DEPLOYED(2)} - Entity is deployed.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#SUSPENDED
 * SUSPENDED(3)} - All VMs of the vApp are suspended.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#POWERED_ON
 * POWERED_ON(4)} - All VMs of the vApp are powered on.
 * <li>
 * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#WAITING_FOR_INPUT
 * WAITING_FOR_INPUT(5)} - VM is pending response on a question.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#UNKNOWN
 * UNKNOWN(6)} - Entity state could not be retrieved from the inventory, e.g., VM power state is
 * null.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#UNRECOGNIZED
 * UNRECOGNIZED(7)} - Entity state was retrieved from the inventory but could not be mapped to an
 * internal state.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#POWERED_OFF
 * POWERED_OFF(8)} - All VMs of the vApp are powered off.
 * <li>
 * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#INCONSISTENT_STATE
 * INCONSISTENT_STATE(9)} - Apply to VM status, if a vm is {@code POWERED_ON}, or
 * {@code WAITING_FOR_INPUT}, but is undeployed, it is in an inconsistent state.
 * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntity.Status#MIXED MIXED(10)}
 * - vApp status is set to {@code MIXED} when the VMs in the vApp are in different power states
 * </ul>
 * <pre>
 * &lt;complexType name="VApp" /&gt;
 * </pre>
 */
@XmlRootElement(name = "VApp")
@XmlType(name = "VAppType")
public class VApp extends AbstractVApp {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromVApp(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends AbstractVApp.Builder<B> {

      private Owner owner;
      private Boolean inMaintenanceMode;
      private VAppChildren children;
      private Boolean ovfDescriptorUploaded;

      /**
       * @see VApp#getOwner()
       */
      public B owner(Owner owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public B isInMaintenanceMode(Boolean inMaintenanceMode) {
         this.inMaintenanceMode = inMaintenanceMode;
         return self();
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public B inMaintenanceMode() {
         this.inMaintenanceMode = Boolean.TRUE;
         return self();
      }

      /**
       * @see VApp#isInMaintenanceMode()
       */
      public B notInMaintenanceMode() {
         this.inMaintenanceMode = Boolean.FALSE;
         return self();
      }

      /**
       * @see VApp#getChildren()
       */
      public B children(VAppChildren children) {
         this.children = children;
         return self();
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public B isOvfDescriptorUploaded(Boolean ovfDescriptorUploaded) {
         this.ovfDescriptorUploaded = ovfDescriptorUploaded;
         return self();
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public B ovfDescriptorUploaded() {
         this.ovfDescriptorUploaded = Boolean.TRUE;
         return self();
      }

      /**
       * @see VApp#isOvfDescriptorUploaded()
       */
      public B ovfDescriptorNotUploaded() {
         this.ovfDescriptorUploaded = Boolean.FALSE;
         return self();
      }

      @Override
      public VApp build() {
         return new VApp(this);
      }

      public B fromVApp(VApp in) {
         return fromAbstractVAppType(in)
               .owner(in.getOwner()).isInMaintenanceMode(in.isInMaintenanceMode())
               .children(in.getChildren()).isOvfDescriptorUploaded(in.isOvfDescriptorUploaded());
      }
   }

   protected VApp() {
      // For JAXB and builder use
   }

   protected VApp(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.inMaintenanceMode = builder.inMaintenanceMode;
      this.children = builder.children;
      this.ovfDescriptorUploaded = builder.ovfDescriptorUploaded;
   }

   @XmlElement(name = "Owner")
   private Owner owner;
   @XmlElement(name = "InMaintenanceMode")
   private Boolean inMaintenanceMode;
   @XmlElement(name = "Children")
   private VAppChildren children;
   @XmlAttribute
   private Boolean ovfDescriptorUploaded;

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the inMaintenanceMode property.
    */
   public Boolean isInMaintenanceMode() {
      return inMaintenanceMode;
   }

   /**
    * Gets the value of the children property.
    */
   public VAppChildren getChildren() {
      return children;
   }

   /**
    * Gets the value of the ovfDescriptorUploaded property.
    */
   public Boolean isOvfDescriptorUploaded() {
      return ovfDescriptorUploaded;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      VApp that = VApp.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && equal(this.inMaintenanceMode, that.inMaintenanceMode) &&
            equal(this.children, that.children) && equal(this.ovfDescriptorUploaded, that.ovfDescriptorUploaded);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), owner, inMaintenanceMode, children, ovfDescriptorUploaded);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner).add("inMaintenanceMode", inMaintenanceMode)
            .add("children", children).add("ovfDescriptorUploaded", ovfDescriptorUploaded);
   }
}
