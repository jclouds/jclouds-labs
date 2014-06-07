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
package org.jclouds.fujitsu.fgcp.config;


import java.util.Calendar;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.date.TimeStamp;
import org.jclouds.fujitsu.fgcp.FGCPApi;
import org.jclouds.fujitsu.fgcp.FGCPCredentials;
import org.jclouds.fujitsu.fgcp.handlers.FGCPServerErrorRetryHandler;
import org.jclouds.fujitsu.fgcp.handlers.ResponseNotSuccessHandler;
import org.jclouds.fujitsu.fgcp.http.ChangeReturnCodeTo500IfErrorJavaUrlHttpCommandExecutorService;
import org.jclouds.fujitsu.fgcp.location.SystemAndNetworkSegmentToLocationSupplier;
import org.jclouds.fujitsu.fgcp.suppliers.FGCPCredentialsSupplier;
import org.jclouds.fujitsu.fgcp.suppliers.SSLContextWithKeysSupplier;
import org.jclouds.fujitsu.fgcp.xml.FGCPJAXBParser;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.config.SSLModule.LogToMapHostnameVerifier;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.implicit.FirstNetwork;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.xml.XMLParser;

import com.google.common.base.Supplier;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;


/**
 * Configures the connection to the FGCP endpoint.
 */
@ConfiguresHttpApi
public class FGCPHttpApiModule extends HttpApiModule<FGCPApi> {

   public FGCPHttpApiModule() {
      super();
   }

   protected FGCPHttpApiModule(Class<FGCPApi> api) {
      super(api);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ResponseNotSuccessHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ResponseNotSuccessHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ResponseNotSuccessHandler.class);
   }

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
            FGCPServerErrorRetryHandler.class);
      bind(HttpRetryHandler.class).annotatedWith(ServerError.class).to(
            FGCPServerErrorRetryHandler.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(XMLParser.class).to(FGCPJAXBParser.class);
      bind(new TypeLiteral<Supplier<FGCPCredentials>>() {}).to(FGCPCredentialsSupplier.class);
      bind(new TypeLiteral<Supplier<SSLContext>>() {
      }).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
      });
      bind(HostnameVerifier.class).to(LogToMapHostnameVerifier.class);
      bind(JavaUrlHttpCommandExecutorService.class).to(ChangeReturnCodeTo500IfErrorJavaUrlHttpCommandExecutorService.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(FirstNetwork.class).in(Scopes.SINGLETON);
      bind(LocationsSupplier.class).to(SystemAndNetworkSegmentToLocationSupplier.class).in(Scopes.SINGLETON);
   }

   @Provides
   @TimeStamp
   protected Calendar provideCalendar() {
      return Calendar.getInstance();
   }
}
