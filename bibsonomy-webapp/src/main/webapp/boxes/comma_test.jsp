<br/>
<c:if test="${!empty param.testComma}">
  <label for="comma">Confirm to accept tags including commas/semicolons:</label> <input type="Checkbox" name="acceptComma" value="true" id="comma">
  <div class="errmsg">In ${projectName} commas/semicolons are not used as separators.</div>
</c:if>