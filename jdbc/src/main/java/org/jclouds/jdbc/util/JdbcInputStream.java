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
package org.jclouds.jdbc.util;

import org.jclouds.jdbc.entity.ChunkEntity;
import org.jclouds.jdbc.service.JdbcService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JdbcInputStream extends InputStream {

   private JdbcService jdbcService;

   private final List<Long> chunks;
   private ChunkEntity currentChunk;
   private int bytesRead;

   public JdbcInputStream(JdbcService jdbcService, List<Long> chunks) {
      this.jdbcService = checkNotNull(jdbcService, "jdbcService");
      // Need to remove duplicates due to https://hibernate.atlassian.net/browse/HHH-6783
      this.chunks = new ArrayList<Long>(new LinkedHashSet<Long>(checkNotNull(chunks, "chunks")));
      try {
         readNextChunk();
      } catch (IOException e) {
         throw new IllegalArgumentException(e);
      }
   }

   @Override
   public synchronized int read() throws IOException {
      if (hasFinished()) {
         return -1;
      }
      int b = currentChunk.getData()[bytesRead] & 0xff;
      bytesRead = bytesRead + 1;
      if (bytesRead >= currentChunk.getSize()) {
         readNextChunk();
      }
      return b;
   }

   private boolean hasFinished() {
      return currentChunk == null || (chunks.size() == 0 && bytesRead >= currentChunk.getSize());
   }

   private void readNextChunk() throws IOException {
      if (chunks.size() > 0) {
         this.currentChunk = jdbcService.findChunkById(chunks.get(0));
         if (currentChunk == null) {
            throw new IOException("Could not find chunk.");
         }
         chunks.remove(0);
         this.bytesRead = 0;
      }
   }

}
