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
 * @author dzo
 * @version $Id$
 * @param <RR> the resource class of the reference class of <R>
 * @param <R> the resource class that is managed by this class
 * @param <P> 
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
	private String getResourceClassName() {
		return this.getResourceClass().getSimpleName();
	}
	
	@Override
	public Post<R> getPostDetails(String loginUserName, String resourceHash, String userName, List<Integer> visibleGroupIDs, DBSession session) {
		final Post<R> post = this.getGoldStandardPostByHash(resourceHash, session);
		// get the references for this post
		if (present(post)) {
			final R goldStandard = post.getResource();
			goldStandard.addAllToReferences(this.getReferencesForPost(goldStandard.getInterHash(), session));
		} else {
			log.debug("gold standard post with interhash '" + resourceHash + "' not found.");
		}
		
		return post;
	}
	
	@SuppressWarnings("unchecked")
	protected Post<R> getGoldStandardPostByHash(final String resourceHash, final DBSession session) {
		return (Post<R>) this.queryForObject("get" + this.resourceClassName + "ByHash", resourceHash, session);
	}
	
	@SuppressWarnings("unchecked")
	protected Set<RR> getReferencesForPost(final String interHash, final DBSession session) {
		final List<RR> references = this.queryForList("get" + this.resourceClassName + "Refercences", interHash, session);
		return new HashSet<RR>(references);
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
	
	protected abstract void onGoldStandardCreate(String resourceHash, DBSession session);

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
			final String resourceHash = post.getResource().getInterHash();
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
					final String hash = post.getResource().getInterHash();
					/*
					 * not found -> throw exception
					 * 
					 * TODODZ: text in UpdatePostErrorMessage => intrahash
					 */
					final ErrorMessage errorMessage = new UpdatePostErrorMessage(this.resourceClassName, hash);
					session.addError(hash, errorMessage);
					
					session.commitTransaction();
					
					return false;
				}
			} else {
				throw new IllegalArgumentException("Could not update post: no interhash specified.");
			}
			
			/*
			 * check for possible duplicates 
			 */
			final Post<R> newPostInDB = this.getGoldStandardPostByHash(resourceHash, session);
			
			if (present(newPostInDB)) {
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
	
	protected abstract void onGoldStandardUpdate(final String oldHash, final String newResourceHash, final DBSession session);
	
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

	/**
	 * @param resourceHash
	 * @param session
	 */
	protected abstract void onGoldStandardDelete(final String resourceHash, DBSession session);
	
	protected GoldStandardReferenceParam createParam(final Post<R> post) {
		final GoldStandardReferenceParam param = new GoldStandardReferenceParam();
		param.setHash(post.getResource().getInterHash());
		param.setUsername(post.getUser().getName());
		
		return param;
	}
	
	/**
	 * TODO: discuss method signature TODODZ
	 * adds references to a post
	 * 
	 * @param username 
	 * @param interHash  the hash of the post
	 * @param references the references to add
	 * @param session
	 * @return <code>true</code> iff all references were added to the database
	 */
	public boolean addReferencesToPost(String username, final String interHash, final Set<R> references, final DBSession session) {
		final Post<R> post = this.getGoldStandardPostByHash(interHash, session);
		if (!present(post)) {
			log.debug("gold standard post with interhash '" + interHash + "'  not found");
			return false;
		}
		
		post.getUser().setName(username);
		return this.addReferencesToPost(post, references, session);
	}
	
	protected boolean addReferencesToPost(final Post<R> post, final Set<R> references, final DBSession session) {
		if (present(references)) {
			final GoldStandardReferenceParam param = this.createParam(post);
			
			for (final R resource : references) {
				param.setRefHash(resource.getInterHash());
				this.insert("insert" + this.resourceClassName + "Reference", param, session);
			}
		}
		
		return true;
	}
	 
	/**
	 * TODO: discuss method signature TODODZ
	 * removes references from the specified post
	 * 
	 * @param userName
	 * @param interHash  the hash of the post
	 * @param references the references to remove
	 * @param session
	 * @return <code>true</code> iff every reference was removed successfully
	 */
	public boolean removeReferencesFromPost(String userName, final String interHash, final Set<R> references, final DBSession session) {
		final Post<R> post = this.getGoldStandardPostByHash(interHash, session);
		if (!present(post)) {
			log.debug("gold standard post with interhash '" + interHash + "'  not found");
			return false;
		}
		
		post.getUser().setName(userName);
		return this.removeReferencesFromPost(post, references, session);
	}
	
	protected boolean removeReferencesFromPost(final Post<R> post, final Set<R> references, final DBSession session) {
		if (present(references)) {
			final GoldStandardReferenceParam param = this.createParam(post);
			
			for (final R reference : references) {
				param.setRefHash(reference.getInterHash());
				this.onGoldStandardReferenceDelete(param.getUsername(), param.getHash(), param.getRefHash(), session);
				this.insert("delete" + this.resourceClassName + "Reference", param, session);
			}
		}
		
		return true;
	}
	
	protected abstract void onGoldStandardReferenceDelete(final String userName, final String interHash, final String interHashRef, final DBSession session);
}