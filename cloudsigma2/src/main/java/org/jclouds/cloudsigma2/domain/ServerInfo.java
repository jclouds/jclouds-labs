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
package org.jclouds.cloudsigma2.domain;

import com.google.common.collect.ImmutableList;

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Vladimir Shevchenko
 */
public class ServerInfo extends Server {

   public static class Builder extends Server.Builder {
      private int cpu;
      private boolean cpusInsteadOfCores;
      private List<ServerDrive> drives;
      private boolean enableNuma;
      private boolean hvRelaxed;
      private boolean hvTsc;
      private BigInteger memory;
      private Map<String, String> meta;
      private List<NIC> nics;
      private List<String> requirements;
      private List<String> tags;
      private String vncPassword;
      private int smp;

      /**
       * @param cpu server’s CPU Clock speed measured in MHz
       * @return ServerInfo Builder
       */
      public Builder cpu(int cpu) {
         this.cpu = cpu;
         return this;
      }

      /**
       * @param cpusInsteadOfCores expose server SMPs as separate CPUs, instead of cores of a single CPU
       * @return ServerInfo Builder
       */
      public Builder cpusInsteadOfCores(boolean cpusInsteadOfCores) {
         this.cpusInsteadOfCores = cpusInsteadOfCores;
         return this;
      }

      /**
       * @param drives list of attached Drives to server
       * @return ServerInfo Builder
       */
      public Builder drives(List<ServerDrive> drives) {
         this.drives = ImmutableList.copyOf(drives);
         return this;
      }

      /**
       * @param enableNuma expose NUMA topology to the server
       * @return ServerInfo Builder
       */
      public Builder enableNuma(boolean enableNuma) {
         this.enableNuma = enableNuma;
         return this;
      }

      /**
       * @param hvRelaxed improve performance of Windows servers
       * @return ServerInfo Builder
       */
      public Builder hvRelaxed(boolean hvRelaxed) {
         this.hvRelaxed = hvRelaxed;
         return this;
      }

      /**
       * @param hvTsc improves performance of Windows servers with the trade off that the servers
       * @return ServerInfo Builder
       */
      public Builder hvTsc(boolean hvTsc) {
         this.hvTsc = hvTsc;
         return this;
      }

      /**
       * @param memory server’s Random Access Memory measured in bytes
       * @return ServerInfo Builder
       */
      public Builder memory(BigInteger memory) {
         this.memory = memory;
         return this;
      }

      /**
       * @param meta user assigned meta information for this server
       * @return ServerInfo Builder
       */
      public Builder meta(Map<String, String> meta) {
         this.meta = meta;
         return this;
      }

      /**
       * @param nics list of nics attached to this server
       * @return ServerInfo Builder
       */
      public Builder nics(List<NIC> nics) {
         this.nics = ImmutableList.copyOf(nics);
         return this;
      }

      /**
       * @param requirements a collection of special requirements for this server
       * @return ServerInfo Builder
       */
      public Builder requirements(List<String> requirements) {
         this.requirements = ImmutableList.copyOf(requirements);
         return this;
      }

      /**
       * @param tags list of tags this server is associated with
       * @return ServerInfo Builder
       */
      public Builder tags(List<String> tags) {
         this.tags = ImmutableList.copyOf(tags);
         return this;
      }

      /**
       * @param vncPassword VNC Password to connect to server
       * @return ServerInfo Builder
       */
      public Builder vncPassword(String vncPassword) {
         this.vncPassword = vncPassword;
         return this;
      }

      /**
       * @param smp Symmetric Multiprocessing (SMP) i.e. number of CPU cores
       * @return ServerInfo Builder
       */
      public Builder smp(int smp) {
         this.smp = smp;
         return this;
      }

      /**
       * {@inheritDoc}
       *
       * @return ServerInfo Builder
       */
      @Override
      public Builder owner(Owner owner) {
         return Builder.class.cast(super.owner(owner));
      }

      /**
       * {@inheritDoc}
       *
       * @return ServerInfo Builder
       */
      @Override
      public Builder status(ServerStatus status) {
         return Builder.class.cast(super.status(status));
      }

      /**
       * {@inheritDoc}
       *
       * @return ServerInfo Builder
       */
      @Override
      public Builder runtime(ServerRuntime runtime) {
         return Builder.class.cast(super.runtime(runtime));
      }

