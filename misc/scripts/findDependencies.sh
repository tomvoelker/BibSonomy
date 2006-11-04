#!/bin/bash

ls README.txt &>/dev/null
test $? -ne 0 && echo "You're not in the root directory of bibsonomy" && exit 1

echo "digraph dependencies {"
echo "size=\"3,3\";"
echo "graph [ rankdir = \"BT\" ];"

for i in `find . -mindepth 2 -maxdepth 2 -name pom.xml`; do
	NAME=`echo $i | cut -f2 -d/`
	NAME_ALT=`echo $NAME | sed 's/-/_/g' | sed 's/bibsonomy_//g'`
	echo "$NAME_ALT;"
	for j in `grep -i "artifactId>bibsonomy-" $i | cut -f2 -d'<' | cut -f2 -d'>'`; do
		if [ $j != $NAME ]; then
			j_ALT=`echo $j | sed 's/-/_/g'`
			echo "$NAME_ALT -> $j_ALT;" | sed 's/bibsonomy_//g'
		fi
	done
done

echo "}"
