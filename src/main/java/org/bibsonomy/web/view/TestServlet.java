/*
 * Created on 15.05.2006
 */
package org.bibsonomy.web.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bibsonomy.database.TestStrategy;
import org.bibsonomy.viewmodel.TestViewModel;

public class TestServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TestServlet.class);
	private static final long serialVersionUID = HttpServlet.class.getName().hashCode() << 32L + "$Id$".hashCode();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info('a');
		try {
			TestViewModel bean = new TestStrategy().perform();
			req.setAttribute("bean",bean);
			getServletConfig().getServletContext().getRequestDispatcher("/test.jsp").forward(req, resp);
		} finally {
			log.info('b');
		}
	}
}
