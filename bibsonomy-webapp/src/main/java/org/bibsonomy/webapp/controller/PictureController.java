package org.bibsonomy.webapp.controller;

import java.io.File;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.file.FileUtil;
import org.bibsonomy.webapp.command.actions.DownloadFileCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * this controller returns view with requested userpicture or a dummypicture 
 * @author Waldemar Lautenschlager
 * @version $Id$
 */
public class PictureController implements MinimalisticController<DownloadFileCommand> {

	private String path;
	private LogicInterface adminLogic;
	
	@Override
	public DownloadFileCommand instantiateCommand() {
		return new DownloadFileCommand();
	}

	@Override
	public View workOn(DownloadFileCommand command) {
		
		String name = command.getRequestedUser();
		String currentUserName = command.getContext().getLoginUser().getName(); //user who started the reqest
		User picOwner = adminLogic.getUserDetails(name);
		
		
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
		int visibility = picOwner.getSettings().getProfilePrivlevel().getProfilePrivlevel();
		
		/*
		 * case the picture is public
		 */
		if (visibility == GroupID.PUBLIC.getId()) {
			showPicture(command, name);
			return Views.DOWNLOAD_FILE;
		}
		
		/*
		 * request of not logged-in user, and visibility is not public
		 * TODO visibility may be blocked for all not logged-in users?
		 */
		if (currentUserName == null && visibility != GroupID.PUBLIC.getId()) {
			showDummy(command);
			return Views.DOWNLOAD_FILE;
		}
		
		
		/*
		 * check friends of user
		 */
		if (visibility == GroupID.FRIENDS.getId()) {
			List<User> friends = adminLogic.getFriendsOfUser(picOwner);
			for (User friend : friends) {
				if (currentUserName.equals(friend.getName())) { //currentUserName cannot be null here
					showPicture(command, name);
					return Views.DOWNLOAD_FILE;
				}
			}
		}
		
		showDummy(command);
		return Views.DOWNLOAD_FILE;
	}
	
	/**
	 * this method search for user picture and select it to show
	 * @param command
	 * @param name
	 */
	private void showPicture(DownloadFileCommand command, String name) {
		/*
		 * pattern of the name of picture file: "hash(username)_username.jpg"
		 */
		String hash = StringUtils.getMD5Hash(name);
		
		/*
		 * pictures are in the different folders, named by the fist
		 * two signs of hash
		 */
		String picturePath = path + hash.substring(0, 2) + "/" + hash + "_" + name + ".jpg";
	
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
	 * this method selects a dummypicture to show
	 * @param command
	 */
	private void showDummy(DownloadFileCommand command) {
		command.setPathToFile(path + "/no_picture.png");
		command.setContentType(FileUtil.getContentType(command.getPathToFile()));
		command.setFilename("no_picture.png");
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

}
