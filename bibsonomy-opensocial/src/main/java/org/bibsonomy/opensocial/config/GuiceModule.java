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

import org.apache.shindig.auth.AnonymousAuthenticationHandler;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AlbumService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.MediaItemService;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.sample.oauth.SampleOAuthDataStore;
import org.apache.shindig.social.sample.spi.JsonDbOpensocialService;
import org.springframework.context.ApplicationContext;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.shindig.auth.SecurityTokenCodec;
import org.bibsonomy.opensocial.oauth.database.BibSonomyOAuthDataStore;

/**
 * Guice configuration module that binds required shindig bindings
 * see http://mail-archives.apache.org/mod_mbox/shindig-dev/201007.mbox/%3C4C37599A.9000600@epfl.ch%3E
 */
public class GuiceModule extends AbstractModule {

	/** The Constant PERSON_SPI_BEAN_NAME. */
	private static final String PERSON_SPI_BEAN_NAME = "personSpi";
	private static final String OAUTH_DATA_STORE_BEAN_NAME = "oAuthDataStore";

	@Override
	protected void configure() {
		// Get spring application context
		ApplicationContext applicationContext = ApplicationContextFactory.getApplicationContext();

		bind(String.class).annotatedWith(Names.named("shindig.canonical.json.db")).toInstance("testdb.json");
		//bind(String.class).annotatedWith(Names.named("shindig.signing.key-file")).toInstance("res://oauthconfig.json");
		
		
		bind(ActivityService.class).to(JsonDbOpensocialService.class);
		bind(AlbumService.class).to(JsonDbOpensocialService.class);
		bind(MediaItemService.class).to(JsonDbOpensocialService.class);
		bind(AppDataService.class).to(JsonDbOpensocialService.class);

		// Bind Mock Person Spi
		//bind(PersonService.class).to(JsonDbOpensocialService.class);
		this.bind(PersonService.class).toInstance((PersonService) applicationContext.getBean(PERSON_SPI_BEAN_NAME));
		
		bind(MessageService.class).to(JsonDbOpensocialService.class);
		//bind(OAuthDataStore.class).toInstance((OAuthDataStore) applicationContext.getBean(OAUTH_DATA_STORE_BEAN_NAME));
		//bind(OAuthDataStore.class).to(SampleOAuthDataStore.class);
		bind(OAuthDataStore.class).to(BibSonomyOAuthDataStore.class);
		
		bind(SecurityTokenCodec.class).to(org.bibsonomy.opensocial.security.BibSonomySecurityTokenCodec.class);
		/*
		bind(Boolean.class)
        .annotatedWith(Names.named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
        .toInstance(Boolean.TRUE);
        */

	}

}
