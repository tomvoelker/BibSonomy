package recommender.model;

public class TagVectorEntry implements TagVector.Entry {
	private int tagId;
	private String key;
	private Integer value;
	
	public TagVectorEntry(int tagId, String key, Integer value) {
		this.tagId = tagId;
		this.key = key;
		this.value = value;
	}
	
	public int getTagId() {
		return tagId;
	}

	public String getKey() {
		return key;
	}

	public Integer getValue() {
		return value;
	}

	public Integer setValue(Integer arg0) {
		Integer oldVal = value;
		value = arg0;
		return oldVal;
	}

}
