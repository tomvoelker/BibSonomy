package resources;

public class Question{

	private int ranking;
	
	private String user;
	
	private int type;
	
	private int content_id1;
	
	private int content_id2;
	
	private String tagname1;
	
	private String tagname2;
	
	private int tas_id1;
	
	private int tas_id2;
	
	private int count1;
	
	private int count2;
	
	private int content_count1;
	
	private int content_count2;
	
	private int relationID;
	
	private int questionID;
	
	private String title1;
	
	private String extrainfo1;
	
	private String title2;
	
	private String extrainfo2;
	
	private String result;
	
	private String hash1;
	
	private String hash2;
	
	private String type1;
	
	private String type2;

	public Question(){
	}
	
	public boolean equals(Question q){
		if(user==null || q==null)
			return false;
		if(user.equals(q.getUser())){
			if(type==q.getType()){
				if(type == 10){
					if( (content_id1 == content_id2) && (content_id2 == q.getContent_id1()) && (q.getContent_id1()== q.getContent_id2())){
						if(tagname1.equals(q.getTagname1()) && tagname2.equals(q.getTagname2()))
							return true;// is equal
						if(tagname1.equals(q.getTagname2()) && tagname2.equals(q.getTagname1()))
							return true;// twisted is equal						
					}else if(content_id1==q.getContent_id1() && content_id2==q.getContent_id2()){
						if(tagname1.equals(q.getTagname1()) && tagname2.equals(q.getTagname2()))
							return true;// is equal
					}else if(content_id1==q.getContent_id2() && content_id2==q.getContent_id1()){
						if(tagname1.equals(q.getTagname2()) && tagname2.equals(q.getTagname1()))
							return true;// twisted is equal
					}				
				}else{
					if(content_id1==q.getContent_id1() && content_id2==q.getContent_id2()){
						if(tas_id1==q.getTas_id1() && tas_id2==q.getTas_id2())
							return true;// is equal
					}else if(content_id1==q.getContent_id2() && content_id2==q.getContent_id1()){
						if(tas_id1==q.getTas_id2() && tas_id2==q.getTas_id1())
							return true;// twisted is equal
					}
				}
			}
		}
		return false;
	}
		
	public int getTas_id1() {
		return tas_id1;
	}

	public void setTas_id1(int tas_id1) {
		this.tas_id1 = tas_id1;
	}

	public int getTas_id2() {
		return tas_id2;
	}

	public void setTas_id2(int tas_id2) {
		this.tas_id2 = tas_id2;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public String getType1() {
		return type1;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public String getHash1() {
		return hash1;
	}

	public void setHash1(String hash1) {
		this.hash1 = hash1;
	}

	public String getHash2() {
		return hash2;
	}

	public void setHash2(String hash2) {
		this.hash2 = hash2;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getExtrainfo1() {
		return extrainfo1;
	}

	public void setExtrainfo1(String extrainfo1) {
		this.extrainfo1 = extrainfo1;
	}

	public String getExtrainfo2() {
		return extrainfo2;
	}

	public void setExtrainfo2(String extrainfo2) {
		this.extrainfo2 = extrainfo2;
	}

	public int getContent_id1() {
		return content_id1;
	}

	public void setContent_id1(int content_id1) {
		this.content_id1 = content_id1;
	}

	public int getContent_id2() {
		return content_id2;
	}

	public void setContent_id2(int content_id2) {
		this.content_id2 = content_id2;
	}

	public int getRelationID() {
		return relationID;
	}

	public void setRelationID(int relationID) {
		this.relationID = relationID;
	}

	public String getTagname1() {
		return tagname1;
	}

	public void setTagname1(String tagname1) {
		this.tagname1 = tagname1;
	}

	public String getTagname2() {
		return tagname2;
	}

	public void setTagname2(String tagname2) {
		this.tagname2 = tagname2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getContent_count1() {
		return content_count1;
	}

	public void setContent_count1(int content_count1) {
		this.content_count1 = content_count1;
	}

	public int getContent_count2() {
		return content_count2;
	}

	public void setContent_count2(int content_count2) {
		this.content_count2 = content_count2;
	}

}