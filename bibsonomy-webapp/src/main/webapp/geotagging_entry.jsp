<%@include file="include_jsp_head.jsp" %>
<jsp:useBean id="PictureBean" class="beans.PictureBean" scope="request">
	<jsp:setProperty name="PictureBean" property="*"/>
	<%-- <jsp:setProperty name="PictureBean" property="username" value="${user.name}"/> --%>
</jsp:useBean>
<% PictureBean.getContent(); %>
<c:set var="book" value="${PictureBean.bookmark}" />

<%-- HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="geotagging" />
</jsp:include>


<%-------------------------- Heading -----------------------%>
<h1><a href="/">${projectName}</a> :: geotagging ::
  <form class="smallform" method="get" action="/search">
    <input type="text" name="q" value="${book.title}" size="30"/>
  </form>
</h1> 


<%------------- Navigation --------------%>  
<%@include file="/boxes/navi.jsp" %> 

   
<div id="outer"><%-- neccessary so that tags are boxed to the right --%>

<div style="margin: 5px 1% 2% 1%;">

  <div style="margin: 0px 0px 1em 0px;" class="kiste">
    <span style="color: #aaaaaa; font-size: 80%">
      <a href="/user/<mtl:encode value='${book.user}'/>"><c:out value="${book.user}"/></a>'s GeoTagging entry:
    </span>
    <%@ include file="/boxes/bookmark_action.jsp" %>
  </div>
  
  <h2 style="font-size: 120%; margin-bottom: 0px;"><a ${book.title}/></a></h2>             

    <table>
	    <tr>
			<td class="expl2">Photographer:</td>
			<td class="cont"><c:out value="${book.user}"/></td>
	    </tr>
        <tr>
			<td class="expl2">Description:</td>
			<td class="cont"><c:out value="${book.extended}"/></td>
		</tr>
		<tr>
			<td class="expl2">Latitude:</td>
			<td class="cont"><c:out value="${PictureBean.lat} ${PictureBean.latD}"/></td>
		</tr>
		<tr>
			<td class="expl2">Longitude:</td>
			<td class="cont"><c:out value="${PictureBean.lon} ${PictureBean.lonD}"/></td>
		</tr>
		<tr>
		    <td></td>
		    <td><img src="/images/${book.docHash}/<mtl:encode value='${param.requUser}'/>"/></td>
		</tr>
        <tr>		
		    <td class="expl2">Tags:</td>
		    <td class="cont">
		      <c:forEach var="tag" items="${book.tags}">             
		        <a href="/user/<mtl:encode value='${book.user}'/>/<mtl:encode value='${tag}'/>"><c:out value="${tag}"/></a>
		      </c:forEach></td>
        </tr>
        <c:if test="${user.name eq book.user}"><tr>
          <td colspan="2">
                <form action="/TagHandler" method="post">
			  	  <input type="text" name="${book.hash}" size="40" value='<c:out value="${book.fullTagString}"/>'>
				  <input type="hidden" name="0${book.hash}" value="<c:out value='${book.fullTagString}'/>"/>
				  <input type="submit" value="update tags">
				  <input type="hidden" name="referer" value="<c:out value='${header.referer}'/>"/>
				  <input type="hidden" name="requTask" value="bookmark">
				</form>
          </td>   
        </tr>
		</c:if>
     </table>
     
    <div class="kiste">
    	<%@ include file="/boxes/bookmark_action.jsp" %>
    </div>
    
    <table>
      <tr>
        <td>Show in:</td>
        <td class="exp12"><a href="http://maps.google.com/maps?q=&ll=${PictureBean.lat },${PictureBean.lon}&spn=0.0015,0.0015&t=k&hl=de">Google Maps</a></td>
        <td class="exp12"><a href="http://www.goyellow.de/map?lat=${PictureBean.lat }&lon=${PictureBean.lon}&z=19&mt=1">GoYellow.de</a></td>
        <td class="exp12"><a href="http://www.multimap.com/map/browse.cgi?lat=${PictureBean.lat}&lon=${PictureBean.lon}&scale=10000&icon=x">Multimap.com</a></td>
        <td class="exp12"><a href="http://www.mapquest.com/maps/map.adp?latlongtype=decimal&latitude=${PictureBean.lat}&longitude=${PictureBean.lon}&zoom=9">MapQuest.com</a></td>
        <td class="exp12"><a href="http://www.globexplorer.com/ImageAtlas/view.do?group=ImageAtlas&lat=${PictureBean.lat}&lon=${PictureBean.lon}&zoom_level=8">GlobeExplorer.com</a></td>
      </tr>
    </table>
		
</div>
</div>

<ul id="sidebar">
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul>

<%@ include file="/footer.jsp" %>