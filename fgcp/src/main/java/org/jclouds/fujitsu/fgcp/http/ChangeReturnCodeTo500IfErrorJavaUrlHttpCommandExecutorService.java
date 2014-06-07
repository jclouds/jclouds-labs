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
package org.jclouds.fujitsu.fgcp.http;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.Constants;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.io.ContentMetadataCodec;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Changes super class' behaviour to return an HTTP response with status code 500 instead of the 200 returned by
 * the FGCP API endpoint when the XML payload indicates an error with the request.
 */
@Singleton
public class ChangeReturnCodeTo500IfErrorJavaUrlHttpCommandExecutorService extends
      JavaUrlHttpCommandExecutorService {

   @Inject
   public ChangeReturnCodeTo500IfErrorJavaUrlHttpCommandExecutorService(
         HttpUtils utils,
         ContentMetadataCodec contentMetadataCodec,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
         DelegatingRetryHandler retryHandler,
         IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire,
         HostnameVerifier verifier,
         Supplier<SSLContext> untrustedSSLContextProvider,
         Function<URI, Proxy> proxyForURI) throws SecurityException,
         NoSuchFieldException {
      super(utils, contentMetadataCodec, ioExecutor, retryHandler,
            ioRetryHandler, errorHandler, wire, verifier,
            untrustedSSLContextProvider, proxyForURI);
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection)
         throws IOException, InterruptedException {
      HttpResponse response = super.invoke(connection);

      byte[] data = closeClientButKeepContentStream(response);
      if (data != null
            && !new String(data).contains("<responseStatus>SUCCESS</responseStatus>")) {
         response = response.toBuilder().statusCode(500).build();
      }

      return response;
   }
}
