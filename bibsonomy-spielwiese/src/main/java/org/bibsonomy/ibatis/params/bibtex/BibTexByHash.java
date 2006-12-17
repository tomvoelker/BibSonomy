package org.bibsonomy.ibatis.params.bibtex;

import org.bibsonomy.ibatis.enums.ConstantID;
import org.bibsonomy.ibatis.params.generic.common.aggregate.LimitOffsetSimHash;

/**
 * Can be used to get all bibtex entries by its hash.
 * 
 * @author mgr
 * 
 */
public class BibTexByHash extends LimitOffsetSimHash {

	private ConstantID groupType;
	private String requBibtex;
	// FIXME are requSim and simHash the same ???
	private ConstantID requSim;

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

	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}
}