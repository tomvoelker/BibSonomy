<%@include file="/include_jsp_head.jsp" %>

<%-- forward to login site if not logged in --%>
<c:if test="${empty user.name}">
	<jsp:forward page="/login" />
</c:if>

<%-- include Bean --%>
<jsp:useBean id="AdvancedSearchBean" class="beans.AdvancedSearchBean" scope="request">
	<jsp:setProperty name="AdvancedSearchBean" property="user" value="${user.name}" />
</jsp:useBean>

<%-- include HTML header --%>
<jsp:include page="/html_header.jsp">
  <jsp:param name="title" value="advanced search" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path">
  <a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="#" >advanced search&nbsp;<img src="/resources/image/box_arrow.png"></a>
</h1>

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%@include file="/boxes/navi.jsp" %>    <%-------------------------- Navigation -----------------------%>

<div id="searchbox">
	<h2>Search myBibSonomy</h2>
	
	<form name="searchform" onsubmit="checkInput()">
	<fieldset style="border: 1px ridge #069;background-color: #eee;">
	<legend>search options</legend>		
		<table border="0">
			<tr>
				<td>tags</td>
				<td>authors</td>
			</tr>
			<tr>
				<td>	
					<select name="tags" size="10" onchange="clearFilter(); updateAuthorsForTags(); updateTitles(); showTitleList();" multiple></select>	
				</td>
				<td>
					<select name="authors" size="10" onchange="clearFilter(); updateTitles(); showTitleList();" multiple></select>		
				</td>
			</tr>
			<tr>
				<td>
					<input type="radio" name="tag_op" value="and"/>and
					<input type="radio" name="tag_op" value="or" checked />or 
				</td>
				<td>
					<input type="radio" name="author_op" value="and"/>and
					<input type="radio" name="author_op" value="or" checked />or 		
				</td>
			</tr>
		</table>
	</fieldset>
	<br>
	
	<fieldset style="border: 1px solid #069;background-color: #eee;">
	<legend>search results</legend>			
		filter: <input value="<enter title, authors or tags>" name="filter" type="text" size="40" onKeyUp="showTitleList();"
						onfocus="if (this.value=='<enter title, authors or tags>') {this.value = '';this.style.color='black';}"
						onblur="if (this.value == '') {this.value = '<enter title, authors or tags>';this.style.color='gray';}" 
						style="color: gray;" /> 						
		<p>		
			<div id='numresult' style="text-align:right; font-size:70%; padding-bottom:2px; color:gray;">&nbsp;</div>	
			<div class="scroller">
				<table class="result_table"><tbody id="resultlist"></tbody></table>
			</div>			
		</p>						
	</fieldset>		
	</form>	
</div>

