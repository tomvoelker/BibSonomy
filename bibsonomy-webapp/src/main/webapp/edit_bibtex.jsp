<%@include file="include_jsp_head.jsp" %>

<jsp:useBean id="bibtexHandlerBean" class="beans.BibtexHandlerBean" scope="request"/>

<c:if test="${empty user.name}">
  <jsp:forward page="login"/>
</c:if>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="edit bibtex" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/">${projectName}</a> :: <a rel="path_menu" href="#"><img src="/resources/image/box_arrow.png">&nbsp;edit bibtex</a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%-- 2008/12/18, dbe: passing parameter 'selectedNaviTab' due to bug 646 --%>
<c:set var="selectedNaviTab" value="nav_postPub"/>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 


<style type="text/css">
<!--
input, textarea {
  width: 100%;
}

td.expl_s {
  padding-left: 1em;
  max-width: 300px;
}

-->
</style>



<!------------------------------------------->
<!--          Content goes here            -->
<!------------------------------------------->
<div id="general">
<c:forEach var="errorkey" items="${bibtexHandlerBean.errorKeys}">
  <div class="errmsg">${bibtexHandlerBean.errors[errorkey]}</div> 
</c:forEach>

<%-- entry already exists  --%>
<c:if test="${!empty bibtexHandlerBean.oldentry}">
  <script type="text/javascript">
    var toggledOff = true;
    function toggleOld() {
       var divs = document.getElementsByTagName("div");
       if (toggledOff) {
          state = "block";
       } else {
          state = "none";
       }
       toggledOff = !toggledOff;
       for (i=0; i<divs.length; i++) {
          if (divs[i].className == "oldentry") {
             divs[i].style.display = state;
          }
       }       
    }
  </script>
  
  <div class="errmsg">
    You posted an entry, which already exists in your ${projectName} repository. 
    To see the fields from the existing entry <a name="oldentry" onclick="toggleOld()" style="cursor:pointer;">click here</a>.
  </div>
  
</c:if>

<c:if test="${!empty param.selection}">
  <h2>Selected Text</h2>
  <pre><c:out value="${mtl:trimWhiteSpace(param.selection)}"/></pre>
</c:if>


<h2>Feel free to edit your BibTeX entry</h2>

<form name="post_bibtex" method="post" action="/bibtex_posting_process">
  <input type="hidden" name="oldhash" value="${bibtexHandlerBean.oldhash}"/>
  <input type="hidden" name="rating" value="${bibtexHandlerBean.rating}"/>
  <input type="hidden" value="${ckey}" name="ckey"/>


<table>
    
  <tr>
    <td class="expl">tags*</td>
    <td>
      <input class="reqinput" type="text" id="inpf" name="tags" onClick="setActiveInputField(this.id); enableHandler();" onFocus="setActiveInputField(this.id); enableHandler()" onBlur="disableHandler()" value='<c:out value="${bibtexHandlerBean.tags}" />' autocomplete="off" onClick="setActiveInputField(this.id)" onFocus="setActiveInputField(this.id)" >
      <%@include file="/boxes/comma_test.jsp" %> 
    
      <div class="errmsg">${bibtexHandlerBean.errors.tags}</div>
      <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.fullTagString}"/></div>
    </td>
    <td class="expl_s">space separated</td>				
  </tr>

<tr>
  <td class="expl">description,<br>comment</td>
  <td>
    <textarea name="description" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.description}"/></textarea>
    <div class="errmsg">${bibtexHandlerBean.errors.description}</div>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.description}"/></div>
  </td>
  <td></td>				
