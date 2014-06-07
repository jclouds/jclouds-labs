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

import com.google.inject.name.Named;

import java.beans.ConstructorProperties;

public class AccountUsage {

   public static class Builder {
      private Usage cpu;
      private Usage dssd;
      private Usage ip;
      private Usage mem;
      private Usage windowsWebServer2008;
      private Usage windowsServer2008Standard;
      private Usage sqlServerStandard2008;
      private Usage sms;
      private Usage ssd;
      private Usage tx;
      private Usage vlan;

      public Builder cpu(Usage cpu) {
         this.cpu = cpu;
         return this;
      }

      public Builder dssd(Usage dssd) {
         this.dssd = dssd;
         return this;
      }

      public Builder ip(Usage ip) {
         this.ip = ip;
         return this;
      }

      public Builder mem(Usage mem) {
         this.mem = mem;
         return this;
      }

      public Builder windowsWebServer2008(Usage windowsWebServer2008) {
         this.windowsWebServer2008 = windowsWebServer2008;
         return this;
      }

      public Builder windowsServer2008Standard(Usage windowsServer2008Standard) {
         this.windowsServer2008Standard = windowsServer2008Standard;
         return this;
      }

      public Builder sqlServerStandard2008(Usage sqlServerStandard2008) {
         this.sqlServerStandard2008 = sqlServerStandard2008;
         return this;
      }

      public Builder ssd(Usage ssd) {
         this.ssd = ssd;
         return this;
      }

      public Builder vlan(Usage vlan) {
         this.vlan = vlan;
         return this;
      }

      public Builder tx(Usage tx) {
         this.tx = tx;
         return this;
      }

      public Builder sms(Usage sms) {
         this.sms = sms;
         return this;
      }

      public AccountUsage build() {
         return new AccountUsage(cpu, dssd, ip, mem, windowsWebServer2008, windowsServer2008Standard,
               sqlServerStandard2008, sms, ssd, tx, vlan);
      }
   }

   private final Usage cpu;
   private final Usage dssd;
   private final Usage ip;
   private final Usage mem;
   @Named("msft_lwa_00135")
   private final Usage windowsWebServer2008;
   @Named("msft_p37_04837")
   private final Usage windowsServer2008Standard;
   @Named("msft_tfa_00009")
   private final Usage sqlServerStandard2008;
   private final Usage sms;
   private final Usage ssd;
   private final Usage tx;
   private final Usage vlan;

   @ConstructorProperties({
         "cpu", "dssd", "ip", "mem", "msft_lwa_00135", "msft_p37_04837", "msft_tfa_00009", "sms", "ssd", "tx", "vlan"
   })
   public AccountUsage(Usage cpu, Usage dssd, Usage ip, Usage mem, Usage windowsWebServer2008,
                       Usage windowsServer2008Standard, Usage sqlServerStandard2008, Usage sms, Usage ssd, Usage tx,
                       Usage vlan) {
      this.cpu = cpu;
      this.dssd = dssd;
      this.ip = ip;
      this.mem = mem;
      this.windowsWebServer2008 = windowsWebServer2008;
      this.windowsServer2008Standard = windowsServer2008Standard;
      this.sqlServerStandard2008 = sqlServerStandard2008;
      this.sms = sms;
      this.ssd = ssd;
      this.tx = tx;
      this.vlan = vlan;
   }

   public Usage getVlan() {
      return vlan;
   }

   public Usage getCpu() {
      return cpu;
   }

   public Usage getDssd() {
      return dssd;
   }

   public Usage getIp() {
      return ip;
   }

   public Usage getMem() {
      return mem;
   }

   public Usage getSsd() {
      return ssd;
   }

   public Usage getWindowsWebServer2008() {
      return windowsWebServer2008;
   }

   public Usage getWindowsServer2008Standard() {
      return windowsServer2008Standard;
   }

   public Usage getSqlServerStandard2008() {
      return sqlServerStandard2008;
   }

   public Usage getSms() {
      return sms;
   }

   public Usage getTx() {
      return tx;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AccountUsage)) return false;

      AccountUsage that = (AccountUsage) o;

      if (cpu != null ? !cpu.equals(that.cpu) : that.cpu != null) return false;
      if (dssd != null ? !dssd.equals(that.dssd) : that.dssd != null) return false;
      if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
      if (mem != null ? !mem.equals(that.mem) : that.mem != null) return false;
      if (sms != null ? !sms.equals(that.sms) : that.sms != null) return false;
      if (sqlServerStandard2008 != null
            ? !sqlServerStandard2008.equals(that.sqlServerStandard2008)
            : that.sqlServerStandard2008 != null)
         return false;
      if (ssd != null ? !ssd.equals(that.ssd) : that.ssd != null) return false;
      if (tx != null ? !tx.equals(that.tx) : that.tx != null) return false;
      if (vlan != null ? !vlan.equals(that.vlan) : that.vlan != null) return false;
      if (windowsServer2008Standard != null
            ? !windowsServer2008Standard.equals(that.windowsServer2008Standard)
            : that.windowsServer2008Standard != null)
         return false;
      if (windowsWebServer2008 != null
            ? !windowsWebServer2008.equals(that.windowsWebServer2008)
            : that.windowsWebServer2008 != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = cpu != null ? cpu.hashCode() : 0;
      result = 31 * result + (dssd != null ? dssd.hashCode() : 0);
      result = 31 * result + (ip != null ? ip.hashCode() : 0);
      result = 31 * result + (mem != null ? mem.hashCode() : 0);
      result = 31 * result + (windowsWebServer2008 != null ? windowsWebServer2008.hashCode() : 0);
      result = 31 * result + (windowsServer2008Standard != null ? windowsServer2008Standard.hashCode() : 0);
      result = 31 * result + (sqlServerStandard2008 != null ? sqlServerStandard2008.hashCode() : 0);
      result = 31 * result + (sms != null ? sms.hashCode() : 0);
      result = 31 * result + (ssd != null ? ssd.hashCode() : 0);
      result = 31 * result + (tx != null ? tx.hashCode() : 0);
      result = 31 * result + (vlan != null ? vlan.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "cpu=" + cpu +
            ", dssd=" + dssd +
            ", ip=" + ip +
            ", mem=" + mem +
            ", windowsWebServer2008=" + windowsWebServer2008 +
            ", windowsServer2008Standard=" + windowsServer2008Standard +
            ", sqlServerStandard2008=" + sqlServerStandard2008 +
            ", sms=" + sms +
            ", ssd=" + ssd +
            ", tx=" + tx +
            ", vlan=" + vlan +
            "]";
   }
}