<script language="javascript">
	/**
	* data arrays of authors, tags and titles
	**/	
	var authors = new Array(<c:forEach var='author' items='${AdvancedSearchBean.authors}' varStatus="loopStatus">
								"<c:out value='${author}'/>"<c:if test="${not loopStatus.last}">,</c:if>
							</c:forEach>);
							
	var tags = new Array(<c:forEach var='tag' items='${AdvancedSearchBean.tags}' varStatus="loopStatus">
								"<c:out value='${tag}'/>"<c:if test="${not loopStatus.last}">,</c:if>
						</c:forEach>);
	
	var titles = new Array(<c:forEach var='title' items='${AdvancedSearchBean.titles}' varStatus="loopStatus">
								"<mtl:bibclean value='${title}'/>"<c:if test="${not loopStatus.last}">,</c:if>
							</c:forEach>);
							
	/**
	* relation arrays for relations between authors, tags and titles
	**/	
	var tagTitle 		= <c:out value='${AdvancedSearchBean.tagTitles}'/>;
	var authorTitle 	= <c:out value='${AdvancedSearchBean.authorTitles}'/>;	
	var tagAuthor 		= <c:out value='${AdvancedSearchBean.tagAuthors}'/>;	
	var titleAuthor 	= <c:out value='${AdvancedSearchBean.titleAuthors}'/>;
	
	/**
	* array containing bibtex hashes of each publication
	**/
	var contentIds = new Array(<c:forEach var='id' items='${AdvancedSearchBean.bibtexHash}' varStatus="loopStatus">
								"<c:out value='${id}'/>"<c:if test="${not loopStatus.last}">,</c:if>
							</c:forEach>);	
							
	var titleUrls = new Array(<c:forEach var='id' items='${AdvancedSearchBean.bibtexUrls}' varStatus="loopStatus">
								"<c:out value='${id}'/>"<c:if test="${not loopStatus.last}">,</c:if>
							</c:forEach>);				
	var titleTag 	= new Array();
	var titlesIdx	= new Array();
	
	// a string of authors separated by comma
	var titleAuthorStringArray = new Array();	
	
	for (var i=0;i<titles.length;i++) {
		titleTag[i] = "";
	}
	
	for (var i=0;i<tagTitle.length;i++) {
		var lst = tagTitle[i];
		for (var j=0;j<lst.length;j++) {
			titleTag[lst[j]] += tags[i] + " ";
		}
	}	
	
	/**
	* updates authorlist for selected tag in taglist
	**/
	function updateAuthorsForTags() {
		var authorsIdx 	= new Array()
		var tagList		= document.searchform.tags;
		var authorList 	= document.searchform.authors;		
		
		// no tag selected = all tags
		if (tagList.selectedIndex <= 0) {
			for (var i = 0; i<authors.length; i++) {
				authorsIdx[authorsIdx.length] = i;
			}		
		} else {
			var options = tagList.options;
			var found 	= false;
			
			for (var i=1; i<options.length; i++) {
				if (options[i].selected) {
					if (!found) {						
						authorsIdx = tagAuthor[options[i].value];
						found = true;
					} else {													
						if (getRadioValue("tag_op") == "and")
							authorsIdx = intersect(authorsIdx,tagAuthor[options[i].value]);
						else
							authorsIdx = union(authorsIdx,tagAuthor[options[i].value]);
					}
				}
			}							
		}	
		
		// fill list with authors	
		authorsIdx = authorsIdx.sort(comparator);	
		authorList.length = 0;		
		authorList.options[0] = new Option("- all authors -",-1);
		
		for (var i=0; i<authorsIdx.length; i++) {
			authorList.options[i+1] = new Option(authors[authorsIdx[i]],authorsIdx[i]);		
		}		
		authorList.selectedIndex = 0;	
	}	
	
	/**
	* updates the title list 
	**/
	function updateTitles() {		
		var tagsTitles		= new Array();
		var authorsTitles 	= new Array();
		
		var tagList		= document.searchform.tags;
		var authorList 	= document.searchform.authors;			
		
		// no tags and authors selected --> all titles
		if (tagList.selectedIndex <= 0 && authorList.selectedIndex <= 0) {
			titlesIdx.length = 0;
			for (var i=0; i<titles.length; i++) {
				titlesIdx[titlesIdx.length] = i;
			}			
		} else {
			// titles from tags	
			if (tagList.selectedIndex <= 0) {
				for (var i=0; i<titles.length; i++) {
					tagsTitles[tagsTitles.length] = i;
				}
			} else {
				var options = tagList.options;
				var found = false;				
				for (var i=1; i<options.length; i++) {
					if (options[i].selected) {
						if (!found) {						
							tagsTitles = tagTitle[options[i].value];
							found = true;
						} else {													
							if (getRadioValue("tag_op") == "and")
								tagsTitles = intersect(tagsTitles,tagTitle[options[i].value]);
							else
								tagsTitles = union(tagsTitles,tagTitle[options[i].value]);
						}
					}
				}
			}
			
			// titles from authors
			if (authorList.selectedIndex <= 0) {
				for (var i=0; i<titles.length; i++) {
					authorsTitles[authorsTitles.length] = i;
				}
			} else {
				var options = authorList.options;
				var found = false;			
				for (var i=1; i<options.length; i++) {
					if (options[i].selected) {
						if (!found) {						
							authorsTitles = authorTitle[options[i].value];
							found = true;
						} else {													
							if (getRadioValue("author_op") == "and")
								authorsTitles = intersect(authorsTitles,authorTitle[options[i].value]);
							else
								authorsTitles = union(authorsTitles,authorTitle[options[i].value]);
						}
					}
				}
			}
			
			// intersection of both sets
			titlesIdx = intersect(tagsTitles,authorsTitles);			
		}			
		titlesIdx = titlesIdx.sort(comparator);	
	}			
		
	/**
	* shows titles in the list
	**/	
	function showTitleList() {		
		removeAllChildren('resultlist');
		
		var results = 0;	
		var k=0;
		for (var i=0;i<titlesIdx.length;i++) {
			var t = titles[titlesIdx[i]];
			var opt;
			var auth = titleAuthorStringArray[titlesIdx[i]];
			var my_tags = titleTag[titlesIdx[i]];
			if (filter(t + " " + auth + " " + my_tags)) {				
				appendRow(trim(t, 90), trim(auth, 80), contentIds[titlesIdx[i]], titleUrls[titlesIdx[i]]);
				k++;
				results++;
			}
		}		
		document.getElementById("numresult").innerHTML = results + " results";		
	}	
	
	/**
	* checks the form before submitting
	**/		
	function checkForm(bibtexhash) {
		var url = "/bibtex/<%=Bibtex.INTRA_HASH %>" + bibtexhash + "/<c:out value='${user.name}'/>";	
		document.location.href = url;	
		return false;
	}
	
	/**
	* initialize the option boxes
	**/
	function initBoxes() {
		// creates array of authors seperated by "," for each publication
		createTitleAuthorStringArray();
		
		var taglist 	= document.searchform.tags;
		var authorlist 	= document.searchform.authors;
		var resultlist 	= document.searchform.results;
		
		taglist.options[0] 		= new Option("- all tags -",-1);
		authorlist.options[0] 	= new Option("- all authors -",-1);		
		
		// taglist
		for (var i=0; i<tags.length; i++) {
			var tag = new Option(tags[i],i);
			taglist.options[i+1] = tag;
		}
		
		// authorlist
		for (var i=0; i<authors.length; i++) {
			var author = new Option(authors[i],i);
			authorlist.options[i+1] = author;
		}
		
		// titlelist
		for (var i=0; i<titles.length; i++) {
			appendRow(trim(titles[i], 90), trim(titleAuthorStringArray[i], 80), contentIds[i], titleUrls[i]);
			titlesIdx[titlesIdx.length] = i;
		}
		
		// default select		
		taglist.selectedIndex 	 = 0;
		authorlist.selectedIndex = 0;	
		
		// update number of search results
		document.getElementById("numresult").innerHTML = titles.length + " results";		
	}
	
		
	/******************************************************
	* some useful functions
	*******************************************************/
	
	/**
	* removes all children from given node
	**/
	function removeAllChildren(nodename) {
		var node = document.getElementById(nodename);

		if (node.hasChildNodes()) {
		    while (node.childNodes.length >= 1) {
		        node.removeChild( node.firstChild );       
		    } 
		}
	}
	
	/**
	* appends a row in search result list with given information 
	**/
	function appendRow(title, authors, bibtexhash, url) {		
		var table = document.getElementById("resultlist");
		var spacer1 = document.createTextNode(" | ");	
		var spacer2 = document.createTextNode(" | ");
		var tr = document.createElement("tr");
		
		var titletd = document.createElement("td");			
		titletd.onclick = function() {checkForm(bibtexhash);}
		
		var div1 = document.createElement("div");	
		div1.style.fontSize = "90%";		
		
		var span1 = document.createElement("span");	
		span1.style.fontSize = "80%";
		
		var linktd = document.createElement("td");	
		linktd.style.borderBottom = "1px solid #BBBBBB";
		linktd.style.textAlign = "right";		
		linktd.style.fontSize = "70%";	
				
		var link = document.createElement("a");	
		link.href = "/bib/bibtex/<%=Bibtex.INTRA_HASH %>" + bibtexhash + "/<c:out value='${user.name}'/>";
		link.innerHTML = "BibTeX";

		var pick = document.createElement("a");
		pick.onclick = pickUnpickPublication;
		pick.href = "/Collector?pick=" + bibtexhash + "&user=<c:out value='${user.name}'/>&ckey=<c:out value='${ckey}'/>";
		pick.innerHTML = "pick";
		
		if (url != "") {
			var urllink = document.createElement("a");
			urllink.href = url;		
			urllink.innerHTML = "URL";						
		}
				
		div1.innerHTML = title;	
		span1.innerHTML = authors;				
		titletd.appendChild(div1);					
		titletd.appendChild(span1);
					
		linktd.appendChild(link);
		linktd.appendChild(spacer1);		
		linktd.appendChild(pick);
		if (url != "") {
			linktd.appendChild(spacer2);
			linktd.appendChild(urllink);			
		}	
					
		tr.appendChild(titletd);	
		tr.appendChild(linktd);	
				
		table.appendChild(tr);
	}
	
	/**
	* compartor function for array sort
	**/
	function comparator(x,y) {
		return (x-y);
	}
	
	/**
	* returns the unionset of 2 arrays
	**/
	function union(x,y) {
		var result = new Array();
		
		var found = new Array();
		for (var i=0; i<x.length; i++) {
			if (found["a" + x[i]] != 1) {
				result[result.length] = x[i];
				found["a" + x[i]] = 1;			
			}			
		} 			
		for (var i=0; i<y.length; i++) {
			if (found["a" + y[i]] != 1) {
				result[result.length] = y[i];
				found["a" + y[i]] = 1;			
			}			
		} 		
		return result.sort(comparator);	
	}	
	
	/**
	* returns intersection of 2 sets (arrays)
	**/
	function intersect(x,y) {
		var result = new Array();
		
		var found = new Array();
		for (var i=0; i<x.length; i++) {
			for (var j=0; j<y.length; j++) {
				if (x[i] == y[j] && found["a" + x[i]] != 1) {
					result[result.length] = x[i];
					found["a" + x[i]] = 1;
				}
			}
		}		
		return result.sort(comparator);	
	}	
	
	/**	
	* clears the filter input field
	**/
	function clearFilter() {
		var filter_field = document.searchform.filter;
		filter_field.value = "<enter title, authors or tags>";
		filter_field.style.color = "gray";
	}		
	
	/**
	* returns the value of an radiobutton(group) in our form
	**/
	function getRadioValue(radioName) {
		for(i=0;i<document.searchform[radioName].length;i++){
			if (document.searchform[radioName][i].checked == true)
				return document.searchform[radioName][i].value;
		}	
	}
	
	/**
	* trim function for strings
	**/
	function trim(t, len) {
		if(t.length > len) {
			t = t.substring(0,len-2) + "...";
		}
		return t;
	}
	
	/**
	* filters resultset 
	**/ 
	function filter(str) {
		var filterstring = document.searchform.filter.value.toLowerCase();
	
		if (filterstring == "" || filterstring == "<enter title, authors or tags>") {
			return true;
		}
	
		var lower = str.toLowerCase();	
		var a = filterstring.split(" ");
		for (var i=0; i<a.length; i++) {
			if (a[i]!="" && lower.search(a[i]) == -1) {
				return false;
			}
		}
		return true;
	}	
	
	/**
	* creates an array of strings containing authornames separated by comma 
	**/
	function createTitleAuthorStringArray() {
		for (var i=0; i<titleAuthor.length; i++) {
			authorIds = titleAuthor[i];
			
			var temp = new Array();
			for (var j=0; j<authorIds.length; j++) {
				temp[temp.length] = authors[authorIds[j]];
			}
			titleAuthorStringArray[i] = temp.join(", ");			
		}		
	}	

	// initialize the form
	initBoxes();		
</script>

<%@ include file="/footer.jsp" %>