
<c:if test="${empty user.name || not(user.name == 'jaeschke' || user.name == 'hotho' || user.name == 'schmitz' || user.name == 'stumme' || user.name == 'grahl' || user.name == 'beate' || user.name == 'dbenz')}">
  <jsp:forward page="/login" />
</c:if>