</tr>


  <tr>
    <td class="expl">
      viewable for
    </td>
    
    <td>
      <span style="float: left;">
        <%@include file="/boxes/groupselection.jsp" %>
      </span>
  
      <c:if test="${not empty user.groups}">

        <select style="float:right;" id="relevantGroups" size="3" multiple="true">
          <option value="">--please select--</option>
          <c:forEach var="group" items="${user.groups}">
            <option value="${f:escapeXml(group)}"><c:out value="${group}"/></option> 
          </c:forEach>
        </select>
        <span class="expl" style="float:right; padding-right: 1em;">
          relevant for
        </span>
        
      </c:if>
   </td>
   <td class="expl_s">
     <c:if test="${not empty user.groups}">
       If your post is interesting for one or more groups, you can mark your post "relevant" for these groups.
     </c:if>
   </td>
</tr>


<tr>
      <td height="40">
	    <ul id="suggTags">
	      <li>suggested</li>
	    </ul>
	  </td>
	  <td height="40">
        <ul id="suggested" class="suggtag">
        </ul>
        <div class="errmsg"></div>
  	  </td>
	</tr>
<c:if test="${not empty bibtexHandlerBean.recommendedTags}" >
		<tr>
          <td height="40">
    	    <ul id="suggTags">
    	      <li>recommendation:</li>
    	    </ul>
    	  </td>
          <td height="40" id="recommender">
            <ul id="recommendtag">
                <c:forEach var="tag" items="${bibtexHandlerBean.recommendedTags}">
                    <li class="recommended"><a onclick="toggle(event); return false;" title="<c:out value="${tag.score}"/> score"><c:out value="${tag.name}"/></a></li>
    			</c:forEach>
            </ul>
      	  </td>
    	</tr>		
	</c:if>


<%--  insert copy tags --%>
<c:if test="${bibtexHandlerBean.copytag != null}">
  <tr>
    <td colspan="3">
      <h2>Tags of copied item: </h2>
      <ul id="copytag" >
        <c:forEach var="elem" items="${bibtexHandlerBean.copytag}">
          <li ><c:out value='${elem}'/></li>
        </c:forEach>
      </ul>    
    </td>
  </tr>
</c:if>







<tr><td colspan="3"><hr style="margin: 20px 0px 20px 0px;"></td></tr>

<tr>
  <td>
    <input type="submit" value="post_bibtex" onclick="clear_tags()"/>
    <input type="hidden" name="requTask" value="upload">  
  </td>
  <td class="expl_s"><a id="collapse" href="javascript:showAll()">show all fields</a> </td>
  <td></td>
</tr>

<tr>
  <td class="expl">type*</td>
  <td>
    <select class="reqinput" name="entrytype" onChange="changeView();">
      <c:forEach var="et" items="${bibtexHandlerBean.entrytypes}">
          <c:choose>
	          <c:when test="${bibtexHandlerBean.entrytype eq et}">
	      	    <option value="${et}" selected="true">${et}</option>
	      	  </c:when>
	      	  <c:otherwise>
  	      	    <option value="${et}">${et}</option>
  	      	  </c:otherwise>
  	      	</c:choose>
          </c:forEach>
    </select>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.entrytype}"/></div>
  </td>
  <td></td>
</tr>

<tr>
  <td class="expl">BibTeX key*</td>
  <td>
    <input class="reqinput" type="text" name="bibtexKey" value='<c:out value="${bibtexHandlerBean.bibtexKey}"/>' style="width: 10em;"/>
    <div class="errmsg">${bibtexHandlerBean.errors.bibtexKey}</div>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.bibtexKey}"/></div>
  </td>
  <td class="expl_s">a memorable key without spaces</td>
</tr>


<tr>
  <td class="expl">title*</td>
  <td>
    <textarea class="reqinput" name="title" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.title}"/></textarea>
    <div class="errmsg">${bibtexHandlerBean.errors.title}</div>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.title}"/></div>
  </td>
  <td></td>
</tr>

<tr>
  <td class="expl">authors*</td>
  <td>
    <textarea class="reqinput" name="author" id="lauthor" onkeyup="toggle_required_author_editor(); sz(this);"" rows="3"><c:out value="${bibtexHandlerBean.authorLineBreak}"/></textarea>
    <div class="errmsg">${bibtexHandlerBean.errors.author}</div>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.author}"/></div>
  </td>
  <td class="expl_s">each author on a separate line, format "Firstname Lastname"</td>
