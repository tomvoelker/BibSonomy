#!/bin/sh
find . -iname .classpath -or -iname .project | sed 's/^.\/\(.*$\)/\1 ..\/..\/..\/\1/' | xargs -L1 cp
