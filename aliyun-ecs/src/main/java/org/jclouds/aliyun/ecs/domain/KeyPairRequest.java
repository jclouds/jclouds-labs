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
package org.jclouds.aliyun.ecs.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.beans.ConstructorProperties;

import static com.google.common.base.Preconditions.checkNotNull;

public class KeyPairRequest extends Request {

   private final String keyPairName;
   private final String keyPairFingerPrint;
   private final String privateKeyBody;

   @ConstructorProperties({ "RequestId", "KeyPairName", "KeyPairFingerPrint", "PrivateKeyBody" })
   public KeyPairRequest(String requestId, String keyPairName, String keyPairFingerPrint, String privateKeyBody) {
      super(requestId);
      this.keyPairName = checkNotNull(keyPairName, "name");
      this.keyPairFingerPrint = checkNotNull(keyPairFingerPrint, "keyPairFingerPrint");
      this.privateKeyBody = checkNotNull(privateKeyBody, "privateKeyBody");
   }

   public String getKeyPairName() {
      return keyPairName;
   }

   public String getKeyPairFingerPrint() {
      return keyPairFingerPrint;
   }

   public String getPrivateKeyBody() {
      return privateKeyBody;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      KeyPairRequest that = (KeyPairRequest) o;
      return Objects.equal(keyPairName, that.keyPairName) &&
              Objects.equal(keyPairFingerPrint, that.keyPairFingerPrint) &&
              Objects.equal(privateKeyBody, that.privateKeyBody);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), keyPairName, keyPairFingerPrint, privateKeyBody);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this)
              .add("name", keyPairName)
              .add("keyPairFingerPrint", keyPairFingerPrint)
              .add("privateKeyBody", privateKeyBody)
              .toString();
   }
}
