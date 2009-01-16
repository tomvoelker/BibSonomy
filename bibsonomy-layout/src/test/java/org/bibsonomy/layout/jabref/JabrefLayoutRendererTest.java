package org.bibsonomy.layout.jabref;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
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
			getRenderer().renderInternal("dblp", getPosts(), "foo", System.out);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
	}

	private JabrefLayoutRenderer getRenderer() {
		final JabrefLayoutRenderer renderer = new JabrefLayoutRenderer();
		renderer.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
		renderer.init();
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
		
		posts.add(post);
		return posts;
	}

	public static void main(String[] args) {
		final JabrefLayoutRenderer renderer = new JabrefLayoutRenderer();
		renderer.setDefaultLayoutFilePath(args[0]);
		renderer.init();
		
	}
	
}

