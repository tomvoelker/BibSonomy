/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.junit.Test;

/**
 * @author Jens Illig
 */
public class EndnoteUtilsTest {
	private static final String expected = "%0 Journal Article\n" + //
			"%1 schoolofenglishliterature2009newcastle\n" + //
			"%A School of English Literature, Language and Linguistics, \n" + //
			"%D 2009\n" + //
			"%E Günter, Günter\n" + //
			"%K test\n" + //
			"%T Newcastle working papers in linguistics\n";
	
	private static final String expectedWithDummies = "%0 Journal Article\n" + //
			"%1 schoolofenglishliterature2009newcastle\n" + //
			"%A HEB12334, noauthor\n" + //
			"%D noyear\n" + //
			"%E HEB12334, noeditor\n" + //
			"%K test\n" + //
			"%T Newcastle working papers in linguistics\n";
	
	private static final String expectedWithSkippedDummies = "%0 Journal Article\n" + //
			"%1 schoolofenglishliterature2009newcastle\n" + //
			"%K test\n" + //
			"%T Newcastle working papers in linguistics\n";

	@Test
	public void testIt() throws IOException {
		Post<BibTex> post = createPost();
		String rendered = EndnoteUtils.toEndnoteString(post, false);
		assertEquals(expected, rendered);
	}
	
	@Test
	public void testSkipDummyValues() throws IOException {
		Post<BibTex> post = createPost();
		post.getResource().setAuthor(BibtexUtilsTest.createPersonList("noauthor", "HEB12334"));
		post.getResource().setEditor(BibtexUtilsTest.createPersonList("noeditor", "HEB12334"));
		post.getResource().setYear("noyear");
		assertEquals(expectedWithDummies, EndnoteUtils.toEndnoteString(post, false));
		assertEquals(expectedWithSkippedDummies, EndnoteUtils.toEndnoteString(post, true));
	}

	public static Post<BibTex> createPost() {
		BibTex b = new BibTex();
		b.setEntrytype("article");
		b.setAuthor(new ArrayList<PersonName>());
		b.setTitle("Newcastle working papers in linguistics");
		// TODO: what about this?: b.setTitle("Newcastle working papers in linguistics \\& stuff");
		b.getAuthor().add(new PersonName("", "{School of English Literature, Language and Linguistics}"));
		b.setEditor(new ArrayList<PersonName>());
		b.getEditor().add(new PersonName("Günter").withFirstName("G\"unter"));
		b.setYear("{2009}");
		b.setBibtexKey("schoolofenglishliterature2009newcastle");
		Post<BibTex> post = new Post<BibTex>();
		post.setResource(b);
		post.addTag("test");
		return post;
	}
}
