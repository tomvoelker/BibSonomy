<?xml version="1.0" ?>
<jsp:root version="2.0"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"
	xmlns:form="http://www.springframework.org/tags/form"
	xmlns:post="urn:jsptagdir:/WEB-INF/tags/post"
	xmlns:errors="urn:jsptagdir:/WEB-INF/tags/errors"
	xmlns:layout="urn:jsptagdir:/WEB-INF/tags"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:mtl="urn:jsptld:/WEB-INF/taglibs/mytaglib.tld"
>
	
	<jsp:directive.page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" session="true" />
	
	
		
		
	<layout:layout pageTitle="Recommender Test Page">
		<!--+
    	 	| html header extension: page specific javascript
    	 	+-->
		<jsp:attribute name="headerExt">
			<link rel="stylesheet" type="text/css" href="http://www.bibsonomy.org/resources/css/fsform.css" />
			<script type="text/javascript" src="http://www.bibsonomy.org/resources/javascript/recommender.js">&amp;nbsp;</script>
			<script type="text/javascript" src="http://www.bibsonomy.org/resources/javascript/functions.js">&amp;nbsp;</script>
			
			<style type="text/css">
				.postBookmarkNotValidTip {
					visibility: hidden;
				}
			</style> 
	    </jsp:attribute>
	    
		<jsp:attribute name="heading">
			<fmt:message key="error"/>
		</jsp:attribute>	
    
		<jsp:attribute name="content">

		
	<script type="text/javascript">
              //<mtl:cdata>
				<![CDATA[
					function setSuggestionFromUrlDescription(tagname){
						document.getElementById('post.description').value=tagname;
						document.getElementById('suggestion.description').innerHTML = "";
					}
			
					function setSuggestionFromUrlTitle(tagname){
						document.getElementById('post.resource.title').value=tagname;
						document.getElementById( "suggestion.title" ).innerHTML ="";
					}

					/**
					 * Ajax callback function for inserting recommended tags:
					 *    1) insert tags to form's recommendations field
					 *    2) append recommended tags to potential suggestions
					 *    3) enable recommendation reload button
					 */
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

						// add recommended tags to suggestions
						// populateSuggestionsFromRecommendations(tagSuggestions);

						// enable reload button
						var link = document.getElementById("fsReloadLink");
	                    var button = document.getElementById("fsReloadButton");
			            link.setAttribute("href","javascript:reloadRecommendation()");
			  	        button.setAttribute("src","http://www.bibsonomy.org/resources/image/button_reload.png");
					}
          
                    /**
                     * handler for the 'reload recommendations button'
                     * FIXME: all js-functions for recommendations should be located in a single file.
                     */
                      function reloadRecommendation() {
                          var link = document.getElementById("fsReloadLink");
                          var button = document.getElementById("fsReloadButton");
                          link.setAttribute("href","#");
                          button.setAttribute("src","http://www.bibsonomy.org/resources/image/button_reload-inactive.png");
                
                          clearTagField();      
                
                          $('#postBookmarkForm').ajaxSubmit({url:'ajax/getBookmarkRecommendedTags', success: showResponse}); 
                      }
          
				]]>
              //</mtl:cdata>
		</script>
		

    
          <div id="general">

     			<div style="float left;width:97%;margin-left:10px; padding-bottom: 13px;">
						<h2 style="text-align: center; margin-bottom: 0"><fmt:message key="post.resource.editbookmark"/></h2>
                        
                        <c:if test="${not empty command.diffPost}">
                          <p>
                          	<fmt:message key="post.bookmark.edit.existent" />
                          </p>
                        </c:if>

                      
                	<form:form id="postBookmarkForm" action="postBookmark" method="post">
	                	<div id="fsform">
	                		<fieldset class="fsOuter">
								<fieldset class="fsInner">
									<legend><fmt:message key="general"/></legend>
									<form:label cssClass="fsLabel" path="post.resource.url" ><fmt:message key="post.resource.url"/>*</form:label>
									<form:input cssClass="fsInput" path="post.resource.url" tabindex="1"/>
									<div class="postBookmarkNotValidTip"><form:errors path="post.resource.url" /></div>

									<form:label cssClass="fsLabel" path="post.resource.title" ><fmt:message key="post.resource.title"/>*</form:label>
									<form:input cssClass="fsInput" path="post.resource.title" tabindex="2"/>
									<div class="postBookmarkNotValidTip"><form:errors path="post.resource.title" /></div>
	                                <div class="fsSuggestion" id="suggestion.title"><!-- This comment is needed, otherwise this will result in an self-closing element --></div>
	                                
									<form:label cssClass="fsLabel" path="post.description" ><fmt:message key="post.resource.description"/>, <fmt:message key="post.resource.comment"/></form:label>
									<form:textarea cssClass="fsInput" path="post.description" tabindex="3" rows="3" onkeyup="sz(this);" />
									<div class="fsSuggestion" id="suggestion.description"><!-- This comment is needed, otherwise this will result in an self-closing element --></div>
								</fieldset>
								
						    	<post:tagfield containsComma="${command.containsComma}" tags="${command.tags}" recommendedTags="${command.recommendedTags}"/>

                                    <post:groupBox post="${command.post}" groups="${loginUser.groups}"/>
  
									<c:if test="${not empty loginUser.groups}">
										<post:relevantForBox groups="${loginUser.groups}"/>
									</c:if>	
	
									<div class="clearfloat p">
										<post:tagSets groups="${loginUser.groups}"/>
									</div>
	
	
									<div class="clearfloat p">
										<c:set var="save"><fmt:message key="save"/></c:set>
		                                <c:set var="reset"><fmt:message key="resetbutton"/></c:set>
		                                <input type="hidden" name="postID" value="${command.postID}"/>
                                        <input type="hidden" name="jump" value="${fn:escapeXml(command.jump)}"/>
		                                <form:hidden path="intraHashToUpdate"/>
										<input type="reset"  tabindex="5" value="${reset}" />
										<!-- <input type="submit" tabindex="6" value="${save}" onclick="clear_tags();"/> -->
									</div>
	
							</fieldset>
						</div>
					</form:form>

         
                    <div id="scrapable" style="display:none">
                      <br/>
                      <form:form action="/BibtexHandler" method="post" id="bibtex_snippet">      
                          <fieldset style="border: 1px solid #069;background-color: #eee; position:relative;">
                              <fieldset class="fsnoborder" style="position:relative;">
                                <h2><fmt:message key="post.meta.recommended"/>: <fmt:message key="post.meta.save_as_publication"/></h2>
                                <p>
                                  <fmt:message key="post.resource.bookmark_a_publication"/>:<c:out value=" "/>
                                  <fmt:message key="post.meta.save_as_publication" var="post_publication"/>
                                  <input id="button" type="submit" name="submit" value="${post_publication}"/>
                                </p>
                                <textarea style="width: 100%;" id="bib" name="selection" rows="15" class="reqinput"><c:out value=" "/></textarea>
                                <input type="hidden" name="requTask" value="upload" />
                                
                              </fieldset>
                          </fieldset>   
                      </form:form>
                    </div>
                    
				</div>
  
          </div>

        <script type="text/javascript">
		    <![CDATA[
				                
		      /* setup jQuery to update recommender with form data */
		      var options = { 
			            url:  'ajax/getBookmarkRecommendedTags', 
			            success:       showResponse, 
			        }; 
		      $('#postBookmarkForm').ajaxSubmit(options); 
		
			  /* jQuery's response containing recommended tags */
			  function showResponse(responseText, statusText)  { 
			  	handleRecommendedTags(responseText);
			  } 
		      
		    ]]> 
		  </script>            
		</jsp:attribute>				
	</layout:layout>

</jsp:root>