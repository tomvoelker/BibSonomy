package org.bibsonomy.importer.event.iswc.exceptions;

import org.bibsonomy.importer.event.iswc.rdf.RDFRepository;

/**
 * failure during processing and accessing the Sesame repository in the {@link RDFRepository}.
 * @author tst
 */
public class RepositoryException extends Exception {

	/**
	 * ID for serialization 
	 */
	private static final long serialVersionUID = -5163667048236256959L;
	
	/**
	 * init exceptions with a given message
	 * @param msg Message of this message
	 */
	public RepositoryException(String msg){
		super(msg);
	}
	
	/**
	 * init exception with a previous exception
	 * @param e previous exception
	 */
	public RepositoryException(Exception e){
		super(e);
	}

}
