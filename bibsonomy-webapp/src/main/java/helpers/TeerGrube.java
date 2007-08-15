package helpers;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** This class collects entities in a queue and counts how often they're added. 
 * Every add-call increases the penalty waiting time of that entity.
 *     
 * @author rja
 *
 */
public class TeerGrube {

	/**
	 * how long to wait for each retry?
	 */
	private static final int WAITING_SECONDS_PER_RETRY = 3;
	/**
	 * after how many seconds should an entity be removed from the queue?
	 */
	private static final int MAX_QUEUE_AGE_SECONDS = 30 * 60; 
	private Map<String, WaitingEntity> waitQueue;
	

	/**
	 * Default constructor.
	 */
	public TeerGrube () {
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
	 */
	public long getRemainingWaitSeconds (String id) {
		WaitingEntity entity = waitQueue.get(id);
		if (entity != null) {
			if (entity.secondsSinceLastAccess() > MAX_QUEUE_AGE_SECONDS) {
				/*
				 * entity is longer in queue than max age --> remove it
				 */
				waitQueue.remove(entity);
			} else {
				/*
				 * entity is in queue and younger than max age
				 */
				long waitingTimeInSeconds = entity.getRetryCounter() * WAITING_SECONDS_PER_RETRY;
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
		 * @return
		 */
		public long secondsSinceLastAccess () {
			return (new Date().getTime() - lastAccessTime) / 1000;
		}
		
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
		
		public boolean equals (WaitingEntity other) {
			return this.id.equals(other.id);
		}
	}
	
}
