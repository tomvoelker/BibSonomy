/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.bibsonomy.opensocial.config;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A factory for managing the ApplicationContext instance.
 *
 */
public class ApplicationContextFactory {

  /** The Constant LOCATIONS. */
  private static final String[] LOCATIONS = {"application-context.xml"};

  /** The Constant CONTEXT. */
  private static final AbstractApplicationContext CONTEXT = new ClassPathXmlApplicationContext(LOCATIONS);

  /**
   * Instantiates a new application context factory.
   */
  private ApplicationContextFactory() {
  }

  /**
   * Gets the application context.
   *
   * @return the application context
   */
  public static AbstractApplicationContext getApplicationContext() {
    return CONTEXT;
  }

}
