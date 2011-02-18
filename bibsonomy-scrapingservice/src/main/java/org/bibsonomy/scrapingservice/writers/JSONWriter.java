/**
 *
 *  BibSonomy-Scrapingservice - Web application to test the BibSonomy web page scrapers (see
 * 		bibsonomy-scraper)
 *
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scrapingservice.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.regex.Pattern;

import org.bibsonomy.scraper.Tuple;

/**
 * Writes given lists in JSON format.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
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
	
	public void write(int depth, final Collection<Tuple<Pattern, Pattern>> patterns) throws UnsupportedEncodingException, IOException {
		int ctr = 0;
		int max = patterns.size();
		write(depth, "[\n");
		for (final Tuple<Pattern, Pattern> tuple : patterns) {
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
	 * FIXME: copied from Functions.java
	 * 
	 * @param value
	 * @return The quoted String.
	 */
	public static String quoteJSON(final Pattern value) {
		if(value == null)
			return null;
		final StringBuffer sb = new StringBuffer();
		escapeJSON(value.toString(), sb);
		return sb.toString();
	}

	/**
	 * Taken from http://code.google.com/p/json-simple/
	 * 
	 * FIXME: copied from Functions.java
	 * 
	 * @param s - Must not be null.
	 * @param sb
	 */
	private static void escapeJSON(final String s, final StringBuffer sb) {
		for (int i = 0; i < s.length(); i++) {
			char ch=s.charAt(i);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				// Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
					sb.append(ch);
				}
			}
		}
	}
	
	public void write(final String s) throws UnsupportedEncodingException, IOException {
		outputStream.write(s.getBytes("UTF-8"));
	}
	
	public void write(final int depth, final String s) throws UnsupportedEncodingException, IOException {
		outputStream.write((getDepth(depth) + s).getBytes("UTF-8"));
	}
	
	
	private static String getDepth(final int depth) {
		if (depth >= depths.length) return depths[depths.length - 1];
		return depths[depth];
	}
	
	
}

