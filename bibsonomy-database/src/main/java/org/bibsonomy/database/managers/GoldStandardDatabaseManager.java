package org.bibsonomy.database.managers;

import static org.bibsonomy.util.ValidationUtils.present;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.errors.DuplicatePostErrorMessage;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.errors.UpdatePostErrorMessage;
import org.bibsonomy.common.exceptions.ResourceNotFoundException;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.params.GoldStandardReferenceParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * Used to create, read, update and delete gold standard posts from the database
 * 
 * @param <RR> the resource class of the reference class of <R>
 * @param <R> the resource class that is managed by this class
 * @param <P> 
 * 
 * @author dzo
 * @version $Id$
 */
public abstract class GoldStandardDatabaseManager<RR extends Resource, R extends Resource & GoldStandard<RR>, P extends GenericParam> extends AbstractDatabaseManager implements CrudableContent<R, P> {
	private static final Log log = LogFactory.getLog(GoldStandardDatabaseManager.class);
	
	/** simple class name of the resource managed by the class */
	protected final String resourceClassName;
	
	protected final DatabasePluginRegistry plugins;
	
	protected GoldStandardDatabaseManager() {
		this.resourceClassName = this.getResourceClassName();
		this.plugins = DatabasePluginRegistry.getInstance();
	}
	
	/**
	 * @return the class of the second generic param (the Resource <R>)
	 */
	private Class<?> getResourceClass() {
		final ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[1];
	}

	/**
	 * @return the simple class name of the first generic param (<R>, Resource)
	 */
	protected String getResourceClassName() {
		return this.getResourceClass().getSimpleName();
	}
	
	@Override
	public Post<R> getPostDetails(String loginUserName, String resourceHash, String userName, List<Integer> visibleGroupIDs, DBSession session) {
		if (present(userName)) {
			return null; // TODO: think about this return
		}
		
		final Post<R> post = this.getGoldStandardPostByHash(resourceHash, session);
		// get the references for this post
		if (present(post)) {
			final R goldStandard = post.getResource();
			goldStandard.addAllToReferences(this.getReferencesForPost(resourceHash, session));
			goldStandard.addAllToReferencedBy(this.getRefencedByForPost(resourceHash, session));
		} else {
			log.debug("gold standard post with interhash '" + resourceHash + "' not found.");
		}
		
		return post;
	}
	
	@SuppressWarnings("unchecked")
	protected Set<RR> getRefencedByForPost(String resourceHash, DBSession session) {
		return new HashSet<RR>(this.queryForList("get" + this.resourceClassName + "RefercencedBy", resourceHash, session));
	}

	@SuppressWarnings("unchecked")
	protected Post<R> getGoldStandardPostByHash(final String resourceHash, final DBSession session) {
		return (Post<R>) this.queryForObject("get" + this.resourceClassName + "ByHash", resourceHash, session);
	}
	
	@SuppressWarnings("unchecked")
	protected Set<RR> getReferencesForPost(final String interHash, final DBSession session) {
		return new HashSet<RR>(this.queryForList("get" + this.resourceClassName + "Refercences", interHash, session));
	}
	
	@Override
	public List<Post<R>> getPosts(final P param, final DBSession session) {
		throw new UnsupportedOperationException("chain not (yet) available for gold standard posts");
	}

