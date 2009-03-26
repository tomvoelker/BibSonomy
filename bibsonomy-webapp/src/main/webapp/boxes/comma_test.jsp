<c:if test="${not empty param.testComma}">
  <br/>
  <label for="comma">Confirm to accept tags including commas/semicolons:</label> <input type="Checkbox" name="acceptComma" value="true" id="comma">
  <div class="errmsg">In ${projectName} commas/semicolons are not used as separators.</div>
</c:if>