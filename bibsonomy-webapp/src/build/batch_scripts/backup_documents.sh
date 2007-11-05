#!/bin/sh

# location of directories
HOME_DIR=/home/bibsonomy
DOC_DIR=$HOME_DIR/bibsonomy_docs
# rsync options
RHOST=daffy.cs.uni-kassel.de
RUSER=bibbackup
RDIR=backup
ROPTIONS="-av --rsh=ssh"

# backup document directory
rsync $ROPTIONS $DOC_DIR $RUSER@$RHOST:$RDIR
