package org.bibsonomy.rest.client.worker;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.renderer.xml.BibsonomyXML;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class GetWorker extends HttpWorker
{
	private int httpResult;

	public GetWorker( String username, String password )
	{
		super( username, password );
	}
	
	public BibsonomyXML perform( String url ) throws ErrorPerformingRequestException
	{
		LOGGER.log( Level.INFO, "GET: URL: " + url );
		
		GetMethod get = new GetMethod( url );
		get.addRequestHeader( HEADER_AUTHORIZATION, encodeForAuthorization() );
		get.setDoAuthentication( true );
		get.setFollowRedirects( true );
		
		try
		{
			httpResult = getHttpClient().executeMethod( get );
			LOGGER.log( Level.INFO, "Result: " + httpResult );
			if( get.getResponseBodyAsStream() != null )
			{
				return getBibsonomyXML( get.getResponseBodyAsStream() );
			}
		}
		catch( IOException e )
		{
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			throw new ErrorPerformingRequestException( e );
		}
		catch( JAXBException e )
		{
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
			throw new ErrorPerformingRequestException( e );
		}
		finally
		{
			get.releaseConnection();
		}
		
		return new BibsonomyXML();
	}

	private BibsonomyXML getBibsonomyXML( InputStream is ) throws JAXBException 
	{
        JAXBContext jc = JAXBContext.newInstance( "org.bibsonomy.rest.renderer.xml" );
        
        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();

        /*
         * unmarshal a xml instance document into a tree of Java content
         * objects composed of classes from the restapi package. 
         */
        JAXBElement<?> xmlDoc = (JAXBElement<?>)u.unmarshal( is );
        return (BibsonomyXML)xmlDoc.getValue();
	}

	/**
	 * @return Returns the httpResult.
	 */
	public int getHttpResult()
	{
		return httpResult;
	}
}

/*
 * $Log$
 * Revision 1.2  2006-06-07 19:37:28  mbork
 * implemented post queries
 *
 * Revision 1.1  2006/06/06 22:20:54  mbork
 * started implementing client api
 *
 */