      /**
       * {@inheritDoc}
       *
       * @return ServerInfo Builder
       */
      @Override
      public Builder uuid(String uuid) {
         return Builder.class.cast(super.uuid(uuid));
      }

      /**
       * {@inheritDoc}
       *
       * @return ServerInfo Builder
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       *
       * @return ServerInfo Builder
       */
      @Override
      public Builder resourceUri(URI resourceUri) {
         return Builder.class.cast(super.resourceUri(resourceUri));
      }

      public static Builder fromServerInfo(ServerInfo serverInfo) {
         return new Builder()
               .uuid(serverInfo.getUuid())
               .name(serverInfo.getName())
               .resourceUri(serverInfo.getResourceUri())
               .owner(serverInfo.getOwner())
               .status(serverInfo.getStatus())
               .runtime(serverInfo.getRuntime())
               .cpusInsteadOfCores(serverInfo.isCpusInsteadOfCores())
               .drives(serverInfo.getDrives())
               .enableNuma(serverInfo.isNumaEnabled())
               .hvRelaxed(serverInfo.isHvRelaxed())
               .hvTsc(serverInfo.isHvTsc())
               .memory(serverInfo.getMemory())
               .meta(serverInfo.getMeta())
               .nics(serverInfo.getNics())
               .requirements(serverInfo.getRequirements())
               .tags(serverInfo.getTags())
               .vncPassword(serverInfo.getVncPassword())
               .smp(serverInfo.getSmp());
      }

      public static Builder fromServer(Server in) {
         return new Builder()
               .uuid(in.getUuid())
               .name(in.getName())
               .resourceUri(in.getResourceUri())
               .owner(in.getOwner())
               .status(in.getStatus())
               .runtime(in.getRuntime());
      }

      /**
       * Warning: name, cpu, memory & vncPassword should be specified
       *
       * @return server with details
       */
      public ServerInfo build() {
         return new ServerInfo(uuid, name, resourceUri, owner, status, runtime, cpu, cpusInsteadOfCores, drives,
               enableNuma, hvRelaxed, hvTsc, memory, meta, nics, requirements, tags, vncPassword, smp);
      }
   }

   private final int cpu;
   @Named("cpus_instead_of_cores")
   private final boolean cpusInsteadOfCores;
   private final List<ServerDrive> drives;
   @Named("enable_numa")
   private final boolean enableNuma;
   @Named("hv_relaxed")
   private final boolean hvRelaxed;
   @Named("hv_tsc")
   private final boolean hvTsc;
   @Named("mem")
   private final BigInteger memory;
   private final Map<String, String> meta;
   private final List<NIC> nics;
   private final List<String> requirements;
   private final List<String> tags;
   @Named("vnc_password")
   private final String vncPassword;
   private final int smp;

   @ConstructorProperties({
         "uuid", "name", "resource_uri", "owner", "status", "runtime",
         "cpu", "cpus_instead_of_cores", "drives", "enable_numa",
         "hv_relaxed", "hv_tsc", "mem", "meta", "nics",
         "requirements", "tags", "vnc_password", "smp"
   })
   public ServerInfo(String uuid, String name, URI resourceUri, Owner owner, ServerStatus status, ServerRuntime runtime,
                     int cpu, boolean cpusInsteadOfCores, List<ServerDrive> drives, boolean enableNuma,
                     boolean hvRelaxed, boolean hvTsc, BigInteger memory, Map<String, String> meta, List<NIC> nics,
                     List<String> requirements, List<String> tags, String vncPassword, int smp) {
      super(uuid, name, resourceUri, owner, status, runtime);
      this.cpu = cpu;
      this.cpusInsteadOfCores = cpusInsteadOfCores;
      this.drives = drives == null ? new ArrayList<ServerDrive>() : drives;
      this.enableNuma = enableNuma;
      this.hvRelaxed = hvRelaxed;
      this.hvTsc = hvTsc;
      this.memory = memory;
      this.meta = meta;
      this.nics = nics == null ? new ArrayList<NIC>() : nics;
      this.requirements = requirements;
      this.tags = tags;
      this.vncPassword = vncPassword;
      this.smp = smp;
   }

