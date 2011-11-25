#!/bin/bash

#
# builds for each uploaded file (if possible) a preview image
#
# parameters:
# 1) folder with the documents
# 2) task \in {"force", "delete"}
#
# Changes:
# 2011-11-24 (rja)
# - initial version
# 2011-11-25 (rja)
# - added conversion of image/png, image/tiff, image/jpeg
# - added .jpg extentions to really create JPEGs :-(
#
# TODO: merge thumbnail generation into one method
#

if [ $# -lt 2 ]; then
    echo "usage:"
    echo "  $0 document_dir task"
    echo "  where task is either force, delete"
    exit 1;
fi

DOCS=$1
TASK=$2

for doc in $(find $1 -type f -name "[0-9a-f]*[0-9a-f]"); do
    # find out file's MIME type
    type=$(file --brief --mime-type $doc)
    case "$type" in 
	"application/postscript" | "application/pdf")
	    # when no small preview there, generate a new one
	    small=${doc}_SMALL
	    if [ ! -f $small -o $TASK == "force" ]; then
		echo "converting $doc ($type)"
		# get a temporary file
		temp=$(tempfile)
		# convert to PNG
		gs -q -dSAFER -sDEVICE=png16m -dLastPage=1 -r150 -o$temp $doc
		# make small JPEG previews
		convert -thumbnail '100x100>' $temp ${doc}_SMALL.jpg
		convert -thumbnail '200x200>' $temp ${doc}_MEDIUM.jpg
		convert -thumbnail '400x400>' $temp ${doc}_LARGE.jpg
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