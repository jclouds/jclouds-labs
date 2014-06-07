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
package org.jclouds.abiquo.functions.auth;

import javax.inject.Singleton;

import org.jclouds.domain.Credentials;

import com.google.common.base.Function;

/**
 * Gets the authentication token from the configured credentials.
 * <p>
 * Note that in order to request an authentication token once it expires, the
 * username and password must be provided. This means that when the context is
 * created using an already existing token (and thus no username and password
 * are provided), jclouds will not be able to request a new token once it
 * expires.
 * <p>
 * If the context is created with an already existing token, it is up to the
 * user to renew it when it expires.
 */
@Singleton
public class GetTokenFromCredentials implements Function<Credentials, String> {

   @Override
   public String apply(Credentials input) {
      return input.credential;
   }

}
