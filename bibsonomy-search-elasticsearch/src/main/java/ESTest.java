import org.bibsonomy.search.es.ESClient;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * TODO: remove
 *
 * @author nosebrain
 */
public class ESTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("TestElasticSearch.xml");
		final ESClient client = context.getBean("esClient", ESClient.class);
		
		client.getClient().admin().cluster().state(new ClusterStateRequest());
		context.close();
	}
}
