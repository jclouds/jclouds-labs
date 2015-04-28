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
package org.jclouds.jdbc.repository;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.jclouds.jdbc.entity.Container;

import javax.persistence.EntityManager;
import java.util.List;

@Singleton
public class ContainerRepository extends GenericRepository<Container, Long> {

   @Inject
   private ContainerRepository(Provider<EntityManager> entityManager) {
      super(entityManager);
   }

   public Container findContainerByName(String name) {
      return entityManager.get().createQuery("SELECT c FROM " + entityClass.getName() + " c WHERE c.name = :name", entityClass)
            .setParameter("name", name)
            .getSingleResult();
   }

   public List<Container> findAllContainers() {
      return entityManager.get().createQuery("SELECT c FROM " + entityClass.getName() + " c", entityClass)
            .getResultList();
   }

   public void deleteContainerByName(String name) {
      delete(findContainerByName(name));
   }

}
