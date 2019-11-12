/*
 * Copyright 2015 the original author or authors.
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.tuple.Tuple;
import org.springframework.tuple.TupleBuilder;

/**
 * Integration Tests for JdbcSink. Uses hsqldb as a (real) embedded DB.
 *
 * @author Eric Bottard
 * @author Thomas Risberg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JdbcSinkIntegrationTests.JdbcSinkApplication.class})
@SpringBootTest({"server.port=-1"})
@DirtiesContext
public abstract class JdbcSinkIntegrationTests {

	@Autowired
	protected Sink channels;

	@SpringBootTest(value = {"jdbc.columns=a,b"})
	public static class StringPayloadInsertTests extends JdbcSinkIntegrationTests {

		@Test
		public void testInsertion() {
			String stringA = "aaaaa";
			String stringB = "bbbb";
			String stringC = "cccc";
			channels.input().send(MessageBuilder.withPayload(stringA).build());
			channels.input().send(MessageBuilder.withPayload(stringB).build());
			channels.input().send(MessageBuilder.withPayload(stringC).build());

		}


		@SpringBootTest(value = {"jdbc.columns=a,b"})
		public static class TuplePayloadInsertTests extends JdbcSinkIntegrationTests {

			@Test
			public void testInsertion() {
				Tuple tupleA = TupleBuilder.tuple().of("a", "hello1", "b", 42);
				Tuple tupleB = TupleBuilder.tuple().of("a", "hello2", "b", null);
				Tuple tupleC = TupleBuilder.tuple().of("a", "hello3");
				channels.input().send(MessageBuilder.withPayload(tupleA).build());
				channels.input().send(MessageBuilder.withPayload(tupleB).build());
				channels.input().send(MessageBuilder.withPayload(tupleC).build());

			}
		}

		@SpringBootTest(value = {"jdbc.columns=a,b"})
		public static class JsonStringPayloadInsertTests extends JdbcSinkIntegrationTests {

			@Test
			public void testInsertion() {
				String stringA = "{\"a\": \"hello1\", \"b\": 42}";
				String stringB = "{\"a\": \"hello2\", \"b\": null}";
				String stringC = "{\"a\": \"hello3\"}";
				channels.input().send(MessageBuilder.withPayload(stringA).build());
				channels.input().send(MessageBuilder.withPayload(stringB).build());
				channels.input().send(MessageBuilder.withPayload(stringC).build());
			}
		}

	}
		@SpringBootApplication
		public static class JdbcSinkApplication {

		}


}