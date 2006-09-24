package org.bibsonomy.rest.strategy.tags;

import java.io.StringWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.enums.GroupingEntity;
import org.bibsonomy.rest.exceptions.InternServerException;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfTagsStrategy extends Strategy
{
	/**
	 * @param context
	 */
	public GetListOfTagsStrategy( Context context )
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
	public void perform( HttpServletRequest request, StringWriter writer ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );

		GroupingEntity grouping = chooseGroupingEntity();
		String groupingValue = "";
		if( grouping != GroupingEntity.ALL )
		{
			groupingValue = context.getStringAttribute( grouping.toString().toLowerCase(), "" );
		}
		
		String regex = context.getStringAttribute( "filter", "" );
		
      Set<Tag> tags = context.getLogic().getTags( context.getAuthUserName(), grouping, groupingValue, regex, start, end );
      
      ViewModel viewModel = new ViewModel();
      if( tags.size() < end + 1 )
      {
         end = tags.size() - 1;
      }
      else
      {
         String next = Context.API_URL + Context.URL_TAGS + "?start=" + String.valueOf( end + 1 ) + 
         "&end=" + String.valueOf( end + 10 );
         if( grouping != GroupingEntity.ALL )
         {
            next += "&" + grouping.toString().toLowerCase() + "=" + groupingValue; 
         }
         if( !"".equals( regex ) )
         {
            next += "&" + "filter=" + regex;
         }
         viewModel.setUrlToNextResources( next );
      }
		viewModel.setStartValue( start );
		viewModel.setEndValue( end );
		
		// delegate to the renderer
		context.getRenderer().serializeTags( writer, tags, viewModel );
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.strategy.Strategy#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType( String userAgent )
	{
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/tags+" + context.getRenderingFormat().toString();
		return Context.DEFAULT_CONTENT_TYPE;
	}
}

/*
 * $Log$
 * Revision 1.9  2006-09-24 21:26:21  mbork
 * enabled sending the content-lenght, so that clients now can register callback objects which show the download progress.
 *
 * Revision 1.8  2006/07/05 16:27:57  mbork
 * fixed issues with link to next list of resources
 *
 * Revision 1.7  2006/07/05 15:27:51  mbork
 * place constants on left side of comparison
 *
 * Revision 1.6  2006/06/23 20:50:08  mbork
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
 * Revision 1.3  2006/06/05 14:14:11  mbork
 * implemented GET strategies
 *
 * Revision 1.2  2006/05/24 13:02:43  cschenk
 * Introduced an enum for the HttpMethod and moved the exceptions
 *
 * Revision 1.1  2006/05/22 10:42:25  mbork
 * implemented context chooser for /tags
 *
 */