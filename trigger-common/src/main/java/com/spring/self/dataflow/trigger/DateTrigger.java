/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spring.self.dataflow.trigger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A {@link Trigger} implementation that enables execution on a series of Dates.
 * 
 * @author Glenn Renfro
 * @author Florent Biville
 */
public class DateTrigger implements Trigger {

	private final List<Date> nextFireDates = new ArrayList<Date>();

	private final Log logger = LogFactory.getLog(getClass());

	public DateTrigger(Date... dates) {
		for (Date date : dates) {
			addDate(date);
		}
		Collections.sort(nextFireDates);
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date result = null;
		if (nextFireDates.size() > 0) {
			try {
				result = nextFireDates.remove(0);
			}
			catch (IndexOutOfBoundsException e) {
				logger.debug(e.getMessage());
			}
		}
		return result;
	}

	private void addDate(Date date) {
		Assert.notNull(date, "Date must not be null");
		nextFireDates.add(date);
	}

}
