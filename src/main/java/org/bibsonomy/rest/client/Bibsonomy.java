package org.bibsonomy.rest.client;

import java.util.logging.Logger;

import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;

/**
 * Bibsonomy is a class for accessing the <a href="http://www.bibsonomy.org/api/">Bibsonomy REST API</a>.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class Bibsonomy
{
	public static final Logger LOGGER = Logger.getLogger( Bibsonomy.class.getName() );

    private String username;
    private String password;
    
	/**
	 * Create an object to interact with Bibsonomy
	 * 
	 * @param username Username
	 * @param password Password
	 */
	public Bibsonomy( String username, String password )
	{
        this.username = username;
        this.password = password;
	}
	
	public void executeQuery( AbstractQuery query ) throws ErrorPerformingRequestException
	{
		query.execute( username, password );
	}
}

/*
 * $Log$
 * Revision 1.1  2006-06-06 22:20:55  mbork
 * started implementing client api
 *
 */