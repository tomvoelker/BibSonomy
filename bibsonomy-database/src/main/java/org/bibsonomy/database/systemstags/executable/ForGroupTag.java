/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.systemstags.executable;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.database.DBLogicNoAuthInterfaceFactory;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsExtractor;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PostUtils;
import org.bibsonomy.model.util.file.FileSystemFile;
import org.bibsonomy.services.filesystem.FileLogic;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * System tag 'sys:for:&lt;groupname&gt;'
 * Description: 
 *   If user tags a post with [sys:]for:&lt;groupname&gt;, a copy of the resource
 *   is created which is owned by the group. Furthermore, the copied resource is tagged 
 *   with from:&lt;username&gt; instead of for:&lt;groupname&gt;.
 *   
 *  Precondition: 
 *   User is member of given group 
 * @author fei
 */
public class ForGroupTag extends AbstractSystemTagImpl implements ExecutableSystemTag {

	private static final String NAME = "for";
	private static boolean toHide = true;

	private DBLogicNoAuthInterfaceFactory logicInterfaceFactory = null;
	private FileLogic fileLogic;
	
	@Override
	public ForGroupTag newInstance() {
		return new ForGroupTag();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return toHide;
	}

	/**
	 * @param logicInterfaceFactory the logicInterfaceFactory to set
	 */
	public void setLogicInterfaceFactory(final DBLogicNoAuthInterfaceFactory logicInterfaceFactory) {
		this.logicInterfaceFactory = logicInterfaceFactory;
	}

	/**
	 * @param fileLogic the fileLogic to set
	 */
	public void setFileLogic(FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}

	@Override
	public <T extends Resource> void performBeforeCreate(final Post<T> post, final DBSession session) {
		log.debug("performing before create");
		// we assume, that the post itself is valid
		this.copyPostToGroup(post, post.getTags(), session);
	}

	@Override
	public <T extends Resource> void performAfterCreate(final Post<T> post, final DBSession session) {
		if (post.getResource() instanceof BibTex) {
			if (!this.hasPermissions(this.getArgument(), post.getUser().getName(), session)) {
				/*
				 *  user is not allowed to use this tag
				 */
				return;
			}
			final BibTex publication = (BibTex) post.getResource();
			this.copyDocuments(publication.getIntraHash(), post.getUser().getName(), publication.getDocuments(), session);
		}
	}

	private void copyDocuments(final String intraHash, final String userName, final List<Document> documents, DBSession session) {
		final String groupName = this.getArgument();
		if (!this.hasPermissions(groupName, userName, session)) {
			/*
			 *  user is not allowed to use this tag
			 */
			return;
		}
		final LogicInterface groupDBLogic = this.getGroupDbLogic();
		if (!present(groupDBLogic.getGroupDetails(groupName, false))) {
			return;
		}
		
		if (present(documents)) {
			for (final Document document : documents) {
				final String fileName = document.getFileName();
				final Document existingGroupDoc = groupDBLogic.getDocument(groupName, intraHash, fileName);
				if (!present(existingGroupDoc)) {
					final File file = this.fileLogic.getFileForDocument(document);
					try {
						final Document groupDocument = this.fileLogic.saveDocumentFile(groupName, new FileSystemFile(file, fileName));
						groupDocument.setFileName(fileName);
						groupDBLogic.createDocument(groupDocument, intraHash);
					} catch (final Exception e) {
						log.error("error while copying user document to group post", e);
					}
				} else {
					/*
					 * only if files are different create a copy
					 */
					if (!document.getMd5hash().equals(existingGroupDoc.getMd5hash())) {
						final File file = this.fileLogic.getFileForDocument(document);
						try {
							final Document groupDocument = this.fileLogic.saveDocumentFile(groupName, new FileSystemFile(file, fileName));
							String oldFileName = fileName;
							final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy_mm_dd_hh_mm_ss");
							
							final String newFileName = oldFileName.replace(".", "_" + fmt.print(new DateTime()) + ".");
							groupDocument.setFileName(newFileName);
							groupDBLogic.createDocument(groupDocument, intraHash);
						} catch (final Exception e) {
							log.error("error while copying document to group post", e);
						}
					}
				}
			}
		}
	}

	@Override
	public <T extends Resource> void performBeforeUpdate(final Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session) {
		/*
		 * e.g PostUpdateOperation TagOnly as only a dummy as new post
		 * so we have to choose here the correct post
		 */
		final Post<T> post;
		if (PostUpdateOperation.UPDATE_ALL.equals(operation)) {
			post = newPost;
		} else {
			post = oldPost;
		}
		this.copyPostToGroup(post, newPost.getTags(), session);
	}

	@Override
	public <T extends Resource> void performAfterUpdate(final Post<T> newPost, final Post<T> oldPost, final PostUpdateOperation operation, final DBSession session) {
		log.debug("performing after update");
		if (oldPost.getResource() instanceof BibTex) {
			final BibTex oldPublication = (BibTex) oldPost.getResource();
			final List<Document> documents = new LinkedList<Document>(oldPublication.getDocuments());
			final List<Document> newDocuments = ((BibTex) newPost.getResource()).getDocuments();
			if (present(newDocuments)) {
				for (final Document newDocument : newDocuments) {
					/*
					 * to secure only add temp docs of newPost
					 */
					if (newDocument.isTemp()) {
						documents.add(newDocument);
					}
				}
			}
			this.copyDocuments(newPost.getResource().getIntraHash(), newPost.getUser().getName(), documents, session);
		}
	}

