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
package org.jclouds.greenqloud.compute.suppliers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Singleton;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.ec2.compute.domain.EC2HardwareBuilder;
import org.jclouds.ec2.compute.suppliers.EC2HardwareSupplier;


import java.util.Set;

@Singleton
public class GreenQloudEC2HardwareSupplier extends EC2HardwareSupplier {

    @Override
    public Set<? extends Hardware> get() {
        return ImmutableSet.of(t1_nano().build(),
                t1_micro().build(),
                t1_milli().build(),
                m1_small().build(),
                m1_medium().build(),
                m1_large().build(),
                m1_xlarge().build(),
                m2_2xlarge().build());
    }

    public EC2HardwareBuilder t1_nano() {
        return new EC2HardwareBuilder("t1.nano")
                .ram(256)
                .processors(ImmutableList.of(new Processor(1, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(10.0f, true, true)));
    }

    public EC2HardwareBuilder t1_micro() {
        return new EC2HardwareBuilder("t1.micro")
                .ram(512)
                .processors(ImmutableList.of(new Processor(1, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(20.0f, true, true)));
    }

    public EC2HardwareBuilder t1_milli() {
        return new EC2HardwareBuilder("t1.milli")
                .ram(1024)
                .processors(ImmutableList.of(new Processor(1, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(40.0f, true, true)));
    }

    public EC2HardwareBuilder m1_small() {
        return new EC2HardwareBuilder("m1.small")
                .ram(2048)
                .processors(ImmutableList.of(new Processor(2, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(80.0f, true, true)));
    }

    public EC2HardwareBuilder m1_medium() {
        return new EC2HardwareBuilder("m1.medium")
                .ram(4096)
                .processors(ImmutableList.of(new Processor(4, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(160.0f, true, true)));
    }

    public EC2HardwareBuilder m1_large() {
        return new EC2HardwareBuilder("m1.large")
                .ram(8192)
                .processors(ImmutableList.of(new Processor(8, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(320.0f, true, true)));
    }

    public EC2HardwareBuilder m1_xlarge() {
        return new EC2HardwareBuilder("m1.xlarge")
                .ram(15872)
                .processors(ImmutableList.of(new Processor(16, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(640.0f, true, true)));
    }

    public EC2HardwareBuilder m2_2xlarge() {
        return new EC2HardwareBuilder("m2.2xlarge")
                .ram(30720)
                .processors(ImmutableList.of(new Processor(16, 1)))
                .volumes(ImmutableList.<Volume>of(new VolumeImpl(640.0f, true, true)));
    }
}