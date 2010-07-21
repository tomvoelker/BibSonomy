package DBLP.update;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


/**
 * @author rja
 * @version $Id$
 */
public class HTMLResultHandlerTest {

	private static final String testString1 = 
		"<html>\n" + 
		"  <body>\n" + 
		"    <h3>error</h3>\n" + 
		"    <pre class=\"error\">Your submitted publications contain errors at the lines with the following numbers: 202, 356</pre>\n" + 
		"    <table>\n" + 
		"      <tr>\n" + 
		"        <td>\n" + 
		"          <div style=\"\" class=\"help\">\n" + 
		"            <b style=\"color:red; font-size:120%\" class=\"smalltext\">X</b>\n" + 
		"            <div>\n" + 
		"              <ul><li class=\"info_bold\">Please enter a year.</li></ul>\n" + 
		"            </div>\n" + 
		"          </div>\n" + 
		"          <b class=\"smalltext\">error</b>\n" + 
		"        </td>\n" + 
		"      </tr>\n" + 
		"    </table>\n" + 
		"  </body>\n" + 
		"</html>"; 

	@Test
	public void testGlobalErrorParsing() throws Exception {
		boolean found = false;
		
		final Document document = getDocument(testString1);
		final NodeList pres = document.getElementsByTagName("pre");
		for (int i = 0; i < pres.getLength(); i++) {
			final Node pre = pres.item(i);
			if ("error".equals(pre.getAttributes().getNamedItem("class").getNodeValue())) {
				/*
				 * pre with error found -> add global error
				 */
				Assert.assertEquals("Your submitted publications contain errors at the lines with the following numbers: 202, 356", pre.getChildNodes().item(0).getNodeValue());
				found = true;
			}
		}
		
		Assert.assertTrue(found);

	}


	@Test
	public void testSingleErrorParsing() throws Exception {
		final Document document = getDocument(testString1);
		boolean found = false;

		
		final NodeList divs = document.getElementsByTagName("div");
		for (int i = 0; i < divs.getLength(); i++) {
			final NodeList childNodesDiv = divs.item(i).getChildNodes();
			for (int divCn = 0; divCn < childNodesDiv.getLength(); divCn++) {
				final Node item = childNodesDiv.item(divCn);
				if ("ul".equals(item.getNodeName())) {
					final NodeList childNodesUl = item.getChildNodes();
					for (int ulCn = 0; ulCn < childNodesUl.getLength(); ulCn++) {
						final Node li = childNodesUl.item(ulCn);
						Assert.assertEquals("Please enter a year.", li.getChildNodes().item(0).getNodeValue());
						found = true;
					}
				}
			}
			
		}
		Assert.assertTrue(found);
	}


	private static void printChildNodes(final NodeList l) {
		System.out.println("[");
		for (int i = 0; i < l.getLength(); i++) {
			printChildNodes(l.item(i));
		}
		System.out.println("]");
	}
	
	private static void printChildNodes(final Node b) {
		for (int cn = 0; cn < b.getChildNodes().getLength(); cn++) {
			final Node node = b.getChildNodes().item(0);
			System.out.println("cn: " + node.getNodeName() + " / " + node.getNodeValue());
		}
	}

	
	
	private Document getDocument(final String string) throws UnsupportedEncodingException {
		final Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		tidy.setMakeClean(false);
		return tidy.parseDOM(new ByteArrayInputStream(string.getBytes("UTF-8")), null);
	}


}

