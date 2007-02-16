package org.bibsonomy.rest.strategy;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.exceptions.ValidationException;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public abstract class Strategy
{
	protected final Context context;
	
	public Strategy( Context context )
	{
		this.context = context;
	}
	
	/**
	 * validates a state: correct userName, etc
	 * @throws ValidationException
	 */
	public abstract void validate() throws ValidationException;

	/**
	 * @param request
	 * @param responseAdapter
	 * @throws InternServerException
    * @throws NoSuchResourceException if one part of the uri doesnt exist (the user, eg) 
	 */
	public abstract void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException, NoSuchResourceException;

	/**
	 * @param userAgent
	 * @return the contentType of the answer document
	 */
	public abstract String getContentType( String userAgent );
	
	protected GroupingEntity chooseGroupingEntity()
	{
		String value = context.getStringAttribute( "user", null );
		if( value != null )
		{
			return GroupingEntity.USER;
		}
		value = context.getStringAttribute( "group", null );
		if( value != null )
		{
			return GroupingEntity.GROUP;
		}
		value = context.getStringAttribute( "viewable", null );
		if( value != null )
		{
			return GroupingEntity.VIEWABLE;
		}
		return GroupingEntity.ALL;
	}
}

/*
 * $Log$
 * Revision 1.2  2007-02-16 16:11:28  mbork
 * changed default value from "" to null
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.6  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.5  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.4  2006/06/13 18:07:39  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * Revision 1.3  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 * Revision 1.2  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */