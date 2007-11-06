package org.bibsonomy.importer.event.iswc.rdf;

/**
 * All queries are written in RDQL.
 * @author tst
 *
 */
public class RDFQueries {
	
	/**
	 * selects all topic which are assigned to a publication
	 */
	public static final String KEYWORDS = 
		"select ?publ, ?tag " +
		"where (?publ, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://swrc.ontoware.org/ontology#InProceedings>)" +
			  "(?publ, <http://data.semanticweb.org/ns/swc/ontology#hasTopic>, ?tagLabel) " +
			  "(?tagLabel, <http://www.w3.org/2000/01/rdf-schema#label>, ?tag)";
	
	/**
	 * selects all sessions which are assigned to a publication
	 */
	public static final String SESSIONS = 
		"select ?publ, ?label " +
		"where   (?session, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://data.semanticweb.org/ns/swc/ontology#SessionEvent>) " +
				"(?session, <http://data.semanticweb.org/ns/swc/ontology#isSuperEventOf>, ?event) " +
				"(?event, <http://data.semanticweb.org/ns/swc/ontology#hasRelatedDocument>, ?publ ) " +
				"(?session, <http://www.w3.org/2000/01/rdf-schema#label> ?label)";
	
	/**
	 * Query which selects all proceedings which has an editor 
	 */
	public static final String PROCEEDINGS = 
		"select ?pro, ?pred, ?object, ?listNumber, ?editorName " +
		"where (?pro, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://swrc.ontoware.org/ontology#Proceedings>)" +
			  "(?pro, ?pred, ?object) " +
			  "(?pro, <http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#editorList>, ?blindList) " +
			  "(?blindList, ?listNumber, ?editor)" +
			  "(?editor, <http://xmlns.com/foaf/0.1/name>, ?editorName)";
	
	/**
	 * Query which selects all inproceedings which has a author 
	 */
	public static final String INPROCEEDINGS = 
		"select ?publ, ?pred, ?object, ?listNumber, ?authorName " +
		"where (?publ, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://swrc.ontoware.org/ontology#InProceedings>)" +
			  "(?publ, ?pred, ?object) " +
			  "(?publ, <http://www.cs.vu.nl/~mcaklein/onto/swrc_ext/2005/05#authorList>, ?blindList) " +
			  "(?blindList, ?listNumber, ?author)" +
			  "(?author, <http://xmlns.com/foaf/0.1/name>, ?authorName)";

}
