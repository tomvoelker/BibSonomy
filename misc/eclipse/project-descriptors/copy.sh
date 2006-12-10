#!/bin/sh
test "`pwd | sed 's/.*\/\(.*\)/\1/'`" != "project-descriptors" && echo "Change to misc/eclipse/project-descriptors" && exit 1
find . -iname .classpath -or -iname .project | sed 's/^.\/\(.*$\)/\1 ..\/..\/..\/\1/' | xargs -L1 cp
