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

import static org.junit.Assert.assertEquals;
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
	    final StringBuffer renderedLayout = renderer.renderLayout(layout, getPosts(), false);
	    final String cleanedLayout = renderedLayout.toString().replaceAll(" +", " ");//.replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"");
	    assertEquals("<?xml version=\"1.0\"?>\n<!DOCTYPE dblp SYSTEM \"http://www.informatik.uni-trier.de/~ley/db/about/dblp.dtd\">\n<!-- This file was exported from BibSonomy, http://www.bibsonomy.org -->\n\n<dblp><inproceedings mdate=\"2009\" key=\"benz2009managing\">\n <author>Dominik Benz</author>\n<author>Folke Eisterlehner</author>\n<author>Andreas Hotho</author>\n<author>Robert J&#228;schke</author>\n<author>Beate Krause</author>\n<author>Gerd Stumme</author> \n <editor>Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer</editor>\n <address>New York&#44; NY&#44; USA</address>\n\n <title>Managing publications and bookmarks with BibSonomy</title>\n <booktitle>HT &#39;09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia</booktitle>\n <pages>323&#x2013;324</pages>\n <year>2009</year>\n\n\n\n <month>June</month>\n <url>http://portal.acm.org/citation.cfm&#63;doid=1557914.1557969&#35;</url>\n\n <publisher>ACM</publisher>\n\n\n\n\n <isbn>978&#45;1&#45;60558&#45;486&#45;7</isbn>\n\n\n</inproceedings>\n \n\n\n\n<book mdate=\"\" key=\"knuth\">\n <author>Donald E. Knuth</author> \n\n\n\n <title>The Art of Computer Programming</title>\n\n\n\n\n\n\n\n\n\n <publisher>Addison Wesley</publisher>\n\n\n\n\n\n\n\n</book>\n \n\n\n\n</dblp>\n", cleanedLayout);
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

	final User u = new User();
	u.setName("Wiglaf Droste");
	post.setUser(u);

	posts.add(post);

	/*
	 * next one
	 */
	final BibTex b2 = new BibTex(); 
	b2.setEntrytype("inproceedings");
	b2.setBibtexKey("benz2009managing"); 
	b2.setAddress("New York, NY, USA");
	b2.setAuthor("Dominik Benz and Folke Eisterlehner and Andreas Hotho and Robert Jäschke and Beate Krause and Gerd Stumme");
	b2.setBooktitle("HT '09: Proceedings of the 20th ACM Conference on Hypertext and Hypermedia");
	b2.setEditor("Ciro Cattuto and Giancarlo Ruffo and Filippo Menczer");
	b2.setPages("323--324");
	b2.setPublisher("ACM");
	b2.setTitle("Managing publications and bookmarks with BibSonomy");
	b2.setUrl("http://portal.acm.org/citation.cfm?doid=1557914.1557969#");
	b2.setYear("2009");
	b2.setMisc("isbn = {978-1-60558-486-7},\ndoi = {10.1145/1557914.1557969}");
	b2.setMonth("jun");
	b2.setPrivnote("This is a test note"); 
	b2.setAbstract("In this demo we present BibSonomy, a social bookmark and publication sharing system.");

	final Post<BibTex> p2 = new Post<BibTex>();
	p2.setResource(b2);
	p2.setUser(u);
	p2.setDescription("Our demo at HT 2009");
	
	posts.add(p2);

	return posts;
    }

    public static void main(String[] args) {
	//final JabrefLayoutRenderer renderer = JabrefLayoutRenderer.getInstance();
	//renderer.setDefaultLayoutFilePath(args[0]);

	final JabrefLayoutRendererTest t = new JabrefLayoutRendererTest();
	t.testRenderInternal();
	System.out.println("finished");




	System.exit(0);	
    }

    @SuppressWarnings("unused")
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

