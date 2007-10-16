<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:stripes="http://stripes.sourceforge.net/stripes.tld">

	<fmt:message var="errorText" key="error"/>
	<stripes:layout-render name="/WEB-INF/jsp/layout/layout.jspx" title="${errorText}">
		<stripes:layout-component name="fullContent">
			<h2>${error}</h2>
		</stripes:layout-component>
	</stripes:layout-render>

</jsp:root>
