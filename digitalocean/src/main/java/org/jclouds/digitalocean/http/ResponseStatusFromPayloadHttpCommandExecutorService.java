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
package org.jclouds.digitalocean.http;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Closeables.close;
import static org.jclouds.io.Payloads.newInputStreamPayload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.jclouds.Constants;
import org.jclouds.digitalocean.domain.BaseResponse;
import org.jclouds.digitalocean.domain.BaseResponse.Status;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.handlers.DelegatingErrorHandler;
import org.jclouds.http.handlers.DelegatingRetryHandler;
import org.jclouds.http.internal.HttpWire;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.jclouds.io.ContentMetadataCodec;
import org.jclouds.io.Payload;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Custom implementation of the HTTP driver to read the response body in order to get the real response status.
 * <p>
 * The DigitalOcean API always return 200 codes even if a request failed due to some internal error, but populates an
 * <code>ERROR</code> string in the response payload.
 * <p>
 * This class will read the body of the response and populate a 500 status code if an error is found.
 */
@Singleton
public class ResponseStatusFromPayloadHttpCommandExecutorService extends JavaUrlHttpCommandExecutorService {

   public static final String ACCESS_DENIED = "Access Denied";
   public static final String NOT_FOUND = "Not Found";

   private final ParseJson<BaseResponse> errorParser;

   @Inject
   ResponseStatusFromPayloadHttpCommandExecutorService(HttpUtils utils, ContentMetadataCodec contentMetadataCodec,
         @Named(Constants.PROPERTY_IO_WORKER_THREADS) ListeningExecutorService ioExecutor,
         DelegatingRetryHandler retryHandler, IOExceptionRetryHandler ioRetryHandler,
         DelegatingErrorHandler errorHandler, HttpWire wire, @Named("untrusted") HostnameVerifier verifier,
         @Named("untrusted") Supplier<SSLContext> untrustedSSLContextProvider, Function<URI, Proxy> proxyForURI,
         ParseJson<BaseResponse> errorParser) throws SecurityException, NoSuchFieldException {
      super(utils, contentMetadataCodec, ioExecutor, retryHandler, ioRetryHandler, errorHandler, wire, verifier,
            untrustedSSLContextProvider, proxyForURI);
      this.errorParser = checkNotNull(errorParser, "errorParser cannot be null");
   }

   @Override
   protected HttpResponse invoke(HttpURLConnection connection) throws IOException, InterruptedException {
      HttpResponse original = super.invoke(connection);
      HttpResponse.Builder<?> response = original.toBuilder();

      if (hasPayload(original)) {
         // As we need to read the response body to determine if there are errors, but we may need to process the body
         // again later in the response parsers if everything is OK, we buffer the body into an InputStream we can reset
         InputStream in = null;
         InputStream originalInputStream = original.getPayload().openStream();

         if (originalInputStream instanceof ByteArrayInputStream) {
            in = originalInputStream;
         } else {
            try {
               in = new ByteArrayInputStream(ByteStreams.toByteArray(originalInputStream));
            } finally {
               close(originalInputStream, true);
            }
         }

         // Process the payload and look for errors
         BaseResponse responseContent = errorParser.apply(in);
         if (responseContent != null && responseContent.getStatus() == Status.ERROR) {
            // Yes, this is ugly, but the DigitalOcean API sometimes sets the status code to 200 for these errors and
            // the only way to know what happened is parsing the error message
            String message = responseContent.getMessage();
            if (ACCESS_DENIED.equals(message)) {
               response.statusCode(401);
            } else if (NOT_FOUND.equals(message)) {
               response.statusCode(404);
            } else {
               response.statusCode(500);
            }
            response.message(responseContent.getDetails());
         }

         // Reset the input stream and set the payload, so it can be read again
         // by the response and error parsers
         in.reset();
         Payload payload = newInputStreamPayload(in);
         contentMetadataCodec.fromHeaders(payload.getContentMetadata(), original.getHeaders());
         response.payload(payload);
      }

      return response.build();
   }

   private static boolean hasPayload(final HttpResponse response) {
      return response.getPayload() != null && response.getPayload().getRawContent() != null;
   }
}
