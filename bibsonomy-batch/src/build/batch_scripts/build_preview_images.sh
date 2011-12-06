#!/bin/bash

#
# builds for each uploaded file (if possible) a preview image
#
# parameters:
# 1) folder with the documents
# 2) task \in {"force", "delete"}
#
# Version: $Id$
#
# Changes:
# 2011-12-06 (rja)
# - added "-cnewer" option to "find" and corresponding file touching
# - for PDF/PS convert is now only called successful ghostscript conversion
# 2011-11-25 (rja)
# - added conversion of image/png, image/tiff, image/jpeg
# - added .jpg file extension to really create JPEGs :-(
# - added renicing of script
# 2011-11-24 (rja)
# - initial version
# 
# TODO: merge thumbnail generation into one method
# 
# TODO: improve file name pattern for "find" (five times [0-9a-f] at
# the end ensures that no preview images match - currently)
#
# TODO: refactor "find" operation into one line

if [ $# -lt 2 ]; then
    echo "usage:"
    echo "  $0 document_dir task"
    echo "  where task is either force, delete"
    exit 1;
fi

DOCUMENT_DIRECTORY=$1
TASK=$2
# this file's create date is used as reference to find only newly
# documents
TIMESTAMPFILE=$DOCUMENT_DIRECTORY/timestamp_preview

# renice script to the lowest level
PID=$$
renice -n 20 -p $PID
ionice -c 3 -p $PID

# remember current time (before searching for files)
NOW=$(date --rfc-2822)

# check for timestamp file
if [ -f $TIMESTAMPFILE ]; then
    # found -> find only files created after the timestamp file
    DOCUMENTS=$(find $DOCUMENT_DIRECTORY -type f -cnewer $TIMESTAMPFILE -name "[0-9a-f]*[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]")
else 
    # no timestamp file available -> find all documents
    DOCUMENTS=$(find $DOCUMENT_DIRECTORY -type f -name "[0-9a-f]*[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]")
fi
# touch timestamp file
touch --date "$NOW" $TIMESTAMPFILE
echo found $(echo $DOCUMENTS | wc -l ) new documents

for doc in $DOCUMENTS; do
    # find out file's MIME type
    type=$(file --brief --mime-type $doc)
    case "$type" in 
	"application/postscript" | "application/pdf")
	    # when no small preview there, generate a new one
	    small=${doc}_SMALL.jpg
	    if [ ! -f $small -o $TASK == "force" ]; then
		echo "converting $doc ($type)"
		# get a temporary file
		temp=$(tempfile)
		# convert to PNG
		gs -q -dSAFER -sDEVICE=png16m -dLastPage=1 -r150 -o$temp $doc
		if [ $? ]; then
		    # make small JPEG previews
		    convert -thumbnail '100x100>' $temp ${doc}_SMALL.jpg
		    convert -thumbnail '200x200>' $temp ${doc}_MEDIUM.jpg
		    convert -thumbnail '400x400>' $temp ${doc}_LARGE.jpg
		fi
		# remove temporary file
		rm $temp
	    fi
	    ;;
	"image/jpeg" | "image/png" | "image/tiff" )
	    # when no small preview there, generate a new one
	    small=${doc}_SMALL
	    if [ ! -f $small -o $TASK == "force" ]; then
		echo "converting $doc ($type)"
		# make small JPEG previews
		convert -thumbnail '100x100>' $doc ${doc}_SMALL.jpg
		convert -thumbnail '200x200>' $doc ${doc}_MEDIUM.jpg
		convert -thumbnail '400x400>' $doc ${doc}_LARGE.jpg
	    fi
	    ;;
	*)
	    echo "$type can not be handled"
	    ;;
    esac
done