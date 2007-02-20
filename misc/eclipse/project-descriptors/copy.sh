#!/bin/sh
case $1 in
	save)	test "`pwd | sed 's/.*\/\(.*\)/\1/'`" != "bibsonomy2" && echo "Change to the bibsonomy2 directory" && exit 1
		find . -mindepth 2 -maxdepth 2 -iname .classpath -or -iname .project | sed 's/^\.\/\(.*\)/\1 misc\/eclipse\/project-descriptors\/\1/' | xargs -l1 cp
		;;
	*)	test "`pwd | sed 's/.*\/\(.*\)/\1/'`" != "project-descriptors" && echo "Change to misc/eclipse/project-descriptors" && exit 1
		find . -iname .classpath -or -iname .project | sed 's/^.\/\(.*$\)/\1 ..\/..\/..\/\1/' | xargs -l1 cp
		;;
esac
