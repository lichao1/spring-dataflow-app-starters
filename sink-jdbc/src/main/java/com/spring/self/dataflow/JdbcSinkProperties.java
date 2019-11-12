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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;

/**
 * Holds configuration properties for the Jdbc Sink module.
 *
 * @author lichao
 */
@ConfigurationProperties("sink-jdbc")
public class JdbcSinkProperties {

	/**
	 * The name of the table to write into.
	 */
	private String tableName;


	/**
	 * 'true', 'false' or the location of a custom initialization script for the table.
	 */
	private String initialize = "false";


}