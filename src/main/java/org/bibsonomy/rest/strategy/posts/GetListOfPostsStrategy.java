package org.bibsonomy.rest.strategy.posts;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.model.Post;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.enums.ResourceType;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfPostsStrategy extends Strategy
{
	/**
	 * @param context
	 */
	public GetListOfPostsStrategy( Context context )
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
		
		ResourceType resourceType = ResourceType.getResourceType( context.getStringAttribute( "resourcetype", "all" ) );
		
		String hash = context.getStringAttribute( "resource", "" );
		
		GroupingEntity grouping = chooseGroupingEntity();
		String groupingValue = "";
		if( grouping != GroupingEntity.ALL )
		{
			groupingValue = context.getStringAttribute( grouping.toString().toLowerCase(), "" );
		}
		
      Set<Post> posts = context.getLogic().getPosts( context.getAuthUserName(), resourceType, grouping,
            groupingValue, context.getTags( "tags" ), hash, false, false, start, end );
      ViewModel viewModel = new ViewModel();
      if( posts.size() < end + 1 )
      {
         end = posts.size() - 1;
      }
      else
      {
         String next = Context.API_URL + Context.URL_POSTS + "?start=" + String.valueOf( end + 1 ) + "&end="
         + String.valueOf( end + 10 );
         if( resourceType != ResourceType.ALL )
         {
            next += "&resourcetype=" + resourceType.toString().toLowerCase();
         }
         String tags = context.getStringAttribute( "tags", "" );
         if( !"".equals( tags ) )
         {
            next += "&tags=" + tags;
         }
         if( !"".equals( hash ) )
         {
            next += "&resource=" + hash;
         }
         if( grouping != GroupingEntity.ALL )
         {
            next += "&" + grouping.toString().toLowerCase() + "=" + groupingValue; 
         }
         viewModel.setUrlToNextResources( next );
      }
		viewModel.setStartValue( start );
		viewModel.setEndValue( end );
		
		// delegate to the renderer
		try
		{
			context.getRenderer().serializePosts( response.getWriter(), posts, viewModel );
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
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/posts+" + context.getRenderingFormat().toString();
		return Context.DEFAULT_CONTENT_TYPE;
	}
}

/*
 * $Log$
 * Revision 1.9  2006-07-05 16:27:57  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.8  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.7  2006/07/05 15:20:14  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.6  2006/06/23 20:50:09  mbork
 * clientlib:
 * - added head request
 * - fixed issues with enums using uppercase letters invoked with toString()
 * serverlib:
 * - fixed some issues
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
 * Revision 1.2  2006/05/24 13:02:44  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:52:46  mbork
 * implemented context chooser for /posts
 *
 */