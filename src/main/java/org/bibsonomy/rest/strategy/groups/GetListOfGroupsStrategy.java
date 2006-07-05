package org.bibsonomy.rest.strategy.groups;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfGroupsStrategy extends Strategy
{
	/**
	 * @param context
	 */
	public GetListOfGroupsStrategy( Context context )
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
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );
		
      Set<Group> groups = context.getLogic().getGroups( context.getAuthUserName(), start, end );
      
      ViewModel viewModel = new ViewModel();
      if( groups.size() < end + 1 )
      {
         end = groups.size() - 1;
      }
      else
      {
         String next = Context.API_URL + Context.URL_GROUPS + "?start=" + String.valueOf( end + 1 ) + 
         "&end=" + String.valueOf( end + 10 );
         viewModel.setUrlToNextResources( next );
      }
      viewModel.setStartValue( start );
      viewModel.setEndValue( end );
		
		try 
		{
		   // delegate to the renderer
			context.getRenderer().serializeGroups( response.getWriter(), groups, viewModel );
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
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/groups+" + context.getRenderingFormat().toString();
		return Context.DEFAULT_CONTENT_TYPE;
	}
}

/*
 * $Log$
 * Revision 1.6  2006-07-05 16:27:58  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.5  2006/06/13 21:30:40  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
 *
 * Revision 1.4  2006/06/11 15:25:25  mbork
 * removed gatekeeper, changed authentication process
 *
 * Revision 1.3  2006/06/05 14:14:12  mbork
 * implemented GET strategies
 *
 * Revision 1.2  2006/05/24 13:02:43  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:34:38  mbork
 * implemented context chooser for /groups
 *
 */