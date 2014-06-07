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

import javax.inject.Named;
import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

public class ProfileInfo {

   public static class Builder {
      private String address;
      private boolean isApiHttpsOnly;
      private String autotopupAmount;
      private String autotopupThreshold;
      private String bankReference;
      private String company;
      private String country;
      private String currency;
      private String email;
      private String firstName;
      private boolean hasAutotopup;
      private boolean invoicing;
      private boolean isKeyAuth;
      private String language;
      private String lastName;
      private boolean isMailingListEnabled;
      private Map<String, String> meta;
      private String myNotes;
      private String nickname;
      private String phone;
      private String postcode;
      private String reseller;
      private Date signupTime;
      private String state;
      private String taxName;
      private double taxRate;
      private String title;
      private String town;
      private String uuid;
      private String vat;

      public Builder uuid(String uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder address(String address) {
         this.address = address;
         return this;
      }

      public Builder isApiHttpsOnly(boolean isApiHttpsOnly) {
         this.isApiHttpsOnly = isApiHttpsOnly;
         return this;
      }

      public Builder autotopupAmount(String autotopupAmount) {
         this.autotopupAmount = autotopupAmount;
         return this;
      }

      public Builder autotopupThreshold(String autotopupThreshold) {
         this.autotopupThreshold = autotopupThreshold;
         return this;
      }

      public Builder bankReference(String bankReference) {
         this.bankReference = bankReference;
         return this;
      }

      public Builder company(String company) {
         this.company = company;
         return this;
      }

      public Builder country(String country) {
         this.country = country;
         return this;
      }

      public Builder currency(String currency) {
         this.currency = currency;
         return this;
      }

      public Builder email(String email) {
         this.email = email;
         return this;
      }

      public Builder firstName(String firstName) {
         this.firstName = firstName;
         return this;
      }

      public Builder hasAutotopup(boolean hasAutotopup) {
         this.hasAutotopup = hasAutotopup;
         return this;
      }

      public Builder invoicing(boolean invoicing) {
         this.invoicing = invoicing;
         return this;
      }

      public Builder isKeyAuth(boolean isKeyAuth) {
         this.isKeyAuth = isKeyAuth;
         return this;
      }

      public Builder language(String language) {
         this.language = language;
         return this;
      }

      public Builder lastName(String lastName) {
         this.lastName = lastName;
         return this;
      }

      public Builder isMailingListEnabled(boolean isMailingListEnabled) {
         this.isMailingListEnabled = isMailingListEnabled;
         return this;
      }

      public Builder meta(Map<String, String> meta) {
         this.meta = meta;
         return this;
      }

      public Builder myNotes(String myNotes) {
         this.myNotes = myNotes;
         return this;
      }

      public Builder nickname(String nickname) {
         this.nickname = nickname;
         return this;
      }

      public Builder phone(String phone) {
         this.phone = phone;
         return this;
      }

      public Builder postcode(String postcode) {
         this.postcode = postcode;
         return this;
      }

      public Builder reseller(String reseller) {
         this.reseller = reseller;
         return this;
      }

      public Builder signupTime(Date signupTime) {
         this.signupTime = signupTime;
         return this;
      }

      public Builder state(String state) {
         this.state = state;
         return this;
      }

      public Builder taxName(String taxName) {
         this.taxName = taxName;
         return this;
      }

      public Builder taxRate(double taxRate) {
         this.taxRate = taxRate;
         return this;
      }

      public Builder title(String title) {
         this.title = title;
         return this;
      }

      public Builder town(String town) {
         this.town = town;
         return this;
      }

      public Builder vat(String vat) {
         this.vat = vat;
         return this;
      }

      public ProfileInfo build() {
         return new ProfileInfo(address, isApiHttpsOnly, autotopupAmount, autotopupThreshold, bankReference, company,
               country, currency, email, firstName, hasAutotopup, invoicing, isKeyAuth, language, lastName,
               isMailingListEnabled, meta, myNotes, nickname, phone, postcode, reseller, signupTime, state,
               taxName, taxRate, title, town, uuid, vat);
      }

   }

   private final String address;
   @Named("api_https_only")
   private final boolean isApiHttpsOnly;
   @Named("autotopup_amount")
   private final String autotopupAmount;
   @Named("autotopup_threshold")
   private final String autotopupThreshold;
   @Named("bank_reference")
   private final String bankReference;
   private final String company;
   private final String country;
   private final String currency;
   private final String email;
   @Named("first_name")
   private final String firstName;
   @Named("has_autotopup")
   private final boolean hasAutotopup;
   private final boolean invoicing;
   @Named("key_auth")
   private final boolean isKeyAuth;
   private final String language;
   @Named("last_name")
   private final String lastName;
   @Named("mailing_list")
   private final boolean isMailingListEnabled;
   private final Map<String, String> meta;
   @Named("my_notes")
   private final String myNotes;
   private final String nickname;
   private final String phone;
   private final String postcode;
   private final String reseller;
   @Named("signup_time")
   private final Date signupTime;
   private final String state;
   @Named("tax_name")
   private final String taxName;
   @Named("tax_rate")
   private final double taxRate;
   private final String title;
   private final String town;
   private final String uuid;
   private final String vat;

