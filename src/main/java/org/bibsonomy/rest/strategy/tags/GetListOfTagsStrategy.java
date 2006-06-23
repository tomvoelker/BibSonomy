package org.bibsonomy.rest.strategy.tags;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	public void perform( HttpServletRequest request, HttpServletResponse response ) throws InternServerException
	{
		// setup viewModel
		int start = context.getIntAttribute( "start", 0 );
		int end = context.getIntAttribute( "end", 19 );
		String next = Context.API_URL + Context.URL_TAGS + "?start=" + String.valueOf( end + 1 ) + 
			"&end=" + String.valueOf( end + 10 );

		GroupingEntity grouping = chooseGroupingEntity();
		String groupingValue = "";
		if( grouping != GroupingEntity.ALL )
		{
			groupingValue = context.getStringAttribute( grouping.toString().toLowerCase(), "" );
			next += "&" + grouping.toString().toLowerCase() + "=" + groupingValue; 
		}
		
		String regex = context.getStringAttribute( "filter", "" );
		if( !regex.equals( "" ) )
		{
			next += "&" + "filter=" + regex;
		}
		
		ViewModel viewModel = new ViewModel();
		viewModel.setStartValue( start );
		viewModel.setEndValue( end );
		viewModel.setUrlToNextResources( next );
		
		// delegate to the renderer
		Set<Tag> tags = context.getLogic().getTags( context.getAuthUserName(), grouping, groupingValue, regex, start, end );
		try 
		{
			context.getRenderer().serializeTags( response.getWriter(), tags, viewModel );
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
		if( context.apiIsUserAgent( userAgent ) ) return "bibsonomy/tags+" + context.getRenderingFormat().toString();
		return Context.DEFAULT_CONTENT_TYPE;
	}
}

/*
 * $Log$
 * Revision 1.6  2006-06-23 20:50:08  mbork
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