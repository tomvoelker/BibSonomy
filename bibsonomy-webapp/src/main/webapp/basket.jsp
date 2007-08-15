<%@include file="include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="ResourceBean" class="beans.ResourceBean" scope="request"/>

<c:if test="${empty user.name}">
   <jsp:forward page="/login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="basket" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1><a href="/" rel="Start">${projectName}</a> :: <a href="/basket">basket</a></h1> 

<%@include file="/boxes/navi.jsp" %> 

<%----------------------collected bibtex entries - extra meta handling without pick button--------------------%>
<div id="bibbox">
  
  <p>Here you can manage publication entries you picked with the "pick" button. For details see <a href="/help/basic/firststeps#basket">here</a>.</p>

  <%@ include file="/boxes/basket_actions.jsp" %>
  
  <%-------------------- Publication entries -----------------------%>
  <c:set var="basePath" value="/"/>
  <ul class="bblist">
    <c:forEach var="bib" items="${ResourceBean.bibtex}">
        <%@ include file="/boxes/bibtex_own_entry_mark.jsp" %>
        <%@ include file="/boxes/bibtex_desc.jsp" %>
        <%@ include file="/boxes/bibtex_desc2.jsp" %> 
        <%@ include file="/boxes/bibtex_action.jsp" %>
        <%-- unpick publication entry --%>
        <span class="bmaction">
	      <a href="/Collector?unpick=${bib.hash}&user=<mtl:encode value='${bib.user}'/>&amp;ckey=${ckey}" title="remove this entry from the basket">remove from basket</a>  
        </span>
      </li>
    </c:forEach>
  </ul>

  <%@ include file="/boxes/basket_actions.jsp" %>

</div>

<script type="text/javascript">
maximizeById("bibbox");
</script>


<%@ include file="footer.jsp" %>