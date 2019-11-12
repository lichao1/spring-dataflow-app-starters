/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spring.self.dataflow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Integration Tests for JdbcSource. Uses hsqldb as a (real) embedded DB.
 *
 * @author Thomas Risberg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JdbcSourceIntegrationTests.JdbcSourceApplication.class,
		EmbeddedDataSourceConfiguration.class})
@DirtiesContext
public abstract class JdbcSourceIntegrationTests {

	//@Bindings(JdbcSourceConfiguration.class)
	@Autowired
	protected Source source;

	@Autowired
	protected JdbcOperations jdbcOperations;

	@Autowired
	protected MessageCollector messageCollector;

	/*@IntegrationTest(value={"source-jdbc.query= select * from STREAM_DEFINITIONS","trigger.cron=0 0/5 * * * ? "
			                 "source-jdbc.maxRowsPerPoll=10"})*/
	@SpringBootTest(value={"source-jdbc.query= select * from STREAM_DEFINITIONS",
			"source-jdbc.maxRowsPerPoll=10"})
	public static class DefaultBehaviorTests extends JdbcSourceIntegrationTests {

		@Test
		public void testExtraction() throws InterruptedException {
			Message<?> received = messageCollector.forChannel(source.output()).poll(10, TimeUnit.SECONDS);
			if(null==received){
				System.out.println("this time return null");
			}else{
			List<Map<String,String>> listMap= (List<Map<String,String>>) received.getPayload();
				System.out.println(listMap);
			}
			//Thread.sleep(1000*60*60*24);
			//assertNotNull(received);
		}

	}

	@SpringBootApplication
	public static class JdbcSourceApplication {

	}

}