</tr>



<tr>
  <td valign="top" class="expl">editors*</td>
  <td>
    <textarea class="reqinput" name="editor" id="leditor" onkeyup="toggle_required_author_editor(); sz(this);" rows="3" ><c:out value="${bibtexHandlerBean.editorLineBreak}"/></textarea>
    <div class="errmsg">${bibtexHandlerBean.errors.editor}</div>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.editor}"/></div>
    <script type="text/javascript">toggle_required_author_editor();</script>    
  </td>
  <td class="expl_s">each editor on a separate line, format "Firstname Lastname"</td>
</tr>



<tr id="booktitleR">
  <td class="expl">booktitle</td>
  <td>
    <input type="text" name="booktitle" value="${f:escapeXml(bibtexHandlerBean.booktitle)}"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.booktitle}"/></div>
  </td>
  <td class="expl_s">title of a book when only part is cited</td>
</tr>



<tr id="journalR">
  <td class="expl">journal</td>
  <td>
    <input type="text" name="journal" value='<c:out value="${bibtexHandlerBean.journal}"/>' />
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.journal}"/></div>
  </td>
  <td class="expl_s">full (unabbreviated) journal title</td>
</tr>




<tr id="volumeR">
  <td class="expl">volume</td>
  <td>
    <input type="text" name="volume" value='<c:out value="${bibtexHandlerBean.volume}"/>' style="width: 10em;" />
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.volume}"/></div>
  </td>
  <td></td>
</tr>


<tr id="numberR">
  <td class="expl">number</td>
  <td>
    <input type="text" name="number" value='<c:out value="${bibtexHandlerBean.number}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.number}"/></div>
  </td>
  <td class="expl_s">number of journal, magazine, technical report, or work in a series</td>
</tr>

<tr id="pagesR">
  <td class="expl">pages</td>
  <td>
    <input type="text" name="pages" value='<c:out value="${bibtexHandlerBean.pages}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.pages}"/></div>    
  </td>
  <td></td>
</tr>


<tr id="publisherR">
  <td class="expl">publisher</td>
  <td>
    <input type="text" name="publisher" value='<c:out value="${bibtexHandlerBean.publisher}"/>' />
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.publisher}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="addressR">
  <td class="expl">address</td>
  <td>
    <input type="text" name="address" value="<c:out value='${bibtexHandlerBean.address}'/>" />
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.address}"/></div>    
  </td>
  <td class="expl_s">address of the publisher or institution</td>
</tr>



<tr>
  <td class="expl">year*</td>
  <td>
    <input class="reqinput" type="text" name="year" value='<c:out value="${bibtexHandlerBean.year}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.year}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="monthR">
  <td class="expl">month</td>
  <td>
    <input type="text" name="month" value='<c:out value="${bibtexHandlerBean.month}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.month}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="dayR">
  <td class="expl">day</td>
  <td>
    <input type="text" name="day" value='<c:out value="${bibtexHandlerBean.day}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.day}"/></div>    
  </td>
  <td></td>
</tr>


<tr id="editionR">
  <td class="expl">edition</td>
  <td>
    <input type="text" name="edition" value='<c:out value="${bibtexHandlerBean.edition}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.edition}"/></div>    
  </td>
  <td class="expl_s">edition of a book, usually written in full as "Second"</td>
</tr>


<tr id="chapterR">
  <td class="expl">chapter</td>
  <td>
    <input type="text" name="chapter" value='<c:out value="${bibtexHandlerBean.chapter}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.chapter}"/></div>    
  </td>
  <td class="expl_s">chapter or section number</td>
</tr>


