package org.bibsonomy.importer.event.iswc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.openrdf.sesame.Sesame;
import org.openrdf.sesame.admin.StdOutAdminListener;
import org.openrdf.sesame.config.AccessDeniedException;
import org.openrdf.sesame.config.ConfigurationException;
import org.openrdf.sesame.constants.QueryLanguage;
import org.openrdf.sesame.constants.RDFFormat;
import org.openrdf.sesame.query.MalformedQueryException;
import org.openrdf.sesame.query.QueryEvaluationException;
import org.openrdf.sesame.query.QueryResultsTable;
import org.openrdf.sesame.repository.local.LocalRepository;
import org.openrdf.sesame.repository.local.LocalService;

/**
 * only a little test class. Here you can check single RDQL queries.
 * @author tst
 */
public class QueryRunner {
	
	public static final String TEST_RDF_FILE = "data/conferenceTest.rdf";
	
	public static final String KEYWORDS = 
		"select ?publ, ?tag " +
		"where (?publ, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://swrc.ontoware.org/ontology#InProceedings>)" +
			  "(?publ, <http://data.semanticweb.org/ns/swc/ontology#hasTopic>, ?tagLabel) " +
			  "(?tagLabel, <http://www.w3.org/2000/01/rdf-schema#label>, ?tag)";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// init sesame
		LocalService service = Sesame.getService();

		LocalRepository repository = null;
		// create and init new LocalRepository
		try {
			// building new sesame repository
			repository = service.createRepository("swrc", false);
			
			// load RDF file into repository
			repository.addData(new File(TEST_RDF_FILE), "", RDFFormat.RDFXML, false, new StdOutAdminListener());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		try {
			QueryResultsTable resultsTable = repository.performTableQuery(QueryLanguage.RDQL, KEYWORDS);
			
			System.out.println("done, row count: " + resultsTable.getRowCount());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		}

	}

}
