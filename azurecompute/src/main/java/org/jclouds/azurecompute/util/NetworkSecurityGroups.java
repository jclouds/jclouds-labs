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
package org.jclouds.azurecompute.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.domain.NetworkSecurityGroup;
import org.jclouds.azurecompute.domain.Rule;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

public class NetworkSecurityGroups {

   public static List<Rule> getCustomRules(NetworkSecurityGroup networkSecurityGroup) {
      final List<Rule> rules = networkSecurityGroup.rules();
      return FluentIterable.from(rules)
              .filter(Predicates.notNull())
              .filter(new Predicate<Rule>() {
                 @Override
                 public boolean apply(Rule rule) {
                    return rule.isDefault() == null || !rule.isDefault();
                 }
              })
              .toSortedList(new Comparator<Rule>() {
                 @Override
                 public int compare(Rule r1, Rule r2) {
                    int p1 = Integer.parseInt(r1.priority());
                    int p2 = Integer.parseInt(r2.priority());
                    return p1 < p2 ? -1 : p1 == p2 ? 0 : 1;

                 }
              });
   }

   public static int getFirstAvailablePriority(List<Rule> rules) {
      int priority;
      if (rules.isEmpty()) {
         priority = 100;
      } else {
         priority = Integer.parseInt(Collections.max(rules, new Comparator<Rule>() {
            @Override
            public int compare(Rule rule1, Rule rule2) {
               return Integer.valueOf(rule1.priority()).compareTo(Integer.valueOf(rule2.priority()));
            }
         }).priority()) + 1;
      }
      return priority;
   }

   public static String createRuleName(int fromPort, int toPort) {
      return String.format(AzureComputeProperties.TCP_RULE_FORMAT, fromPort, toPort);
   }

}
