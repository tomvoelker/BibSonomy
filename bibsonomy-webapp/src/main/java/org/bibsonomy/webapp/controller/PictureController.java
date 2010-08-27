package org.bibsonomy.webapp.controller;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.bibsonomy.common.enums.ProfilePrivlevel;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.utils.FileUploadInterface;
import org.bibsonomy.rest.utils.impl.FileUploadFactory;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.PictureCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * this controller returns handles picture upload and download
 * @author wla
 * @version $Id$
 */
public class PictureController implements MinimalisticController<PictureCommand> {

	/**
	 * Path to the picture folder
	 */
	private String path;
	
	private LogicInterface adminLogic;
	
	private RequestLogic requestLogic;
	
	/**
     * the factory used to get an instance of a FileUploadHandler.
     */
    private FileUploadFactory uploadFactory;
    
    
    private final Errors errors = null;
    
    private int sizeOfLargestSide;
    
    private String defaultFileName;
	
	@Override
	public PictureCommand instantiateCommand() {
		return new PictureCommand();
	}
	
	@Override
	public View workOn(PictureCommand command) {
		String method = requestLogic.getMethod();
		if(method.equals("GET")) { //picture requested
			return downloadPicture(command);
		} else if(method.equals("POST")) { //picture upload requested
			Views view = uploadPicture(command);
			if(view == null) {
				return new ExtendedRedirectView("/settings");
			}
			return view;
		} else { 
			return Views.ERROR;
		}
		
	}
	
	private View downloadPicture(PictureCommand command) {
		String name = command.getRequestedUser();
		String currentUserName = command.getContext().getLoginUser().getName(); //user who started the request
		User picOwner = adminLogic.getUserDetails(name);

		boolean showpicture = false;
		
		 /* 
		  * if the owner of the picture wasn't  founded 
		  */
		if (picOwner == null) {
			return Views.ERROR;
		}
		
		/*
		 * request from picture owner himself  
		 */
		if (picOwner.getName().equals(currentUserName)) {
			showPicture(command, name);
			return Views.DOWNLOAD_FILE;
		}
		
		/*
		 *Visibility states: 0 = public, 1 = private, 2 = friends
		 */
		ProfilePrivlevel visibility = picOwner.getSettings().getProfilePrivlevel();
		//Stirng test = picOwner.getSettings().getProfilePrivlevel().
		switch(visibility) {
			case PUBLIC:
				showpicture = true;
				break;
			case FRIENDS:
				if (currentUserName != null) {
					List<User> friends = adminLogic.getFriendsOfUser(picOwner);
					for (User friend : friends) {
						if (currentUserName.equals(friend.getName())) { //currentUserName cannot be null here
							showpicture = true;
						}
					}
				}
				break;
			default:
				break;
					
		}
		
		if (showpicture) {
			showPicture(command, name);
		} else {
			showDummy(command);
		}
		return Views.DOWNLOAD_FILE;
	}
	
	/**
	 * this method search for user picture and select it to show
	 * @param command
	 * @param name
	 */
	private void showPicture(PictureCommand command, String name) {
		/*
		 * pattern of the name of picture file: "hash(username)"
		 */
		String hash = StringUtils.getMD5Hash(name);
		
		/*
		 * pictures are in the different folders, named by the fist
		 * two signs of hash
		 */
		String picturePath = FileUtil.getDocumentPath(path, hash);
	
		/*
		 * verify existence of picture, and set it to view 
		 */
		File pic = new File(picturePath);
		if (pic.exists()) { 
			command.setPathToFile(picturePath);
			command.setContentType(FileUtil.getContentType(picturePath));
			command.setFilename(hash);
		} else {
			showDummy(command);
		}
	}
	
	
	/**
	 * this method selects a dummy picture to show
	 * @param command
	 */
	private void showDummy(PictureCommand command) {
		command.setPathToFile(path + defaultFileName);
		command.setContentType(FileUtil.getContentType(command.getPathToFile()));
		command.setFilename(defaultFileName);
	}
	
