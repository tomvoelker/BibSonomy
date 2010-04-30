
<c:if test="${empty user.name || not(user.name == 'jaeschke' || user.name == 'hotho' || user.name == 'schmitz' || user.name == 'stumme' || user.name == 'bugsbunny' || user.name == 'stefani' || user.name == 'beate' || user.name == 'dbenz' || user.name == 'folke' || user.name == 'sdo' || user.name == 'onkelfaust')}">
  <jsp:forward page="/login" />
</c:if>