/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.PostAccess;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.RepositoryParam;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ScraperMetadata;
import org.bibsonomy.model.extra.BibTexExtra;
import org.bibsonomy.model.util.file.FileSystemFile;
import org.bibsonomy.services.filesystem.FileLogic;

/**
 * Used to create, read, update and delete BibTexs from the database.
 * 
 * FIXME: why do some methods use loginUserName and some methods not? Shouldn't
 * all methods need loginUserName?
 * 
 * @author Miranda Grahl
 * @author Jens Illig
 * @author Christian Schenk
 * @author Daniel Zoller
 * 
 */
public class BibTexDatabaseManager extends PostDatabaseManager<BibTex, BibTexParam> {
	private static final Log log = LogFactory.getLog(BibTexDatabaseManager.class);

	private static final BibTexDatabaseManager singleton = new BibTexDatabaseManager();

	private static final HashID[] hashRange = HashID.getAllHashIDs();

	/**
	 * @return BibTexDatabaseManager
	 */
	public static BibTexDatabaseManager getInstance() {
		return singleton;
	}

	/** database manager */
	private final BibTexExtraDatabaseManager extraDb;
	private final DocumentDatabaseManager docDb;

	private FileLogic fileLogic;

	private BibTexDatabaseManager() {
		this.docDb = DocumentDatabaseManager.getInstance();
		this.extraDb = BibTexExtraDatabaseManager.getInstance();
	}

	/**
	 * Prepares a query which returns all duplicate BibTex posts of the
	 * requested user. Duplicates are BibTex posts which have the same simhash1,
	 * but a different simhash0 (the latter is always true within the posts of a
	 * single user).
	 * 
	 * @param requestedUserName
	 * @param visibleGroupIDs
	 * @param simHash
	 * @param session
	 * @param systemTags
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsDuplicate(final String requestedUserName, final List<Integer> visibleGroupIDs, final HashID simHash, final DBSession session, final Collection<SystemTag> systemTags) {
		final BibTexParam param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);
		param.setGroups(visibleGroupIDs);
		param.setSimHash(simHash);
		param.addAllToSystemTags(systemTags);

		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.postList("getBibTexDuplicate", param, session);
	}

	/**
	 * TODO: check method
	 * 
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 * 
	 * @param requestedUserName
	 * @param session
	 * @return number of duplicates
	 */
	public int getPostsDuplicateCount(final String requestedUserName, final DBSession session) {
		final BibTexParam param = this.getNewParam();
		param.setRequestedUserName(requestedUserName);

		final Integer result = this.queryForObject("getBibTexDuplicateCount", param, Integer.class, session);
		return present(result) ? result : 0;
	}

	/**
	 * adds document retrieval to
	 * {@link PostDatabaseManager#getPostsForUser(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);

		if (PostAccess.POST_ONLY.equals(param.getPostAccess())) {
			return super.getPostsForUser(param, session);
		}

		// document retrieval
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch (filter) {
			case JUST_PDF:
				// retrieve only entries with a document attached
				return this.postList("getJustBibTexForUserWithPDF", param, session);
			case DUPLICATES:
				// retrieve duplicate entries
				return this.getPostsDuplicate(param.getRequestedUserName(), param.getGroups(), HashID.getSimHash(param.getSimHash()), session, null);
			case POSTS_WITH_DISCUSSIONS:
				// posts with discussions
				return this.postList("getBibTexWithDiscussions", param, session);
			default:
				throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}

		// posts with docs
		return this.postList("getBibTexForUserWithPDF", param, session);
	}

	/**
	 * adds document retrieval to
	 * {@link PostDatabaseManager#getPostsForGroup(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsByTagNamesForUser(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param, session);
		HashID.getSimHash(param.getSimHash()); // ensures correct simHash is set
												// (exception would be thrown
												// otherwise)

		/*
		 * check if user can't access documents
		 */
		if (PostAccess.POST_ONLY.equals(param.getPostAccess())) {
			return super.getPostsByTagNamesForUser(param, session);
		}

		// if user wants to retrieve documents
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch (filter) {
			case JUST_PDF:
				return this.postList("getJustBibTexByTagNamesForUserWithPDF", param, session);
			default:
				throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}

