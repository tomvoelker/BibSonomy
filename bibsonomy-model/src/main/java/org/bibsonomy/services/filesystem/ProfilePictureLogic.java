package org.bibsonomy.services.filesystem;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.bibsonomy.model.util.file.UploadedFile;

/**
 * @author dzo
 * @version $Id$
 */
public interface ProfilePictureLogic {
	
	/** the profile picture file extension */
	public static final String FILE_EXTENSION = ".jpg";
	
	/** all allowed file extensions for upload */
	public static final Collection<String> PICTURE_EXTENSIONS = Arrays.asList("png", FILE_EXTENSION, "jpeg");
	
	/**
	 * saves a profile picture for the provided username
	 * @param username
	 * @param pictureFile
	 * @throws Exception TODO
	 */
	public void saveProfilePictureForUser(final String username, final UploadedFile pictureFile) throws Exception;
	
	/**
	 * deletes the profile picture of user (identified by username)
	 * @param username
	 */
	public void deleteProfilePictureForUser(final String username);
	
	/**
	 * @param loggedinUser the name of the loggedin user
	 * @param username
	 * @return the profile picture
	 */
	public File getProfilePictureForUser(final String loggedinUser, final String username);
}
