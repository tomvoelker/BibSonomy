<%@include file="include_jsp_head.jsp" %>

<%-- Bean einbinden --%>
<jsp:useBean id="WarningBean" class="beans.WarningBean" scope="request"/>
<jsp:useBean id="bibtexUploadBean" class="beans.UploadBean" scope="request"/>

<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="upload info" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a href="#" rel="path_menu"><img src="/resources/image/box_arrow.png">&nbsp;upload info</a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 


<div id="general">
  
    <h2>Info</h2>
      <c:choose>
        <c:when test="${bibCounter == 0}">
          None of your BibTeX entries were imported. Please check the right box on this page for additional information. If you 
          have further questions about importing publications see hints below.
        </c:when>
        <c:otherwise>
          ${bibCounter} of ${bibTotalCounter} BibTeX entries were successfully imported.
        </c:otherwise>
    </c:choose>
 
   <c:choose>
     <c:when test="${bibCounter > 0}">
   	   <p>Below you can tag your imported BibTeX entries.</p>
  	
       <form name="tagging" action="/TagHandler" method=post>
  	     <table style="margin: 1em 0em 0em 0em;">
  		   <tr>
  		     <th>Your tags</th>
  			 <th>Your BibTeX entry</th>   
  		   </tr>
  			 
  		   <c:forEach var="bub" items="${bibtexUploadBean.bibtex}">
  		     <tr>
  			   <td><input type="text" name="${bub.hash}" size="40" value="<c:out value='${bub.fullTagString}'/>"/>
     			   <input type="hidden" name="0${bub.hash}" value="<c:out value='${bub.fullTagString}'/>"/>
  			   </td>
  			   <td class="chunkybib"><mtl:bibclean value="${bub.chunky}"/></td>   
  			 </tr>
  		   </c:forEach> 
  		 </table>
  		 <input type="hidden" name="requTask" value="bibtex" />
         <input type="hidden" name="ckey" value="${ckey}"/>
  	     <input type="submit" value="update" />
  	   </form>
  	   
     </c:when>
     <c:when test="${!empty bibtexUploadBean.bibtex && bibtexUploadBean.bibtexCount >= 1000}">
      <p>We got your BibTeX entries and will insert them now.</p>
     </c:when>
  </c:choose>
  
  <%-- import info part will be displayed if 0 entries were imported --%>
  <c:if test="${bibCounter == 0}">
  	<%@include file="/boxes/import_publication_hints.jsp" %> 
  </c:if>
</div>


<%--  boxes with warnings, incomplete and duplicate entries 
      WARNING: don't touch this stuff, since it is neccessary for DBLP upload handler (see HTMLResultHandler)
--%>
<div id="sidebarroundcorner" >
<ul id="sidebar">
  
  <%-- errors --%>
  <c:if test="${!empty WarningBean.errors}">
    <li>
    <span class="sidebar_h">Errors</span>
    <ul id="error_entry">
      <c:forEach var="error" items="${WarningBean.errors}">
        <li class="uploadinfo">
          <strong><c:out value="${error.value}" />:</strong><br/>
          <c:out value="${error.key.chunky}"/>
        </li> 
      </c:forEach>
    </ul>
    </li>
  </c:if>
  
  <%-- warnings --%>
  <c:if test="${!empty WarningBean.warning}">
    <li>
    <span class="sidebar_h">Warnings</span>
    <ul id="warning_entry">
      <c:forEach var="warning" items="${WarningBean.warning}">
        <li class="uploadinfo">
          <c:out value="${warning}" />
        </li> 
      </c:forEach>
    </ul>
    </li>
  </c:if>
  
  <%-- incompletes --%>
  <c:if test="${!empty WarningBean.incomplete}">
    <li>
    <span class="sidebar_h">The following entries were ignored because they are incomplete:</span>
    <ul id="incomplete_entry">
      <c:forEach var="bib" items="${WarningBean.incomplete}">
        <li class="uploadinfo">
          <%@ include file="/boxes/bibtex_short_code.jsp" %>
        </li> 
      </c:forEach>
    </ul>
    </li>
  </c:if>   

  <%-- duplicates --%>
  <c:if test="${!empty WarningBean.duplicate}">
    <li>
    <span class="sidebar_h">The following entries were ignored because you already have them in your library:</span> 
    <ul id="duplicate_entry">
      <c:forEach var="bib" items="${WarningBean.duplicate}">
        <li class="uploadinfo">
          <%@ include file="/boxes/bibtex_short_code.jsp" %>
        </li> 
      </c:forEach>
    </ul>
    </li>
  </c:if>   

</ul>
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>

<%@ include file="footer.jsp" %>