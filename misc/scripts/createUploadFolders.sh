#!/bin/bash
# check line arguments
if [[ $# == 0 ]]; then
  echo "usage: createUploadFolders.sh project_name"
  exit -1
fi

prefix=$1

# create temp dir
mkdir ${prefix}_temp
# create doc and pictures folder
for folder_suffix in _docs _pictures
do
  # create root folder
  folder=$prefix$folder_suffix
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