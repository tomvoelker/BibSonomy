package org.bibsonomy.importer.event.iswc.rdf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.bibsonomy.importer.event.iswc.exceptions.RepositoryException;
import org.bibsonomy.importer.event.iswc.model.Publication;

import static org.junit.Assert.*;

/**
 * Test class for {@link RDFRepository}.
 * @author tst
 */
public class RDFRepositoryTest {

	/**
	 * standard test RDF file
	 */
	public static final String TEST_RDF_FILE = "src/test/resources/conferenceTest.rdf";
	public static final String TEST_WHITELIST_FILE = "whitelist.txt";

	/**
	 * checks if the RDF file is successfull loaded and checks if inproceedings can be extracted. 
	 */@Ignore
	public void readInproceedingsFromRDFTest(){
		try {
			
			// first check repository
			RDFRepository repository = new RDFRepository(TEST_RDF_FILE, TEST_WHITELIST_FILE);
			assertNotNull(repository);

			// build and check inproceedings result
			List<Publication> publications = repository.getInproceedings();
			assertNotNull(publications);
			assertTrue(publications.size() > 0);
			
		} catch (RepositoryException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}		
	}

	/**
	 * checks if the RDF file is successfull loaded and checks if proceedings can be extracted. 
	 */@Ignore
	public void readProceedingsFromRDFTest(){
		try {
			
			// first check repository
			RDFRepository repository = new RDFRepository(TEST_RDF_FILE, TEST_WHITELIST_FILE);
			assertNotNull(repository);

			// build and check proceedings result
			List<Publication> publications = repository.getProceedings();
			assertNotNull(publications);
			assertTrue(publications.size() > 0);
				
		} catch (RepositoryException e) {
			fail(e.getMessage());
		}
	}
			
	/**
	 * checks if the RDF file is successfull loaded and checks if sessions can be extracted. 
	 */@Test
	public void readSessionsFromRDFTest(){
		try {
			
			// first check repository
			RDFRepository repository = new RDFRepository(TEST_RDF_FILE, "");
			assertNotNull(repository);

			// build and check session result
			Map<String, String> sessions = repository.getSessions();
			assertNotNull(sessions);
			assertTrue(sessions.size() > 0);
			
		} catch (RepositoryException e) {
			fail(e.getMessage());
		}
	}		 
}
