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
 *                               Leibniz University Hannover, Germany
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

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Runtime configuration for the Zotero translation-server backed scraper.
 *
 * @author tvolker
 */
public class ZoteroTranslationServerConfig {

	/**
	 * Java system property containing the translation server base URL.
	 */
	public static final String PROPERTY_URL = "bibsonomy.scraper.zotero.url";
	/**
	 * Environment variable containing the translation server base URL.
	 */
	public static final String ENV_URL = "BIBSONOMY_SCRAPER_ZOTERO_URL";
	/**
	 * Java system property enabling/disabling the Zotero scraper.
	 */
	public static final String PROPERTY_ENABLED = "bibsonomy.scraper.zotero.enabled";
	/**
	 * Environment variable enabling/disabling the Zotero scraper.
	 */
	public static final String ENV_ENABLED = "BIBSONOMY_SCRAPER_ZOTERO_ENABLED";
	/**
	 * Java system property enabling/disabling fallback to legacy scrapers.
	 */
	public static final String PROPERTY_LEGACY_FALLBACK_ENABLED = "bibsonomy.scraper.legacyFallback.enabled";
	/**
	 * Environment variable enabling/disabling fallback to legacy scrapers.
	 */
	public static final String ENV_LEGACY_FALLBACK_ENABLED = "BIBSONOMY_SCRAPER_LEGACY_FALLBACK_ENABLED";
	/**
	 * Java system property for the Zotero connection timeout.
	 */
	public static final String PROPERTY_CONNECT_TIMEOUT = "bibsonomy.scraper.zotero.connectTimeoutMillis";
	/**
	 * Environment variable for the Zotero connection timeout.
	 */
	public static final String ENV_CONNECT_TIMEOUT = "BIBSONOMY_SCRAPER_ZOTERO_CONNECT_TIMEOUT_MILLIS";
	/**
	 * Java system property for the Zotero socket timeout.
	 */
	public static final String PROPERTY_SOCKET_TIMEOUT = "bibsonomy.scraper.zotero.socketTimeoutMillis";
	/**
	 * Environment variable for the Zotero socket timeout.
	 */
	public static final String ENV_SOCKET_TIMEOUT = "BIBSONOMY_SCRAPER_ZOTERO_SOCKET_TIMEOUT_MILLIS";

	private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
	private static final int DEFAULT_SOCKET_TIMEOUT = 20000;

	private final String baseUrl;
	private final boolean zoteroEnabled;
	private final boolean legacyFallbackEnabled;
	private final int connectTimeout;
	private final int socketTimeout;

	/**
	 * @return configuration loaded from system properties and environment variables
	 */
	public static ZoteroTranslationServerConfig fromEnvironment() {
		final String baseUrl = getSetting(PROPERTY_URL, ENV_URL);
		final boolean zoteroEnabled = getBooleanSetting(PROPERTY_ENABLED, ENV_ENABLED, present(baseUrl));
		final boolean legacyFallbackEnabled = getBooleanSetting(PROPERTY_LEGACY_FALLBACK_ENABLED, ENV_LEGACY_FALLBACK_ENABLED, true);
		final int connectTimeout = getIntegerSetting(PROPERTY_CONNECT_TIMEOUT, ENV_CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
		final int socketTimeout = getIntegerSetting(PROPERTY_SOCKET_TIMEOUT, ENV_SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
		return new ZoteroTranslationServerConfig(baseUrl, zoteroEnabled, legacyFallbackEnabled, connectTimeout, socketTimeout);
	}

	/**
	 * @param baseUrl translation server base URL
	 * @param zoteroEnabled whether the Zotero scraper is enabled
	 * @param legacyFallbackEnabled whether legacy fallback is enabled
	 * @param connectTimeout connect timeout in milliseconds
	 * @param socketTimeout socket timeout in milliseconds
	 */
	public ZoteroTranslationServerConfig(final String baseUrl, final boolean zoteroEnabled, final boolean legacyFallbackEnabled, final int connectTimeout, final int socketTimeout) {
		this.baseUrl = normalizeBaseUrl(baseUrl);
		this.zoteroEnabled = zoteroEnabled;
		this.legacyFallbackEnabled = legacyFallbackEnabled;
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
	}

	/**
	 * @return translation server base URL
	 */
	public String getBaseUrl() {
		return this.baseUrl;
	}

	/**
	 * @return <code>true</code> if the Zotero scraper should be used
	 */
	public boolean isZoteroEnabled() {
		return this.zoteroEnabled && present(this.baseUrl);
	}

	/**
	 * @return <code>true</code> if legacy fallback should be used
	 */
	public boolean isLegacyFallbackEnabled() {
		return this.legacyFallbackEnabled;
	}

	/**
	 * @return connect timeout in milliseconds
	 */
	public int getConnectTimeout() {
		return this.connectTimeout;
	}

	/**
	 * @return socket timeout in milliseconds
	 */
	public int getSocketTimeout() {
		return this.socketTimeout;
	}

	private static String getSetting(final String property, final String environment) {
		final String propertyValue = System.getProperty(property);
		if (present(propertyValue)) {
			return propertyValue.trim();
		}

		final String environmentValue = System.getenv(environment);
		if (present(environmentValue)) {
			return environmentValue.trim();
		}

		return null;
	}

	private static boolean getBooleanSetting(final String property, final String environment, final boolean defaultValue) {
		final String value = getSetting(property, environment);
		if (!present(value)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	private static int getIntegerSetting(final String property, final String environment, final int defaultValue) {
		final String value = getSetting(property, environment);
		if (!present(value)) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (final NumberFormatException ex) {
			return defaultValue;
		}
	}

	private static String normalizeBaseUrl(final String baseUrl) {
		if (!present(baseUrl)) {
			return null;
		}
		String normalized = baseUrl.trim();
		while (normalized.endsWith("/")) {
			normalized = normalized.substring(0, normalized.length() - 1);
		}
		return normalized;
	}
}
