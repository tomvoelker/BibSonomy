package beans;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.DBLogicNoAuthInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * Bean for retrieving Tags using the logicinterface 
 * TODO: Delete this class when start using the spring framework
 * 
 * @author Stefan Stuetzer 
 * @version $Id$
 */
public class TagCloudBean {
	private static final Log LOGGER = LogFactory.getLog(TagCloudBean.class);

	/** the username of the current user */
	private String username = null;

	/** factory for creating logic interface */
	private DBLogicNoAuthInterfaceFactory dbLogicFactory = null;

	/** the logic interface */
	private LogicInterface logic = null;

	/**
	 * name of the grouping. if grouping is user, then its the username. if
	 * grouping is set to {@link GroupingEntity#ALL}, then its an empty string!
	 */
	private String groupingName = "";

	/** list of tags for which to search related tags for */
	private List<String> requTagsList;	
	
	/** the requested tag or set of tags */
	private String requTags = "";
	
	/** list of retrieved tags */
	List<Tag> tags = null;

	/**
	 * Default Constructor
	 */
	public TagCloudBean() {
		requTagsList = new ArrayList<String>();
		
		dbLogicFactory = new DBLogicNoAuthInterfaceFactory();
		dbLogicFactory.setDbSessionFactory(new IbatisDBSessionFactory());

		initLogicInterface();
	}

	private void initLogicInterface() {
		logic = dbLogicFactory.getLogicAccess(this.username, null);
	}

	public List<Tag> getTags() {
		if (logic != null && tags == null) {			
			tags = logic.getTags(Resource.class, GroupingEntity.GROUP, groupingName , "", requTagsList, null, null, 0, 25, null, null);			
		}
		return tags;
	}
	
	public List<String> getRequTagsList() {
		return this.requTagsList;
	}

	public void setRequTags(String requTags) {
		this.requTags = requTags;
		
		StringTokenizer st = new StringTokenizer(requTags);
		while (st.hasMoreTokens()) {			
			String tagname = st.nextToken();			
			requTagsList.add(tagname);			
		}
	}	
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
		
		// create logicinterface again
		initLogicInterface();
	}
	
	public String getGroupingName() {
		return this.groupingName;
	}
	
	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}
}
