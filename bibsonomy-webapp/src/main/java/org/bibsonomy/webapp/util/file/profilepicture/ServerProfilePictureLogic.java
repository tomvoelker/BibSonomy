/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util.file.profilepicture;

import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.file.UploadedFile;
import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.services.filesystem.TempFileLogic;
import org.bibsonomy.services.filesystem.extension.ExtensionChecker;
import org.bibsonomy.services.filesystem.extension.ListExtensionChecker;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.util.file.profilepicture.PictureScaler;

/**
 * @author dzo
 */
public class ServerProfilePictureLogic implements ProfilePictureLogic {
	private final String path;
	private String defaultFileName;
	
	private TempFileLogic tempFileLogic;
	private final ExtensionChecker extensionChecker = new ListExtensionChecker(PICTURE_EXTENSIONS);
	
	private PictureScaler pictureScaler;
	private LogicInterface adminLogic; // TODO: remove?
	
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
	public File getProfilePictureForUser(String username) {
		File file = getProfilePicture(username);
		
		if (file == null || !file.exists())
			return this.getDefaultFile();
		
		//else:
		file.setReadOnly(); // never modify files outside the logic!
		return file;
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
		final File defaultFile = new File(this.path, this.defaultFileName);
		defaultFile.setReadOnly();
		return defaultFile;
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
