import org.bibsonomy.search.es.management.ElasticSearchManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * TODO: remove
 *
 * @author nosebrain
 */
public class ESTest {
	public static void main(String[] args) throws Exception {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("TestElasticSearch.xml");
		
		final ElasticSearchManager manager = context.getBean("publicationManager", ElasticSearchManager.class);
		manager.generateIndexForResource();
		// manager.generateIndexForResource("elasticSearch", "");
		// manager.generateIndexForResource("elasticSearch", "");
		context.close();
	}
}
