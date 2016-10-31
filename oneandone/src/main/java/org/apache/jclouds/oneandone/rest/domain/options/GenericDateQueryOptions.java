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
package org.apache.jclouds.oneandone.rest.domain.options;

import java.util.Date;
import org.apache.jclouds.oneandone.rest.domain.Types.CustomPeriodType;
import org.apache.jclouds.oneandone.rest.domain.Types.PeriodType;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.options.BaseHttpRequestOptions;

public class GenericDateQueryOptions extends BaseHttpRequestOptions {

   public static final String PERIOD = "period";
   public static final String STARTDATE = "start_date";
   public static final String ENDDATE = "end_date";
   private static final DateService DATE_SERVICE = new SimpleDateFormatDateService();

   public GenericDateQueryOptions customPeriod(Date startDate, Date endDate) {

      queryParameters.put(PERIOD, CustomPeriodType.CUSTOM.toString());
      queryParameters.put(STARTDATE, DATE_SERVICE.iso8601SecondsDateFormat(startDate));
      queryParameters.put(ENDDATE, DATE_SERVICE.iso8601SecondsDateFormat(endDate));
      return this;
   }

   public GenericDateQueryOptions fixedPeriods(PeriodType period) {

      queryParameters.put(PERIOD, period.toString());
      return this;
   }
}
