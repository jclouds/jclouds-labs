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
package org.apache.jclouds.profitbricks.rest.domain;

import com.google.auto.value.AutoValue;
import java.util.Date;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Metadata {

    public abstract Date createdDate();

    public abstract String createdBy();

    public abstract String etag();

    public abstract Date lastModifiedDate();

    public abstract String lastModifiedBy();

    public abstract State state();

    @SerializedNames({"createdDate", "createdBy", "etag", "lastModifiedDate", "lastModifiedBy", "state"})
    public static Metadata create(Date createdDate, String createdBy, String etag, Date lastModifiedDate, String lastModifiedBy, State state) {
        return new AutoValue_Metadata(createdDate, createdBy, etag, lastModifiedDate, lastModifiedBy, state);
    }
}
