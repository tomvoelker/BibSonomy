package org.bibsonomy.model;

/**
 * TODO: add documentation to this class
 *
 * @author Chris
 */
public class ResourcePersonRelation {
	
	private int id;
	private String simhash1;
	private String simhash2;
	private String pubOwner;
	private String relatorCode;
	private int qualifying;
	private int personNameId;
	private PersonName personName;
	private Post<BibTex> post;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the simhash1
	 */
	public String getSimhash1() {
		return this.simhash1;
	}
	/**
	 * @param simhash1 the simhash1 to set
	 */
	public void setSimhash1(String simhash1) {
		this.simhash1 = simhash1;
	}
	/**
	 * @return the simhash2
	 */
	public String getSimhash2() {
		return this.simhash2;
	}
	/**
	 * @param simhash2 the simhash2 to set
	 */
	public void setSimhash2(String simhash2) {
		this.simhash2 = simhash2;
	}
	/**
	 * @return the relatorCode
	 */
	public String getRelatorCode() {
		return this.relatorCode;
	}
	/**
	 * @param relatorCode the relatorCode to set
	 */
	public void setRelatorCode(String relatorCode) {
		this.relatorCode = relatorCode;
	}
	/**
	 * @return the qualifying
	 */
	public int getQualifying() {
		return this.qualifying;
	}
	/**
	 * @param qualifying the qualifying to set
	 */
	public void setQualifying(int qualifying) {
		this.qualifying = qualifying;
	}
	
	/**
	 * @return the personNameId
	 */
	public int getPersonNameId() {
		return this.personNameId;
	}
	/**
	 * @param personNameId the personNameId to set
	 */
	public void setPersonNameId(int personNameId) {
		this.personNameId = personNameId;
	}
	/**
	 * @return the personName
	 */
	public PersonName getPersonName() {
		return this.personName;
	}
	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(PersonName personName) {
		this.personName = personName;
	}
	/**
	 * @return the pubOwner
	 */
	public String getPubOwner() {
		return this.pubOwner;
	}
	/**
	 * @param pubOwner the pubOwner to set
	 */
	public void setPubOwner(String pubOwner) {
		this.pubOwner = pubOwner;
	}
	/**
	 * @param formResourceHash
	 * @return
	 */
	public ResourcePersonRelation withSimhash1(String formResourceHash) {
		this.setSimhash1(formResourceHash);
		return this;
	}
	/**
	 * @param relatorCode2
	 * @return
	 */
	public ResourcePersonRelation withRelatorCode(String relatorCode2) {
		this.setRelatorCode(relatorCode2);
		return this;
	}
	/**
	 * @param formPersonNameId
	 * @return
	 */
	public ResourcePersonRelation withPersonNameId(int formPersonNameId) {
		this.setPersonNameId(formPersonNameId);
		return this;
	}
	/**
	 * @param requestedUser
	 * @return
	 */
	public ResourcePersonRelation withPubOwner(String pubOwner2) {
		this.setPubOwner(pubOwner2);
		return this;
	}
	/**
	 * @return the post
	 */
	public Post<BibTex> getPost() {
		return this.post;
	}
	/**
	 * @param post the post to set
	 */
	public void setPost(Post<BibTex> post) {
		this.post = post;
	}
	/**
	 * @param formInterHash
	 * @return
	 */
	public ResourcePersonRelation withSimhash2(String simhash2) {
		this.setSimhash2(simhash2);
		return this;
	}
}
