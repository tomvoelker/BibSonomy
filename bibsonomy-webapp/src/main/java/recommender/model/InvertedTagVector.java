/*
 * Created on 15.12.2005
 */
package recommender.model;

public interface InvertedTagVector extends Iterable<TagVector.Entry> {
	public static interface Factory {
		public InvertedTagVector produce(final String userName);
	}
	public double getLength();
	public int getTagFrequency();
	public String getKey();
	public Integer getValueFor(Integer tagId);
	public void addToTagId(Integer tagId, int amount);
	/**
	 * Dient lediglich zur Beschleunigung und darf nur aufgerufen werden,
	 * wenn sichergestellt ist, dass zuvor kein Eintrag unter dem key
	 * eingefügt wurde.
	 */
	public void setNewTagId(Integer tagId, int amount);
}
