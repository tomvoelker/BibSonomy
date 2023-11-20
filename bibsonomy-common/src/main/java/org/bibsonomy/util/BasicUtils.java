/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * some basic utils
 *
 * @author dzo
 */
public final class BasicUtils {
	private static final Log log = LogFactory.getLog(BasicUtils.class);
	
	private BasicUtils() {}
	
	private static final String PROPERTIES_FILE_NAME = "org/bibsonomy/common/bibsonomy-common.properties";
	private static final String PROPERTIES_VERSION_KEY = "version";
	
	/** the version of the system */
	public static final String VERSION;
	
	static {
		String version = "unknown";
		/*
		 * load version of client from properties file
		 */
		try {
			final Properties properties = new Properties();
			
			final InputStream stream = BasicUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
			properties.load(stream);
			stream.close();
			
			version = properties.getProperty(PROPERTIES_VERSION_KEY);
		} catch (final IOException ex) {
			log.error("could not load version", ex);
		}
		VERSION = version;
	}

	/**
	 * iterates over a list which is queried with limit and offset
	 * @param limitOffsetIterator
	 * @param limit
	 * @param <T>
	 */
	public static <T> void iterateListWithLimitAndOffset(final BiFunction<Integer, Integer, List<T>> limitOffsetIterator, Consumer<List<T>> itemWorker, final int limit) {
		int size;

		int offset = 0;

		do {
			final List<T> items = limitOffsetIterator.apply(limit, offset);
			itemWorker.accept(items);
			offset += limit;
			size = items.size();
		} while (size == limit);
	}

}
