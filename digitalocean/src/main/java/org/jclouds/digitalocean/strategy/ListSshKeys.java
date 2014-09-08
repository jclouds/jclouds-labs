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
package org.jclouds.digitalocean.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.util.concurrent.Futures.allAsList;
import static com.google.common.util.concurrent.Futures.getUnchecked;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.digitalocean.DigitalOceanApi;
import org.jclouds.digitalocean.domain.SshKey;
import org.jclouds.digitalocean.features.KeyPairApi;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.assistedinject.Assisted;

/**
 * The {@link org.jclouds.digitalocean.features.KeyPairApi} only returns the id and name of each key but not the actual
 * public key when listing all keys.
 * <p>
 * This strategy provides a helper to get all the keys with all details populated.
 */
@Singleton
public class ListSshKeys {

   public interface Factory {
      ListSshKeys create(ListeningExecutorService executor);
   }

   private final KeyPairApi keyPairApi;
   private final ListeningExecutorService executor;

   @Inject
   ListSshKeys(DigitalOceanApi api, @Assisted ListeningExecutorService executor) {
      checkNotNull(api, "api cannot be null");
      this.executor = checkNotNull(executor, "executor cannot be null");
      this.keyPairApi = api.getKeyPairApi();
   }

   public List<SshKey> execute() {
      List<SshKey> keys = keyPairApi.list();

      ListenableFuture<List<SshKey>> futures = allAsList(transform(keys,
            new Function<SshKey, ListenableFuture<SshKey>>() {
               @Override
               public ListenableFuture<SshKey> apply(final SshKey input) {
                  return executor.submit(new Callable<SshKey>() {
                     @Override
                     public SshKey call() throws Exception {
                        return keyPairApi.get(input.getId());
                     }
                  });
               }
            }));

      return getUnchecked(futures);
   }
}
