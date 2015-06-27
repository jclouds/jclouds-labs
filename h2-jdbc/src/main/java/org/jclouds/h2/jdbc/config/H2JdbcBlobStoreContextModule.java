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
package org.jclouds.h2.jdbc.config;

import java.util.Properties;

import org.jclouds.jdbc.config.JdbcBlobStoreContextModule;

import com.google.inject.persist.jpa.JpaPersistModule;

public class H2JdbcBlobStoreContextModule extends JdbcBlobStoreContextModule {

   private static final String DEFAULT_FILE = "./jclouds-db";

   protected void configure() {
      super.configure();

      Properties properties = new Properties();
      properties.setProperty("hibernate.connection.url", "jdbc:h2:" + DEFAULT_FILE);

      install(new JpaPersistModule("jclouds-h2").properties(properties));
   }

}
