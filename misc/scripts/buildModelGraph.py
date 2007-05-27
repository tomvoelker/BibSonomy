#!/usr/bin/python

import re

model = ["BibTex", "Bookmark", "Group", "Post", "Resource", "Tag", "User"]

graph = """digraph dependencies {
	graph [ rankdir = "LR" ]
	node [ shape = "record" ]
	size = "7,7"
	// Nodes
%s
	// Links
%s
	// Hardcoded links
	Bookmark -> Resource
	BibTex -> Resource
}"""

classes = ""
clazz = '%s [label = "%s | %s"]'
links = ""

#
# Returns a link from "start:part" to the corresponding "part" in
# the model as a string
#
def printLink(start, part):
  for i in model:
    if part.lower().__contains__(i.lower()): target = i
  return "\t" + start + ":" + part + " -> " + target + "\n"

#
# runs through all classes from the model
#
for m in model:
  filename = "bibsonomy-model/src/main/java/org/bibsonomy/model/" + m + ".java"
  #print "Opening " + filename
  file = open(filename, "r").read().splitlines()

  fields = ""
  for i in file:
    i = i.lstrip()

    # ignore static fields
    if i.__contains__("static"): continue
    # collect private fields
    if not i.startswith("private"): continue

    # remove generics, "private" and semicolon
    p = re.compile("<.*>|private|;")
    field = p.sub("", i).lstrip()

    # check whether we want to create a link to another node
    containsListOrClassFromModel = False
    if field.__contains__("List"): containsListOrClassFromModel = True
    if field.__contains__("T"): containsListOrClassFromModel = True
    for clocfm in model:
      if field.__contains__(clocfm): containsListOrClassFromModel = True

    if containsListOrClassFromModel:
      field = "<" + field.split()[1] + "> " + field.replace(" ", " : ")
      links += printLink(m, field.split()[3])
    else:
      field = field.replace(" ", " : ")

    fields += field + " | "

  classes += "\t" + (clazz % (m, m, fields)).replace("| \"", "\"") + "\n"

print (graph % (classes, links))
