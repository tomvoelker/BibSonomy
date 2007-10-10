<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">


  <div id="bookbox">
      <h2>Welcome to the ${projectName} FAQ</h2>

      <p>Questions, feedback and announcements concerning ${projectName} 
	  can be shared on the <a
      href="http://www.kde.cs.uni-kassel.de/mailman/listinfo/bibsonomy-discuss">mailing
      list</a>.</p>


      <a id="top" name="top"></a> 
      <h3 id="faq-overview">FAQ Overview</h3>

      <ul class="faq" id="faqbase"/>
	<!-- This element should look like this:

        li Importing and Exporting Data
	ul class="subfaq" 
	li Is it possible to import my data from del.icio.us?
	li Is it possible to import my local BibTeX files?  
        -->
	<li style="list-style: none">




	  <!--             General             -->




	  <div class="section">
	    <h3 id="faq-general">General</h3>
	    <a class="up" href="#top">[up]</a></div>

	  <dl class="faq">
	    <dt>What if I have forgotten my password?</dt>
	    <dd>
          Just go to the <a href="/reminder">password reminder</a> page and enter
          your username and the e-mail address you used for registering at ${projectName}. 
          After confirming the security code we will send you an e-mail with a temporary 
          password with which you can login and change your password.
	    </dd>
	    
	  </dl>
	  <!--             Importing and Exporting              -->



	  <div class="section">
	    <h3 id="faq-import">Importing and Exporting
	      Data</h3>

	    <a class="up" href="#top">[up]</a></div>

	  <dl class="faq">
	    <dt>Is it possible to import my data from del.icio.us?</dt>

	    <dd>
	      Yes, it is possible via the <a href="/settings">settings page</a>. 
	      There you can enter
		your username and password and all your del.icio.us bookmarks will
		be imported.
	    </dd>

	    <dt>Is it possible to import my Firefox bookmarks?</dt>

	    <dd>
	      Yes, it is possible via the <a href="/settings">settings page</a>.
	    </dd>

	    <dt>Is it possible to import my local BibTeX files?</dt>

	    <dd>
          Yes, it is possible via the <a href="/post_bibtex" >post bibtex page</a>. There you can
	  	  upload a file. Please choose the right character encoding and
		  select who may view the BibTeX items. If a BibTeX entry has a field
		  <code>tag = "tagA tagB tagC"</code> or <code>keywords = "tagA tagB tagC"</code>
          then these tags are imported and show up on the summary page which appears after 
          uploading the file. There you can enter a set of tags for every imported BibTeX entry. 
          The system assigns the tag <em>imported</em> to every BibTeX entry without a tag.
          <br/>
          If the tags in your BibTeX file are not separated by whitespace (but by comma, e.g.) you
          can change this by clicking on the <em>options</em> link below the upload form on
          the <a href="/post_bibtex">post bibtex page</a>.
	    </dd>
	  </dl>





	  <!--              User Interface              -->






	  <div class="section">
	    <h3 id="faq-ui">User Interface</h3>
	    
	    <a class="up" href="#top">[up]</a></div>
	  
	  <dl class="faq">
	    <dt>Is the bookmark/publication entry directly usable after the
      upload into the system?</dt>
      
      <dd>
	  Yes and No. Most of the functionality is directly usable. You
	  will see the new entry and you can browse. But the entry will not
	  directly be found in searches, as the database is periodically
	  updated.
      </dd>
      
      <dt>Do I have to type every tag I want to use each time?</dt>

      <dd>
	  No. Your tag cloud on the right hand side, as well as the tags
	  of an item you may be copying, are clickable in the posting
	  form. This means that clicking on a tag in the tag cloud while 
      posting an entry allows you to add that tag to the post.
      </dd>

      <dt>What are those input fields used for?</dt>      
      <dd>On a typical ${projectName} page, there are three input fields:
	<dl>
	  <dt>Top left:</dt>
	  <dd>The paths shown in the top left corner such as 
	    ${projectName}::user::schmitz::[input field]
	    can be completed by typing a tag (in this example), a user, or a group.
	    into the input field.</dd>
	  <dt>Top right:</dt>
	  <dd>The input field in the top right corner is used for full-text
	    searches over posts. The search can be restricted to the posts
	    by a particular user by specifying <code>user:username</code>
	    as a search term, or by choosing the respective entry from the drop-down
	    list.</dd>
	  <dt>Above the tag cloud:</dt>
	  <dd>This field can be used to filter the tags shown in the tag cloud.
	    Just type ahead and only those tags containing the text you typed
	    will be shown in the cloud.</dd>
	</dl>
      </dd>


      <dt>Can I search for multiple terms or tags at the same time?</dt>
      <dd>
	Yes. Just type more than one tag or term in the tag search field (top left)
	or the fulltext search field (top right). The search results will include all
	posts that contain <em>all</em> of the tags or terms.
      </dd>


      <dt>What does the <a href="/basket">basket</a> menu item do?</dt>
      <dd>
	You may have noticed the button titled <em>pick</em> below 
	publication posts. You can use this button to collect a number of
	publications, which can then be downloaded as one BibTeX file from the <a href="/basket">basket</a>
      </dd>

      <dt>Can I rename tags in batch mode?</dt>
      <dd>Yes, by using the <a href="/edit_tags">edit tags</a> page.</dd>

      <dt>Which items appear on the <a href="/popular">popular</a> page?</dt>
      <dd>The <a href="/popular">popular</a> page shows those 100 items which 
      have been tagged most often within the last 5,000 posts and is updated
	every 15 minutes. This means that the absolute counts of posts for the items
	do not neccessarily have to be in the order of the popular page, as it
	only considers the most recently posted items.
      </dd>


    </dl>




      <!--              Groups and Privacy              -->





      <div class="section">
	<h3 id="faq-privacy">Groups and Privacy</h3>
	<a class="up" href="#top">[up]</a></div>

      <dl class="faq">
	<dt>Is it possible to have private posts?</dt>

	<dd>
	    Yes, just select <em>private</em> in the <em>viewable for</em> dropdown box
	    when you post a <a href="/post_bookmark">bookmark</a> or
	    a <a href="/post_bibtex">publication</a>. Private posts
	    will only be visible to the user who posted them.
	</dd>

	<dt>Is it possible to have a group of users sharing public and
	  private resources?</dt>

  <dd>
      Yes. Just create a user account with the desired group name and write an e-mail to
      <jsp:directive.include file="/boxes/emailaddress.jsp"/>. We will then make this user the
      group admin.<br/>
      
      There are two aspects of groups:

    <dl>
      <dt>Seeing everything posted by group members:</dt>

      <dd>Everything you post with <em>public</em> visibility will then be
        visible under all groups you belong to. This can be used to
        publicly present a group of users under a common name (which can be
        found under the <a href="/groups">groups</a> link).</dd>

      <dt>Restricting access to group members:</dt>

      <dd>You can choose to post content with visibility restricted to
        members of a certain group. If you select a group when posting,
        only members of that particular group will be able to see that
        content.</dd>
    </dl>
  </dd>



	<dt>What do you mean by <em>friends</em>?</dt>

	<dd>
	    Each user can state that some other users are his friends
	    on the <a href="/friends">friends</a> page. Links can
	    be posted so that they are visible for friends of the posting user
	    only.
	</dd>

	<dt>Why are my private posts not visible on some pages?</dt>

	<dd>
	    Except on user or group specific pages, only public posts are
	    visible on all pages.
	</dd>

	<dt>Can I make posts available for multiple groups?</dt>
	<dd>No. For performance reasons, each post belongs to exactly one group
	  (<em>public, private, friends</em> being special groups, too). For a workaround,
	refer to the next question.</dd>

	<dt>Will the posts be lost for a group if the user leaves?</dt>
	<dd>Posts made by a user are lost if she decides to cancel her account. In order to
	  prevent the loss of group knowledge, posts can be automatically copied to one or
	  more groups which the posting user is a member of by attaching the special tag
	  <code>for:groupname</code> to the post. This causes the post to be copied 
	  automatically to the respective group.
	</dd>

	<dt>Can I post to a particular group by default?</dt>

	<dd>Yes, but you'll have to hack the Javascript in the post button for this.
	  Just append <code>+'&amp;group=mygroup'</code> (where mygroup is the group you want to use)
	  to the Javascript code of the button.
	</dd>

      </dl>





      <!--              Handling Publications              -->





      <div class="section">
	<h3 id="faq-pub">Handling Publications</h3>

	<a class="up" href="#top">[up]</a></div>

      <dl class="faq">
	<dt>Is it possible to upload a BibTeX snippet from a web site?</dt>

	<dd>
	  Add this  <jsp:directive.include file="/boxes/button_postbibtex.jsp" />
	    button to the links toolbar of your browser and
	    surf to the web site of interest. Then mark the BibTeX source code
	    on the web site and upload the snippet by clicking the button. You
	    will be redirected to the usual post page but the system has parsed
	    the snippet and filled out the form.<br/>
        We have also implemented some <em>scrapers</em> which enable you to
        use the post publication button on pages without explicit BibTeX 
        source code. For an overview on which services are supported have a
        look at the <a href="/scraperinfo">scraper info page</a>.
      
	</dd>

	<dt>Is there a way to get a nicely formatted output for my
	  publication?</dt>

	<dd>
	    Just put <code>/publ</code> after the hostname on any page,
	    which shows resources, for example: 
        <a href="${projectHome}publ/tag/web">${projectHome}publ/tag/web</a>. 
        There are several other layouts available, just click on the 
        <img src="/resources/image/more.png" alt="more"/>
        button on the top of each page to access them.
	</dd>

	<dt>What if I post the same BibTeX entry twice?</dt>
	<dd>
	    If you post exactly the same BibTeX entry twice, the duplicate will be detected
	    and you will be offered a form for merging the two. Minor changes outside the 
	    entry type, title, author, editor, booktitle, journal, key and year fields will
	    not be considered.
      <br/>
	    For the <a href="/popular">popular</a> page and similar purposes, there is a
	    weaker form of duplicate detection that tries to find as many similar publications
	    as possible, so that the social effect of having similar interests can be
	    exploited. For this kind of duplicate detection, only author, title and year are 
	    considered in a normalized form (e.g. excluding spaces, non-word characters and 
	    punctuation).
	</dd>

	<dt>Can I download all of my publications at once?</dt>
	<dd>
    A user who is logged in can use the BibTeX button at the top of 
	the page to download the currently selected number of his BibTeX entries 
	as one file. To get all (e.g., 10000) entries just append 
	<code>'?items=10000'</code> 
	at the end of the current URL.</dd>
      </dl>

          <!--            Bechmark DataSet            -->




	  <div class="section">
	    <h3 id="faq-dataset">Get a benchmark dataset of ${projectName}</h3>
	    <a class="up" href="#top">[up]</a></div>

	  <dl class="faq">
	    <dt>How can I get a dataset of ${projectName}?</dt>
	    <dd>We offer a benchmark dataset in form of an SQL dump to interested people.
	     Before you get access to our dataset, you have to sign up our 
         <a href="/help/BibSonomy_Agreement.pdf">data agreement</a>
	     and send it via fax to our office.
	    </dd>
	    <dt>Can I use the dataset for publications?</dt>
	    <dd>Of course, you can! We are quite interested in results, therefore
	        please inform us about your publications by using the ${projectName} dataset.
	        Concerning citing this data in publications,
	        please refer to the following reference:
	        
            <p> Knowledge and Data Engineering Group, University of Kassel: Benchmark
            Folksonomy Data from ${projectName}, version of June 31st, 2007.</p>
            
            If you want to refer to the system, please use the 
            <a href="/bibtex/1d28c9f535d0f24eadb9d342168836199">following publication</a>:
            
            <p>Andreas Hotho, Robert Jäschke, Christoph Schmitz, and Gerd
            Stumme. BibSonomy: A Social Bookmark and Publication Sharing System. In
            Aldo de Moor, Simon Polovina, and Harry Delugach, editors, <em>Proceedings
            of the Conceptual Structures Tool Interoperability Workshop at the 14th
            International Conference on Conceptual Structures</em>, Aalborg, Denmark, 
            July 2006. Aalborg University Press.</p>
	    </dd>
	  </dl>



    </li>
  </div>


</html>

