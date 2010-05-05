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

package org.bibsonomy.layout.jabref;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.bst.VM;

import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class JabrefLayoutRendererTest {

	@Test
	public void testInit() {
		getRenderer();
	}

	@Test
	public void testRenderInternal() {
		try {
			
			final JabrefLayoutRenderer renderer = getRenderer();
			final JabrefLayout layout = renderer.getLayout("dblp", "foo");
			renderer.renderLayout(layout, getPosts(), false);
		} catch (LayoutRenderingException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

	}

	private JabrefLayoutRenderer getRenderer() {
		final JabrefLayoutRenderer renderer = JabrefLayoutRenderer.getInstance();
		renderer.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
		return renderer;
	}


	private List<Post<BibTex>> getPosts() {
		final List<Post<BibTex>> posts = new LinkedList<Post<BibTex>>();

		final BibTex bibtex = new BibTex();
		bibtex.setTitle("The Art of Computer Programming");
		bibtex.setAuthor("Donald E. Knuth");
		bibtex.setPublisher("Addison Wesley");
		bibtex.setEntrytype("book");
		bibtex.setBibtexKey("knuth");

		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(bibtex);
		
		User u = new User();
		u.setName("Wiglaf Droste");
		post.setUser(u);

		posts.add(post);
		return posts;
	}

	public static void main(String[] args) {
		final JabrefLayoutRenderer renderer = JabrefLayoutRenderer.getInstance();
		//renderer.setDefaultLayoutFilePath(args[0]);

		final JabrefLayoutRendererTest t = new JabrefLayoutRendererTest();
		t.testRenderInternal();
		System.out.println("finished");




		System.exit(0);	
	}
	
	private void testBstVM() {
		try {
			final File bst = new File("/home/rja/paper/papers/2007/issi/lni.bst");
			VM vm = new VM(bst);
			
			final List<BibtexEntry> bibtexs = new LinkedList<BibtexEntry>();
			final BibtexEntry entry = new BibtexEntry();
			
			entry.setField("title", "Als ich ein kleiner Junge war");
			entry.setField("author", "Erich Kästner");
			entry.setField("year", "2007");
			entry.setType(BibtexEntryType.BOOK);
			bibtexs.add(entry);
			
			
			final String result = vm.run(bibtexs);
			
			System.out.println("-------------------------------------------------------------------");
			System.out.println(result);
			System.out.println("-------------------------------------------------------------------");
			
		} catch (Exception ex) {
			System.out.println(ex);
		}


	}

}