<tr>
  <td class="expl">url</td>
  <td>
    <input type="text" name="url" value='<c:out value="${bibtexHandlerBean.url}"/>'>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.url}"/></div>    
  </td>
  <td></td>
</tr>


<tr id="keyR">
  <td class="expl">key</td>
  <td>
    <input type="text" name="key" value='<c:out value="${bibtexHandlerBean.key}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.key}"/></div>    
  </td>
  <td class="expl_s">used by BibTeX for sorting</td>
</tr>

<tr id="typeR">
  <td class="expl">type</td>
  <td>
    <input type="text" name="type" value='<c:out value="${bibtexHandlerBean.type}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.type}"/></div>    
  </td>
  <td class="expl_s">type of technical report, e.g. "research report"</td>
</tr>


<tr id="annoteR">
  <td class="expl">annote</td>
  <td>
    <textarea name="annote" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.annote}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.annote}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="noteR">
  <td class="expl">note</td>
  <td>
    <textarea name="note" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.note}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.note}"/></div>    
  </td>
  <td class="expl_s">additional information which could help the reader</td>
</tr>



<tr id="howpublishedR">
  <td class="expl">howpublished</td>
  <td>
    <input type="text" name="howpublished" value='<c:out value="${bibtexHandlerBean.howpublished}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.howpublished}"/></div>    
  </td>
  <td class="expl_s">anything unusual about the method of publishing, e.g. "privately published"</td>
</tr>

<tr id="institutionR">
  <td class="expl">institution</td>
  <td>
    <input type="text" name="institution" value='<c:out value="${bibtexHandlerBean.institution}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.institution}"/></div>    
  </td>
  <td class="expl_s">name of the sponsoring institution for a technical report</td>
</tr>

<tr id="organizationR">
  <td class="expl">organization</td>
  <td>
    <input type="text" name="organization" value='<c:out value="${bibtexHandlerBean.organization}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.organization}"/></div>    
  </td>
  <td class="expl_s">sponsoring organization for a conference or manual</td>
</tr>


<tr id="schoolR">
  <td class="expl">school</td>
  <td>
    <input type="text" name="school" value='<c:out value="${bibtexHandlerBean.school}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.school}"/></div>    
  </td>
  <td class="expl_s">name of the academic institution where a thesis was written</td>
</tr>

<tr id="seriesR">
  <td class="expl">series</td>
  <td>
    <input type="text" name="series" value='<c:out value="${bibtexHandlerBean.series}"/>'/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.series}"/></div>    
  </td>
  <td class="expl_s">name of a series or a set of books</td>
</tr>

<tr id="crossrefR">
  <td class="expl">crossref</td>
  <td>
    <input type="text" name="crossref" value='<c:out value="${bibtexHandlerBean.crossref}"/>' style="width: 10em;"/>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.crossref}"/></div>    
  </td>
  <td></td>
</tr>

<tr>
  <td class="expl">misc</td>
  <td>
    <textarea name="misc" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.misc}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.misc}"/></div>    
  </td>
  <td class="expl_s">this field can be used to import nonstandard fields in addition to the predefined ones. please use BibTeX compliant syntax.</td>
</tr>

<tr>
  <td class="expl">abstract</td>
  <td>
    <textarea name="bibtexAbstract" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.bibtexAbstract}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.bibtexAbstract}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="miscR">
  <td class="expl">private note</td>
  <td>
    <textarea name="privnote" rows="3" onkeyup="sz(this);"><c:out value="${bibtexHandlerBean.privnote}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.privnote}"/></div>    
  </td>
  <td class="expl_s">here you can enter a private note which is not visible to other users</td>
</tr>

<tr>
  <td>
    <input type="submit" value="post_bibtex" onclick="clear_tags()"/>
    <input type="hidden" name="requTask" value="upload"/>  
    <input type="hidden" name="scraperid" value="${bibtexHandlerBean.scraperid}"/>
  </td>
  <td></td>
  <td></td>
