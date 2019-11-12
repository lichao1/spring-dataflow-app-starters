/*
 * Copyright 2015-2016 the original author or authors.
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
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

/**
 * A Processor app that transforms messages.
 *
 * @author lichao
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(TransformProcessorProperties.class)
@SpringBootApplication
public class TransformProcessorConfiguration {

	private static final Log logger = LogFactory.getLog(TransformProcessorConfiguration.class);

	@Autowired
	private TransformProcessorProperties properties;

	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public Object transform(Message<?> message) {
		System.out.println("message TransformProcessorConfiguration ====");
		logger.info("\"message TransformProcessorConfiguration ===="+message.getPayload());
		return properties.getExpression().getValue(message);
	}

	public static void main(String[] args) {
		SpringApplication.run(TransformProcessorConfiguration.class,args);
	}

}
