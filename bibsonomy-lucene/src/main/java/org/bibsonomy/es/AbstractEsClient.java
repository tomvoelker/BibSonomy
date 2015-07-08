package org.bibsonomy.es;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardPublication;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public abstract class AbstractEsClient implements ESClient {
	private ReadWriteLock rwLockBibTex;
	private ReadWriteLock rwLockBookmark;
	private ReadWriteLock rwLockGoldStandard;
	private Lock readLockBibTex;
	private Lock writeLockBibTex;
	private Lock readLockBookmark;
	private Lock writeLockBookmark;
	private Lock readLockGoldStandard;
	private Lock writeLockGoldStandard;
	/**
	 * 
	 */
	protected void init() {
		//TODO another lock for temporary index
		rwLockBibTex =  new ReentrantReadWriteLock();
		readLockBibTex = rwLockBibTex.readLock();
		writeLockBibTex =  rwLockBibTex.writeLock();
		rwLockBookmark =  new ReentrantReadWriteLock();
		readLockBookmark = rwLockBookmark.readLock();
		writeLockBookmark =  rwLockBookmark.writeLock();
		rwLockGoldStandard =  new ReentrantReadWriteLock();
		readLockGoldStandard = rwLockGoldStandard.readLock();
		writeLockGoldStandard =  rwLockGoldStandard.writeLock();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.es.ESClient#getReadLock(java.lang.String)
	 */
	@Override
	public Lock getReadLock(String resourceType) {
		if(resourceType.equalsIgnoreCase(BibTex.class.getSimpleName())){
			return this.readLockBibTex;
		}
		if(resourceType.equalsIgnoreCase(Bookmark.class.getSimpleName())){
			return this.readLockBookmark;
		}
		if(resourceType.equalsIgnoreCase(GoldStandardPublication.class.getSimpleName())){
			return this.readLockGoldStandard;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.es.ESClient#getWriteLock(java.lang.String)
	 */
	@Override
	public Lock getWriteLock(String resourceType) {
		if(resourceType.equalsIgnoreCase(BibTex.class.getSimpleName())){
			return this.writeLockBibTex;
		}
		if(resourceType.equalsIgnoreCase(Bookmark.class.getSimpleName())){
			return this.writeLockBookmark;
		}
		if(resourceType.equalsIgnoreCase(GoldStandardPublication.class.getSimpleName())){
			return this.writeLockGoldStandard;
		}
		return null;
	}

}
