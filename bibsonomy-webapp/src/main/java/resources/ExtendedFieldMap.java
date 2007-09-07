package resources;

import java.io.Serializable;

public class ExtendedFieldMap implements Serializable {
	private static final long serialVersionUID = 81364905956396728L;
	private int group;
	private int key_id;
	private String key;
	private String description;
	private int order;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getKey_id() {
		return key_id;
	}
	public void setKey_id(int key_id) {
		this.key_id = key_id;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}