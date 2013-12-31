package org.bibsonomy.webapp.util.picture;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.actions.PictureCommand;

/**
 * A {@link PictureHandler} implementation requesting user's <a href="http://gravatar.com">Gravatar</a> profile picture.
 * 
 * @author cut
 * @version $Id:$
 * @see PictureHandler
 * @see ExternalPictureHandler
 * @see <a href="http://gravatar.com/site/implement/">http://gravatar.com/site/implement/</a>
 */
public class GravatarPictureHandler extends ExternalPictureHandler
{
	
	/**
	 * Creates a new {@link GravatarPictureHandler} instance with target user and command.
	 * 
	 * @param user - requested user
	 * @param command - actual picture command
	 */
	public GravatarPictureHandler ( User user, PictureCommand command )
	{
		super(user, command);
	}

	/**
	 * The Gravatar request url, where
	 * <ul>
	 * 	<li>first variable is user's hash;</li>
	 * 	<li>second picture file extension;</li>
	 * 	<li>third default behaviour.</li>
	 * </ul>
	 * See also {@link <a href="http://gravatar.com/site/implement/images/">http://gravatar.com/site/implement/images/</a>}
	 */
	protected final static String GRAVATAR_REQ_URL 
										= "http://www.gravatar.com/avatar/%s%s?d=%s&s=128";
	
	/**
	 * Gravatar request url actual parameter indicating the behaviour, if there isn't any picture file uploaded.</br>
	 * 
	 * <p>
	 * 	See also {@link <a href="http://de.gravatar.com/site/implement/images/">http://de.gravatar.com/site/implement/images/</a>}
	 * </p>
	 */
	protected final static String DEFAULT_BEHAVIOUR = "mm";
	
	
	/**
	 * Generates Gravatar URI for this request's email address.
	 * 
	 * @param address :	Gravatar email address as String
	 * @param defaultBehav : specifies Gravatar behaviour if there is no picture for the address
	 * @param fileExtension : requested file extension or empty String.
	 * @return Gravatar URI as String
	 */
	@Override
	protected URL getPictureURL ( String address, String fileExtension )
	{
		//hash user's gravatar email, use default-picture "mystery-man", use resolution 128x128;
		try {
			return new URL( String.format(GRAVATAR_REQ_URL, hashAddress(address), fileExtension, DEFAULT_BEHAVIOUR) );
		} 
		catch (MalformedURLException ex) {
			//shouldn't happen!
			return null;
		}
	}
	
	/**
	 * Hashes target user's address.
	 * <p>Note: {@link ExternalPictureHandler} implementation applies <em>Apache's MD5</em> hash.</p>
	 * 
	 * @param address - target address as string
	 * @return address' hash
	 */
	protected String hashAddress ( String address )
	{
		if ( address == null || address.isEmpty() )
			return "0";
		
		//else:
		String result = DigestUtils.md5Hex( address.trim().toLowerCase() );
		return result;
	}

}