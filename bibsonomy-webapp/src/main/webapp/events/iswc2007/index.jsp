<%@include file="header.jsp" %>
  
<body>


<center>
  <div class="topic">ISWC 2007. In Keywords.</div>
</center>

<div class="explanation">
  This page leads you to <a href="http://www.bibsonomy.org/">BibSonomy</a>, a social bookmark and publication 
  sharing system. All contributions submitted to the <a href="http://iswc2007.semanticweb.org/">ISWC 2007</a> conference 
  have been loaded into the system, together with the keywords (tags) that authors have associated with their abstracts. 
  Tags can be used to navigate through the conference abstracts.
</div>

<div class="linkbox">
    <a href="/help/basic">What is BibSonomy?</a>
    <a href="/help/tutorials">Tutorials</a>
    <a href="bibsonomy2/events/iswc2007/register.jsp">Get a BibSonomy account!</a>
</div>


<div class="explanation">
  Below, the tags (keywords) most frequently used by ISWC authors are shown. The size of each tag (keyword) is 
  proportional to the logarithm of the number of abstracts that have been tagged with it. The number of abstracts 
  associated with a tag (keyword) is displayed when hovering over it. Colors encode topics (tracks) of the ISWC2007 
  conference, as shown at the bottom of this page. The color of each tag indicates the topic to which most abstracts 
  annotated with that tag belong to. Clicking on a tag (keyword) will retrieve from BibSonomy a list of the abstracts 
  that have been tagged with it. The full abstract of each contribution is available in BibTeX format.
</div>


<%@include file="tagcloud.jsp" %>


<div class="linkbox linkbox2">

  <span id="cool">
    <a href="/events/iswc2007/cool.jsp">See what your collegues find cool</a>

    or

  </span>
    
    <span style="font-weight: bold; font-size: 120%;padding-left:15px;display:inline;">Search for an author:</span>
      <form id="specialsearch" method="get" action="/specialsearch" style="display:inline;">
        <input type="hidden" name="scope" value="author">
        <input type="hidden" name="requUser" value="iswc2007">
        <input type="text" id="inpf" name="q" size="25"/>
      </form>
</div>

<%@include file="footer.jsp" %>