	/**
	 * Make post for the group and store it in the database
	 * @param <T>
	 * @param userPost the post to store (we ignore its tags)
	 * @param userTags the tags for the post
	 * @param session
	 * @return <code>true</code> if the post was created for the grou
	 */
	protected <T extends Resource> boolean copyPostToGroup(final Post<T> userPost, final Set<Tag> userTags, final DBSession session) {
		log.debug("copy post to group");
		final String groupName = this.getArgument(); // the group's name
		final String userName = userPost.getUser().getName();
		final T resource = userPost.getResource();
		final String intraHash = resource.getIntraHash();

		if (!this.hasPermissions(groupName, userName, session)) {
			/*
			 *  user is not allowed to use this tag
			 */
			return false;
		}
		/*
		 * Make a DBLogic for the group
		 */
		final LogicInterface groupDBLogic = this.getGroupDbLogic();
		/*
		 *  Check if the group exists and whether it owns the post already
		 */
		if (!present(groupDBLogic.getGroupDetails(groupName, false))) {
			/*
			 *  We decided to ignore errors in systemTags. Thus the user
			 *  is free use any tag. XXX: The drawback: If it is the user's
			 *  intention to use a systemTag, he will never know if there
			 *  was a typo! 
			 */
			return false; // this tag can not be used => abort
		}
		try {
			if (present(groupDBLogic.getPostDetails(intraHash, groupName) )) {
				log.debug("Given post already owned by group. Skipping...");
				return false;
			}
		} catch (final Exception ex) {
			// ignore
		}
		/*
		 *  Permissions are granted and the group doesn't own the post yet
		 *  => Copy the post and store it for the group
		 *  FIXME: How do we properly clone a post?
		 */
		final Post<T> groupPost = new Post<T>();
		groupPost.setResource(resource);
		/*
		 * we must unset the documents else the group saves
		 * temp files
		 */
		List<Document> documents = null;
		if (resource instanceof BibTex) {
			final BibTex publication = (BibTex) resource;
			documents = publication.getDocuments();
			publication.setDocuments(null);
		}
		groupPost.setDescription(userPost.getDescription());
		groupPost.setDate(new Date());
		groupPost.setUser(new User(groupName));
		/* 
		 * Copy Tags: 
		 * remove all systemTags to avoid any side effects and contradictions 
		 */
		final Set<Tag> groupTags = new HashSet<Tag>(userTags);
		SystemTagsExtractor.removeAllExecutableSystemTags(groupTags);
		/*
		 * adding this tag also guarantees, that the new post will
		 * have an empty tag set (which would be illegal)!
		 */
		groupTags.add(new Tag("from:" + userName));
		groupPost.setTags(groupTags);
		/*
		 *  Copy Groups: the visibility of the postCopy is:
		 *  original == public => copy = public
		 *  original != public => copy = dbGroup
		 *  => check if post.groups has only the public group
		 */
		if ((userPost.getGroups().size() == 1) && (userPost.getGroups().contains(GroupUtils.buildPublicGroup()))) {
			// public is the only group (if visibility was public, there should be only one group)
			groupPost.setGroups(new HashSet<Group>());
			groupPost.getGroups().add(GroupUtils.buildPublicGroup());
		} else {
			// visibility is different from public => post is only visible for dbGroup
			groupPost.addGroup(groupName);
		}
		/*
		 * groupPost is complete and can be stored for the group
		 */
		try {
			groupDBLogic.createPosts(Collections.<Post<?>>singletonList(groupPost));
		} catch (final DatabaseException dbex) {
			/*
			 *  Add the DatabaseException of the copied post to the Exception of the original one
			 */
			for (final String hash : dbex.getErrorMessages().keySet()) {
				for (final ErrorMessage errorMessage : dbex.getErrorMessages(hash)) {
					errorMessage.setDefaultMessage("This error occured while executing the for: tag: " + errorMessage.getDefaultMessage());
					errorMessage.setErrorCode("database.exception.systemTag.forGroup.copy");
					session.addError(PostUtils.getKeyForPost(userPost), errorMessage);
					log.warn("Added SystemTagErrorMessage (for group: errors while storing group's post) for post " + intraHash);
				}
			}
		}
		
		if (resource instanceof BibTex) {
			final BibTex publication = (BibTex) resource;
			publication.setDocuments(documents);
		}
		log.debug("copied post was stored successfully");
		return true;
	}

	protected LogicInterface getGroupDbLogic() {
		return this.logicInterfaceFactory.getLogicAccess(this.getArgument(), "");
	}

	/**
	 * Checks the preconditions to this tags usage, adds errorMessages
	 * @param groupName
	 * @param userName
	 * @param session
	 * @return true if user is allowed to use the tag
	 */
	private boolean hasPermissions(final String groupName, final String userName, final DBSession session) {
		final PermissionDatabaseManager permissionDb = PermissionDatabaseManager.getInstance();
		if (permissionDb.isSpecialGroup(groupName) ) {
			/*
			 *  We decided to ignore errors in systemTags. Thus the user is free use any tag.
			 *  The drawback: If it is the user's intention to use a systemTag, he will never know if there was a typo! 
			 */
			return false;
		} 
		return permissionDb.isMemberOfGroup(userName, groupName, session);
	}

	/*
	 * We overwrite this method because we want to interpret also the send tag 
	 * without prefix (sys/system) as systemTag and we need an argument
	 * @see org.bibsonomy.database.systemstags.AbstractSystemTagImpl#isInstance(java.lang.String)
	 */
	@Override
	public boolean isInstance(final String tagName) {
		// the send tag must have an argument, the prefix is not required
		return SystemTagsUtil.hasTypeAndArgument(tagName) && this.getName().equals(SystemTagsUtil.extractType(tagName));
	}

	@Override
	public ExecutableSystemTag clone() {
		try {
			return (ExecutableSystemTag) super.clone();
		} catch (final CloneNotSupportedException ex) {
			// never ever reached
			return null;
		}
	}
}

