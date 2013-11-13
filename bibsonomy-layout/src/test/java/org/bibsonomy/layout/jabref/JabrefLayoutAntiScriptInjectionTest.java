package org.bibsonomy.layout.jabref;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class JabrefLayoutAntiScriptInjectionTest extends AbstractJabrefLayoutTest{

	//Layouts that will be tested
	private static final Set<String> TESTEDLAYOUTS = 
		Sets.asSet(new String[]{"apa_html","chicago","din1505","dblp","harvardhtml","simplehtml","simplehtmlyear",
								"tablerefs","tablerefsabsbib","tablerefsabsbibsort","html"});
	private static final String TESTCASEFOLDERPATH = "/jabref-layout-anti-script-tests";
	private static final String ENTRY_TYPESPLIT_SUFFIX = "xmlesc#";
	
	public JabrefLayoutAntiScriptInjectionTest(File layoutTest, String layoutName) {
		super(layoutTest, layoutName);
	}
	
	@Parameters
	public static Collection<Object[]> data() {
		return initTests(TESTEDLAYOUTS, TESTCASEFOLDERPATH, ENTRY_TYPESPLIT_SUFFIX);
	}
	
	@Override
	@Test
	public void testRender() throws Exception {
		testRender(getPosts(this.entryType));
	}
	
	public static List<Post<BibTex>> getPosts(String entryType) throws PersonListParserException{
		final User u = new User();
    	u.setName("TestUser TestUserUser");

		final BibTex publication = new BibTex(); 
		publication.setEntrytype(entryType);
		publication.setBibtexKey("<xxx>testuser2013test" + entryType + "</xxx>"); 
		publication.setAddress("<xxx>TestState</xxx>, <xxx>TestCountry</xxx>");
		publication.setAuthor(PersonNameUtils.discoverPersonNames("<xxx>TestUser TestUserUser</xxx> and <xxx>TestUser FriendOne</xxx>"));
		publication.setBooktitle("<xxx>BookTitleTest</xxx>");
		publication.setEditor(PersonNameUtils.discoverPersonNames("<xxx>TestEditorOne EditorOne</xxx> and <xxx>TestEditorTwo EditorTwo</xxx>"));
		publication.setPages("<xxx>323--324</xxx>");
		publication.setPublisher("<xxx>PublisherTest</xxx>");
		publication.setTitle("<xxx>alert \"ScriptTest\"</xxx><xxx>TitleTest</xxx>");
		publication.setUrl("<xxx>http://www.test.de</xxx>");
		publication.setYear("<xxx>20013</xxx>");
		publication.setMisc("isbn = {<xxx>978-1-60558-486-7</xxx>},\ndoi = {<xxx>10.1145/1557914.1557969</xxx>}");
		publication.setMonth("<xxx>jun</xxx>");
		publication.setPrivnote("<xxx>This is a test note</xxx>"); 
		publication.setAbstract("<xxx>AbstractTest</xxx>");
		final Post<BibTex> post = new Post<BibTex>();
		post.setResource(publication);
		post.setUser(u);
		post.setDescription("<xxx>DescriptionTest</xxx>");
		
		return Collections.singletonList(post);
	}
	
}
