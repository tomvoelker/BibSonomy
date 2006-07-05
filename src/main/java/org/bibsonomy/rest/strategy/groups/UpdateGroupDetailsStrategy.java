package org.bibsonomy.rest.strategy.groups;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class UpdateGroupDetailsStrategy extends Strategy
{
	private String groupName;

   /**
	 * @param context
	 * @param groupName 
	 */
	public UpdateGroupDetailsStrategy( Context context, String groupName )
	{
		super( context );
      this.groupName = groupName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
      // TODO only groupmembers may change a group?
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
      try
      {
         Group group = context.getRenderer().parseGroup( request.getInputStream() );
         // ensure right groupname
         group.setName( groupName );
         context.getLogic().storeGroup( group, true );
      }
      catch( IOException e )
      {
         throw new InternServerException( e );
      }
   }

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
   {
      // TODO no content-contenttype
      return null;
   }
}

/*
 * $Log$
 * Revision 1.3  2006-07-05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.2  2006/05/24 13:02:43  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 */