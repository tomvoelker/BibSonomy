#!/bin/sh
BIBSONOMY2_DIR="../../.."
BIBSONOMY1_DIR="$BIBSONOMY2_DIR/../bibsonomy"
BIBSONOMY1TMP_DIR="/tmp/bibsonomy1tmp"
WEBAPP_PROJECT_DIR="$BIBSONOMY2_DIR/bibsonomy-webapp"
WEBAPP_DIR="$WEBAPP_PROJECT_DIR/src/main/webapp"
mkdir $BIBSONOMY1TMP_DIR
cp -a $BIBSONOMY1_DIR/* $BIBSONOMY1TMP_DIR/
find $BIBSONOMY1TMP_DIR -name CVS -exec rm -r {} \;
cp -a $BIBSONOMY1TMP_DIR/*.jsp $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/ajax $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/boxes $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/crfBibsonomy $WEBAPP_DIR/
mkdir $WEBAPP_DIR/documentation
mkdir $WEBAPP_DIR/documentation/faq
cp -a $BIBSONOMY1TMP_DIR/documentation/faq/bookbox.jsp $WEBAPP_DIR/documentation/faq/
mkdir $WEBAPP_PROJECT_DIR/src/build
mkdir $WEBAPP_PROJECT_DIR/src/build/perl
cp -a $BIBSONOMY1TMP_DIR/documentation/faq $WEBAPP_PROJECT_DIR/src/build/perl/
echo "bookbox.jsp muss nach src/main/webapp/documantation/faq kopiert werden." >> $WEBAPP_PROJECT_DIR/src/build/perl/faq/README
cp -a $BIBSONOMY1TMP_DIR/errors $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/events $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/jabrefExportFilter $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/perl $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/resources $WEBAPP_DIR/
cp -a $BIBSONOMY1TMP_DIR/WEB-INF/{Domain,urlrewrite}.xml $WEBAPP_DIR/WEB-INF/
cp -a $BIBSONOMY1TMP_DIR/WEB-INF/web.xml $WEBAPP_DIR/WEB-INF/
mkdir $WEBAPP_PROJECT_DIR/src/main/java
mkdir $WEBAPP_DIR/WEB-INF/taglibs
mv $BIBSONOMY1TMP_DIR/WEB-INF/src/tags/*tld $WEBAPP_DIR/WEB-INF/taglibs
mv $BIBSONOMY1TMP_DIR/WEB-INF/src/recommender/logic/termprocessing/multilangST.txt $WEBAPP_PROJECT_DIR/src/main/resources/recommender/logic/termprocessing/
rm $BIBSONOMY1TMP_DIR/WEB-INF/src/scraper/test/log4j.properties
mkdir $BIBSONOMY1TMP_DIR/WEB-INF/test/scraper
mv $BIBSONOMY1TMP_DIR/WEB-INF/src/scraper/test $BIBSONOMY1TMP_DIR/WEB-INF/test/scraper/
rm $BIBSONOMY1TMP_DIR/WEB-INF/test/testng.xml
mkdir $WEBAPP_PROJECT_DIR/src/test/java
cp -a $BIBSONOMY1TMP_DIR/WEB-INF/test/* $WEBAPP_PROJECT_DIR/src/test/java/
rm -r $BIBSONOMY1TMP_DIR/WEB-INF/test
rm $BIBSONOMY1TMP_DIR/WEB-INF/src/log4j.properties
cp -a $BIBSONOMY1TMP_DIR/WEB-INF/src/* $WEBAPP_PROJECT_DIR/src/main/java/
rm -r $BIBSONOMY1TMP_DIR
cd $WEBAPP_PROJECT_DIR
patch -p1 < ../src/migration/sh/bibsonomy-migration.patch
cd -

# änderungen an: ExportBibtex.java LayoutHandler.java BibtexHandler.java include_jsp_head.jsp...
