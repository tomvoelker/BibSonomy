BibSonomy README

1.) Getting Started

Getting you started in this project should be easy. If you are not familiar with
Maven you should install it first. You can fetch the commandline version from
http://maven.apache.org/download.html. Just get the latest version of Maven2
which is important, because this is a Maven2-Project. Unpack the file you
downloaded and attach "/path/to/maven2/bin" to your Path-Variable (e.g.
PATH=$PATH:/path/to/maven2/bin).

Once you're ready with that create a ".m2"-directory in your homedirectory. This
step is necessary, because we want to download a Maven plugin for Eclipse and it
gets confused if the directory isn't there. So open your Eclipse IDE and click
on "Help", "Software Updates" and on "Find and Install". Select that you want to
install a new feature and add a new remote site. As the name enter want every
you like (e.g. Maven2) and let the URL point to http://m2eclipse.codehaus.org/.
In the following dialogs simply select the latest version of Maven2 and let
Eclipse install it.

If you're done with that open a shell and change into the projectdirectory.
Inside this directory you can invoke Maven; don't try it outside the directory
because this will just not work. When you run Maven for the first time it'll
download all JARs your project is depending on. So type 'mvn compile' and wait
until it's finished. Once it's done you can get back to Eclipse and start
developing. Notice the "Maven2 Dependencies"-library in the "Package
Explorer"-view; all dependencies are inside it.

If you want to know more about Maven then here's a list of links which migth be
interesting for you:

* http://maven.apache.org/guides/getting-started/index.html
* http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
* http://maven.apache.org/maven-conventions.html
* http://maven.apache.org/guides/mini/guide-naming-conventions.html
* http://maven.apache.org/guides/index.html
* http://maven.apache.org/ ;-))


2.) Documentation

Maven has got its own documentation-mechanism. It's simply called a site. A site
can be rendered to HTML so you can easily browse and read it. Just have a look
inside the "src/site"-folder, where you can find the existing documentation. If
you want to write documentation it's probably helpfull to read the following:

* http://maven.apache.org/guides/mini/guide-apt-format.html
* http://maven.apache.org/guides/mini/guide-site.html

To generate the site type 'mvn site', wait until it's finished and open the
"target/site"-folder with you browser. You can now browse the documentation.
