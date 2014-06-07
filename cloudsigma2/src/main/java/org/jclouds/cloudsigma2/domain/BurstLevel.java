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

public class BurstLevel {

   public static class Builder {
      private int cpu;
      private int dssd;
      private int ip;
      private int mem;
      private int windowsWebServer2008;
      private int windowsServer2008Standard;
      private int sqlServerStandard2008;
      private int sms;
      private int ssd;
      private int tx;
      private int vlan;

      public Builder cpu(int cpu) {
         this.cpu = cpu;
         return this;
      }

      public Builder dssd(int dssd) {
         this.dssd = dssd;
         return this;
      }

      public Builder ip(int ip) {
         this.ip = ip;
         return this;
      }

      public Builder mem(int mem) {
         this.mem = mem;
         return this;
      }

      public Builder windowsWebServer2008(int windowsWebServer2008) {
         this.windowsWebServer2008 = windowsWebServer2008;
         return this;
      }

      public Builder windowsServer2008Standard(int windowsServer2008Standard) {
         this.windowsServer2008Standard = windowsServer2008Standard;
         return this;
      }

      public Builder sqlServerStandard2008(int sqlServerStandard2008) {
         this.sqlServerStandard2008 = sqlServerStandard2008;
         return this;
      }

      public Builder ssd(int ssd) {
         this.ssd = ssd;
         return this;
      }

      public Builder vlan(int vlan) {
         this.vlan = vlan;
         return this;
      }

      public Builder tx(int tx) {
         this.tx = tx;
         return this;
      }

      public Builder sms(int sms) {
         this.sms = sms;
         return this;
      }

      public BurstLevel build() {
         return new BurstLevel(cpu, dssd, ip, mem, windowsWebServer2008, windowsServer2008Standard,
               sqlServerStandard2008, sms, ssd, tx, vlan);
      }
   }

   private final int cpu;
   private final int dssd;
   private final int ip;
   private final int mem;
   @Named("msft_lwa_00135")
   private final int windowsWebServer2008;
   @Named("msft_p37_04837")
   private final int windowsServer2008Standard;
   @Named("msft_tfa_00009")
   private final int sqlServerStandard2008;
   private final int sms;
   private final int ssd;
   private final int tx;
   private final int vlan;

   @ConstructorProperties({
         "cpu", "dssd", "ip", "mem", "msft_lwa_00135", "msft_p37_04837", "msft_tfa_00009", "sms", "ssd", "tx", "vlan"
   })
   public BurstLevel(int cpu, int dssd, int ip, int mem, int windowsWebServer2008, int windowsServer2008Standard,
                     int sqlServerStandard2008, int sms, int ssd, int tx, int vlan) {
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

   public int getVlan() {
      return vlan;
   }

   public int getCpu() {
      return cpu;
   }

   public int getDssd() {
      return dssd;
   }

   public int getIp() {
      return ip;
   }

   public int getMem() {
      return mem;
   }

   public int getSsd() {
      return ssd;
   }

   public int getWindowsWebServer2008() {
      return windowsWebServer2008;
   }

   public int getWindowsServer2008Standard() {
      return windowsServer2008Standard;
   }

   public int getSqlServerStandard2008() {
      return sqlServerStandard2008;
   }

   public int getSms() {
      return sms;
   }

   public int getTx() {
      return tx;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BurstLevel)) return false;

      BurstLevel that = (BurstLevel) o;

      if (cpu != that.cpu) return false;
      if (dssd != that.dssd) return false;
      if (ip != that.ip) return false;
      if (mem != that.mem) return false;
      if (sms != that.sms) return false;
      if (sqlServerStandard2008 != that.sqlServerStandard2008) return false;
      if (ssd != that.ssd) return false;
      if (tx != that.tx) return false;
      if (vlan != that.vlan) return false;
      if (windowsServer2008Standard != that.windowsServer2008Standard) return false;
      if (windowsWebServer2008 != that.windowsWebServer2008) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = cpu;
      result = 31 * result + dssd;
      result = 31 * result + ip;
      result = 31 * result + mem;
      result = 31 * result + windowsWebServer2008;
      result = 31 * result + windowsServer2008Standard;
      result = 31 * result + sqlServerStandard2008;
      result = 31 * result + sms;
      result = 31 * result + ssd;
      result = 31 * result + tx;
      result = 31 * result + vlan;
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
