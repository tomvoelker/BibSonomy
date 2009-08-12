package org.bibsonomy.webapp.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** This class collects entities in a queue and counts how often they're added. 
 * Every add-call increases the penalty waiting time of that entity.
 *     
 * @author rja
 * @version $Id$
 */
public class TeerGrube {

	private static final Log log = LogFactory.getLog(TeerGrube.class);
	
	/**
	 * How long to wait for each retry? Default: 3 seconds.
	 */
	private int waitingSecondsPerRetry = 3;
	/**
	 * After how many seconds should an entity be removed from the queue? Default: 30minutes (1800 seconds). 
	 */
	private int maxQueueAgeSeconds = 30 * 60;
	
	private Map<String, WaitingEntity> waitQueue;
	

	/**
	 * Default constructor.
	 */
	public TeerGrube () {
		log.info("New instance of TeerGrube created.");
		waitQueue = Collections.synchronizedMap(new HashMap<String, WaitingEntity>());
	}

	/** Adds an entity to the waitingQueue. If it is already in the queue, it's retry count is increased by one.
	 * 
	 * @param id - the id of the entitiy. If id is <code>null</code>, it is ignored
	 */
	public void add (String id) {
		if (id != null) {
			WaitingEntity entity = waitQueue.get(id);
			if (entity == null) {
				// new entity
				waitQueue.put(id, new WaitingEntity(id));
			} else {
				// entity already exists ... increase retry counter
				entity.incRetryCounter();
			}
		}
	}
	
	/** If the entity is contained in the waitQueue, this method will wait a certain 
	 * amount of time, depending on the retry count of the entity.
	 * If the entity is in the queue and the last access was more than MAX_QUEUE_AGE_SECONDS ago,
	 * it is removed from the queue. 
	 *  
	 * @param id - the id of the entitiy.
	 * @return The remaining seconds to wait.
	 */
	public long getRemainingWaitSeconds (String id) {
		WaitingEntity entity = waitQueue.get(id);
		if (entity != null) {
			if (entity.secondsSinceLastAccess() > maxQueueAgeSeconds) {
				/*
				 * entity is longer in queue than max age --> remove it
				 */
				waitQueue.remove(entity);
			} else {
				/*
				 * entity is in queue and younger than max age
				 */
				long waitingTimeInSeconds = entity.getRetryCounter() * waitingSecondsPerRetry;
				/*
				 * restrict max waiting time to 90 seconds
				 */
				if (waitingTimeInSeconds > 90) {
					waitingTimeInSeconds = 90;
				}
				/*
				 * return difference between waiting time and time since last access
				 * if waitingTime > timeSinceLastAccess result is positive, access forbidden
				 * if waitingTime < timeSinceLastAccess result is negative, access allowed
				 * (if negative, 0 is returned since otherwise a negative waiting time for an IP
				 * could cancel a positive waiting time for a user name IF both waiting times 
				 * are added and then compared to zero)
				 */
				final long remainingWaitSeconds = waitingTimeInSeconds - entity.secondsSinceLastAccess();
				if (remainingWaitSeconds > 0) return remainingWaitSeconds;
			}
		}
		return 0;
	}
		
	/** This class represents an entity in the waiting queue. It is typically either an IP or a user.
	 * @author rja
	 *
	 */
	private static class WaitingEntity {
		private String id;
		private int retryCounter;
		private long lastAccessTime; // number of miliseconds since 1970
		
		/** Public constructor
		 * @param id
		 */
		public WaitingEntity (String id) {
			this.id = id;
			retryCounter = 1;
			lastAccessTime = new Date().getTime();
		}
		
		/**
		 * Increasing the retry counter also resets the last access time.
		 */
		public void incRetryCounter () {
			lastAccessTime = new Date().getTime();
			retryCounter++;
		}
				
		/** Returns the "age" of the object, or more exactly: the time (in seconds) since the last access.
		 *  
		 * @return The number of seconds since this item has been accessed the last time.
		 */
		public long secondsSinceLastAccess () {
			return (new Date().getTime() - lastAccessTime) / 1000;
		}
		
		/**
		 * @return The number of times this item has been accessed.
		 */
		public int getRetryCounter () {
			return retryCounter;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof WaitingEntity)) {
				return false;
			}
			return equals ((WaitingEntity) obj);
		}
		
		/**
		 * @param other
		 * @return <code>true</code>, if <code>other</code> is equal to this instance.
		 */
		public boolean equals (WaitingEntity other) {
			return this.id.equals(other.id);
		}
	}

	/**
	 * @return The numberof seconds to add for each retry.
	 */
	public int getWaitingSecondsPerRetry() {
		return this.waitingSecondsPerRetry;
	}

	/** Set the number of seconds which the user has to wait for each retry.
	 * 
	 * Default: 3 seconds.
	 * 
	 * 
	 * @param waitingSecondsPerRetry
	 */
	public void setWaitingSecondsPerRetry(int waitingSecondsPerRetry) {
		this.waitingSecondsPerRetry = waitingSecondsPerRetry;
	}

	/**
	 * @return The number of seconds after which an item is removed from the queue. 
	 */
	public int getMaxQueueAgeSeconds() {
		return this.maxQueueAgeSeconds;
	}

	/** Set the number of seconds after which an item is removed from the queue. 
	 * 
	 * Default: 30minutes (1800 seconds).
	 * 
	 * @param maxQueueAgeSeconds
	 */
	public void setMaxQueueAgeSeconds(int maxQueueAgeSeconds) {
		this.maxQueueAgeSeconds = maxQueueAgeSeconds;
	}
	
}