   @ConstructorProperties({
         "address", "api_https_only", "autopopup_amount", "autopopup_threshold", "bank_reference", "company", "country",
         "currency", "email", "first_name", "has_autotopup", "invoicing", "key_auth", "language", "last_name",
         "mailing_list", "meta", "my_notes", "nickname", "phone", "postcode", "reseller", "signup_time", "state",
         "tax_name", "tax_rate", "title", "town", "uuid", "vat"
   })
   public ProfileInfo(String address, boolean apiHttpsOnly, String autotopupAmount, String autotopupThreshold,
                      String bankReference, String company, String country, String currency, String email,
                      String firstName, boolean hasAutotopup, boolean invoicing, boolean keyAuth, String language,
                      String lastName, boolean mailingListEnabled, Map<String, String> meta, String myNotes,
                      String nickname, String phone, String postcode, String reseller, Date signupTime, String state,
                      String taxName, double taxRate, String title, String town, String uuid, String vat) {
      this.address = address;
      isApiHttpsOnly = apiHttpsOnly;
      this.autotopupAmount = autotopupAmount;
      this.autotopupThreshold = autotopupThreshold;
      this.bankReference = bankReference;
      this.company = company;
      this.country = country;
      this.currency = currency;
      this.email = email;
      this.firstName = firstName;
      this.hasAutotopup = hasAutotopup;
      this.invoicing = invoicing;
      isKeyAuth = keyAuth;
      this.language = language;
      this.lastName = lastName;
      isMailingListEnabled = mailingListEnabled;
      this.meta = meta;
      this.myNotes = myNotes;
      this.nickname = nickname;
      this.phone = phone;
      this.postcode = postcode;
      this.reseller = reseller;
      this.signupTime = signupTime;
      this.state = state;
      this.taxName = taxName;
      this.taxRate = taxRate;
      this.title = title;
      this.town = town;
      this.uuid = uuid;
      this.vat = vat;
   }

   public String getAddress() {
      return address;
   }

   public boolean isApiHttpsOnly() {
      return isApiHttpsOnly;
   }

   public String getAutotopupAmount() {
      return autotopupAmount;
   }

   public String getAutotopupThreshold() {
      return autotopupThreshold;
   }

   public String getBankReference() {
      return bankReference;
   }

   public String getCompany() {
      return company;
   }

   public String getCountry() {
      return country;
   }

   public String getCurrency() {
      return currency;
   }

   public String getEmail() {
      return email;
   }

   public String getFirstName() {
      return firstName;
   }

   public boolean isHasAutotopup() {
      return hasAutotopup;
   }

   public boolean isInvoicing() {
      return invoicing;
   }

   public boolean isKeyAuth() {
      return isKeyAuth;
   }

   public String getLanguage() {
      return language;
   }

   public String getLastName() {
      return lastName;
   }

   public boolean isMailingListEnabled() {
      return isMailingListEnabled;
   }

   public Map<String, String> getMeta() {
      return meta;
   }

   public String getMyNotes() {
      return myNotes;
   }

   public String getNickname() {
      return nickname;
   }

   public String getPhone() {
      return phone;
   }

   public String getPostcode() {
      return postcode;
   }

   public String getReseller() {
      return reseller;
   }

   public Date getSignupTime() {
      return signupTime;
   }

   public String getState() {
      return state;
   }

   public String getTaxName() {
      return taxName;
   }

   public double getTaxRate() {
      return taxRate;
   }

   public String getTitle() {
      return title;
   }

   public String getTown() {
      return town;
   }

   public String getUuid() {
      return uuid;
   }

   public String getVat() {
      return vat;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ProfileInfo)) return false;

      ProfileInfo that = (ProfileInfo) o;

