/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover,
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scraper.zotero;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link ZoteroTranslationServerConfig}.
 *
 * @author tvolker
 */
public class ZoteroTranslationServerConfigTest {

	private String previousUrl;
	private String previousEnabled;
	private String previousLegacyFallbackEnabled;

	/**
	 *
	 */
	@Before
	public void setUp() {
		this.previousUrl = System.getProperty(ZoteroTranslationServerConfig.PROPERTY_URL);
		this.previousEnabled = System.getProperty(ZoteroTranslationServerConfig.PROPERTY_ENABLED);
		this.previousLegacyFallbackEnabled = System.getProperty(ZoteroTranslationServerConfig.PROPERTY_LEGACY_FALLBACK_ENABLED);
		System.clearProperty(ZoteroTranslationServerConfig.PROPERTY_URL);
		System.clearProperty(ZoteroTranslationServerConfig.PROPERTY_ENABLED);
		System.clearProperty(ZoteroTranslationServerConfig.PROPERTY_LEGACY_FALLBACK_ENABLED);
	}

	/**
	 *
	 */
	@After
	public void tearDown() {
		restoreProperty(ZoteroTranslationServerConfig.PROPERTY_URL, this.previousUrl);
		restoreProperty(ZoteroTranslationServerConfig.PROPERTY_ENABLED, this.previousEnabled);
		restoreProperty(ZoteroTranslationServerConfig.PROPERTY_LEGACY_FALLBACK_ENABLED, this.previousLegacyFallbackEnabled);
	}

	/**
	 *
	 */
	@Test
	public void testConfiguredUrlDisablesLegacyFallbackByDefault() {
		assumeTrue("Unset " + ZoteroTranslationServerConfig.ENV_LEGACY_FALLBACK_ENABLED + " for this default-value test.",
						!hasEnvironmentSetting(ZoteroTranslationServerConfig.ENV_LEGACY_FALLBACK_ENABLED));
		System.setProperty(ZoteroTranslationServerConfig.PROPERTY_URL, "http://zotero.example/");

		final ZoteroTranslationServerConfig config = ZoteroTranslationServerConfig.fromEnvironment();

		assertEquals("http://zotero.example", config.getBaseUrl());
		assertTrue(config.isZoteroEnabled());
		assertFalse(config.isLegacyFallbackEnabled());
	}

	/**
	 *
	 */
	@Test
	public void testExplicitLegacyFallbackCanBeEnabledDuringRollout() {
		System.setProperty(ZoteroTranslationServerConfig.PROPERTY_URL, "http://zotero.example");
		System.setProperty(ZoteroTranslationServerConfig.PROPERTY_LEGACY_FALLBACK_ENABLED, "true");

		final ZoteroTranslationServerConfig config = ZoteroTranslationServerConfig.fromEnvironment();

		assertTrue(config.isZoteroEnabled());
		assertTrue(config.isLegacyFallbackEnabled());
	}

	private static void restoreProperty(final String property, final String value) {
		if (value == null) {
			System.clearProperty(property);
			return;
		}
		System.setProperty(property, value);
	}

	private static boolean hasEnvironmentSetting(final String environmentVariable) {
		final String value = System.getenv(environmentVariable);
		return value != null && value.trim().length() > 0;
	}
}
