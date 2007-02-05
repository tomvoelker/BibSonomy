package org.bibsonomy.rest.strategy.users;

import java.io.StringWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.gen_model.Post;
import org.bibsonomy.rest.RestProperties;
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
public class GetUserPostsStrategy extends Strategy
{
	private String userName;

	/**
	 * @param context
	 * @param userName 
	 */
	public GetUserPostsStrategy( Context context, String userName )
	{
		super( context );
		this.userName = userName;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#validate()
	 */
	@Override
	public void validate() throws ValidationException
	{
		// TODO check username for existance - or should the request then just return an empty list?
		// should be ok for everybody
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#perform(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );
		
		ResourceType resourceType = ResourceType.getResourceType( context.getStringAttribute( "resourcetype", "all" ) );
		
      Set<Post> posts = context.getLogic().getPosts( context.getAuthUserName(), resourceType, GroupingEntity.USER,
            userName, context.getTags( "tags" ), "", false, false, start, end );
      
      ViewModel viewModel = new ViewModel();
      if( posts.size() < end + 1 )
      {
         end = posts.size() - 1;
      }
      else
      {
         String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getUsersUrl() + "/" + userName + "/" + RestProperties.getInstance().getPostsUrl() + "?start="
         + String.valueOf( end + 1 ) + "&end=" + String.valueOf( end + 10 );
        
         String tags = context.getStringAttribute( "tags", "" );
         if( !"".equals( tags ) )
         {
            next += "&tags=" + tags;
         }
         
         if( resourceType != ResourceType.ALL )
         {
            next += "&resourcetype=" + resourceType.toString();
         }
         viewModel.setUrlToNextResources( next );
      }
		viewModel.setStartValue( start );
		viewModel.setEndValue( end );
		
		// delegate to the renderer
		context.getRenderer().serializePosts( writer, posts, viewModel );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/posts+" + context.getRenderingFormat().toString();
      return RestProperties.getInstance().getContentType();
	}
}

/*
 * $Log$
 * Revision 1.2  2007-02-05 10:35:54  cschenk
 * Distributed code from the spielwiese among the modules
 *
 * Revision 1.1  2006/10/24 21:39:52  mbork
 * split up rest api into correct modules. verified with junit tests.
 *
 * Revision 1.1  2006/10/10 12:42:12  cschenk
 * Auf Multi-Module Build umgestellt
 *
 * Revision 1.9  2006/09/24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.8  2006/07/05 16:27:57  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.7  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.6  2006/07/05 15:20:13  mbork
 * implemented missing strategies, little changes on datamodel --> alpha :)
 *
 * Revision 1.5  2006/06/13 21:30:41  mbork
 * implemented unit tests for get-strategies; fixed some minor bugs
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
 */