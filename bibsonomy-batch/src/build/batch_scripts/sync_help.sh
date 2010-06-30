#!/bin/sh

# copies the help to PUMA

BASEDIR=/home/kde/bibbackup/backup/bibsonomy-help_

RHOST=puma
RDIR=/home/puma/hilfe/
ROPTIONS="-av --rsh=ssh"

# add languages here
for lang in en de; do
  rsync $ROPTIONS $BASEDIR$lang $RHOST:$RDIR
done

