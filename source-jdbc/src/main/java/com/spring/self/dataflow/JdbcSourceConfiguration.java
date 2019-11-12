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

import com.alibaba.druid.pool.DruidDataSource;
import com.spring.self.dataflow.trigger.TriggerConfiguration;
import com.spring.self.dataflow.trigger.TriggerPropertiesMaxMessagesDefaultOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.support.Consumer;
import org.springframework.integration.scheduling.PollerMetadata;

import javax.sql.DataSource;

/**
 * A module that reads data from an RDBMS using JDBC and creates a payload with the data.
 *
 * @author lichao
 */
@EnableBinding(Source.class)
@Import(TriggerConfiguration.class)
@EnableConfigurationProperties({JdbcSourceProperties.class, TriggerPropertiesMaxMessagesDefaultOne.class})
@SpringBootApplication
public class JdbcSourceConfiguration {

	@Autowired
	@Qualifier("defaultPoller")
	private PollerMetadata poller;

	@Autowired
	private JdbcSourceProperties properties;

	@Autowired
	private Source source;

	@Bean
	public MessageSource<Object> jdbcMessageSource() {
		JdbcPollingChannelAdapterSpec jdbcPollingChannelAdapterSpec =
				new JdbcPollingChannelAdapterSpec(this.getDataSource(), this.properties.getQuery());
		jdbcPollingChannelAdapterSpec.setMaxRowsPerPoll(this.properties.getMaxRowsPerPoll());
		//jdbcPollingChannelAdapterSpec.setUpdateSql(this.properties.getUpdate());
		return jdbcPollingChannelAdapterSpec;
	}

	private DataSource getDataSource() {
		DruidDataSource dds = new DruidDataSource();
		dds.setDriverClassName("com.mysql.jdbc.Driver");
		dds.setUrl("jdbc:mysql://192.168.1.112:3306/csa");
		dds.setUsername("root");
		dds.setPassword("123456");
		dds.setDbType("mysql");
		return dds;
	}

	@Bean
	public IntegrationFlow pollingFlow() {
		IntegrationFlowBuilder flowBuilder = IntegrationFlows.from(jdbcMessageSource(),
				new Consumer<SourcePollingChannelAdapterSpec>() {

					@Override
					public void accept(SourcePollingChannelAdapterSpec sourcePollingChannelAdapterSpec) {
						sourcePollingChannelAdapterSpec.poller(poller);
					}

				});
		if (this.properties.isSplit()) {
			flowBuilder.split();
		}
		flowBuilder.channel(this.source.output());
		return flowBuilder.get();
	}

	public static void main(String[] args) {
		SpringApplication.run(JdbcSourceConfiguration.class,args);
	}
}