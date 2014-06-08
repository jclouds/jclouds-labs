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
package org.jclouds.rackspace.autoscale.v1.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.jclouds.rackspace.autoscale.v1.domain.Webhook;

/**
 * 
 * This is more of an example code of how to execute the anonymous webhook call without jclouds.
 * A POST call to the Webhook capability Link executes the scaling policy that webhook belongs to.
 * Calling the capability Link ensures this is done without authentication and anonymously (the webhook information is hashed).
 */
public class AutoscaleUtils {
   public static boolean execute(URI webhookUri) throws IOException {
      URL url = webhookUri.toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setInstanceFollowRedirects(false);
      connection.setRequestMethod("POST");
      connection.setUseCaches(false);
      connection.connect();
      int code = connection.getResponseCode();
      connection.disconnect();
      
      return code == 202;
   }
   
   public static boolean execute(Webhook webhook) throws IOException {
      return execute(webhook.getAnonymousExecutionURI().get());
   }
}
