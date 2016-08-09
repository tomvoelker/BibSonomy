/**
 * BibSonomy-Scrapingservice - Stand-alone web application for web page scrapers (see bibsonomy-scraper)
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.scrapingservice.writers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.regex.Pattern;

import org.bibsonomy.common.Pair;
import org.bibsonomy.util.JSONUtils;
import org.bibsonomy.util.StringUtils;

/**
 * Writes given lists in JSON format.
 * 
 * @author:  rja
 * 
 */
public class JSONWriter {

	private static final String[] depths = new String[] {
		"", 
		"\t", 
		"\t\t", 
		"\t\t\t", 
		"\t\t\t\t", 
		"\t\t\t\t\t", 
		"\t\t\t\t\t\t", 
		"\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t\t\t\t\t",
	};
	
	private final OutputStream outputStream;
	
	public JSONWriter (final OutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}
	
	public void write(int depth, final Collection<Pair<Pattern, Pattern>> patterns) throws UnsupportedEncodingException, IOException {
		int ctr = 0;
		final int max = patterns.size();
		write(depth, "[\n");
		for (final Pair<Pattern, Pattern> tuple : patterns) {
			ctr++;
			depth++;
			write(depth, "{\n");
			
			depth++;
			write(depth, "\"host\" : \"" + quoteJSON(tuple.getFirst())  + "\",\n");
			write(depth, "\"path\" : \"" + quoteJSON(tuple.getSecond()) + "\"\n");
			depth--;
			
			write(depth, "}");
			if (ctr < max) {
				/*
				 * not the last element: print delimiter
				 */
				write(",");
			}
			write("\n");
			depth--;
		}
		write(depth, "]\n");
	}
	
	/**
	 * Quotes a String such that it is usable for JSON.
	 * 
	 * @param value
	 * @return The quoted String.
	 */
	public static String quoteJSON(final Pattern value) {
		if (!present(value)) {
			return null;
		}
		return JSONUtils.quoteJSON(value.toString());
	}

	public void write(final String s) throws UnsupportedEncodingException, IOException {
		outputStream.write(s.getBytes(StringUtils.CHARSET_UTF_8));
	}
	
	public void write(final int depth, final String s) throws UnsupportedEncodingException, IOException {
		outputStream.write((getDepth(depth) + s).getBytes(StringUtils.CHARSET_UTF_8));
	}
	
	private static String getDepth(final int depth) {
		if (depth >= depths.length) return depths[depths.length - 1];
		return depths[depth];
	}
}

