/*
 * Copyright 2015 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import java.util.Set;

/**
 * A module that writes its incoming payload to an RDBMS using JDBC.
 *
 * @author lichao
 */
@EnableBinding(Sink.class)
@EnableConfigurationProperties(JdbcSinkProperties.class)
@SpringBootApplication
public class JdbcSinkConfiguration {

	private static final Log logger = LogFactory.getLog(JdbcSinkConfiguration.class);


	@Autowired
	private JdbcSinkProperties properties;


	@ServiceActivator(inputChannel = Sink.INPUT)
	public void sink(Message<?> message) {

		String ss=message.getPayload().toString();

		logger.info("receive meaage="+ss);
		System.out.println("sout receive meaage="+ss);

	}



	/*
	 * This is needed to prevent a circular dependency issue with the creation of the converter.
	 */


	private String generateSql(String tableName, Set<String> columns) {
		StringBuilder builder = new StringBuilder("INSERT INTO ");
		StringBuilder questionMarks = new StringBuilder(") VALUES (");
		builder.append(tableName).append("(");
		int i = 0;

		for (String column : columns) {
			if (i++ > 0) {
				builder.append(", ");
				questionMarks.append(", ");
			}
			builder.append(column);
			questionMarks.append(':' + column);
		}
		builder.append(questionMarks).append(")");
		return builder.toString();
	}

	public static void main(String[] args) {
		SpringApplication.run(JdbcSinkConfiguration.class,args);
	}

}