	/**
	 * This method manage the picture upload
	 * 
	 * @param command
	 * @return Error view or null if upload successful 
	 */
	private Views uploadPicture(PictureCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		// check if user is logged in, if not throw an error and go directly
		// back to uploadPage
		if (!context.isUserLoggedIn()) {
			errors.reject("error.general.login");
			return Views.ERROR;
		}

		/*
		 * check credentials to fight CSRF attacks 
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		if (command.getFile() != null) {
			/*
			 * set the headless mode for awt library
			 * FIXME does it work?
			 */
			System.setProperty("java.awt.headless", "true");
			
			try {
				if (command.getFile().getSize() != 0) { //if a file to upload selected
					/*
					 * upload file
					 */
					final FileUploadInterface uploadFileHandler = this.uploadFactory.getFileUploadHandler(Collections.singletonList(command.getFile().getFileItem()), FileUploadInterface.pictureExt);
					File file = uploadFileHandler.writeUploadedFile().getFile();
					
					/*
					 * convert picture
					 */
					BufferedImage userPic = convert(file);
					
					/*
					 * delete uploaded file
					 */
					file.delete();
					
					String username = context.getLoginUser().getName();
					
					/*
					 * pattern of the picture path: /picturefolder/"first two chars of filename"
					 */
					String directory = FileUtil.getFileDir(path, StringUtils.getMD5Hash(username)); 
					/*
					 * check existence of target folder
					 */
					file = new File(directory);
					if(!file.exists()) {
						file.mkdir();
					}
					
					/*
					 * store picture
					 */
					file = new File(FileUtil.getDocumentPath(path, StringUtils.getMD5Hash(username)));
					ImageIO.write(userPic, "jpeg", file);
				} else { //if no file to upload selected
					deleteUserPicture(context.getLoginUser().getName());
				}

			} catch (Exception ex) {
				errors.reject("error.upload.failed", new Object[] { ex.getLocalizedMessage() }, "Sorry, we could not process your upload request, an unknown error occurred.");
				return Views.ERROR;
			}
		}
		
		return null;
	}
	
	/**
	 * Deletes the picture from the file system
	 * 
	 * @param username
	 */
	private void deleteUserPicture(String username ) {
		String path = FileUtil.getDocumentPath(this.path, StringUtils.getMD5Hash(username));
		File picture = new File(path);
		if (picture.exists()) {
			picture.delete();
		}
	}
	
	/**
	 * Converts picture to standard size
	 * @param imageFile
	 * @return ready to write BufferedImage
	 * @throws IOException
	 */
	private BufferedImage convert(File imageFile) throws IOException {
		Image image = ImageIO.read(imageFile);
		Image scaledImage = null;
		
		/*
		 * convert picture to the standard size with fixed aspect ratio
		 */
		if (image.getWidth(null) >= image.getWidth(null)) {
			scaledImage = image.getScaledInstance(sizeOfLargestSide, 
					-1, Image.SCALE_SMOOTH);
		} else {
			scaledImage = image.getScaledInstance(-1, sizeOfLargestSide, 
					Image.SCALE_SMOOTH);
		}
		
		/*
		 * create new BufferedImage with converted picture
		 */
		BufferedImage outImage = new BufferedImage(scaledImage.getWidth(null),
				scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = outImage.getGraphics();
		g.drawImage(scaledImage, 0, 0, null);
		g.dispose();
		
		return outImage;
	}
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return adminLogic;
	}

	/**
	 * @param requestLogic the requestLogic to set
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @return the requestLogic
	 */
	public RequestLogic getRequestLogic() {
		return requestLogic;
	}

	/**
	 * @param uploadFactory the uploadFactory to set
	 */
	public void setUploadFactory(FileUploadFactory uploadFactory) {
		this.uploadFactory = uploadFactory;
	}

	/**
	 * @return the uploadFactory
	 */
	public FileUploadFactory getUploadFactory() {
		return uploadFactory;
	}

	/**
	 * @param sizeOfLargestSide the sizeOfLargestSide to set
	 */
	public void setSizeOfLargestSide(int sizeOfLargestSide) {
		this.sizeOfLargestSide = sizeOfLargestSide;
	}

	/**
	 * @return the sizeOfLargestSide
	 */
	public int getSizeOfLargestSide() {
		return sizeOfLargestSide;
	}

	/**
	 * @param defaultFileName the defaultFileName to set
	 */
	public void setDefaultFileName(String defaultFileName) {
		this.defaultFileName = defaultFileName;
	}

	/**
	 * @return the defaultFileName
	 */
	public String getDefaultFileName() {
		return defaultFileName;
	}

}
