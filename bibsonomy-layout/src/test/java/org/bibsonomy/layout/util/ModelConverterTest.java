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
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.imports.BibtexParser;

import org.bibsonomy.bibtex.parser.PostBibTeXParser;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
//import org.bibsonomy.testutil.ModelUtils;
import org.junit.Test;
import org.junit.Assert;

import bibtex.parser.ParseException;

public class ModelConverterTest {

    private static final String COMMON_FIELDS = "abstract, address, author, bibtexkey, booktitle, comment, description, doi, editor, isbn, keywords, month, pages, publisher, title, url, year";

    private static final String bibtexSource = "@book{Loudon2003,\r\n"
	+ "  title = {C++. Kurz und gut.},\r\n"
	+ "  publisher = {O'Reilly},\r\n" + "  year = {2003},\r\n"
	+ "  author = {Kyle Loudon},\r\n" + "  month = {08},\r\n"
	+ "  booktitle = {C++. Kurz und gut.},\r\n"
	+ "  isbn = {3897212625},\r\n" + "  keywords = {boost c++},\r\n"
	+ "  owner = {dasboogie},\r\n"
	+ "  timestamp = {2007-08-18 11:25:11}\r\n" + "}";

    public static final String EXAMPLE_BIBTEX = "@inproceedings{benz2009managing,\n" + 
    "address = {New York, NY, USA},\n" +
    "author = {Dominik Benz and Folke Eisterlehner and Andreas Hotho and Robert Jäschke and Beate Krause and Gerd Stumme},\n" +
    "booktitle = {HT '09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia},\n" +
    "editor = {Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer},\n" +
    "interHash = {aa341801cf9a31d963fccb8a331043dc},\n" +
    "intraHash = {99cafad8ce2afb5879c6c85c14cc5259},\n" +
    "pages = {323--324},\n" +
    "publisher = {ACM},\n" +
    "title = {Managing publications and bookmarks with BibSonomy},\n" +
    "url = {http://portal.acm.org/citation.cfm?doid=1557914.1557969#},\n" +
    "year = {2009},\n" +
    "date = \"2010-07-19\",\n" + 
    "isbn = {978-1-60558-486-7},\n" +
    "doi = {10.1145/1557914.1557969},\n" +
    "month = jun,\n" +
    "comment = {(private-note)This is a test note!},\n" + 
    "abstract = {In this demo we present BibSonomy, a social bookmark and publication sharing system.},\n" +
    "keywords = {2009 bibsonomy demo ht09 myown},\n" +
    "description = {Our demo at HT 2009},\n" + 
    "}";

    @Test
    public void testDecode() throws ParseException, IOException {

	JabRefPreferences.getInstance().put("groupKeywordSeparator", " ");
	final PostBibTeXParser pbp = new PostBibTeXParser();
	final Post<BibTex> post = pbp.parseBibTeXPost(bibtexSource);

	final BibtexEntry entry = JabRefModelConverter.convertPost(post);
	final BibtexEntry expected = BibtexParser.singleFromString(bibtexSource);

	for (final String field : entry.getAllFields())
	    Assert.assertEquals(expected.getField(field), entry.getField(field));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEncode() throws ParseException, IOException, IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

	// set the keyword separator
	JabRefPreferences.getInstance().put("groupKeywordSeparator", " ");

	final BibtexEntry entry = BibtexParser.singleFromString(bibtexSource);

	final Post<BibTex> post = (Post<BibTex>) JabRefModelConverter.convertEntry(entry);

	// Parse a Post of the bibtex string
	final PostBibTeXParser pbp = new PostBibTeXParser();
	final BibTex expected = pbp.parseBibTeXPost(bibtexSource).getResource();

	final BibTex bibtex = post.getResource();

	//ModelUtils.assertPropertyEquality(expected, bibtex, 5, null, new String[]{});
	//	assertEquals(expected, bibtex);
    }

    private void assertEquals(final Object expected, final Object actual) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
	final BeanInfo info = Introspector.getBeanInfo(expected.getClass());
	final PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

