package org.bibsonomy.lucene.ranking;

import java.util.List;

public interface FolkRankDao {

	//public String getTagUserFolkRanksAsMergedString(String hash);
	
	public List<FolkRankInfo> getTagUserFolkRanks(String hash);
}