</tr>
</table>
</form>

<script type="text/javascript">	
	var hide = true;
	
	var fields = new Array(	"booktitle","journal","volume","number","pages",
							"publisher","address","month","day","edition",
							"chapter","key","type","annote","note",
							"howpublished","institution","organization",
							"school","series","crossref","misc");
	
	/* returns required and optional fields for given publication type*/ 
	function getRequiredFieldsForType(type) {
		switch(type) {
			case "article":	
				return new Array("journal","volume","number","pages","month","note"); break;
			case "book": 
				return new Array("booktitle","publisher","volume","number","series","address","edition","month","note"); break;
			case "booklet": 
				return new Array("howpublished","address","month","note"); break;
			case "inbook": 
				return new Array("chapter","pages","publisher","volume","number","series","type","address","edition","month","note"); break;
			case "incollection": 
				return new Array("publisher","booktitle","volume","number","series","type","chapter","pages","address","edition","month","note"); break;
			case "inproceedings": 
				return new Array("publisher","booktitle","volume","number","series","pages","address","month","organization","note"); break;
			case "manual": 
				return new Array("organization","address","edition","month","note"); break;
			case "mastersthesis": 
				return new Array("school","type","address","month","note"); break;
			case "misc": 
				return new Array("howpublished","month","note"); break;
			case "phdthesis": 
				return new Array("school","address","type","month","note"); break;
			case "proceedings": 
				return new Array("publisher","volume","number","series","address","month","organization","note"); break;
			case "techreport": 
				return new Array("institution","number","type","address","month","note"); break;
			case "unpublished": 
				return new Array("month","note"); break;
			default:		
				return fields; break;
		}
	}	
	
	/* update view when user selects another type of publication in list */
	function changeView() {		
		if (hide == false)
			return;
		
		var type = document.getElementsByName('entrytype')[0].value;
		var required = getRequiredFieldsForType(type);
		
		for (i=0; i<fields.length; i++) {			
			if (in_array(required,fields[i])) {
				showElement(fields[i]);
			} else {
				hideElement(fields[i]);
			}
		}			
	}	
	
	/* toggle to show elements */
	function showAll() {
		hide = false;
		document.getElementById('collapse').firstChild.nodeValue = 'hide not required fields';
		document.getElementById('collapse').href = 'javascript:hideElements();';
		for (i=0; i<fields.length; i++) {
			showElement(fields[i]);			
		}		
	}
	
	/* toggle to hide elements */
	function hideElements() {
		hide = true;
		document.getElementById('collapse').firstChild.nodeValue = 'show all fields';
		document.getElementById('collapse').href = 'javascript:showAll();';
		changeView();
	}	
	
	/* hide element (row) with given id */
	function hideElement(id) {			
		var row 	= document.getElementById(id + 'R');			
		var field	= document.getElementsByName(id)[0];		
		
		if (field.value == '') {
			row.style.display 		= 'none';						
		}
	}
	
	/* show element (row) with given id */
	function showElement(id) {		
		var row 	= document.getElementById(id + 'R');
		var field	= document.getElementsByName(id)[0];
		
		if (field.value == '') {
			row.style.display 		= '';
		}		
	}	
	
	/* checks if element is member of given array */
    function in_array(array, element) {    	
    	for(var j = 0; j < array.length; j++) {
      		if(array[j] == element) {
        		return true;
      		}
    	}
    	return false;
  	}  
  	
  	changeView();
</script>

</div><%--closed general box --%>

<div id="sidebarroundcorner" >
<ul id="sidebar">
  <c:set var="markSuperTags" value="false"/>
  <%@include file="/boxes/tags/userstags.jsp"%>
</ul>
</div>
<script type="text/javascript">
   $("#sidebarroundcorner").corner("round bottom 15px").corner("round tl 15px");
</script>

<%@ include file="/boxes/copytag.jsp" %>

<%@ include file="footer.jsp" %>