package org.bibsonomy.importer.event.iswc.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.importer.event.iswc.BibtexHelper;
import org.bibsonomy.importer.event.iswc.exceptions.RepositoryException;
import org.bibsonomy.importer.event.iswc.model.Publication;
import org.openrdf.model.Value;
import org.openrdf.sesame.Sesame;
import org.openrdf.sesame.admin.DummyAdminListener;
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
 * Repository which contains the RDF data, which is in SWC, SWRC and FOAF format. The reposiotory
 * can be accessed for each entrytype which is stored in the repository. 
 * @author tst
 */
public class RDFRepository {
	
	/**
	 * Sesame Repository which contains the RDF data. 
	 */
	private LocalRepository repository;
	
	/**
	 * Init the repository with the given RDF file. The backend of this Sesame store is the
	 * main memory.
	 * @param rdfPath Path to the RDF file, which will be loaded into the Sesame repository.
	 * @throws RepositoryException Failure druing building and accessing the repository. 
	 */
	public RDFRepository(String rdfPath) throws RepositoryException{
		
		// init sesame
		LocalService service = Sesame.getService();

		// create and init new LocalRepository
		try {
			// building new sesame repository
			repository = service.createRepository("swrc", false);
			
			// load RDF file into repository
			repository.addData(new File(rdfPath), "", RDFFormat.RDFXML, false, new DummyAdminListener());
			
		} catch (FileNotFoundException e) {
			throw new RepositoryException(e);
		} catch (IOException e) {
			throw new RepositoryException(e);
		} catch (AccessDeniedException e) {
			throw new RepositoryException(e);
		} catch (ConfigurationException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * Getting the inproceedings from the repository.
	 * @return A list with all inproceedings as {@link Publication}s.
	 * @throws RepositoryException Failure during accessing the repository
	 */
	public List<Publication> getInproceedings() throws RepositoryException{
		
		// init result list
		LinkedList<Publication> result = new LinkedList<Publication>();
		
		// getting the inproceedings with their title, keywords, and abstract
		QueryResultsTable resultsTable;
		try {
			resultsTable = repository.performTableQuery(QueryLanguage.RDQL, RDFQueries.INPROCEEDINGS);
		} catch (IOException e) {
			throw new RepositoryException(e);
		} catch (MalformedQueryException e) {
			throw new RepositoryException(e);
		} catch (QueryEvaluationException e) {
			throw new RepositoryException(e);
		} catch (AccessDeniedException e) {
			throw new RepositoryException(e);
		}
		
		// map which stores all publications
		HashMap<String, Publication> publicationMap = new HashMap<String, Publication>();
		
		// authors map <numberInList, authorName> 
		HashMap<Integer, String> authors = null;
		
		// current proccessed publication (key and publication itself
		String currentPubl = null;
		Publication publication = null;
		
		// iterate over all publication an extract title, abstract and keywords
		for (int row = 0; row < resultsTable.getRowCount(); row++) {
			// id of publication
	        Value publ = resultsTable.getValue(row, 0);
	        // a predicate of a publication
	        Value predicate = resultsTable.getValue(row, 1);
	        // a literal (topic ist link to another resource, but not in this document)
	        Value object = resultsTable.getValue(row, 2);
	        
	        Value authorPosition = resultsTable.getValue(row, 3);
	        Value author = resultsTable.getValue(row, 4);
	        
	        // check if a new publ is reached
	        if(currentPubl==null){
	        	
	        	// switch to new current publication
	        	currentPubl = publ.toString();
	        	publication = new Publication();
	        	
	        	// init author map
	        	authors = new HashMap<Integer, String>();
	        	
	        	// init entrytype
	        	publication.setEntrytype("inproceedings");
	        	
	        }else if(!currentPubl.equals(publ.toString())){

	        	// build authors
	        	ArrayList<String> list = new ArrayList<String>(authors.size());
	        	for(Integer position: authors.keySet()){
	        		list.add(position.intValue()-1, authors.get(position.intValue()));
	        	}

	        	// store authors string in publication
	        	publication.setAuthor(BibtexHelper.buildPersonString(list));
	        	
	        	// build and add bibtexkey
	        	publication.setBibtexkey(BibtexHelper.buildBibtexKey(list.get(0), publication.getYear(), publication.getTitle()));
	        	
	        	// init entrytype
	        	publication.setEntrytype("inproceedings");

	        	// store publication in map
	        	publicationMap.put(currentPubl, publication);

	        	// re-init author map
	        	authors = new HashMap<Integer, String>();

	        	// switch to new current publication
	        	currentPubl = publ.toString();
	        	publication = new Publication();

	        }
	        // check which predicate is used and add it's content to publication
	        if(predicate.toString().equals("http://data.semanticweb.org/ns/swc/ontology#isPartOf"))
	        	publication.setCrossref(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#year"))
	        	publication.setYear(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#month"))
	        	publication.setMonth(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#pages"))
	        	publication.setPages(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#title"))
	        	publication.setTitle(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#abstract"))
	        	publication.setBibabstract(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#url"))
	        	publication.setUrl(object.toString());
	        
	        // add author and position to map
	        authors.put(Integer.parseInt(authorPosition.toString().substring(44)), author.toString());
	        
		}
 
		/*
		 * also add last inproceeding and its values to result map
		 */
		
		// build authors
    	ArrayList<String> list = new ArrayList<String>(authors.size());
    	for(Integer position: authors.keySet()){
    		list.add(position.intValue()-1, authors.get(position.intValue()));
    	}
    	
    	// store authors string in publication
    	publication.setAuthor(BibtexHelper.buildPersonString(list));
		
    	// build and add bibtexkey
    	publication.setBibtexkey(BibtexHelper.buildBibtexKey(list.get(0), publication.getYear(), publication.getTitle()));

    	// init entrytype
    	publication.setEntrytype("inproceedings");
    	
    	// add last publication to map
		publicationMap.put(currentPubl, publication);

		/*
		 * add keywords from topic
		 */
		Map<String, String> publKeywords = getKeywords();
		for(String publ: publKeywords.keySet()){
			Publication inproceeding = publicationMap.get(publ);
			
			// put tags directly into publication, because this is the first time when tags can occur
			if(inproceeding != null)
				inproceeding.setKeywords(publKeywords.get(publ));
		}
		
		/*
		 * add keywords from session
		 */
		
		Map<String, String> publSession = getSessions();
		for(String publ: publSession.keySet()){
			Publication inproceeding = publicationMap.get(publ);
			if(inproceeding != null)
				inproceeding.setKeywords(inproceeding.getKeywords() + " " + publSession.get(publ));
		}

		/*
		 * build result list
		 */
		
    	// build result list
    	for(String publ: publicationMap.keySet()){
    		result.add(publicationMap.get(publ));
    	}
    	
		return result;
	}

	/**
	 * Getting the proceedings from the repository.
	 * @return A list with all proceedings as {@link Publication}s.
	 * @throws RepositoryException Failure during accessing the repository
	 */
	public List<Publication> getProceedings() throws RepositoryException{
		
		// init result list
		LinkedList<Publication> result = new LinkedList<Publication>();
		
		// getting the inproceedings with their title, keywords, and abstract
		QueryResultsTable resultsTable;
		try {
			resultsTable = repository.performTableQuery(QueryLanguage.RDQL, RDFQueries.PROCEEDINGS);
		} catch (IOException e) {
			throw new RepositoryException(e);
		} catch (MalformedQueryException e) {
			throw new RepositoryException(e);
		} catch (QueryEvaluationException e) {
			throw new RepositoryException(e);
		} catch (AccessDeniedException e) {
			throw new RepositoryException(e);
		}
		
		// map which stores all publications
		HashMap<String, Publication> publicationMap = new HashMap<String, Publication>();
		
		// editor map <numberInList, authorName> 
		HashMap<Integer, String> editors = null;
		
		// current proccessed publication (key and publication itself
		String currentPubl = null;
		Publication publication = null;
		
		// iterate over all publication an extract title, abstract and keywords
		for (int row = 0; row < resultsTable.getRowCount(); row++) {
			// id of publication
	        Value publ = resultsTable.getValue(row, 0);
	        // a predicate of a publication
	        Value predicate = resultsTable.getValue(row, 1);
	        // a literal or URI
	        Value object = resultsTable.getValue(row, 2);
	        
	        Value editorPosition = resultsTable.getValue(row, 3);
	        Value editor = resultsTable.getValue(row, 4);
	        
	        // check if a new publ is reached
	        if(currentPubl==null){
	        	
	        	// switch to new current publication
	        	currentPubl = publ.toString();
	        	publication = new Publication();
	        	
	        	// init editor map
	        	editors = new HashMap<Integer, String>();
	        	
	        	// init entrytype
	        	publication.setEntrytype("proceedings");
	        	
	        }else if(!currentPubl.equals(publ.toString())){

	        	// build editors
	        	ArrayList<String> list = new ArrayList<String>(editors.size());
	        	for(Integer position: editors.keySet()){
	        		list.add(position.intValue()-1, editors.get(position.intValue()));
	        	}

	        	// store editor string in publication
	        	publication.setEditor(BibtexHelper.buildPersonString(list));

	        	// init entrytype
	        	publication.setEntrytype("proceedings");

	        	// store publication in map
	        	publicationMap.put(currentPubl, publication);

	        	// re-init editor map
	        	editors = new HashMap<Integer, String>();

	        	// switch to new current publication
	        	currentPubl = publ.toString();
	        	publication = new Publication();

	        }

	        // use publication URI as bibtexkey
	        publication.setBibtexkey(publ.toString());
	        
	        // check which predicate is used and add it's content to publication
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#address"))
	        	publication.setAddress(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#booktitle")) {
	        	publication.setBooktitle(object.toString());
	        	publication.setTitle(object.toString());
	        }
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#month"))
	        	publication.setMonth(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#publisher"))
	        	publication.setPublisher(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#series"))
	        	publication.setSeries(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#volume"))
	        	publication.setVolume(object.toString());
	        if(predicate.toString().equals("http://swrc.ontoware.org/ontology#year"))
	        	publication.setYear(object.toString());

	        
	        
	        // add editor and position to map
	        editors.put(Integer.parseInt(editorPosition.toString().substring(44)), editor.toString());
	        
		}
 
		/*
		 * also add last inproceeding and its values to result map
		 */

		// build editors
    	ArrayList<String> list = new ArrayList<String>(editors.size());
    	for(Integer position: editors.keySet()){
    		list.add(position.intValue()-1, editors.get(position.intValue()));
    	}
    	
    	// store editor string in publication
    	publication.setEditor(BibtexHelper.buildPersonString(list));
		

    	// add last publication to map
		publicationMap.put(currentPubl, publication);
		
		/*
		 * build result list
		 */
		
    	// build result list
    	for(String publ: publicationMap.keySet()){
    		result.add(publicationMap.get(publ));
    	}
    	
		return result;
	}
	
	/**
	 * getting the sessions from the repository 
	 * @return Map with publication as key and the sessions as value 
	 * @throws RepositoryException Failure during accessing the repository
	 */
	public Map<String, String> getSessions() throws RepositoryException{
		
		// init result map
		HashMap<String, String> result = new HashMap<String, String>();
		
		// getting the publications with the matching session label  
		QueryResultsTable resultsTable;
		try {
			resultsTable = repository.performTableQuery(QueryLanguage.RDQL, RDFQueries.SESSIONS);
		} catch (IOException e) {
			throw new RepositoryException(e);
		} catch (MalformedQueryException e) {
			throw new RepositoryException(e);
		} catch (QueryEvaluationException e) {
			throw new RepositoryException(e);
		} catch (AccessDeniedException e) {
			throw new RepositoryException(e);
		}
		
		// iterate over all publications
		for (int row = 0; row < resultsTable.getRowCount(); row++) {

			// publication
	        Value publ = resultsTable.getValue(row, 0);
	        // session label
	        Value label = resultsTable.getValue(row, 1);
	        String labelString = label.toString().substring(0, label.toString().indexOf(":"));
	        labelString = labelString.replaceAll(" ", "_");
	        
	        // check if publication has already a label else store it directly
	        if(result.containsKey(publ.toString()))
	        	result.put(publ.toString(), result.get(publ.toString()) + " " + labelString);
	        else
	        	result.put(publ.toString(), labelString);
	        
		}
		
		return result;
	}
	
	/**
	 * getting the topics(keywords) from the repository 
	 * @return Map with publication as key and the topics as value 
	 * @throws RepositoryException Failure during accessing the repository
	 */
	public Map<String, String> getKeywords() throws RepositoryException{
		
		// init result map
		HashMap<String, String> result = new HashMap<String, String>();
		
		// getting the publications with the matching session label  
		QueryResultsTable resultsTable;
		try {
			resultsTable = repository.performTableQuery(QueryLanguage.RDQL, RDFQueries.KEYWORDS);
		} catch (IOException e) {
			throw new RepositoryException(e);
		} catch (MalformedQueryException e) {
			throw new RepositoryException(e);
		} catch (QueryEvaluationException e) {
			throw new RepositoryException(e);
		} catch (AccessDeniedException e) {
			throw new RepositoryException(e);
		}
		
		// hashset for tags
		HashSet<String> tags = new HashSet<String>();
		
		// current proccessed publication (key and publication itself
		String currentPubl = null;

		// iterate over all publications
		for (int row = 0; row < resultsTable.getRowCount(); row++) {

			// publication
	        Value publ = resultsTable.getValue(row, 0);
	        // topic
	        Value topic = resultsTable.getValue(row, 1);
	        
	        // check if a new publ is reached
	        if(currentPubl==null){
	        	
	        	// switch to new current publication
	        	currentPubl = publ.toString();
	        	
	        	// init tag set
	        	tags = new HashSet<String>();
	        	
	        }else if(!currentPubl.equals(publ.toString())){

	        	// build keywords
	        	StringBuffer tagBuffer = new StringBuffer();
	        	for(String tag: tags){
	        		tagBuffer.append(tag);
	        		tagBuffer.append(" ");
	        	}
	        	
	        	// store publication in map
	        	result.put(currentPubl, tagBuffer.toString());

	        	// re-init tag set
	        	tags = new HashSet<String>();
	        	
	        	// switch to new current publication
	        	currentPubl = publ.toString();

	        }
	        
	        // add tag to set
	        tags.add(topic.toString().replaceAll(" ", "_"));
	        
		}

    	// build keywords
    	StringBuffer tagBuffer = new StringBuffer();
    	for(String tag: tags){
    		tagBuffer.append(tag);
    		tagBuffer.append(" ");
    	}

    	// store last publication in map
    	result.put(currentPubl, tagBuffer.toString());
		
		return result;
	}
	
}
