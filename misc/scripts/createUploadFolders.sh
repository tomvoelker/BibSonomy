#!/bin/bash
PROJECT_NAME="bibsonomy"
# create temp dir
mkdir "$PROJECT_NAME;_temp"
# create doc and pictures folder
for folder_suffix in _doc _pictures
do
  # create root folder
  folder="$PROJECT_NAME$folder_suffix"
  mkdir $folder
  # create subfolders 
  for i in 0 1 2 3 4 5 6 7 8 9 a b c d e f
  do
    for j in 0 1 2 3 4 5 6 7 8 9 a b c d e f
    do
      mkdir $folder/$i$j
    done
  done
done

echo "done"