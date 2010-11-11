#!/bin/bash
# check line arguments
if [[ $# == 0 ]]; then
  echo "usage: createUploadFolders.sh project_name"
  exit
fi
# create temp dir
mkdir "$1_temp"
# create doc and pictures folder
for folder_suffix in _docs _pictures
do
  # create root folder
  folder="$1$folder_suffix"
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

echo "[INFO] done"