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
package org.jclouds.vcloud.director.v1_5;

/**
 * Constants used by VCloudDirector apis
 */
public final class VCloudDirectorConstants {

   /** The XML namespace used by the apis. */
   public static final String VCLOUD_1_5_NS = "http://www.vmware.com/vcloud/v1.5";

   public static final String VCLOUD_VMW_NS = "http://www.vmware.com/schema/ovf";

   /** The property used to configure the timeout for task completion. */
   public static final String PROPERTY_VCLOUD_DIRECTOR_TIMEOUT_TASK_COMPLETED = "jclouds.vcloud-director.timeout.task-complete";

   public static final String PROPERTY_VCLOUD_DIRECTOR_VERSION_SCHEMA = "jclouds.vcloud-director.version.schema";

   /** TODO javadoc */
   public static final String PROPERTY_VCLOUD_DIRECTOR_XML_NAMESPACE = "jclouds.vcloud-director.xml.ns";
   
   /** TODO javadoc */
   public static final String PROPERTY_VCLOUD_DIRECTOR_XML_SCHEMA = "jclouds.vcloud-director.xml.schema";

   private VCloudDirectorConstants() {
      throw new AssertionError("intentionally unimplemented");
   }
}
