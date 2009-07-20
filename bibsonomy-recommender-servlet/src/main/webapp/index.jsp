<%--

     
     BibSonomy Recommender Webapp - Example remote recommender implementation
      
     Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
                               University of Kassel, Germany
                               http://www.kde.cs.uni-kassel.de/
     
     This program is free software; you can redistribute it and/or
     modify it under the terms of the GNU Lesser General Public License
     as published by the Free Software Foundation; either version 2
     of the License, or (at your option) any later version.
    
     This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU Lesser General Public License for more details.
     
     You should have received a copy of the GNU Lesser General Public License
     along with this program; if not, write to the Free Software
     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

--%>

<html>
<body>
<h1>DC09 Recommender Challenge</h1>
<h2>Tag Recommendation Servlet</h2>
<ul>
	<!--
		URLEncoded post:
		<bibsonomy stat="ok">
	    <post postingdate="2009-03-26T12:12:12.179+01:00">
	        <user name="foo" href="http://www.bibsonomy.org/api/users/foo"/>
	        <group name="bar" href="http://www.bibsonomy.org/api/groups/bar"/>
	        <tag name="foobar" href="http://www.bibsonomy.org/api/tags/foobar"/>
	        <bibtex year="2009" title="foo and bar" intrahash="abc" interhash="abc" href="http://www.bibsonomy.org/api/users/foo/posts/abc" entrytype="twse" bibtexKey="test"/>
	    </post>
		</bibsonomy> 
	-->
	<li>
		<a href="TagRecommenderServlet?data=%3Cbibsonomy+stat%3D%22ok%22%3E+++++%3Cpost+postingdate%3D%222009-03-26T12%3A12%3A12.179%2B01%3A00%22%3E+++++++++%3Cuser+name%3D%22foo%22+href%3D%22http%3A%2F%2Fwww.bibsonomy.org%2Fapi%2Fusers%2Ffoo%22%2F%3E+++++++++%3Cgroup+name%3D%22bar%22+href%3D%22http%3A%2F%2Fwww.bibsonomy.org%2Fapi%2Fgroups%2Fbar%22%2F%3E+++++++++%3Ctag+name%3D%22foobar%22+href%3D%22http%3A%2F%2Fwww.bibsonomy.org%2Fapi%2Ftags%2Ffoobar%22%2F%3E+++++++++%3Cbibtex+year%3D%222009%22+title%3D%22foo+and+bar%22+intrahash%3D%22abc%22+interhash%3D%22abc%22+href%3D%22http%3A%2F%2Fwww.bibsonomy.org%2Fapi%2Fusers%2Ffoo%2Fposts%2Fabc%22+entrytype%3D%22twse%22+bibtexKey%3D%22test%22%2F%3E+++++%3C%2Fpost%3E+%3C%2Fbibsonomy%3E">
			Query Recommender
	</a></li>
</ul>
</body>
</html>
