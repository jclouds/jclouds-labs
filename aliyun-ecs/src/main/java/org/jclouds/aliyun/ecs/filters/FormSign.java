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
package org.jclouds.aliyun.ecs.filters;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteProcessor;
import org.jclouds.crypto.Crypto;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.location.Provider;
import org.jclouds.util.Strings2;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.SortedMap;
import java.util.UUID;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.io.ByteStreams.readBytes;
import static org.jclouds.crypto.Macs.asByteProcessor;
import static org.jclouds.http.Uris.uriBuilder;
import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.util.Strings2.toInputStream;

@Singleton
public class FormSign implements HttpRequestFilter {

   private static final String SEPARATOR = "&";
   public static final String ECS_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   private final Supplier<Credentials> creds;
   private final Crypto crypto;

   @Inject
   FormSign(@Provider Supplier<Credentials> creds, Crypto crypto) {
      this.creds = creds;
      this.crypto = crypto;
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");

      Multimap<String, String> decodedParams = queryParser().apply(request.getEndpoint().getQuery());

      SimpleDateFormat df = new SimpleDateFormat(ECS_DATE_FORMAT);
      df.setTimeZone(new SimpleTimeZone(0, "GMT"));

      String timestamp = df.format(new Date());
      String signatureNonce = UUID.randomUUID().toString();

      decodedParams.put("AccessKeyId", currentCreds.identity);
      decodedParams.put("Timestamp", timestamp);
      decodedParams.put("SignatureNonce", signatureNonce);

      String stringToSign = createStringToSign(request.getMethod(), decodedParams);

      String signature = sign(stringToSign, creds.get().credential);
      decodedParams.put("Signature", signature);

      request = request.toBuilder().endpoint(uriBuilder(request.getEndpoint()).query(decodedParams).build()).build();
      return request;
   }

   protected String createStringToSign(String method, Multimap<String, String> params) {

      StringBuilder toSign = new StringBuilder();
      toSign.append(method).append(SEPARATOR).append(Strings2.urlEncode("/")).append(SEPARATOR);
      toSign.append(getCanonicalizedQueryString(params));
      return toSign.toString();
   }

   /**
    * Examines the specified query string parameters and returns a
    * canonicalized form.
    * <p/>
    * The canonicalized query string is formed by first sorting all the query
    * string parameters, then URI encoding both the key and value and then
    * joining them, in order, separating key value pairs with an '&'.
    *
    * @return A canonicalized form for the specified query string parameters.
    */
   protected String getCanonicalizedQueryString(Multimap<String, String> params) {
      SortedMap<String, String> sorted = Maps.newTreeMap();
      if (params == null) {
         return "";
      }
      Iterator<Map.Entry<String, String>> pairs = params.entries().iterator();
      while (pairs.hasNext()) {
         Map.Entry<String, String> pair = pairs.next();
         String key = pair.getKey();
         String value = pair.getValue();
         sorted.put(Strings2.urlEncode(key), Strings2.urlEncode(value));
      }

      return Strings2.urlEncode(Joiner.on("&").withKeyValueSeparator("=").join(sorted));
   }

   public String sign(String toSign, String credentials) {
      try {
         ByteProcessor<byte[]> hmacSHA1 = asByteProcessor(
                 crypto.hmacSHA1(String.format("%s&", credentials).getBytes(UTF_8)));
         return base64().encode(readBytes(toInputStream(toSign), hmacSHA1));
      } catch (Exception e) {
         throw new HttpException("error signing request", e);
      }
   }

}
