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
package org.jclouds.vagrant.internal;

import java.util.Collection;

import org.jclouds.compute.domain.Image;
import org.jclouds.vagrant.api.VagrantBoxApiFacade;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;

public class ImageSupplier<B> implements Supplier<Collection<Image>>, Function<String, Image> {
   private final Function<Collection<B>, Collection<B>> outdatedBoxesFilter;
   private final VagrantBoxApiFacade.Factory<B> cliFactory;
   private final Function<B, Image> boxToImage;

   @Inject
   ImageSupplier(Function<Collection<B>, Collection<B>> outdatedBoxesFilter,
         VagrantBoxApiFacade.Factory<B> cliFactory,
         Function<B, Image> boxToImage) {
      this.outdatedBoxesFilter = outdatedBoxesFilter;
      this.cliFactory = cliFactory;
      this.boxToImage = boxToImage;
   }

   @Override
   public Collection<Image> get() {
      Collection<B> boxes = outdatedBoxesFilter.apply(cliFactory.create().listBoxes());
      return Collections2.transform(boxes, boxToImage);
   }

   @Override
   public Image apply(String id) {
      B box = cliFactory.create().getBox(id);
      return boxToImage.apply(box);
   }

}
