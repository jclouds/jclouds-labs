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
import org.jclouds.jdbc.entity.BlobEntity;
import org.jclouds.jdbc.entity.BlobEntityPK;
import org.jclouds.jdbc.entity.ContainerEntity;

import javax.persistence.EntityManager;
import java.util.List;

@Singleton
public class BlobRepository extends GenericRepository<BlobEntity, BlobEntityPK> {

   @Inject
   private BlobRepository(Provider<EntityManager> entityManager) {
      super(entityManager);
   }

    public List<BlobEntity> findBlobsByContainer(ContainerEntity containerEntity) {
        return entityManager.get().createQuery("SELECT b FROM " + entityClass.getName() + " b "
              + "WHERE"
              + " b.containerEntity = :containerEntity", entityClass)
                .setParameter("containerEntity", containerEntity)
                .getResultList();
    }

   public List<BlobEntity> findBlobsByDirectory(ContainerEntity containerEntity, String directory) {
      return entityManager.get().createQuery("SELECT b FROM " + entityClass.getName() + " b "
            + "WHERE b.containerEntity = :containerEntity AND b.key != :directoryName AND b.key LIKE :directoryLike ", entityClass)
            .setParameter("containerEntity", containerEntity)
            .setParameter("directoryName", directory)
            .setParameter("directoryLike", directory + "%")
            .getResultList();
   }

}