		// posts including documents
		return this.postList("getBibTexByTagNamesForUserWithPDF", param, session);
	}

	/**
	 * adds document retrieval to
	 * {@link PostDatabaseManager#getPostsForGroupByTag(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsForGroupByTag(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);

		/*
		 * use normal query if user can't access documents
		 */
		if (PostAccess.POST_ONLY.equals(param.getPostAccess())) {
			return super.getPostsForGroupByTag(param, session);
		}

		/*
		 * first check for filter
		 */
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch (filter) {
			case JUST_PDF:
				return this.postList("getJustBibTexForGroupByTagWithPDF", param, session);
			default:
				throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}

		/*
		 * no filter -> query documents with the posts
		 */
		return this.postList("getBibTexForGroupByTagWithPDF", param, session);
	}

	/**
	 * adds document retrieval to
	 * {@link PostDatabaseManager#getPostsForGroup(ResourceParam, DBSession)}
	 */
	@Override
	protected List<Post<BibTex>> getPostsForGroup(final BibTexParam param, final DBSession session) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param, session);

		if (PostAccess.POST_ONLY.equals(param.getPostAccess())) {
			return super.getPostsForGroup(param, session);
		}

		// document retrieval
		final FilterEntity filter = param.getFilter();
		if (present(filter)) {
			switch (filter) {
			case JUST_PDF:
				// just entries with document attached
				return this.postList("getJustBibTexForGroupWithPDF", param, session);
			default:
				throw new IllegalArgumentException("Filter " + filter.name() + " not supported");
			}
		}

		// posts including documents
		return this.postList("getBibTexForGroupWithPDF", param, session);
	}

	private List<Post<BibTex>> getLoggedPostsByHashForUser(final String loginUserName, final String intraHash, final String requestedUserName, final List<Integer> visibleGroupIDs, final DBSession session, final HashID hashType) {
		final BibTexParam param = this.createParam(loginUserName, requestedUserName);
		param.addGroups(visibleGroupIDs);
		param.setHash(intraHash);
		param.setSimHash(hashType);

		DatabaseUtils.checkPrivateFriendsGroup(this.generalDb, param, session);
		return this.postList("getLoggedHashesByHashForUser", param, session);
	}

	/**
	 * <em>/bibtexkey/KEY</em> Returns a list of bibtex posts for a given
	 * bibtexKey
	 * 
	 * @param loginUser
	 * @param bibtexKey
	 * @param requestedUserName
	 * @param groupId
	 * @param visibleGroupIDs
	 * @param limit
	 * @param offset
	 * @param systemTags
	 * @param session
	 *            a database session
	 * @return list of publication posts
	 */
	public List<Post<BibTex>> getPostsByBibTeXKey(final String loginUser, final String bibtexKey, final String requestedUserName, final int groupId, List<Integer> visibleGroupIDs, final int limit, final int offset, final Collection<SystemTag> systemTags, final DBSession session) {
		final BibTexParam param = this.createParam(loginUser, requestedUserName, limit, offset);
		param.setBibtexKey(bibtexKey);
		param.setGroupId(groupId);
		param.setGroups(visibleGroupIDs);
		param.addAllToSystemTags(systemTags);

		return this.postList("getBibTexByKey", param, session);
	}

	/**
	 * FIXME: don't use param as parameter (we want to see which attributes are
	 * used by the query) Returns a list of Posts which where send to an
	 * repository and match the given interhash
	 * 
	 * @param param
	 * @param session
	 *            a database session
	 * @return list of bibtex posts
	 */
	public List<Post<BibTex>> getPostsWithRepository(final BibTexParam param, final DBSession session) {
		return this.postList("selectBibtexWithRepositorys", param, session);
	}

	/**
	 * Gets the details of a post, including all extra data like documents,
	 * extra urls and private notes given the INTRA-HASH of the post and the
	 * user name.
	 * 
	 * <ul>
	 * <li>extra URLs</li>
	 * <li>private notes (if userName = loginUserName)</li>
	 * <li>private PDFs (if requirements are met)
	 * <li>
	 * </ul>
	 * 
	 */
	@Override
	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final DBSession session) throws ResourceMovedException, ObjectNotFoundException {
		final boolean failIfDocumentsNotAccessible = false;
		return this.getPostDetails(authUser, resourceHash, userName, visibleGroupIDs, failIfDocumentsNotAccessible, session);
	}

	public Post<BibTex> getPostDetails(final String authUser, final String resourceHash, final String userName, final List<Integer> visibleGroupIDs, final boolean failIfDocumentsNotAccessible, final DBSession session) throws ResourceMovedException, ObjectNotFoundException {
		// get post from database
		final Post<BibTex> post = super.getPostDetails(authUser, resourceHash, userName, visibleGroupIDs, session);

		if (present(post)) {
			final BibTex publication = post.getResource();
			if (this.permissionDb.isAllowedToAccessPostsDocuments(authUser, post, session)) {
				publication.setDocuments(this.docDb.getDocumentsForPost(userName, resourceHash, session));
			} else if (failIfDocumentsNotAccessible) {
				throw new AccessDeniedException("You are not allowed to access documents of this post");
			}

			// add extra URLs
			publication.setExtraUrls(this.extraDb.getURL(resourceHash, userName, session));

			return post;
		}

		/*
		 * post null => not found => second try: look into logging table
		 */
		final List<Post<BibTex>> loggedList = this.getLoggedPostsByHashForUser(authUser, resourceHash, userName, visibleGroupIDs, session, HashID.INTRA_HASH);
		if (present(loggedList)) {
			if (loggedList.size() > 1) {
				// user has multiple posts with the same hash
				log.warn("multiple logged BibTeX-posts from user '" + userName + "' with hash '" + resourceHash + "' for user '" + authUser + "' found ->returning first");
			}
			/*
			 * Resource has been changed and thus could be found in logging
			 * table. We send back the new resource hash.
			 */
			final Post<BibTex> loggedPost = loggedList.get(0);
			final String newIntraHash = loggedPost.getResource().getIntraHash();
			/*
			 * If the hash did not change, this is the "last" post and we should
			 * not throw the exception - otherwise, clients would enter an
			 * infinite loop.
			 */
			if (!resourceHash.equals(newIntraHash)) {
				throw new ResourceMovedException(resourceHash, BibTex.class, newIntraHash, userName, loggedPost.getDate());
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.database.managers.PostDatabaseManager#insertPost(org.bibsonomy
	 * .database.params.ResourcesParam, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void insertPost(final BibTexParam param, final DBSession session) {
		/*
		 * store scraper meta data
		 */
		final ScraperMetadata scraperMetadata = param.getResource().getScraperMetadata();
		if (present(scraperMetadata)) {
			session.beginTransaction();
			try {
				/*
				 * get a scraper id
				 */
				final int id = this.generalDb.getNewId(ConstantID.IDS_SCRAPER_METADATA, session);
				/*
				 * store id in metadata
				 */
				scraperMetadata.setId(id);
				/*
				 * store the metadata
				 */
				this.insertScraperMetadata(scraperMetadata, session);
				/*
				 * store the id in the post
				 */
				param.getResource().setScraperId(id);
				session.commitTransaction();
			} finally {
				session.endTransaction();
			}
		}

		/*
		 * store the post
		 * insert post and update/insert hashes
		 */
		super.insertPost(param, session);
	}

	@Override
	protected void createdPost(final Post<BibTex> post, final DBSession session) {
		super.createdPost(post, session);
		
		this.handleExtraUrls(post, session);
		this.handleDocuments(post, session);
	}

	/**
	 * @param post
	 * @param session
	 */
	private void handleExtraUrls(final Post<BibTex> post, final DBSession session) {
		final List<BibTexExtra> extraUrls = post.getResource().getExtraUrls();
		if (present(extraUrls)) {
			for (final BibTexExtra resourceExtra : extraUrls) {
				this.extraDb.createURL(post.getResource().getIntraHash(), post.getUser().getName(), resourceExtra.getUrl().toExternalForm(), resourceExtra.getText(), session);
			}
		}
	}

	@Override
	protected void updatedPost(final Post<BibTex> post, final DBSession session) {
		super.updatedPost(post, session);
		
		this.handleDocuments(post, session);
	}

	private void handleDocuments(final Post<BibTex> post, final DBSession session) {
		final List<Document> documents = post.getResource().getDocuments();
		if (present(documents)) {
			for (final Document document : documents) {
				if (document.isTemp()) {
					try {
						final String fileName = document.getFileHash();
						log.debug("adding temp file " + fileName);
						final File file = this.fileLogic.getTempFile(fileName);
						final String username = post.getUser().getName();
						final Document savedDocument = this.fileLogic.saveDocumentFile(username, new FileSystemFile(file, document.getFileName()));
						savedDocument.setFileName(document.getFileName());
						final String savedFileHash = savedDocument.getFileHash();
						final String savedMD5Hash = savedDocument.getMd5hash();
						this.docDb.addDocument(username, post.getContentId(), savedFileHash, savedDocument.getFileName(), savedMD5Hash, session);
						document.setFileHash(savedFileHash);
						document.setMd5hash(savedMD5Hash);
						// TODO: delete file?
						// this.fileLogic.deleteTempFile(fileName);
					} catch (final Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	private void insertScraperMetadata(final ScraperMetadata scraperMetadata, final DBSession session) {
		this.insert("insertScraperMetadata", scraperMetadata, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.database.managers.PostDatabaseManager#onPostUpdate(java
	 * .lang.Integer, java.lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostUpdate(final Integer oldContentId, final Integer newContentId, final DBSession session) {
		this.plugins.onPublicationUpdate(oldContentId, newContentId, session);
		/*
		 * rewrites the history
		 */
		// BibTexParam param = new BibTexParam();
		// param.setNewContentId(newContentId);
		// param.setRequestedContentId(oldContentId);
		// this.update("updateBibTexHistory", param, session);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#onPostMassUpdate(java.lang.String, int, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	protected void onPostMassUpdate(String username, int groupId, DBSession session) {
		this.plugins.onPublicationMassUpdate(username, groupId, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.database.managers.PostDatabaseManager#onPostDelete(java
	 * .lang.Integer, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected void onPostDelete(final Integer contentId, final DBSession session) {
		this.plugins.onPublicationDelete(contentId, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getHashRange()
	 */
	@Override
	protected HashID[] getHashRange() {
		return hashRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bibsonomy.database.managers.PostDatabaseManager#getInsertParam(org
	 * .bibsonomy.model.Post, org.bibsonomy.database.util.DBSession)
	 */
	@Override
	protected BibTexParam getInsertParam(final Post<? extends BibTex> post, final DBSession session) {
		final BibTexParam insert = this.getNewParam();
		insert.setResource(post.getResource());
		insert.setRequestedContentId(post.getContentId());
		insert.setDescription(post.getDescription());
		insert.setDate(post.getDate());
		insert.setChangeDate(post.getChangeDate());
		insert.setUserName(((post.getUser() != null) ? post.getUser().getName() : ""));

		// in field group in table bibtex, insert the id for PUBLIC, PRIVATE or
		// the id of the FIRST group in list
		final int groupId = post.getGroups().iterator().next().getGroupId();
		insert.setGroupId(groupId);

		// inform plugin
		this.plugins.onPublicationInsert(post, session);
		return insert;
	}

	/**
	 * TODO: improve documentation
	 * 
	 * @param userName
	 * @param intraHash
	 * @param key
	 * @param value
	 * @param session
	 */
	public void createExtendedField(final String userName, final String intraHash, final String key, final String value, final DBSession session) {
		this.extraDb.createExtendedField(userName, intraHash, key, value, session);

	}

	/**
	 * TODO: improve documentation
	 * 
	 * @param userName
	 * @param hash
	 * @param session
	 */
	public void deleteAllExtendedFieldsData(final String userName, final String hash, final DBSession session) {
		final int contentId = BibTexDatabaseManager.getInstance().getContentIdForPost(hash, userName, session);
		this.extraDb.deleteAllExtendedFieldsData(contentId, session);
	}

	/**
	 * TODO: improve documentation
	 * 
	 * @param userName
	 * @param hash
	 * @param key
	 * @param value
	 * @param session
	 */
	public void deleteExtendedFieldByKeyValue(final String userName, final String hash, final String key, final String value, final DBSession session) {
		this.extraDb.deleteExtendedFieldByKeyValue(userName, hash, key, value, session);
	}

	/**
	 * TODO: improve documentation
	 * 
	 * @param userName
	 * @param hash
	 * @param key
	 * @param session
	 */
	public void deleteExtendedFieldsByKey(final String userName, final String hash, final String key, final DBSession session) {
		this.extraDb.deleteExtendedFieldsByKey(userName, hash, key, session);
	}

	/**
	 * TODO: improve documentation
	 * 
	 * @param userName
	 * @param hash
	 * @param key
	 * @param session
	 * @return the extended fields
	 */
	public Map<String, List<String>> getExtendedFields(final String userName, final String hash, final String key, final DBSession session) {
		if (present(key)) {
			return this.extraDb.getExtendedFieldsByKey(hash, userName, key, session);
		}
		return this.extraDb.getExtendedFields(userName, hash, session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bibsonomy.database.managers.PostDatabaseManager#getNewParam()
	 */
	@Override
	protected BibTexParam getNewParam() {
		return new BibTexParam();
	}

	@Override
	protected void workOnOperation(final Post<BibTex> post, final Post<BibTex> oldPost, final PostUpdateOperation operation, final DBSession session) {
		if (PostUpdateOperation.UPDATE_REPOSITORY.equals(operation)) {
			this.performUpdateRepositorys(post, oldPost, session);
		} else {
			super.workOnOperation(post, oldPost, operation, session);
		}
	}

	protected void performUpdateRepositorys(final Post<BibTex> post, final Post<BibTex> oldPost, final DBSession session) {
		final RepositoryParam param = new RepositoryParam();

		param.setUserName(post.getUser().getName());
		param.setInterHash(post.getResource().getInterHash());
		param.setIntraHash(post.getResource().getIntraHash());

		// TODO: can we be sure that here is _at least_ or exactly one
		// repository ?
		// what is the expected behavior if no repository is given?
		// if(!present(post.getRepositorys()))
		// return;

		// TODO: NPE?
		param.setRepositoryName(post.getRepositorys().get(0).getId());

		this.insert("insertRepository", param, session);
	}

	/**
	 * @param fileLogic
	 *            the fileLogic to set
	 */
	public void setFileLogic(final FileLogic fileLogic) {
		this.fileLogic = fileLogic;
	}
}