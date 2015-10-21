import org.bibsonomy.search.management.SearchResourceManagerInterface;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * TODO: remove
 *
 * @author nosebrain
 */
public class ESTest {
	public static void main(String[] args) throws Exception {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("TestElasticSearch.xml");
		
		final SearchResourceManagerInterface<?> manager = context.getBean("publicationManager", SearchResourceManagerInterface.class);
		// manager.generateIndexForResource("elasticSearch", "");
		// manager.generateIndexForResource("elasticSearch", "");
		manager.updateAllIndices();
		context.close();
	}
}
