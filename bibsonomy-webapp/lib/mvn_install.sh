#!/bin/sh
mvn install:install-file -Dfile=JabRef-2.2.jar -DgroupId=jabref -DartifactId=jabref -Dversion=2.2 -Dpackaging=jar -DgeneratePom=true
#mvn install:install-file -Dfile=$BIBSONOMY1_LIBS_DIR/mallet.jar -DgroupId=jabref -DartifactId=jabref -Dversion=2.2 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=mallet.jar -DgroupId=edu.umass.cs.mallet -DartifactId=mallet -Dversion=0.4 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=lsimplecaptcha-20060705.jar -DgroupId=net.sf.simplecaptcha -DartifactId=simplecaptcha -Dversion=20060705-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
#mvn install:install-file -Dfile=slide-kernel.jar -DgroupId=slide -DartifactId=slide-kernel -Dversion=2.2pre1 -Dpackaging=jar -DgeneratePom=true
#mvn install:install-file -Dfile=slide-roles.jar -DgroupId=slide -DartifactId=slide-roles -Dversion=2.2pre1 -Dpackaging=jar -DgeneratePom=true
#mvn install:install-file -Dfile=slide-stores.jar -DgroupId=slide -DartifactId=slide-stores -Dversion=2.2pre1 -Dpackaging=jar -DgeneratePom=true
#mvn install:install-file -Dfile=slide-webdavservlet.jar -DgroupId=slide -DartifactId=slide-webdavservlet -Dversion=2.2pre1 -Dpackaging=jar -DgeneratePom=true
