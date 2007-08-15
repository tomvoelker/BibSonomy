/*
 * Created on 15.12.2005
 */
package recommender.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Speichert die Anzahl Vorkommnisse eines Users oder Contents bei verschiedenen Tags
 * 
 * @author Jens Illig
 */
public class InvertedTagVectorHashImpl implements InvertedTagVector {
	private static final Logger log = Logger.getLogger(InvertedTagVectorHashImpl.class);
	/** z.b. Anzahl Benutzerverwendungen für jeden User */
	private final Map<Integer,Integer> vectorMap = new HashMap<Integer, Integer>(64);
	private long componentSum = 0;
	/** wie viele verschiedene Tags sind mit diesem key verbunden */
	private int tagFrequency = 0; 
	private final String key;
	
	public InvertedTagVectorHashImpl(final String key) {
		this.key = key;
	}
	
	public void setNewTagId(final Integer tagId, int amount) {
		componentSum += amount;
		tagFrequency++;
		vectorMap.put(tagId,amount);
	}
	
	public void addToTagId(final Integer tagId, int amount) {
		Integer newCount = vectorMap.get(tagId);
		if (newCount == null) {
			newCount = amount;
			tagFrequency++;
		} else {
			componentSum -= newCount;
			newCount += amount;
		}
		componentSum += newCount;
		vectorMap.put(tagId,newCount);
	}

	public double getLength() {
		return Math.sqrt((double)(componentSum*componentSum));
	}
	
	public Integer getValueFor(final Integer tagId) {
		return vectorMap.get(tagId);
	}

	public String getKey() {
		return key;
	}

	public Iterator<TagVector.Entry> iterator() {
		//log.info("using quite slow iterator");
		return new EntryIterator();
	}
	
	private class EntryIterator implements Iterator<TagVector.Entry> {
		private final Iterator<Map.Entry<Integer,Integer>> it = InvertedTagVectorHashImpl.this.vectorMap.entrySet().iterator();;
		
		public boolean hasNext() {
			return it.hasNext();
		}

		public TagVector.Entry next() {
			return new TagVector.Entry() {
				private final Map.Entry<Integer,Integer> entry = EntryIterator.this.it.next();
				
				public String getKey() {
					return InvertedTagVectorHashImpl.this.key;
				}

				public int getTagId() {
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
	
	public static final InvertedTagVector.Factory FACTORY = new InvertedTagVector.Factory() {
		public InvertedTagVector produce(final String key) {
			return new InvertedTagVectorHashImpl(key);
		}
	};

	
	public Map<Integer, Integer> getVectorMap() {
		return vectorMap;
	}

	public int getTagFrequency() {
		return tagFrequency;
	}
	
	
}
