package org.bibsonomy.rest.client.queries.get;

import java.util.logging.Level;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.client.queries.AbstractQuery;
import org.bibsonomy.rest.exceptions.InvalidXMLException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;
import org.bibsonomy.rest.renderer.xml.ModelFactory;

/**
 * Use this Class to receive details about an group of bibsonomy
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetGroupDetailsQuery extends AbstractQuery<Group>
{
	private String groupname;
	private BibsonomyXML bibsonomyXML;

	/**
	 * Gets details of a group
	 * 
	 * @param groupname name of the user
	 */
	public GetGroupDetailsQuery( String groupname )
	{
		if( groupname == null || groupname.length() == 0 ) throw new IllegalArgumentException( "no groupname given" );
		this.groupname = groupname;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#getResult()
	 */
	@Override
	public Group getResult() throws ErrorPerformingRequestException
	{
		if( bibsonomyXML == null ) throw new IllegalStateException( "Execute the query first." );

		if( bibsonomyXML.getGroup() != null )
		{
			try
			{
				return ModelFactory.getInstance().createGroup( bibsonomyXML.getGroup() );
			}
			catch( InvalidXMLException e )
			{
				LOGGER.log( Level.WARNING, e.getMessage(), e );
				throw e;
			}
		}
		throw new ErrorPerformingRequestException( "The received document did not contain the requested data." );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.rest.client.queries.AbstractQuery#doExecute()
	 */
	@Override
	protected void doExecute() throws ErrorPerformingRequestException
	{
		bibsonomyXML = performGetRequest( API_URL + URL_GROUPS + "/" + groupname );
	}
}

/*
 * $Log$
 * Revision 1.2  2006-06-08 08:02:54  mbork
 * fixed erroneous use of generics
 *
 * Revision 1.1  2006/06/06 22:20:54  mbork
 * started implementing client api
 *
 */