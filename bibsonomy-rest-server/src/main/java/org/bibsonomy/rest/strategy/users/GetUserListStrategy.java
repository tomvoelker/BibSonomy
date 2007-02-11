package org.bibsonomy.rest.strategy.users;

import java.io.StringWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * shows all users bibsonomy has
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListStrategy extends Strategy
{
	public GetUserListStrategy( Context context )
	{
		super( context );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// should be ok for everybody
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(java.io.PrintWriter, java.util.Map)
	 */
	@Override
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );
		
		Set<User> users = context.getLogic().getUsers( context.getAuthUserName(), start, end );
      
      ViewModel viewModel = new ViewModel();
      if( users.size() < end + 1 )
      {
         end = users.size() - 1;
      }
      else
      {
         String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getUsersUrl() + "?start=" + String.valueOf( end + 1 ) + 
         "&end=" + String.valueOf( end + 10 ); 
         viewModel.setUrlToNextResources( next );
      }
      viewModel.setStartValue( start );
      viewModel.setEndValue( end );
   
      // delegate to the renderer
		context.getRenderer().serializeUsers( writer, users, viewModel );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/users+" + context.getRenderingFormat().toString();
      return RestProperties.getInstance().getContentType();
	}
}

/*
 * $Log$
 * Revision 1.3  2007-02-11 17:55:26  mbork
 * switched REST-api to the 'new' datamodel, which does not deserve the name...
 *
 * Revision 1.2  2007/02/05 10:35:54  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.7  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.6  2006/07/05 16:27:57  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.5  2006/06/13 18:07:40  mbork
 * introduced unit tests for servlet using null-pattern for request and response. tested to use cactus/ httpunit, but decided not to use them.
 *
 * Revision 1.4  2006/06/11 15:25:25  mbork
 * removed gatekeeper, changed authentication process
 *
 * Revision 1.3  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.2  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/21 20:31:51  mbork
 * continued implementing context
 *
 * Revision 1.1  2006/05/19 21:01:08  mbork
 * started implementing rest api
 *
 */