   /**
    * @return server’s CPU Clock speed measured in MHz
    */
   public int getCpu() {
      return cpu;
   }

   /**
    * @return expose server SMPs as separate CPUs, instead of cores of a single CPU
    */
   public boolean isCpusInsteadOfCores() {
      return cpusInsteadOfCores;
   }

   /**
    * @return list of attached Drives to server
    */
   public List<ServerDrive> getDrives() {
      return drives;
   }

   /**
    * @return expose NUMA topology to the server
    */
   public boolean isNumaEnabled() {
      return enableNuma;
   }

   /**
    * @return improve performance of Windows servers
    */
   public boolean isHvRelaxed() {
      return hvRelaxed;
   }

   /**
    * @return improves performance of Windows servers with the trade off that the servers
    */
   public boolean isHvTsc() {
      return hvTsc;
   }

   /**
    * @return server’s Random Access Memory measured in bytes
    */
   public BigInteger getMemory() {
      return memory;
   }

   /**
    * @return user assigned meta information for this server
    */
   public Map<String, String> getMeta() {
      return meta;
   }

   /**
    * @return list of nics attached to this server
    */
   public List<NIC> getNics() {
      return nics;
   }

   /**
    * @return a collection of special requirements for this server
    */
   public List<String> getRequirements() {
      return requirements;
   }

   /**
    * @return list of tags this server is associated with
    */
   public List<String> getTags() {
      return tags;
   }

   /**
    * @return VNC Password to connect to server
    */
   public String getVncPassword() {
      return vncPassword;
   }

   /**
    * @return Symmetric Multiprocessing (SMP) i.e. number of CPU cores
    */
   public int getSmp() {
      return smp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ServerInfo)) return false;
      if (!super.equals(o)) return false;

      ServerInfo that = (ServerInfo) o;

      if (cpu != that.cpu) return false;
      if (cpusInsteadOfCores != that.cpusInsteadOfCores) return false;
      if (enableNuma != that.enableNuma) return false;
      if (hvRelaxed != that.hvRelaxed) return false;
      if (hvTsc != that.hvTsc) return false;
      if (smp != that.smp) return false;
      if (drives != null ? !drives.equals(that.drives) : that.drives != null) return false;
      if (memory != null ? !memory.equals(that.memory) : that.memory != null) return false;
      if (meta != null ? !meta.equals(that.meta) : that.meta != null) return false;
      if (nics != null ? !nics.equals(that.nics) : that.nics != null) return false;
      if (requirements != null ? !requirements.equals(that.requirements) : that.requirements != null) return false;
      if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
      if (vncPassword != null ? !vncPassword.equals(that.vncPassword) : that.vncPassword != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + cpu;
      result = 31 * result + (cpusInsteadOfCores ? 1 : 0);
      result = 31 * result + (drives != null ? drives.hashCode() : 0);
      result = 31 * result + (enableNuma ? 1 : 0);
      result = 31 * result + (hvRelaxed ? 1 : 0);
      result = 31 * result + (hvTsc ? 1 : 0);
      result = 31 * result + (memory != null ? memory.hashCode() : 0);
      result = 31 * result + (meta != null ? meta.hashCode() : 0);
      result = 31 * result + (nics != null ? nics.hashCode() : 0);
      result = 31 * result + (requirements != null ? requirements.hashCode() : 0);
      result = 31 * result + (tags != null ? tags.hashCode() : 0);
      result = 31 * result + (vncPassword != null ? vncPassword.hashCode() : 0);
      result = 31 * result + smp;
      return result;
   }

   @Override
   public String toString() {
      return "[uuid=" + uuid + ", name=" + name + ", resourceUri=" + resourceUri + ", owner=" + owner
            + ", status=" + status + ", runtime=" + runtime + ", cpu=" + cpu + ", cpusInsteadOfCores="
            + cpusInsteadOfCores + ", enableNuma=" + enableNuma + ", hvRelaxed=" + hvRelaxed + ", "
            + "hvTsc=" + hvTsc + ", memory=" + memory + ", drives=" + drives + ", meta=" + meta + ", "
            + "nics=" + nics + ", requirements=" + requirements + ", tags=" + tags + ", "
            + "vncPassword=" + vncPassword + ", smp=" + smp + "]";
   }

}
