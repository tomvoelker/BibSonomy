#!/usr/bin/env python
# -*- coding: utf-8 -*-

import urllib2
from xml.dom.minidom import parseString

class BibSonomy:
	def __init__(self, username, apikey):
		self.baseurl = 'http://www.bibsonomy.org/api'
		self.username = username
		self.apikey = apikey

		# copied from the docs: http://docs.python.org/lib/urllib2-examples.html
		# Create an OpenerDirector with support for Basic HTTP Authentication...
		auth_handler = urllib2.HTTPBasicAuthHandler()
		auth_handler.add_password('BibSonomyWebService', self.baseurl, self.username, self.apikey)
		opener = urllib2.build_opener(auth_handler)
		# ...and install it globally so it can be used with urlopen.
		urllib2.install_opener(opener)

	def getDom(self, url):
		print 'Getting URL: ' + url
		f = urllib2.urlopen(self.baseurl + url)
		xml = f.read()
		xml = xml.decode('utf8', 'ignore')
		dom = parseString(xml.encode('utf-8', 'ignore'))
		return dom

	# get posts from a user
	def getPosts(self, resourcetype, start=0, end=10):
		type = 'resourcetype=' + getType(resourcetype)
		print 'Retrieving posts'
		dom = self.getDom('/users/' + self.username + '/posts?' + type + '&start=' + str(start) + '&end=' + str(end))
		domPosts = dom.getElementsByTagName('post')
		posts = []
		for domPost in domPosts:
			post = Post(domPost, resourcetype)
			posts += [post]
		return posts

class Post:
	def __init__(self, xml, resourcetype):
		self.description = xml.getAttribute('description')
		self.postingdate = xml.getAttribute('postingdate')

		self.user = User(xml.getElementsByTagName('user')[0])
		self.group = Group(xml.getElementsByTagName('group')[0])

		# tags
		xmlTags = xml.getElementsByTagName('tag')
		self.tags = []
		for xmlTag in xmlTags:
			self.tags += [ Tag(xmlTag) ]

		# resource
		if resourcetype == 'bibtex':
			self.resource = BibTex(xml.getElementsByTagName('bibtex')[0])
		elif resourcetype == 'bookmark':
			raise Exception('Not implemented yet')
		else:
			raise Exception('Type must be either "bookmark" or "bibtex"')

class Resource:
	pass

class Bookmark(Resource):
	pass

class BibTex(Resource):
	def __init__(self, xml):
		self.title = xml.getAttribute('title')
		self.author = xml.getAttribute('author')

class User:
	def __init__(self, xml):
		self.name = xml.getAttribute('name')

class Group:
	def __init__(self, xml):
		self.name = xml.getAttribute('name')

class Tag:
	def __init__(self, xml):
		self.name = xml.getAttribute('name')

# helper
def getType(type):
	if type == 'bookmark' or type == 'bibtex':
		return type;
	raise Exception('Type must be either "bookmark" or "bibtex"')


#
# Tests
#
bibsonomy = BibSonomy('YOUR_USERNAME', 'YOUR_APIKEY')
posts = bibsonomy.getPosts('bibtex')
# do something with the posts...
for post in posts:
	print post.resource.title
