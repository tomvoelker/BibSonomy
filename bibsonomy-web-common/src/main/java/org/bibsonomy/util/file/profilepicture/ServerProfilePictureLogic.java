/**
 *
 *  BibSonomy-Web-Common - Common things for web
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util.file.profilepicture;

import static org.bibsonomy.util.ValidationUtils.present;

import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.services.filesystem.TempFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;

/**
 * @author dzo
 */
public class ServerProfilePictureLogic implements ProfilePictureLogic {
	private final String path;
	private String defaultFileName;
	
	private TempFileLogic tempFileLogic;
	private final ExtensionChecker extensionChecker = new ListExtensionChecker(PICTURE_EXTENSIONS);
	
	private PictureScaler pictureScaler;
	private LogicInterface adminLogic;
	
	/**
	 * default constructor
	 * @param path
	 */
	public ServerProfilePictureLogic(String path) {
		this.path = path;
	}
	
	/**
	 * Constructs the path to the picture, given the user name.
	 * 
	 * @param userName
	 * @return
	 */
	private String getPicturePath(final String userName) {
		/*
		 * pattern of the name of picture file: "hash(username).jpg"
		 */
		final String fileName = StringUtils.getMD5Hash(userName) + FILE_EXTENSION;
		/*
		 * pictures are in the different folders, named by the fist
		 * two signs of hash
		 */
		return FileUtil.getFilePath(this.path, fileName);
	}
	
	@Override
	public File getProfilePictureForUser(String loggedinUser, String username) {
		File file = getVisibleProfilePictureForUser(loggedinUser, username);
		
		if (file == null)
			return this.getDefaultFile();
		
		//else:
		file.setReadOnly(); // never modify files outside the logic!
		return file;
	}
	
	private File getVisibleProfilePictureForUser(String loggedinUser, String username) {
		if (!this.pictureVisible(username, loggedinUser)) {
			return null;
		}
		
		final File file = this.getProfilePicture(username);
		if (!file.exists()) {
			return null;
		}
		
		return file;
	}
	
	public boolean hasVisibleProfilePicture(String loggedinUser, String username) {
		return getVisibleProfilePictureForUser(loggedinUser, username) != null;
	}

	@Override
	public void saveProfilePictureForUser(String username, UploadedFile pictureFile) throws Exception {
		/*
		 * temporary store file on file system
		 */
		final File uploadedFile = this.tempFileLogic.writeTempFile(pictureFile, this.extensionChecker);
		/*
		 * scale picture
		 */
		final RenderedImage scaledPicture = this.pictureScaler.scalePicture(ImageIO.read(uploadedFile));
		
		/*
		 * delete temporary file
		 */
		this.tempFileLogic.deleteTempFile(uploadedFile.getName());

		/*
		 * check existence of target folder
		 */
		final File directory = FileUtil.getFileDirAsFile(this.path, StringUtils.getMD5Hash(username));
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		/*
		 * write scaled image to disk
		 */
		ImageIO.write(scaledPicture, "jpeg", new File(getPicturePath(username)));
	}

	@Override
	public void deleteProfilePictureForUser(String username) {
		final File picture = getProfilePicture(username);
		if (picture.exists()) {
			picture.delete();
		}
	}

	private File getProfilePicture(String username) {
		return new File(getPicturePath(username));
	}
	
	private File getDefaultFile() {
		return new File(this.path, this.defaultFileName);
	}
	
	/**
	 * Checks if the loginUser may see the profile picture of the requested user.
	 * Depends on the profile privlevel of the requested user.
	 * 
	 * @param requestedUser
	 * @param loginUserName
	 * @return
	 */
	private boolean pictureVisible(final String requestedUserName, final String loginUserName) {
		/*
		 * login user may always see his/her photo
		 */
		if (requestedUserName.equals(loginUserName)) return true;
		
		final User requestedUser = this.adminLogic.getUserDetails(requestedUserName);
		if (!present(requestedUser.getName())) {
			throw new ObjectNotFoundException(requestedUserName);
		}
		/*
		 * Check the visibility depending on the profile privacy level.
		 */
		final ProfilePrivlevel visibility = requestedUser.getSettings().getProfilePrivlevel();
		switch(visibility) {
		case PRIVATE:
			return requestedUserName.equals(loginUserName);
		case PUBLIC:
			return true;
		case FRIENDS:
			if (present(loginUserName)) {
				final List<User> friends = adminLogic.getUserRelationship(requestedUserName, UserRelation.OF_FRIEND, null);
				for (final User friend : friends) {
					if (loginUserName.equals(friend.getName())) {
						return true;
					}
				}
			}
			//$FALL-THROUGH$
		default:
			return false;
		}
	}

	/**
	 * @param defaultFileName the defaultFileName to set
	 */
	public void setDefaultFileName(String defaultFileName) {
		this.defaultFileName = defaultFileName;
	}

	/**
	 * @param pictureScaler the pictureScaler to set
	 */
	public void setPictureScaler(PictureScaler pictureScaler) {
		this.pictureScaler = pictureScaler;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param tempFileLogic the tempFileLogic to set
	 */
	public void setTempFileLogic(TempFileLogic tempFileLogic) {
		this.tempFileLogic = tempFileLogic;
	}
}
