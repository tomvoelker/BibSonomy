package org.bibsonomy.recommender.tags.database.params;

/**
 * Bean for recommender settings
 * @author fei
 * @version $Id$
 */
public class RecSettingParam {
	private Long setting_id;
	private String recId;
	private String recDescr;
	private byte[] recMeta;
	
	public void setRecId(String recId) {
		this.recId = recId;
	}
	public String getRecId() {
		return recId;
	}
	public void setRecMeta(byte[] recMeta) {
		this.recMeta = recMeta;
	}
	public byte[] getRecMeta() {
		return recMeta;
	}
	public void setSetting_id(long setting_id) {
		this.setting_id = setting_id;
	}
	public long getSetting_id() {
		return setting_id;
	}
	public void setRecDescr(String recDescr) {
		this.recDescr = recDescr;
	}
	public String getRecDescr() {
		return recDescr;
	}
}