	// check all properties where return type is string
	for (final PropertyDescriptor pd : descriptors) {

	    final Method m = pd.getReadMethod();

	    if (m.getReturnType().equals(String.class)) {
		final String valueActual = (String) m.invoke(actual, (Object[]) null);
		final String valueExpected = (String) m.invoke(expected, (Object[]) null);

		assertEquals(valueExpected, valueActual);
	    }
	}
    }

    @Test
    public void testFromBibTeX() throws Exception {
	/*
	 * let JabRef convert:
	 * 
	 * BibTeX -> JabRef BibTeXEntry
	 */
	final BibtexDatabase bibtexOld = bibtex2Jabref(EXAMPLE_BIBTEX);
	/*
	 * use our BibTeX parser + model converter:
	 * 
	 * BibTeX -> BibSonomy Posts -> JabRef BibTeXEntry
	 */
	final PostBibTeXParser pbp = new PostBibTeXParser();
	final List<Post<BibTex>> postsNew = pbp.parseBibTeXPosts(EXAMPLE_BIBTEX);
	/*
	 * copy posts (I hate Java Generics)
	 */
	final List<Post<? extends Resource>> list = new LinkedList<Post<? extends Resource>>();
	for (final Post<BibTex> post : postsNew) {
	    list.add(post);
	}

	final List<BibtexEntry> bibtexNew = JabRefModelConverter.convertPosts(list);

	final BibtexEntry oldEntry = bibtexOld.getEntries().iterator().next();
	final BibtexEntry newEntry = bibtexNew.get(0);

	final LinkedList<String> oldFields = new LinkedList<String>(oldEntry.getAllFields());
	Collections.sort(oldFields);
	/*
	 * in 2010 we got those fields from the old converter code ...
	 */
	assertEquals("[abstract, address, author, bibtexkey, booktitle, comment, date, description, doi, editor, interhash, intrahash, isbn, keywords, month, pages, publisher, title, url, year]", oldFields.toString());
	final LinkedList<String> newFields = new LinkedList<String>(newEntry.getAllFields());
	Collections.sort(newFields);
	/*
	 * ... the new converter had the following fields
	 */
	assertEquals("[abstract, address, author, bibtexkey, booktitle, comment, description, doi, editor, isbn, keywords, month, pages, privnote, publisher, timestamp, title, url, year]", newFields.toString());
	/*
	 * compare the fields they have in common ...
	 */
	for (final String field: COMMON_FIELDS.split(", ")) {
	    System.out.println("checking field " + field);
	    assertEquals(oldEntry.getField(field), newEntry.getField(field));
	}
	/*
	 * this would be perfect, but due to different supported fields it does not work
	 */
	//	ModelUtils.assertPropertyEquality(oldEntry, newEntry, 5, null, new String[]{"id"});
    }

    private <T extends Resource> BibtexDatabase bibtex2Jabref(final String bibtex) {
	/*
	 * put all bibtex together as string
	 */
	/*
	 * parse them!
	 */
	try {
	    return BibtexParser.parse(new StringReader(bibtex)).getDatabase();
	} catch (final Exception e) {
	    throw new LayoutRenderingException("Error parsing BibTeX entries: " + e.getMessage());
	}
    }
    
    
    /**
     * Test the correct parsing of usernames.
     */
    @Test
    public void testPostToJabrefEntryWithUsername() {
	JabRefPreferences.getInstance().put("groupKeywordSeparator", " ");
	final PostBibTeXParser pbp = new PostBibTeXParser();	
	try {
	    final Post<BibTex> post = pbp.parseBibTeXPost(bibtexSource);	
	    post.setUser(new User("alder"));
	    BibtexEntry jabrefEntry = JabRefModelConverter.convertPost(post);
	    Assert.assertEquals("alder", jabrefEntry.getField("username"));	    
	} catch (ParseException e) {
	    Assert.fail(e.getMessage());
	} catch (IOException e) {
	    Assert.fail(e.getMessage());
	} 
	
	
	
    } 


}
