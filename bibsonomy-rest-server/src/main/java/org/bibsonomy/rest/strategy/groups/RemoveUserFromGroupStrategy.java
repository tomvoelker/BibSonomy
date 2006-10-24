package org.bibsonomy.rest.strategy.groups;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class RemoveUserFromGroupStrategy extends Strategy
{
	private String groupName;
   private String userName;

   /**
	 * @param context
	 * @param userName 
	 * @param groupName 
	 */
	public RemoveUserFromGroupStrategy( Context context, String groupName, String userName )
	{
		super( context );
		this.groupName = groupName;
      this.userName = userName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
      // TODO only groupmembers may remove a user from a group?
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException
   {
      context.getLogic().removeUserFromGroup( groupName, userName );
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
 * Revision 1.1  2006-10-24 21:39:53  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:13  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.4  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.3  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.2  2006/05/24 13:02:43  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 */