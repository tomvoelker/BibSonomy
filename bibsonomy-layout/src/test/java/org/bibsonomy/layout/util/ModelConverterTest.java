/**
 *  
 *  BibSonomy-Layout - Layout engine for the webapp.
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

package org.bibsonomy.layout.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.imports.BibtexParser;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.Assert;
import org.junit.Test;

import bibtex.parser.ParseException;

public class ModelConverterTest {

	private final String bibtexSource = "@book{Loudon2003,\r\n"
			+ "  title = {C++. Kurz und gut.},\r\n"
			+ "  publisher = {O'Reilly},\r\n" + "  year = {2003},\r\n"
			+ "  author = {Kyle Loudon},\r\n" + "  month = {08},\r\n"
			+ "  booktitle = {C++. Kurz und gut.},\r\n"
			+ "  isbn = {3897212625},\r\n" + "  keywords = {boost c++},\r\n"
			+ "  owner = {dasboogie},\r\n"
			+ "  timestamp = {2007-08-18 11:25:11}\r\n" + "}";

	@Test
	public void testDecode() throws ParseException, IOException {

		JabRefPreferences.getInstance().put("groupKeywordSeparator", " ");
		final PostBibTeXParser pbp = new PostBibTeXParser();
		final Post<BibTex> post = pbp.parseBibTeXPost(bibtexSource);

		final BibtexEntry entry = JabRefModelConverter.convertPost(post);
		final BibtexEntry expected = BibtexParser
				.singleFromString(bibtexSource);

		for (final String field : entry.getAllFields())
			Assert.assertEquals(expected.getField(field), entry.getField(field));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEncode() throws ParseException, IOException,
			IntrospectionException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		// set the keyword separator
		JabRefPreferences.getInstance().put("groupKeywordSeparator", " ");

		final BibtexEntry entry = BibtexParser.singleFromString(bibtexSource);

		final Post<BibTex> post = (Post<BibTex>) JabRefModelConverter
				.convertEntry(entry);

		// Parse a Post of the bibtex string
		final PostBibTeXParser pbp = new PostBibTeXParser();
		final Post<BibTex> expected = pbp.parseBibTeXPost(bibtexSource);

		final BibTex bibtex = post.getResource();
		final BeanInfo info = Introspector.getBeanInfo(bibtex.getClass());

		final PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

		// check all properties where return type is string
		for (final PropertyDescriptor pd : descriptors) {

			final Method m = pd.getReadMethod();

			if (m.getReturnType().equals(String.class)) {
				final String valueActual = (String) m.invoke(bibtex,
						(Object[]) null);
				final String valueExpected = (String) m.invoke(expected
						.getResource(), (Object[]) null);

				Assert.assertEquals(valueExpected, valueActual);
			}
		}
	}
}
