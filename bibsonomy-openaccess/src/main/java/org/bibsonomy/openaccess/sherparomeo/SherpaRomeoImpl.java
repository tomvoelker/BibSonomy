package org.bibsonomy.openaccess.sherparomeo;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bibsonomy.openaccess.sherparomeo.model.Condition;
import org.bibsonomy.openaccess.sherparomeo.model.Publisher;
import org.bibsonomy.openaccess.sherparomeo.model.Romeoapi;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SherpaRomeoImpl implements SherpaRomeoInterface {

    /*
     * TODO: working but ugly code.
     */

    private JAXBContext context;

    public SherpaRomeoImpl() {
        try {
            this.context = JAXBContext
                    .newInstance("org.bibsonomy.openaccess.sherparomeo.model");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPolicyForPublisher(String publisher, String qtype) {
        try {
            String url = "http://www.sherpa.ac.uk/romeo/api24.php?pub="
                    + publisher;
            if (qtype != null)
                url += "&qtype=" + qtype;

            return this.doRequest(new URL(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPolicyForJournal(String jtitle, String qtype) {
        try {
            String url = "http://www.sherpa.ac.uk/romeo/api24.php?jtitle="
                    + jtitle;
            if (qtype != null)
                url += "&qtype=" + qtype;

            return this.doRequest(new URL(url));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sending request to sherparomeo.
     * 
     * @param url
     * @return
     */
    @SuppressWarnings("unchecked")
    private String doRequest(URL url) {
        String data = "";
        try {
            Unmarshaller unmarshaller = this.context.createUnmarshaller();
            Romeoapi rp = (Romeoapi) unmarshaller.unmarshal(url);

            // log.debug("Checking Open Access: \t" + url);

            List<Publisher> publisherList = rp.getPublishers().getPublisher();
            JSONObject result = new JSONObject();
            JSONArray output = new JSONArray();
            for (Publisher pub : publisherList) {
                JSONObject pub_json = new JSONObject();
                JSONArray conditions = new JSONArray();
                List<Condition> conditionList = pub.getConditions()
                        .getCondition();
                for (Condition condition : conditionList)
                    conditions.add(condition.getvalue());

                pub_json.put("name", pub.getName());
                pub_json.put("colour", pub.getRomeocolour());
                pub_json.put("conditions", conditions);
                output.add(pub_json);
            }
            result.put("publishers", output.toJSONString());

            data = result.toJSONString();

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
}
