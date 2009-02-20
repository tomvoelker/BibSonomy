package org.bibsonomy.recommender.params;

/**
 * Bean for selection result strategy settings
 * @author fei
 * @version $Id$
 */
public class SelectorSettingParam {
	private Long id;
	private String info;
	private byte[] meta;
	
	
	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getInfo() {
		return info;
	}
	public void setMeta(byte[] meta) {
		this.meta = meta;
	}
	public byte[] getMeta() {
		return meta;
	}
	
}
