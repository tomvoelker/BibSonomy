package org.bibsonomy.database.params;

import java.net.InetAddress;
import java.util.Date;

import org.bibsonomy.common.enums.InetAddressStatus;

/**
 * Holds parameters for admin specific things (e.g. blocking an IP, marking a
 * spammer).
 * 
 * @author Robert Jaeschke
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminParam extends GenericParam {

	/** An inetAddress whose status should be get/set/deleted. */
	private InetAddress inetAddress = null;

	/** Status of the corresponding inetAddress */
	private InetAddressStatus inetAddressStatus;

	/** The spammer status of the user */
	private Boolean spammer;

	/** flag if user should by classified any longer */
	private Integer toClassify;

	/** The prediction of the classifier */
	private Integer prediction;
	
	/** The confidence of the classifier */
	private Double confidence;

	/** The classifier algorithm */
	private String algorithm;

	/** The mode of the classiefier (day or night) */
	private String mode;

	/** The group id of the posts before flagging */
	private int oldGroupId;

	/** The group id after flagging */
	private int newGroupId;

	/** The time of the update */
	private Date updatedAt;

	/** The username of the admin who executes the update */
	private String updatedBy;

	/** key for classifier settings */
	private String key;

	/** Corresponding value for classifier settings */
	private String value;

	/** Interval in hours for retrieve latest classifications */
	private int interval;
	
	/** Integer range for creating groups */
	private int groupRange; 
	
	/** String for describing the table in which to update a group Id **/
	private String groupIdTable;

	public InetAddress getInetAddress() {
		return this.inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	public InetAddressStatus getInetAddressStatus() {
		return this.inetAddressStatus;
	}

	public void setInetAddressStatus(InetAddressStatus inetAddressStatus) {
		this.inetAddressStatus = inetAddressStatus;
	}

	public Boolean getSpammer() {
		return this.spammer;
	}

	public boolean isSpammer() {
		return this.spammer == null ? false : this.spammer;
	}
	
	public void setSpammer(Boolean spammer) {
		this.spammer = spammer;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getToClassify() {
		return this.toClassify;
	}

	public void setToClassify(Integer toClassify) {
		this.toClassify = toClassify;
	}

	public int getOldGroupId() {
		return this.oldGroupId;
	}

	public void setOldGroupId(int oldGroupId) {
		this.oldGroupId = oldGroupId;
	}

	public int getNewGroupId() {
		return this.newGroupId;
	}

	public void setNewGroupId(int newGroupId) {
		this.newGroupId = newGroupId;
	}

	public Date getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Integer getPrediction() {
		return this.prediction;
	}

	public void setPrediction(Integer prediction) {
		this.prediction = prediction;
	}
	
	public Double getConfidence() {
		return this.confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public String getAlgorithm() {
		return this.algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getMode() {
		return this.mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getInterval() {
		return this.interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setGroupRange(int groupRange) {
		this.groupRange = groupRange;
	}

	public int getGroupRange() {
		return groupRange;
	}
	
	public String getGroupIdTable() {
		return this.groupIdTable;
	}

	public void setGroupIdTable(String groupIdTable) {
		this.groupIdTable = groupIdTable;
	}

}