	@Override
	public boolean createPost(final Post<R> post, final DBSession session) {
		session.beginTransaction();
		try {
			final String resourceHash = post.getResource().getInterHash();
			
			final Post<R> newPostInDB = this.getGoldStandardPostByHash(resourceHash, session);
			
			if (present(newPostInDB)) {
				log.debug("gold stanard post with hash \"" + resourceHash + "\" already exists in DB");
				final ErrorMessage errorMessage = new DuplicatePostErrorMessage(this.resourceClassName, resourceHash);
				session.addError(resourceHash, errorMessage);
				session.commitTransaction();
				return false;
			}
			
			this.onGoldStandardCreate(resourceHash, session);
			this.insertPost(post, session);
			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
		return true;
	}

	protected void insertPost(final Post<R> post, final DBSession session) {
		final P insertParam = this.getInsertParam(post);
		this.insert("insert" + this.resourceClassName, insertParam, session);
	}

	protected abstract P getInsertParam(Post<R> post);
	
	@Override
	public boolean updatePost(final Post<R> post, final String oldHash, final PostUpdateOperation operation, final DBSession session) {
		session.beginTransaction();
		try {
			
			/*
			 * the current interhash of the resource
			 */
			final R resource = post.getResource();
			resource.recalculateHashes();
			
			final String resourceHash = resource.getInterHash();
			/*
			 * the resource with the "old" interhash, that was sent
			 * within the update resource request
			 */
			final Post<R> oldPost;
			if (present(oldHash)) {
				// if yes, check if a post exists with the old interhash
				oldPost = this.getGoldStandardPostByHash(oldHash, session);
				/*
				 * check if post to update is in db
				 */
				if (!present(oldPost)) {
					final String hash = resource.getInterHash();
					/*
					 * not found -> add ErrorMessage
					 */
					final ErrorMessage errorMessage = new UpdatePostErrorMessage(this.resourceClassName, hash);
					session.addError(hash, errorMessage);
					log.warn("Added UpdatePostErrorMessage for post " + post.getResource().getIntraHash());
					session.commitTransaction();
					
					return false;
				}
			} else {
				throw new IllegalArgumentException("Could not update standard post: no interhash specified.");
			}
			
			/*
			 * check for possible duplicates 
			 */
			final Post<R> newPostInDB = this.getGoldStandardPostByHash(resourceHash, session);
			
			if (present(newPostInDB) && !(oldHash.equals(resourceHash))) {
				log.debug("gold stanard post with hash \"" + resourceHash + "\" already exists in DB");
				final ErrorMessage errorMessage = new DuplicatePostErrorMessage(this.resourceClassName, resourceHash);
				session.addError(resourceHash, errorMessage);
				
				session.commitTransaction();
				
				return false;
			}
			
			this.onGoldStandardUpdate(oldHash, resourceHash, session); // logs old post and updates reference table
			this.deletePost(oldHash, true, session);
			this.insertPost(post, session);
			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		return true;
	}
	
	@Override
	public boolean deletePost(String userName, String resourceHash, DBSession session) {
		return this.deletePost(resourceHash, false, session);
	}
	
	protected boolean deletePost(final String resourceHash, final boolean update, DBSession session) {		
		session.beginTransaction();
		try {
			final Post<R> post = this.getGoldStandardPostByHash(resourceHash, session);
			
			if (!present(post)) {
				log.debug("gold stanard post with hash \"" + resourceHash + "\" not found");
				return false;
			}
			
			if (!update) {
				this.onGoldStandardDelete(resourceHash, session);
			}
			
			this.delete("delete" + this.resourceClassName, resourceHash, session);
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
		return true;
	}
	
	protected GoldStandardReferenceParam createParam(final Post<R> post) {
		final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
		param.setHash(post.getResource().getInterHash());
		param.setUsername(post.getUser().getName());
		
		return param;
	}
	
	/**
	 * adds references to a standard post
	 * 
	 * @param userName
	 * @param interHash
	 * @param references
	 * @param session
	 */
	public void addReferencesToPost(final String userName, final String interHash, final Set<String> references, final DBSession session) {
		session.beginTransaction();
		try {
			final Post<R> post = this.getGoldStandardPostByHash(interHash, session);
			if (!present(post)) {
				log.debug("gold standard post with interhash '" + interHash + "'  not found");
				throw new ResourceNotFoundException(interHash);
			}
			
			final GoldStandardReferenceParam param = this.createParam(post);
			if (present(references)) {
				for (final String referenceHash : references) {
					final Post<R> refPost = this.getGoldStandardPostByHash(referenceHash, session);
					if (present(refPost)) {
						param.setRefHash(referenceHash);
						this.insert("insert" + this.resourceClassName + "Reference", param, session);
					} else {
						log.info("Can't add reference. Gold standard " + this.resourceClassName +  " reference with resourceHash " + referenceHash + " not found.");
					}
				}
			}
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
		
	}
	
	/**
	 * removes references from a standard post
	 * 
	 * @param userName
	 * @param interHash
	 * @param references
	 * @param session
	 */
	public void removeReferencesFromPost(final String userName, final String interHash, final Set<String> references, final DBSession session) {
		session.beginTransaction();
		try {
			final Post<R> post = this.getGoldStandardPostByHash(interHash, session);
			if (!present(post)) {
				log.debug("gold standard post with interhash '" + interHash + "'  not found");
				return;
			}
			
			final GoldStandardReferenceParam param = this.createParam(post);
			if (present(references)) {
				for (final String referenceHash : references) {
					final Post<R> refPost = this.getGoldStandardPostByHash(referenceHash, session);
					if (present(refPost)) {
						param.setRefHash(referenceHash);
						this.delete("delete" + this.resourceClassName + "Reference", param, session);
					} else {
						log.info("Can't remove reference. Gold standard " + this.resourceClassName +  " reference with resourceHash " + referenceHash + " not found.");
					}
				}
			}
			
			session.commitTransaction();
		} finally {
			session.endTransaction();
		}
	}
	
	protected abstract void onGoldStandardCreate(final String resourceHash, final DBSession session);

	protected abstract void onGoldStandardUpdate(final String oldHash, final String newResourceHash, final DBSession session);
	
	protected abstract void onGoldStandardDelete(final String resourceHash, final DBSession session);
	
	protected abstract void onGoldStandardReferenceDelete(final String userName, final String interHash, final String interHashRef, final DBSession session);
}