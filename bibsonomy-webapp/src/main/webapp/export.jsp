<%@include file="include_jsp_head.jsp" %>

<%-- include HTML header --%>
<jsp:include page="html_header.jsp">
  <jsp:param name="title" value="export" />
</jsp:include>

<%-------------------------- Heading -----------------------%>
<h1 id="path"><a href="/" rel="Start">${projectName}</a> :: <a rel="path_menu" href="/export/<c:out value='${requPath}'/>">export&nbsp;<img src="/resources/image/box_arrow.png"></a></h1> 

<%-------------------------- Path Navigation -----------------------%>
<%@include file="/boxes/path_navi.jsp" %>

<%-------------------------- Navigation -----------------------%>
<%@include file="/boxes/navi.jsp" %> 

<div id="general"> 

<h2>Export publications</h2>

<%--

TODO: enable the user to choose the options 

   duplicates=no, items=1000

for EVERY page

do this with JavaScript and two checkboxes

 --%>

 <style type="text/css">
   .kiste li {
     display:inline;
   }
 </style>

<%-- ----------------------- Quicklinks ---------------------------------------------- --%>
<div class="kiste"> 
  <ul>
    <li><a href="/bib/<c:out value='${requPath}'/>">BibTeX</a> <b>&middot;</b></li>
    <li><a href="/layout/harvard/<c:out value='${requPath}'/>">RTF</a> <b>&middot;</b></li>
    <li><a href="/layout/endnote/<c:out value='${requPath}'/>">EndNote</a> <b>&middot;</b></li>
    <li><a href="/publ/<c:out value='${requPath}'/>">HTML</a> <b>&middot;</b></li>
    <li><a href="/publrss/<c:out value='${requPath}'/>">RSS</a> <b>&middot;</b></li>
    <li><a href="/swrc/<c:out value='${requPath}'/>">SWRC</a> <b>&middot;</b></li>
    <li><a href="/burst/<c:out value='${requPath}'/>">BuRST</a></li>
    <%-- ONLY FOR NEPOMUK! li><a href="/publcsv/<c:out value='${requPath}'/>">CSV</a></li ONLY FOR NEPOMUK! --%>
  </ul>
  
  <ul>
    <li><a href="/layout/custom/<c:out value='${requPath}'/>">Custom</a> <b>&middot;</b></li>
    <li><a href="/layout/simplehtml/<c:out value='${requPath}'/>">SimpleHTML</a> <b>&middot;</b></li>
    <li><a href="/layout/html/<c:out value='${requPath}'/>">HTML</a> <b>&middot;</b></li>
    <li><a href="/layout/tablerefs/<c:out value='${requPath}'/>">TableRefs</a> <b>&middot;</b></li>
    <li><a href="/layout/tablerefsabsbib/<c:out value='${requPath}'/>">TableRefsAbsBib</a> <b>&middot;</b></li>
    <li><a href="/layout/tablerefsabsbibsort/<c:out value='${requPath}'/>">TableRefsAbsBibSort</a> <b>&middot;</b></li>
    <li><a href="/layout/docbook/<c:out value='${requPath}'/>">DocBook</a> <b>&middot;</b></li> 
    <li><a href="/layout/openoffice-csv/<c:out value='${requPath}'/>">OpenOffice-CSV</a> <b>&middot;</b></li>
    <li><a href="/layout/dblp/<c:out value='${requPath}'/>">DBLP</a> <b>&middot;</b></li>
    <li><a href="/layout/text/<c:out value='${requPath}'/>">Text</a> <b>&middot;</b></li>
    <li><a href="/layout/jucs/<c:out value='${requPath}'/>">JUCS</a></li>
  </ul>
</div>

