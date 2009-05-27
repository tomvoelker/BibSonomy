<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:form="http://www.springframework.org/tags/form"
    xmlns:spring="http://www.springframework.org/tags"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:post="urn:jsptagdir:/WEB-INF/tags/post"
>
	
	<jsp:directive.page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />


	<layout:layout pageTitle="post a publication">
		<jsp:attribute name="heading">
			
		</jsp:attribute>	
    
		<jsp:attribute name="content">
    
          <div id="general">

            <form:form id="postPublicationForm" action="/postPublication" method="post">
              <input type="hidden" name="postID" value="${command.postID}"/>
<!--              <input type="hidden" value="${command.context.ckey}" name="ckey"/>-->
            
<table>
    
<tr>
  <td><form:label path="post.description">description, comment</form:label></td>
  <td><form:textarea path="post.description" rows="3"/></td>
  <td></td>       
</tr>

<tr>
  <td colspan="3">
    <post:groupBox post="${command.post}" groups="${loginUser.groups}"/>
  </td>   
</tr>


<tr>
  <td colspan="3">
    <post:tagfield containsComma="${command.containsComma}" tags="${command.tags}" recommendedTags="${command.recommendedTags}"/>
  </td>
</tr>


<tr>
  <td>
    <input type="reset"  value="reset"/>
    <input type="submit" value="save"/>
  </td>
  <td class="expl_s"><a id="collapse" href="javascript:showAll()">show all fields</a> </td>
  <td></td>
</tr>

<tr>
  <td>type*</td>
  <td>
    <select name="entrytype" onChange="changeView();">
      <c:forEach var="et" items="${command.entryTypes}">
          <c:choose>
            <c:when test="${command.post.resource.entrytype eq et}">
              <option value="${et}" selected="true">${et}</option>
            </c:when>
            <c:otherwise>
                <option value="${et}">${et}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
    </select>
  </td>
  <td></td>
</tr>

<tr>
  <td>BibTeX key*</td>
  <td>
    <form:input path="post.resource.bibtexKey"/>
  </td>
  <td>a memorable key without spaces</td>
</tr>
<!--

<tr>
  <td class="expl">title*</td>
  <td>
    <textarea class="reqinput" name="title" rows="3" ><c:out value="${bibtexHandlerBean.title}"/></textarea>
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
    <textarea name="annote" rows="3" ><c:out value="${bibtexHandlerBean.annote}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.annote}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="noteR">
  <td class="expl">note</td>
  <td>
    <textarea name="note" rows="3" ><c:out value="${bibtexHandlerBean.note}"/></textarea>
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
    <textarea name="misc" rows="3" ><c:out value="${bibtexHandlerBean.misc}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.misc}"/></div>    
  </td>
  <td class="expl_s">this field can be used to import nonstandard fields in addition to the predefined ones. please use BibTeX compliant syntax.</td>
</tr>

<tr>
  <td class="expl">abstract</td>
  <td>
    <textarea name="bibtexAbstract" rows="3" ><c:out value="${bibtexHandlerBean.bibtexAbstract}"/></textarea>
    <div class="oldentry"><c:out value="${bibtexHandlerBean.oldentry.bibtexAbstract}"/></div>    
  </td>
  <td></td>
</tr>

<tr id="miscR">
  <td class="expl">private note</td>
  <td>
    <textarea name="privnote" rows="3" ><c:out value="${bibtexHandlerBean.privnote}"/></textarea>
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
<tr style="line-height: 0px; visibility: hidden;">
  <td>&nbsp;</td>
  <td>_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ </td>
  <td>&nbsp;</td>
</tr>
</table>
</form>
            
              <form:form id="postPublicationForm" action="/postPublication" method="post">
                  
                
  
<c:if test="${not empty loginUser.groups}">
<post:relevantForBox groups="${loginUser.groups}"/>
</c:if>
  
-->
</table>
                  

              </form:form>
    
          </div>
            
		</jsp:attribute>				
	</layout:layout>

</jsp:root>