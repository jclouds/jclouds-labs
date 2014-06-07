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
package org.jclouds.abiquo.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.reflect.Reflection2.constructor;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.exception.WrapperException;
import org.jclouds.abiquo.domain.task.AsyncTask;
import org.jclouds.abiquo.domain.task.ConversionTask;
import org.jclouds.abiquo.domain.task.VirtualMachineTask;
import org.jclouds.abiquo.domain.task.VirtualMachineTemplateTask;
import org.jclouds.abiquo.domain.util.LinkUtils;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.WrapperDto;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.enums.TaskType;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
import com.google.inject.TypeLiteral;

/**
 * This class is used to decorate transport objects with high level
 * functionality.
 */
public abstract class DomainWrapper<T extends SingleResourceTransportDto> {
   /** The rest context. */
   protected ApiContext<AbiquoApi> context;

   /** The wrapped object. */
   protected T target;

   protected DomainWrapper(final ApiContext<AbiquoApi> context, final T target) {
      super();
      this.context = checkNotNull(context, "context");
      this.target = checkNotNull(target, "target");
   }

   /**
    * Returns the URI that identifies the transport object
    * 
    * @return The URI identifying the transport object
    */
   public URI getURI() {
      RESTLink link = LinkUtils.getSelfLink(target);
      return link == null ? null : URI.create(link.getHref());
   }

   /**
    * Returns the wrapped object.
    */
   public T unwrap() {
      return target;
   }

   /**
    * Refresh the state of the current object.
    */
   @SuppressWarnings("unchecked")
   public void refresh() {
      RESTLink link = checkNotNull(LinkUtils.getSelfLink(target), ValidationErrors.MISSING_REQUIRED_LINK + " edit/self");

      HttpResponse response = context.getApi().get(link);

      ParseXMLWithJAXB<T> parser = new ParseXMLWithJAXB<T>(context.utils().xml(), TypeLiteral.get((Class<T>) target
            .getClass()));

      target = parser.apply(response);
   }

   /**
    * Read the ID of the parent resource from the given link.
    * 
    * @param parentLinkRel
    *           The link to the parent resource.
    * @return The ID of the parent resource.
    */
   protected Integer getParentId(final String parentLinkRel) {
      return target.getIdFromLink(parentLinkRel);
   }

   /**
    * Wraps an object in the given wrapper class.
    */
   public static <T extends SingleResourceTransportDto, W extends DomainWrapper<T>> W wrap(
         final ApiContext<AbiquoApi> context, final Class<W> wrapperClass, @Nullable final T target) {
      if (target == null) {
         return null;
      }

      try {
         Invokable<W, W> cons = constructor(wrapperClass, ApiContext.class, target.getClass());
         return cons.invoke(null, context, target);
      } catch (InvocationTargetException e) {
         throw new WrapperException(wrapperClass, target, e.getTargetException());
      } catch (IllegalAccessException e) {
         throw new WrapperException(wrapperClass, target, e);
      }
   }

   /**
    * Wrap a collection of objects to the given wrapper class.
    */
   public static <T extends SingleResourceTransportDto, W extends DomainWrapper<T>> Iterable<W> wrap(
         final ApiContext<AbiquoApi> context, final Class<W> wrapperClass, @Nullable final Iterable<T> targets) {
      if (targets == null) {
         return null;
      }

      return transform(targets, new Function<T, W>() {
         @Override
         public W apply(final T input) {
            return wrap(context, wrapperClass, input);
         }
      });
   }

   /**
    * Unwrap a collection of objects.
    */
   public static <T extends SingleResourceTransportDto, W extends DomainWrapper<T>> Iterable<T> unwrap(
         final Iterable<W> targets) {
      return transform(targets, new Function<W, T>() {
         @Override
         public T apply(final W input) {
            return input.unwrap();
         }
      });
   }

