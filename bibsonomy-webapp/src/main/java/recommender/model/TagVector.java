/*
 * Created on 15.12.2005
 */
package recommender.model;

import java.util.Map;

public interface TagVector extends Iterable<TagVector.Entry> {
	public static interface Factory {
		public TagVector produce(int tagId);
	}
	public double getLength();
	public int getTagID();
	public Integer getValueFor(String key);
	public void addToKey(String key, int amount);
	/**
	 * Dient lediglich zur Beschleunigung und darf nur aufgerufen werden,
	 * wenn sichergestellt ist, dass zuvor kein Eintrag unter dem key
	 * eingefügt wurde.
	 */
	public void setNewKey(String key, int amount);
	
	public static interface Entry extends Map.Entry<String,Integer> {
		public int getTagId();
	}
}
