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
    
     
    
    
    
          <style type="text/css">
            textarea, input[type=text] {
              width: 100%;
              min-width: 30em;
              margin-right: 1em;
            }
            label {
              padding-right: 1em;
              width: 20%;
            }
            textarea {
              height: 4em;
            }
            td {
              padding-right: 1em;
            }
          </style>
    
    
          <div id="general">


            <h2>post a publication</h2>


            <form:form method="post" id="postPublicationForm">
              <input type="hidden" name="postID" value="${command.postID}"/>

              <fieldset>
                <legend>post metadata</legend>
                
                  <table>
                    <tr>
                      <td><form:label path="post.user.name">username*</form:label></td>
                      <td><form:input path="post.user.name"/></td>
                      <td>the name of the posting user</td>
                    </tr>
                    <tr>
                      <td><form:label path="post.description">description, comment</form:label></td>
                      <td><form:textarea path="post.description"/></td>
                      <td></td>
                    </tr>
                  </table>

  <post:groupBox post="${command.post}" groups="${loginUser.groups}"/>

  <post:tagfield containsComma="${command.containsComma}" tags="${command.tags}" recommendedTags="${command.recommendedTags}"/>

  <fieldset>
    <legend>actions</legend>
    <input type="reset"  value="reset"/>
    <input type="submit" value="save"/>
  </fieldset>
             </fieldset>
             
             <fieldset>
               <legend>publication metadata</legend>

  <fieldset>
    <legend>mandatory</legend>

    <table>
      <tr>
        <td><form:label path="post.resource.entrytype">type*</form:label></td>
        <td><form:select multiple="false" path="post.resource.entrytype">
              <form:options items="${command.entryTypes}"/>
            </form:select>
        </td>
        <td></td>
      </tr>
      <tr>
        <td><form:label path="post.resource.bibtexKey">BibTeX key*</form:label></td>
        <td><form:input path="post.resource.bibtexKey"/></td>
        <td>a memorable key without spaces</td>
      </tr>
      <tr>
        <td><form:label path="post.resource.title">title*</form:label></td>
        <td><form:textarea path="post.resource.title" /></td>
        <td></td>
      </tr>
      <tr>
        <td><form:label path="post.resource.author">authors*</form:label></td>
        <td><form:textarea path="post.resource.author"/></td>
        <td>each author on a separate line, format "Firstname Lastname"</td>
      </tr>
      <tr>

        <td><form:label path="post.resource.editor">editors*</form:label></td>
        <td><form:textarea path="post.resource.editor"/></td>
        <td>each editor on a separate line, format "Firstname Lastname"</td>
      </tr>
      <tr>
        <td><form:label path="post.resource.year">year*</form:label></td>
        <td><form:input path="post.resource.year"/></td>
        <td></td>        
      </tr>
    </table>
  </fieldset>

  <fieldset>
    <legend>optional</legend>
<table>

<tr>
  
</tr>



<tr>
  <td>booktitle</td>
  <td>
    <form:input path="post.resource.booktitle"/>
  </td>
  <td>title of a book when only part is cited</td>
</tr>



<tr>
  <td>journal</td>
  <td>
    <form:input path="post.resource.journal"/>
  </td>
  <td>full (unabbreviated) journal title</td>
</tr>




<tr>
  <td>volume</td>
  <td>
    <form:input path="post.resource.volume"/>
  </td>
  <td></td>
</tr>


<tr>
  <td>number</td>
  <td>
    <form:input path="post.resource.number"/>
  </td>
  <td>number of journal, magazine, technical report, or work in a series</td>
</tr>

<tr>
  <td>pages</td>
  <td>
    <form:input path="post.resource.pages"/>
  </td>
  <td></td>
</tr>


<tr>
  <td>publisher</td>
  <td>
    <form:input path="post.resource.publisher"/>
  </td>
  <td></td>
</tr>

<tr>
  <td>address</td>
  <td>
    <form:input path="post.resource.address"/>
  </td>
  <td>address of the publisher or institution</td>
</tr>


<tr>
  <td>month</td>
  <td>
    <form:input path="post.resource.month"/>
  </td>
  <td></td>
</tr>

<tr>
  <td>day</td>
  <td>
    <form:input path="post.resource.day"/>
  </td>
  <td></td>
</tr>


<tr>
  <td>edition</td>
  <td>
    <form:input path="post.resource.edition"/>
  </td>
  <td>edition of a book, usually written in full as "Second"</td>
</tr>


<tr>
  <td>chapter</td>
  <td>
    <form:input path="post.resource.chapter"/>
  </td>
  <td>chapter or section number</td>
</tr>


<tr>
  <td>url</td>
  <td>
    <form:input path="post.resource.url"/>
  </td>
  <td></td>
</tr>


<tr>
  <td>key</td>
  <td>
    <form:input path="post.resource.BKey"/>
  </td>
  <td>used by BibTeX for sorting</td>
</tr>

<tr>
  <td>type</td>
  <td>
    <form:input path="post.resource.type"/>
  </td>
  <td>type of technical report, e.g. "research report"</td>
