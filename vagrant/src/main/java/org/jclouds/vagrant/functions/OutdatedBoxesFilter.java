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
package org.jclouds.vagrant.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import vagrant.api.domain.Box;

public class OutdatedBoxesFilter implements Function<Collection<Box>, Collection<Box>> {
   private static final Comparator<Box> VERSION_COMPARATOR = new Comparator<Box>() {
      private final Ordering<Iterable<Comparable<?>>> LEXI_COMPARATOR = Ordering.natural().lexicographical();
      private final Function<String, Comparable<?>> NUMBER_PARSER = new Function<String, Comparable<?>>() {
         @Override
         public Comparable<?> apply(String input) {
            try {
               return Integer.parseInt(input);
            } catch (NumberFormatException e) {
               return input;
            }
         }
      };

      @Override
      public int compare(Box o1, Box o2) {
         int nameCompare = o1.getName().compareTo(o2.getName());
         if (nameCompare != 0) return nameCompare;

         Iterable<Comparable<?>> v1 = Iterables.transform(Splitter.on('.').split(o1.getVersion()), NUMBER_PARSER);
         Iterable<Comparable<?>> v2 = Iterables.transform(Splitter.on('.').split(o2.getVersion()), NUMBER_PARSER);
         return LEXI_COMPARATOR.compare(v1, v2);
      }
   };

   @Override
   public Collection<Box> apply(Collection<Box> input) {
      ArrayList<Box> sorted = new ArrayList<Box>(input);
      Collections.sort(sorted, VERSION_COMPARATOR);
      Map<String, Box> boxes = Maps.newHashMap();
      for (Box box : sorted) {
         boxes.put(box.getName(), box);
      }
      return boxes.values();
   }

}
