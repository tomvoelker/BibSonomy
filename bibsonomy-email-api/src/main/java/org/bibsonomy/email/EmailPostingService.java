package org.bibsonomy.email;

import static org.bibsonomy.util.ValidationUtils.present;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class EmailPostingService {
	private static final Log log = LogFactory.getLog(EmailPostingService.class);

	/*
	 * supported configuration arguments
	 */
	private static final String ARG_GROUP = "--group=";
	private static final String ARG_DATABASE = "--database";
	/*
	 * corresponding configuration attributes
	 */
	private boolean directDatabaseAccess = false;
	private String defaultGroup = GroupUtils.getPublicGroup().getName();

	/*
	 * helper services
	 */
	private EmailParser emailParser;
	private PostBuilder postBuilder;

	
	public static void main(String[] args) {
		log.info("configuring email posting service");
		final EmailPostingService service = new EmailPostingService();

		service.setPostBuilder(new PostBuilder());
		service.getPostBuilder().setUrlProvider(new UrlProvider());
		
		service.setEmailParser(new EmailParser());
		service.getEmailParser().setToFieldParser(new ToFieldParser());
	
		configurService(args, service);

		try {
			/*
			 * read email from STDIN
			 */
			service.postEmail(System.in);
		} catch (IOException e) {
			log.error("Could not store posts.", e);
		}
	}

	private static void configurService(final String[] args, final EmailPostingService service) {
		log.info("parsing command line arguments " + Arrays.toString(args));
		for (final String arg: args) {
			if (ARG_DATABASE.equals(arg)) {
				service.setDirectDatabaseAccess(true);
			} else if (arg.startsWith(ARG_GROUP)) {
				service.setDefaultGroup(arg.substring(ARG_GROUP.length()));
			}
		}
		log.info("using direct database access: " + service.isDirectDatabaseAccess());
		log.info("default posting group: " + service.getDefaultGroup());
	}

	public void postEmail(final InputStream reader) throws IOException {
		postEmail(new BufferedReader(new InputStreamReader(reader, "UTF-8")));
	}

	/**
	 * Posts an email using the configured logic interface implementation.
	 * 
	 * @param reader - provides the email.
	 * @throws IOException
	 */
	public void postEmail(final BufferedReader reader) throws IOException {
		/*
		 * parsing
		 */
		log.info("parsing the email");
		final Email email = emailParser.parseEmail(reader);

		/*
		 * getting DB access
		 */
		log.info("getting database access");
		final LogicInterface logic;
		final LogicFactory logicFactory = new LogicFactory(email.getTo(), email.getFrom());
		if (directDatabaseAccess) {
			logic = logicFactory.getDBLogic();
		} else {
			logic = logicFactory.getRestLogic();
		}
		final String userName = logicFactory.getLoginUserName();

		/*
		 * building posts
		 */
		log.info("building posts");
		final String group = present(email.getTo().getGroup()) ? email.getTo().getGroup() : defaultGroup;
		final List<Post<? extends Resource>> posts = postBuilder.buildPosts(email, userName, group);

		/*
		 * posting
		 */
		log.info("storing " + posts.size() + " posts in database for user " + userName);
		final List<String> createdPosts = logic.createPosts(posts);
		log.info("successfully created " + createdPosts.size() + " posts");
	}

	public EmailParser getEmailParser() {
		return emailParser;
	}
	public void setEmailParser(EmailParser emailParser) {
		this.emailParser = emailParser;
	}

	public PostBuilder getPostBuilder() {
		return postBuilder;
	}
	public void setPostBuilder(PostBuilder postBuilder) {
		this.postBuilder = postBuilder;
	}

	public boolean isDirectDatabaseAccess() {
		return directDatabaseAccess;
	}
	public void setDirectDatabaseAccess(boolean directDatabaseAccess) {
		this.directDatabaseAccess = directDatabaseAccess;
	}

	public String getDefaultGroup() {
		return defaultGroup;
	}
	public void setDefaultGroup(String group) {
		this.defaultGroup = group;
	}
}