</tr>


<tr>
  <td>annote</td>
  <td>
    <form:textarea path="post.resource.annote"/>
  </td>
  <td></td>
</tr>

<tr>
  <td>note</td>
  <td>
    <form:textarea path="post.resource.note"/>
  </td>
  <td>additional information which could help the reader</td>
</tr>



<tr>
  <td>howpublished</td>
  <td>
    <form:input path="post.resource.howpublished" />
  </td>
  <td>anything unusual about the method of publishing, e.g. "privately published"</td>
</tr>

<tr>
  <td>institution</td>
  <td>
    <form:input path="post.resource.institution"/>
  </td>
  <td>name of the sponsoring institution for a technical report</td>
</tr>

<tr>
  <td>organization</td>
  <td>
    <form:input path="post.resource.organization"/>
  </td>
  <td>sponsoring organization for a conference or manual</td>
</tr>


<tr>
  <td>school</td>
  <td>
    <form:input path="post.resource.school"/>
  </td>
  <td>name of the academic institution where a thesis was written</td>
</tr>

<tr>
  <td>series</td>
  <td>
    <form:input path="post.resource.series"/>
  </td>
  <td>name of a series or a set of books</td>
</tr>

<tr>
  <td>crossref</td>
  <td>
    <form:input path="post.resource.crossref"/>
  </td>
  <td></td>
</tr>

<tr>
  <td>misc</td>
  <td>
    <form:textarea path="post.resource.misc"/>
  </td>
  <td>this field can be used to import nonstandard fields in addition to the predefined ones. please use BibTeX compliant syntax.</td>
</tr>

<tr>
  <td>abstract</td>
  <td>
    <form:textarea path="post.resource.abstract"/>
  </td>
  <td></td>
</tr>

<tr>
  <td>private note</td>
  <td>
    <form:textarea path="post.resource.privnote"/>
  </td>
  <td>here you can enter a private note which is not visible to other users</td>
</tr>

<!--   
<c:if test="${not empty loginUser.groups}">
<post:relevantForBox groups="${loginUser.groups}"/>
</c:if>
  
-->
</table>


</fieldset>
</fieldset>

              </form:form>
    
          </div>
    
    
    
    
    
    
    
    
    
     <script type="text/javascript">
        <![CDATA[
         function clearTagField() {
  var sg = document.getElementById("tagField");
  while(sg.hasChildNodes()) 
    sg.removeChild(sg.firstChild);
}
   
    
         function handleRecommendedTags(msg) {
            var tagSuggestions = [];
            var target = 'tagField';

            // lookup and clear target node
            var tagField = document.getElementById(target) 
            clearTagField();
            
            // lookup tags
            var root = msg.getElementsByTagName('tags').item(0);
            if( root == null ) {
              // FIXME: DEBUG
              alert("Invalid Ajax response: <tags/> not found.");
            }
            
            // append each tag to target field
            for (var iNode = 0; iNode < root.childNodes.length; iNode++) {
              var node = root.childNodes.item(iNode);
              // work around firefox' phantom nodes
              if( (node.nodeType == 1) && (node.tagName == 'tag') ) {
                // collect tag informations
                var tagName       = node.getAttribute('name');
                var tagScore      = node.getAttribute('score');
                var tagConfidence = node.getAttribute('confidence');
                
                // create link element from tag
                var newTag = document.createElement('a');
                var newText= document.createTextNode(tagName + " ");
                newTag.setAttribute('href', "javascript:copytag('inpf', '"
                              +node.getAttribute('name')
                              +"')");
                newTag.appendChild(newText);
                tagField.appendChild(newTag);
                
                // append tag to suggestion list
                var suggestion = new Object;
                suggestion.label      = tagName;
                suggestion.score      = tagScore;
                suggestion.confidence = tagConfidence;
                tagSuggestions.push(suggestion);
              }
            }

            // enable reload button
            var link = document.getElementById("fsReloadLink");
                      var button = document.getElementById("fsReloadButton");
                  link.setAttribute("href","javascript:reloadRecommendation()");
                  button.setAttribute("src","http://www.bibsonomy.org/resources/image/button_reload.png");
          }

    /* setup jQuery to update recommender with form data */
    var options = { 
              url:  'ajax/getPublicationRecommendedTags', 
              success:       showResponse 
          }; 
      $('#postPublicationForm').ajaxSubmit(options); 

    function showResponse(responseText, statusText)  { 
      handleRecommendedTags(responseText);
    } 

     function reloadRecommendation() {
          var link = document.getElementById("fsReloadLink");
          var button = document.getElementById("fsReloadButton");
          link.setAttribute("href","#");
          button.setAttribute("src","http://www.bibsonomy.org/resources/image/button_reload-inactive.png");

          var options = { 
                  url:  'ajax/getPublicationRecommendedTags', 
                  success:       showResponse 
              }; 
          $('#postPublicationForm').ajaxSubmit(options); 
      }

          
        ]]>
      </script> 
    
    
    
    
    
            
		</jsp:attribute>				
	</layout:layout>

</jsp:root>