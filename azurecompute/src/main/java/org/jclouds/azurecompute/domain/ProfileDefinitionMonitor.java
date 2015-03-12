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
package org.jclouds.azurecompute.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;

/**
 * Azure Traffic Manager endpoint monitor.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758257.aspx">docs</a>
 */
@AutoValue
public abstract class ProfileDefinitionMonitor {

   public static final int DEFAULT_INTERVAL = 30;
   public static final int DEFAULT_TIMEOUT = 10;
   public static final int DEFAULT_TOLERAION = 3;
   public static final int DEFAULT_EXPECTED = 200;
   public static final String DEFAULT_VERB = "GET";

   ProfileDefinitionMonitor() {
   } // For AutoValue only!

   /**
    * Specifies the number of seconds between consecutive attempts to check the status of a monitoring endpoint. The
    * value must be set to 30.
    *
    * @return intervall in seconds.
    */
   @Nullable
   public abstract Integer intervall();

   /**
    * Specifies the time to wait for response from the monitoring endpoint. The value must be set to 10.
    *
    * @return timeout in seconds.
    */
   @Nullable
   public abstract Integer timeout();

   /**
    * Specifies the number of consecutive failures to probe an endpoint before taking the endpoint out of rotation. The
    * value must be set to 3.
    *
    * @return tolerated number of failures.
    */
   @Nullable
   public abstract Integer toleration();

   /**
    * Specifies the protocol to use to monitor endpoint health. Possible values are: HTTP, HTTPS.
    *
    * @return endpoint protocol.
    */
   public abstract ProfileDefinition.Protocol protocol();

   /**
    * Specifies the port used to monitor endpoint health. Accepted values are integer values greater than 0 and less or
    * equal to 65,535.
    *
    * @return endpoint port.
    */
   public abstract int port();

   /**
    * Specifies the verb to use when making an HTTP request to monitor endpoint health. The value must be set to GET.
    *
    * @return verb to use when making an HTTP request.
    */
   @Nullable
   public abstract String verb();

   /**
    * Specifies the path relative to the endpoint domain name to probe for health state. Restrictions are: The path must
    * be from 1 through 1000 characters. It must start with a forward slash /. It must contain no brackets &lt;&gt;. It
    * must contain no double slashes //. It must be a well-formed URI string.
    *
    * @return endpoint relative path.
    * @see <a href="https://msdn.microsoft.com/en-us/library/system.uri.iswellformeduristring.aspx">
    * Uri.IsWellFormedUriString Method</a>
    */
   public abstract String path();

   /**
    * Specifies the HTTP status code expected from a healthy endpoint. The endpoint is considered unhealthy otherwise.
    * The value must be set to 200.
    *
    * @return expected status code.
    */
   @Nullable
   public abstract Integer expected();

   public static ProfileDefinitionMonitor create(
           final Integer intervall,
           final Integer timeout,
           final Integer toleration,
           final ProfileDefinition.Protocol protocol,
           final int port,
           final String verb,
           final String path,
           final Integer expected) {

      return new AutoValue_ProfileDefinitionMonitor(
              intervall, timeout, toleration, protocol, port, verb, path, expected);
   }
}
