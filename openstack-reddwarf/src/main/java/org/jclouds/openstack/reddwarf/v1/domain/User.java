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
package org.jclouds.openstack.reddwarf.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An Openstack Reddwarf Database User.
 * 
 * @author Zack Shoylev
 */
public class User implements Comparable<User>{
   private final String name;
   private final String password;
   private final List<Map<String,String>> databases;

   @ConstructorProperties({
      "name", "password", "databases"
   })
   protected User(String name, String password, List<Map<String,String>> databases) {
      this.name = checkNotNull(name, "name required");
      this.password = password;
      if(databases == null)this.databases = Lists.newArrayList();
      else {
         this.databases = databases;
      }
   }    

   protected User(String name, String password, Set<String> databases) {
      this.name = checkNotNull(name, "name required");
      this.password = password;
      if(databases == null)this.databases = Lists.newArrayList();
      else {
         List<Map<String,String>> databaseList = Lists.newArrayList();
         for(String databaseName : databases) {
            Map<String,String> singleDatabase = Maps.newHashMap();
            singleDatabase.put("name", databaseName);
            databaseList.add(singleDatabase);
         }
         this.databases = ImmutableList.copyOf(databaseList);
      }
   }   

   /**
    * @return the name of this user
    * @see User.Builder#name(String)
    */
   public String getName() {
      return this.name;
   }

   /**
    * @return the password for this user
    * @see User.Builder#password(String)
    */
   public String getPassword() {
      return this.password;
   }

   /**
    * @return the databases for this user
    * @see User.Builder#databases(String)
    */
   public List<String> getDatabases() {
      List<String> databaseList = Lists.newArrayList();
      for(Map<String,String> database : this.databases) {
         databaseList.add(database.get("name"));
      }
      return ImmutableList.copyOf(databaseList);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, password, databases);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      User that = User.class.cast(obj);
      return Objects.equal(this.name, that.name) && 
            Objects.equal(this.password, that.password) &&
            Objects.equal(this.databases, that.databases);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("name", name)
            .add("password", password)
            .add("databases", databases);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static Builder builder() { 
      return new Builder();
   }

   public Builder toBuilder() { 
      return new Builder().fromUser(this);
   }

   public static class Builder {
      protected String name;
      protected String password;
      protected Set<String> databases;

      /** 
       * @param name The name of this user
       * @return The builder object
       * @see User#getName()
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /** 
       * @param name The password for this user
       * @return The builder object
       * @see User#getPassword()
       */
      public Builder password(String password) {
         this.password = password;
         return this;
      }

      /** 
       * @param name The databases for this user
       * @return The builder object
       * @see User#getDatabases()
       */
      public Builder databases(Set<String> databases) {
         this.databases = databases;
         return this;
      }

      /**
       * 
       * @return A new User object
       */
      public User build() {
         return new User(name, password, databases);
      }

      public Builder fromUser(User in) {
         return this
               .name(in.getName())
               .password(in.getPassword())
               .databases(ImmutableSet.copyOf( in.getDatabases() ));
      }        
   }

   @Override
   public int compareTo(User that) {
      return this.getName().compareTo(that.getName());
   }
}
