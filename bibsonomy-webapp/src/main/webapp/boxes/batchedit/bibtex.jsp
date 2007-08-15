
<c:if test="${ResourceBean.bibtexCount gt 0}">

<form id="batchedit" action="/TagHandler" method="post">
  <table style="margin: 1em 0em 0em 0em;">
    <tr>
      <th>Your tags</th>
      <th>Your posts</th>
      <th>Delete post?</th>
    </tr>
    <tr>
      <td style="text-align:center;"><%@include file="/boxes/nextprevbib.jsp" %></td>
    </tr>
    <tr>
      <td><input type="submit" value="update" /></td>
    </tr>
    <tr style="background-color: #eee;">
      <td><input type="text" name="tags" size="40"/></td>
      <td>These tags will be added to <strong>ALL</strong> posts shown on this page!</td>      
      <td><input type="checkbox" name="all" id="deleteAllId" onclick="deleteAll()"/> delete all posts</td>
    </tr>  
    <c:forEach var="bib" items="${ResourceBean.bibtex}">
      <c:if test="${bib.user eq user.name}">
        <tr>
          <td>
            <input type="text" name="${bib.hash}" size="40" value="<c:out value='${bib.fullTagString}'/>"/>
            <input type="hidden" name="0${bib.hash}" value="<c:out value='${bib.fullTagString}'/>"/>
          </td>
          <td class="chunkybib">
            <mtl:bibclean value="${bib.chunky}"/>
          </td>
          <td>
            <input type="checkbox" name="d${bib.hash}"/> delete post
          </td>
        </tr>
      </c:if>
    </c:forEach> 
    <tr><td>
      <input type="submit" value="update" />    
      <input type="hidden" name="ckey" value="${ckey}"/>
      <input type="hidden" name="referer" value="<c:out value='${header.referer}'/>"/>
      <input type="hidden" name="requTask" value="bibtex" />
    </td><td></td></tr>
    <tr><td style="text-align:center;">
      <%@include file="/boxes/nextprevbib.jsp" %>   
    </td><td></td></tr>
  </table>
</form>


<h3>Disregarded posts</h3>
<p>The following posts are not your own and therefore you can not change their tags.</p>
  
  <ul class="bblist"><c:forEach var="bib" items="${ResourceBean.bibtex}">

   <c:if test="${bib.user ne user.name}">
   <li class="bm">
      <%@include file="/boxes/bibtex_desc.jsp" %>
      <%@include file="/boxes/bibtex_desc2.jsp" %>
      <%@include file="/boxes/bibtex_action.jsp" %>     
   </li>
  </c:if>

  </c:forEach></ul>

</c:if>