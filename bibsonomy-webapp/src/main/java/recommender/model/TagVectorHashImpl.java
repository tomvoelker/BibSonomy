/*
 * Created on 15.12.2005
 */
package recommender.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Speichert die Anzahl Vorkommnisse eines Tags bei Usern oder Contents
 * 
 * @author Jens Illig
 */
public class TagVectorHashImpl implements TagVector {
	private static final Logger log = Logger.getLogger(TagVectorHashImpl.class);
	/** z.b. Anzahl Benutzerverwendungen für jeden User */
	private final Map<String,Integer> vectorMap = new HashMap<String, Integer>(64);
	private long squaredSum = 0;
	private int tagID;
	
	public TagVectorHashImpl(int tagID) {
		this.tagID = tagID;
	}
	
	public void setNewKey(String key, int amount) {
		squaredSum += amount*amount;
		vectorMap.put(key,amount);
	}
	
	public void addToKey(String key, int amount) {
		Integer newCount = vectorMap.get(key);
		if (newCount == null) {
			newCount = amount;
			
		} else {
			squaredSum -= newCount*newCount;
			newCount += amount;
		}
		squaredSum += newCount*newCount;
		vectorMap.put(key,newCount);
	}

	public double getLength() {
		return Math.sqrt((double)squaredSum);
	}
	
	public Integer getValueFor(String key) {
		return vectorMap.get(key);
	}

	public int getTagID() {
		return tagID;
	}

	public Iterator<TagVector.Entry> iterator() {
		//log.info("using quite slow iterator");
		return new EntryIterator();
	}
	
	private class EntryIterator implements Iterator<TagVector.Entry> {
		private final Iterator<Map.Entry<String,Integer>> it = TagVectorHashImpl.this.vectorMap.entrySet().iterator();;
		
		public boolean hasNext() {
			return it.hasNext();
		}

		public TagVector.Entry next() {
			return new TagVector.Entry() {
				private final Map.Entry<String,Integer> entry = EntryIterator.this.it.next();
				
				public int getTagId() {
					return TagVectorHashImpl.this.tagID;
				}

				public String getKey() {
					return entry.getKey();
				}

				public Integer getValue() {
					return entry.getValue();
				}

				public Integer setValue(Integer arg0) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static final TagVector.Factory FACTORY = new TagVector.Factory() {
		public TagVector produce(int tagId) {
			return new TagVectorHashImpl(tagId);
		}
	};
}
