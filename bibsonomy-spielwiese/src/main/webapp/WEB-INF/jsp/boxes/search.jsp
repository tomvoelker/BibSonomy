<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form id="search" method="get" action="/search">
  <c:if test="${!empty user.name}">
    <select name="scope" size="1">
      <option value="all">all</option>
      <option>user:<c:out value='${user.name}'/></option>
    </select>  
  </c:if>
  <input type="text" name="q" value="<c:out value='${search}'/>" size="25" id="se"/>
  <input type="submit" value="search"/>
</form>