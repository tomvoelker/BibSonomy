# BibSonomy BibTeX Parser

## What is it?

The parser is a slightly modified version of Johannes Henkel's BibTeX parser which is GPL2 and can be obtained from 
  
http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
  
We relicense the modified parser with permission of Johannes Henkel under GPLv3+.
Thanks to Johannes for his efforts!

The parser itself is used to parse BibTeX files which users upload to 
BibSonomy to extract the metadata and put it in our Java model. We also
use it at various input stages to ensure consistency of our data, e.g., 
all screen scrapers produce BibTeX which is then feed to the parser. 

The module is part of the [BibSonomy](https://www.bibsonomy.org) social bookmarking system and is maintained by
the [Data Science Chair](https://www.informatik.uni-wuerzburg.de/datascience/home/) at the University of Würzburg, Germany,
the [Information Processing and Analytics Group](https://www.ibi.hu-berlin.de/en/research/Information-processing/) at the Humboldt-Universität zu Berlin, Germany,
the [Knowledge & Data Engineering Group](https://www.kde.cs.uni-kassel.de/) at the University of Kassel, Germany, and
the [L3S Research Center](https://www.l3s.de/) at Leibniz University Hannover, Germany.

## Documentation

The documentation available as of the date of this release is included in
the form of JavaDoc annotations in the source code.


## Release Notes

Please see the [release log](https://bitbucket.org/bibsonomy/bibsonomy/wiki/documentation/releases/Release%20Log).


## System Requirements

* JDK: 1.7 or above.
* Memory: No minimum requirement.
* Disk: No minimum requirement.
* Operating System: No minimum requirement.

## Licensing

* Please see the file [LICENSE.txt](https://bitbucket.org/bibsonomy/bibsonomy/src/tip/bibsonomy-bibtex-parser/LICENSE.txt?at=stable)


## Maven URLS

* [Home Page](https://bitbucket.org/bibsonomy/bibsonomy)
* [Maven Repository](http://dev.bibsonomy.org/maven2/)
* [Issue Tracking](https://bitbucket.org/bibsonomy/bibsonomy/issues)