<%-- ----------------------- Descriptions ---------------------------------------------- --%>

  <p>By clicking on one of the formats you can export the publications of the page <a href="/<c:out value='${requPath}'/>">${projectHome}<c:out value='${mtl:decodeURI(requPath)}'/></a> in that format.</p>

  <dl class="faq">
    <dt><a href="/bib/<c:out value='${requPath}'/>">BibTeX</a></dt>
    <dd><a href="http://en.wikipedia.org/wiki/BibTeX">BibTeX</a> is a very popular bibliography management tool
    for <a href="http://en.wikipedia.org/wiki/LaTeX">LaTeX</a>. Save this output and include it in your LaTeX
    file with the command <code>\bibliography{FILENAME}</code> by substituting <code>FILENAME</code> with the 
    name you gave the file while saving it and omitting the <code>*.tex</code> ending.
    </dd>
    
    <dt><a href="/layout/harvard/<c:out value='${requPath}'/>">RTF</a> <a href="#jabref">*</a></dt>
    <dd>This exports the publication list in <a href="http://en.wikipedia.org/wiki/Rich_Text_Format">Rich Text Format</a>
    which can be used by word processors like <a href="http://www.openoffice.org">OpenOffice</a> or 
    <a href="http://www.microsoft.com/word">Microsoft Word</a>. It is formatted according to the 
    Harvard bibliography style guidelines.</dd>

    <dt><a href="/publ/<c:out value='${requPath}'/>">HTML</a></dt>
    <dd>This gives you a simple <a href="http://en.wikipedia.org/wiki/HTML">HTML</a> formatted page which you can include into your homepage.</dd>
       
    <dt><a href="/publrss/<c:out value='${requPath}'/>">RSS</a></dt>
    <dd>With an appropriate <a href="http://en.wikipedia.org/wiki/RSS">RSS</a>-tool you can subscribe to this 
    RSS feed and get notifications when someone adds publications to it.</dd>
    
    <dt><a href="/aparss/<c:out value='${requPath}'/>">RSS (APA 5th Style)</a></dt>
    <dd>With an appropriate <a href="http://en.wikipedia.org/wiki/RSS">RSS</a>-tool you can subscribe to this 
    RSS feed and get notifications when someone adds publications to it.</dd>
    
    <dt><a href="/swrc/<c:out value='${requPath}'/>">SWRC</a></dt>
    <dd>This is an <a href="http://en.wikipedia.org/wiki/RDF">RDF</a> output according to the 
    <a href="http://ontoware.org/projects/swrc/">SWRC</a> ontology.</dd>
    
    <dt><a href="/burst/<c:out value='${requPath}'/>">BuRST</a></dt>
    <dd>This is an <a href="http://en.wikipedia.org/wiki/RDF">RDF</a> output according to the 
    <a href="http://www.cs.vu.nl/~pmika/research/burst/BuRST.html">BuRST</a> specification. It's a valid 
    <a href="http://en.wikipedia.org/wiki/RSS">RSS</a> feed, therefore you can subscribe to it with appropriate
    applications. It is basically a union of the RSS and SWRC output.</dd>
    
    <%-- ONLY FOR NEPOMUK! dt><a href="/publcsv/<c:out value='${requPath}'/>">CSV</a></dt>
    <dd>CSV, or Comma Separated Values divides all fields of the entry by a comma. It is useful to import the
    publications into spreadsheet programs.</dd ONLY FOR NEPOMUK! --%>

    <dt><a href="/layout/custom/<c:out value='${requPath}'/>">Custom</a></dt>
    <dd>A custom <a href="http://jabref.sourceforge.net/">JabRef</a> layout which you can upload on the <a href="/settings">settings</a> page.
    Information on how to write such a
  layout filter can be found <a href="http://jabref.sourceforge.net/help/CustomExports.html">here</a>.
    </dd>
    
    <dt><a href="/layout/simplehtml/<c:out value='${requPath}'/>">SimpleHTML</a> <a href="#jabref">*</a></dt>
    <dd>An HTML layout without any header or footer but very nice output. This is useful for integrating
    publication lists into other HTML pages.
    </dd>
    
    <dt><a href="/layout/html/<c:out value='${requPath}'/>">HTML</a> <a href="#jabref">*</a></dt>
    <dd>A simple layout where each entry is represented as row in a table.</dd>
    
    <dt><a href="/layout/tablerefs/<c:out value='${requPath}'/>">TableRefs</a> <a href="#jabref">*</a></dt>
    <dd>A more structured HTML output with each entry as a row in a table and additional JavaScript search function.</dd>
    
    <dt><a href="/layout/tablerefsabsbib/<c:out value='${requPath}'/>">TableRefsAbsBib</a> <a href="#jabref">*</a></dt>
    <dd>Similar to TableRefs but additionally includes the BibTeX source and the abstract of the publication.</dd>
    
    <dt><a href="/layout/tablerefsabsbibsort/<c:out value='${requPath}'/>">TableRefsAbsBibSort</a> <a href="#jabref">*</a></dt>
    <dd>Similar to TableRefsAbsBib, but includes possibility to sort the table by each column.</dd>    
    
    <dt><a href="/layout/docbook/<c:out value='${requPath}'/>">DocBook</a> <a href="#jabref">*</a></dt>
    <dd>This is an 
    <a href="http://en.wikipedia.org/wiki/XML">XML</a> output according to the <a href="http://en.wikipedia.org/wiki/DocBook">DocBook</a> schema.</dd>
    
    <dt><a href="/layout/endnote/<c:out value='${requPath}'/>">EndNote</a> <a href="#jabref">*</a></dt>
    <dd>This is an output in <a href="http://en.wikipedia.org/wiki/RIS_%28file_format%29">RIS</a> which is used
    by EndNote, another bibliography management tool.</dd>
    
    <dt><a href="/layout/openoffice-csv/<c:out value='${requPath}'/>">OpenOffice-CSV</a> <a href="#jabref">*</a></dt>
    <dd>CSV, or Comma Separated Values divides all fields of the entry by a comma. It is useful to import the
    publications into spreadsheet programs.</dd>
    
    <dt><a href="/layout/dblp/<c:out value='${requPath}'/>">DBLP</a> <a href="#jabref">*</a></dt>
    <dd>DBLP export your records to a DBLP conformable XML structure.</dd>

    <dt><a href="/layout/text/<c:out value='${requPath}'/>">Text</a> <a href="#jabref">*</a></dt>
    <dd>This is another <a href="http://en.wikipedia.org/wiki/BibTeX">BibTeX</a> output.</dd>
    
    <dt><a href="/layout/jucs/<c:out value='${requPath}'/>">JUCS</a> <a href="#jabref">*</a></dt>
    <dd>Output formatted according to the guidelines of the <a href="http://www.jucs.org/">Journal of Universal 
    Computer Science</a>.</dd>    
  </dl>

  <h3><a name="jabref">JabRef</a></h3>
  <p>
  The export filters marked with a star * are from <a href="http://jabref.sourceforge.net/">JabRef</a>, 
  a tool to manage publication references on 
  the desktop. It includes a mechanism to write layout files which allow the export of references in a nicely
  formatted way. ${projectName} includes some of these layout filters and additionally allows you to upload
  your own custom filter on the <a href="/settings">settings</a> page. Information on how to write such a
  layout filter can be found <a href="http://jabref.sourceforge.net/help/CustomExports.php">here</a>.
  </p>
</div>

<%@ include file="footer.jsp" %>