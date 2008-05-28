package org.bibsonomy.importer.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Adds the ESWC sessions as tags.
 * 
 * @author rja
 * @version $Id$
 */
public class EswcSessionFilter implements PostFilterChainElement {

	private final HashMap<Integer, String> paperSessionMap = new HashMap<Integer, String>();
	
	private final Query query = QueryFactory.create("SELECT ?talk ?track WHERE {" +
			"  ?talk ?type <http://data.semanticweb.org/ns/swc/ontology#PaperPresentation>  . " +
			"  ?talk <http://data.semanticweb.org/ns/swc/ontology#isSubEventOf> ?track " +
			"}");
	
	/** 
	 * Loads the file provided by the 
	 * {@link EswcSessionFilter}{@code .rdfFileName}
	 * property.
	 * 
	 * @param prop
	 * @throws IOException
	 */
	public EswcSessionFilter(final Properties prop) throws IOException {
		/*
		 * load data from a file
		 */
		final String eswcData = prop.getProperty(EswcSessionFilter.class.getName() + ".rdfFileName");
		
		/*
		 * load RDF model
		 */
		final Model model = ModelFactory.createDefaultModel();
		model.read(new FileInputStream(eswcData), "");;

		/*
		 * Gather session information for the papers.
		 */
		
		final QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try {
			final ResultSet result = qexec.execSelect();
			while (result.hasNext()) {
				final QuerySolution solution = result.nextSolution();

				/*
				 * extract paper id and track id from URI
				 */
				final String paperURI = solution.getResource("talk").getURI();
				final String paperId  = paperURI.substring(paperURI.lastIndexOf("/") + 1);
				final String trackURI = solution.getResource("track").getURI();
				final String track = trackURI.substring(trackURI.lastIndexOf("/") + 1);
				
				/*
				 * remember mapping for later use
				 */
				paperSessionMap.put(Integer.parseInt(paperId), track);
			}
		} finally {
			qexec.close();
		}
		
		
	}
	
	/** Removes stopwords from the tags.
	 * 
	 * @see org.bibsonomy.importer.filter.PostFilterChainElement#filterPost(org.bibsonomy.model.Post)
	 */
	public void filterPost(final Post<BibTex> post) {
		final Integer contentId = post.getContentId();
		if (paperSessionMap.containsKey(contentId)) {
			final String track = paperSessionMap.get(contentId);
			post.addTag(track);
		} else {
			System.err.println("no session found for paper " + contentId);
		}
		
		
	}

}
