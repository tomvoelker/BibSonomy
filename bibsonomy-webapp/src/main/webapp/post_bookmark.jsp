<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="post bookmark" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/post_bookmark">post bookmark</a></h1> 


<%@include file="/boxes/navi.jsp" %> 

<div id="general"> 
  
  <c:if test="${not empty user.name}">
    <p>Posting could be so easy: just <strong>drag and drop</strong> the button to your bookmark toolbar:
    <%@include file="boxes/button_postbookmark.jsp" %> (IE users <a href="/help#button_postbookmark">look here</a>)
    </p>
  </c:if>
  
  <form style="margin: 3% 10px 3% 5px;" method="POST" action="<%=response.encodeURL("/ShowBookmarkEntry")%>">
    <label for="inpf">url</label> 
    <input type="text" name="url" id="inpf" size="60" value="http://" />
    <input type="submit" name="submit" value="check" />
  </form>
</div>

<%@ include file="footer.jsp" %>