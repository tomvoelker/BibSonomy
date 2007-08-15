<%@include file="/include_jsp_head.jsp" %>

<jsp:useBean id="upBean" class="beans.UploadBean" scope="request"/>

<%--HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="upload error" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: upload error</h1> 


<%@include file="/boxes/navi.jsp" %> 
	    
<div id="general">
<p>
The following error occured while processing your upload:
<br>
<strong><%=upBean.getErrorMsg("file")%></strong>
</p>

<p>
${projectName} accepts only valid BibTeX (*.bib), PDF, PS, DJVU and *.layout files to be uploaded.<br>
Please check your file carefully to make sure it meets those requirements.
</p>
Please <a href="/post_bibtex">go back</a> and try again.
</div>

<%@ include file="/footer.jsp" %>