   /**
    * Update or creates a link of "target" with the uri of a link from "source".
    */
   protected <T1 extends SingleResourceTransportDto, T2 extends SingleResourceTransportDto> void updateLink(
         final T1 target, final String targetLinkRel, final T2 source, final String sourceLinkRel) {
      RESTLink parent = null;

      checkNotNull(source.searchLink(sourceLinkRel), ValidationErrors.MISSING_REQUIRED_LINK);

      // Insert
      if ((parent = target.searchLink(targetLinkRel)) == null) {
         target.addLink(new RESTLink(targetLinkRel, source.searchLink(sourceLinkRel).getHref()));
      }
      // Replace
      else {
         parent.setHref(source.searchLink(sourceLinkRel).getHref());
      }
   }

   /**
    * Join a collection of {@link WrapperDto} objects in a single collection
    * with all the elements of each wrapper object.
    */
   public static <T extends SingleResourceTransportDto> Iterable<T> join(
         final Iterable<? extends WrapperDto<T>> collection) {
      return concat(transform(collection, new Function<WrapperDto<T>, Collection<T>>() {
         @Override
         public Collection<T> apply(WrapperDto<T> input) {
            return input.getCollection();
         }
      }));
   }

   /**
    * Utility method to get an {@link AsyncTask} given an
    * {@link AcceptedRequestDto}.
    * 
    * @param acceptedRequest
    *           The accepted request dto.
    * @return The async task.
    */
   protected AsyncTask<?, ?> getTask(final AcceptedRequestDto<String> acceptedRequest) {
      RESTLink taskLink = acceptedRequest.getStatusLink();
      checkNotNull(taskLink, ValidationErrors.MISSING_REQUIRED_LINK + AsyncTask.class);

      // This will return null on untrackable tasks
      TaskDto dto = context.getApi().getTaskApi().getTask(taskLink);
      return newTask(context, dto);
   }

   /**
    * Utility method to get all {@link AsyncTask} related to an
    * {@link AcceptedRequestDto}.
    * 
    * @param acceptedRequest
    *           The accepted request dto.
    * @return The async task array.
    */
   protected AsyncTask<?, ?>[] getTasks(final AcceptedRequestDto<String> acceptedRequest) {
      List<AsyncTask<?, ?>> tasks = Lists.newArrayList();

      for (RESTLink link : acceptedRequest.getLinks()) {
         // This will return null on untrackable tasks
         TaskDto dto = context.getApi().getTaskApi().getTask(link);
         if (dto != null) {
            tasks.add(newTask(context, dto));
         }
      }

      AsyncTask<?, ?>[] taskArr = new AsyncTask<?, ?>[tasks.size()];
      return tasks.toArray(taskArr);
   }

   /**
    * Creates a new {@link AsyncTask} for the given {@link TaskDto} and the
    * given result class.
    * 
    * @param context
    *           The API context.
    * @param dto
    *           The dto used to generate the domain object.
    * @return The task domain object.
    */
   protected static AsyncTask<?, ?> newTask(final ApiContext<AbiquoApi> context, final TaskDto dto) {
      // Can be null in untrackable tasks
      if (dto == null) {
         return null;
      }

      Class<? extends AsyncTask<?, ?>> taskClass = null;

      switch (dto.getType().getOwnerType()) {
         case CONVERSION:
            taskClass = ConversionTask.class;
            break;
         case VIRTUAL_MACHINE_TEMPLATE:
            taskClass = VirtualMachineTemplateTask.class;
            break;
         case VIRTUAL_MACHINE:
            // A VirtualMachine task can generate a template (if task is an
            // instance)
            taskClass = dto.getType() == TaskType.INSTANCE || dto.getType() == TaskType.INSTANCE_PERSISTENT ? VirtualMachineTemplateTask.class
                  : VirtualMachineTask.class;
            break;
      }

      try {
         Invokable<? extends AsyncTask<?, ?>, ? extends AsyncTask<?, ?>> cons = constructor(taskClass,
               ApiContext.class, dto.getClass());
         return cons.invoke(null, context, dto);
      } catch (InvocationTargetException e) {
         throw new WrapperException(taskClass, dto, e.getTargetException());
      } catch (IllegalAccessException e) {
         throw new WrapperException(taskClass, dto, e);
      }
   }

}
