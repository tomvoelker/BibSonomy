package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * Can be used to get all bibtex entries by its hash.
 * 
 * @author mgr
 * 
 */
public class BibTexByHash {

	private ConstantID groupType;
	private String requBibtex;
	private ConstantID requSim;
	private int limit;
	private int offset;

	public BibTexByHash() {
		this.groupType = ConstantID.GROUP_PUBLIC;
		this.requSim = ConstantID.SIM_HASH;
	}

	public int getContentType() {
		return ConstantID.BIBTEX_CONTENT_TYPE.getId();
	}

	public String getRequBibtex() {
		return requBibtex;
	}

	public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}

	public int getRequSim() {
		return requSim.getId();
	}

	public void setRequSim(ConstantID requSim) {
		this.requSim = requSim;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int itemCount) {
		this.limit = itemCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int startBib) {
		this.offset = startBib;
	}

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}
}