      if (hasAutotopup != that.hasAutotopup) return false;
      if (invoicing != that.invoicing) return false;
      if (isApiHttpsOnly != that.isApiHttpsOnly) return false;
      if (isKeyAuth != that.isKeyAuth) return false;
      if (isMailingListEnabled != that.isMailingListEnabled) return false;
      if (Double.compare(that.taxRate, taxRate) != 0) return false;
      if (address != null ? !address.equals(that.address) : that.address != null) return false;
      if (autotopupAmount != null ? !autotopupAmount.equals(that.autotopupAmount) : that.autotopupAmount != null)
         return false;
      if (autotopupThreshold != null
            ? !autotopupThreshold.equals(that.autotopupThreshold)
            : that.autotopupThreshold != null)
         return false;
      if (bankReference != null ? !bankReference.equals(that.bankReference) : that.bankReference != null)
         return false;
      if (company != null ? !company.equals(that.company) : that.company != null) return false;
      if (country != null ? !country.equals(that.country) : that.country != null) return false;
      if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
      if (email != null ? !email.equals(that.email) : that.email != null) return false;
      if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
      if (language != null ? !language.equals(that.language) : that.language != null) return false;
      if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
      if (meta != null ? !meta.equals(that.meta) : that.meta != null) return false;
      if (myNotes != null ? !myNotes.equals(that.myNotes) : that.myNotes != null) return false;
      if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null) return false;
      if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
      if (postcode != null ? !postcode.equals(that.postcode) : that.postcode != null) return false;
      if (reseller != null ? !reseller.equals(that.reseller) : that.reseller != null) return false;
      if (signupTime != null ? !signupTime.equals(that.signupTime) : that.signupTime != null) return false;
      if (state != null ? !state.equals(that.state) : that.state != null) return false;
      if (taxName != null ? !taxName.equals(that.taxName) : that.taxName != null) return false;
      if (title != null ? !title.equals(that.title) : that.title != null) return false;
      if (town != null ? !town.equals(that.town) : that.town != null) return false;
      if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
      if (vat != null ? !vat.equals(that.vat) : that.vat != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result;
      long temp;
      result = address != null ? address.hashCode() : 0;
      result = 31 * result + (isApiHttpsOnly ? 1 : 0);
      result = 31 * result + (autotopupAmount != null ? autotopupAmount.hashCode() : 0);
      result = 31 * result + (autotopupThreshold != null ? autotopupThreshold.hashCode() : 0);
      result = 31 * result + (bankReference != null ? bankReference.hashCode() : 0);
      result = 31 * result + (company != null ? company.hashCode() : 0);
      result = 31 * result + (country != null ? country.hashCode() : 0);
      result = 31 * result + (currency != null ? currency.hashCode() : 0);
      result = 31 * result + (email != null ? email.hashCode() : 0);
      result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
      result = 31 * result + (hasAutotopup ? 1 : 0);
      result = 31 * result + (invoicing ? 1 : 0);
      result = 31 * result + (isKeyAuth ? 1 : 0);
      result = 31 * result + (language != null ? language.hashCode() : 0);
      result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
      result = 31 * result + (isMailingListEnabled ? 1 : 0);
      result = 31 * result + (meta != null ? meta.hashCode() : 0);
      result = 31 * result + (myNotes != null ? myNotes.hashCode() : 0);
      result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
      result = 31 * result + (phone != null ? phone.hashCode() : 0);
      result = 31 * result + (postcode != null ? postcode.hashCode() : 0);
      result = 31 * result + (reseller != null ? reseller.hashCode() : 0);
      result = 31 * result + (signupTime != null ? signupTime.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (taxName != null ? taxName.hashCode() : 0);
      temp = Double.doubleToLongBits(taxRate);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      result = 31 * result + (title != null ? title.hashCode() : 0);
      result = 31 * result + (town != null ? town.hashCode() : 0);
      result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
      result = 31 * result + (vat != null ? vat.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "[" +
            "address='" + address + '\'' +
            ", isApiHttpsOnly=" + isApiHttpsOnly +
            ", autotopupAmount='" + autotopupAmount + '\'' +
            ", autotopupThreshold='" + autotopupThreshold + '\'' +
            ", bankReference='" + bankReference + '\'' +
            ", company='" + company + '\'' +
            ", country='" + country + '\'' +
            ", currency='" + currency + '\'' +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", hasAutotopup=" + hasAutotopup +
            ", invoicing=" + invoicing +
            ", isKeyAuth=" + isKeyAuth +
            ", language='" + language + '\'' +
            ", lastName='" + lastName + '\'' +
            ", isMailingListEnabled=" + isMailingListEnabled +
            ", meta=" + meta +
            ", myNotes='" + myNotes + '\'' +
            ", nickname='" + nickname + '\'' +
            ", phone='" + phone + '\'' +
            ", postcode='" + postcode + '\'' +
            ", reseller='" + reseller + '\'' +
            ", signupTime='" + signupTime + '\'' +
            ", state='" + state + '\'' +
            ", taxName='" + taxName + '\'' +
            ", taxRate=" + taxRate +
            ", title='" + title + '\'' +
            ", town='" + town + '\'' +
            ", uuid='" + uuid + '\'' +
            ", vat='" + vat + '\'' +
            "]";
   }
}
