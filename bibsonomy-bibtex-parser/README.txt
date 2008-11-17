                          BibSonomy BibTeX Parser

  What is it?
  -----------

  The parser is a slightly modified version of Johannes Henkel's BibTeX parser
  which is GPL and can be obtained from 
  
  http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
  
  Thanks to Johannes for his efforts!
  
  The parser itself is used to parse BibTeX files which users upload to 
  BibSonomy to extract the metadata and put it in our Java model. We also
  use it at various input stages to ensure consistency of our data, e.g., 
  all screen scrapers produce BibTeX which is then feed to the parser. 
    
  The parser is part of the BibSonomy social bookmarking system 
  <http://www.bibsonomy.org> and is maintained by the Knowledge & 
  Data Engineering Group at the University of Kassel, Germany 
  <http://www.kde.cs.uni-kassel.de/>.

  Documentation
  -------------

  The documentation available as of the date of this release is included in
  the form of JavaDoc annotations in the source code.
  
  Release Notes
  -------------

  System Requirements
  -------------------

  JDK:
    1.5 or above.
  Memory:
    No minimum requirement.
  Disk:
    No minimum requirement.
  Operating System:
    No minimum requirement.

  Licensing
  ---------

  Please see the file called LICENSE.txt

  Maven URLS
  ----------

  Home Page:          http://dev.bibsonomy.org/
  Maven Repository:   http://dev.bibsonomy.org/maven2/
  Issue Tracking:     http://gforge.cs.uni-kassel.de/tracker/?atid=480&group_id=52&func=browse
