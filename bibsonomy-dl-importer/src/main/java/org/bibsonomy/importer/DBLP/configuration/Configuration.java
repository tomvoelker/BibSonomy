package org.bibsonomy.importer.DBLP.configuration;

/*
 * This class stores values about several items. This items are about the 
 * connectivity to the BibSonomy database and the download and store of the DBLP dataset.   
 */
public class Configuration{
	
	/*
	 * BibSonomy home (example: http://localhost:8080/)
	 */
	private String home;
	
	/*
	 * URL to DBLP dataset(in XML)
	 */
	private String url = null;
		
	/*
	 * BibSonomy-DBLP-User
	 */
	private String user = null;
	
	/*
	 * DB host
	 */
	private String dbhost = null;
	
	/*
	 * username for DB login
	 */
	private String dbuser = null;
	
	/*
	 * database name
	 */
	private String dbname = null;
	
	/*
	 * password for DB login
	 */
	private String dbpassword = null;
	
	public Configuration(){
		url="";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		url = url.trim();
		if(!url.equals(""))
			this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDbhost() {
		return dbhost;
	}

	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}

	public String getDbpassword() {
		return dbpassword;
	}

	public void setDbpassword(String dbpassword) {
		this.dbpassword = dbpassword;
	}

	public String getDbuser() {
		return dbuser;
	}

	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	
	public boolean isValid(){
		if(url != null && user != null && dbhost != null && dbuser != null && dbname != null){
			return true;
		}
		return false;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}
	
}