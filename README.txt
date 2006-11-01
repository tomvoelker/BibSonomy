Bibsonomy README

1.) Getting Started

Um das Projekt in Eclipse einzurichten, ist folgendes nötig:

1. Projekt aus CVS auschecken
2. Im Projektroot mvn eclipse:eclipse ausführen
3. .project im Projektroot löschen
4. In Eclipse: File - Import - Existing Projects into Workspace
4.1 Als "root directory" Projektroot auswählen
4.2 Alle Module sollten unter "Projects" auftauchen
4.3 Sicherstellen, dass "Copy projects into workspace" aus ist
4.4 Finish
5. Alle Module werden als separate Projekte in Eclipse angezeigt
5.1 Die meisten haben Build-Fehler - Dependencies fehlen!
6. In das Verzeichnis misc/eclipse/project-descriptors wechseln und copy.sh ausführen
7. Die Projekte sollten neu gebaut werden und alle Fehler verschwinden
7.1 Meldet Eclipse, dass bspw. .project "out of sync with the filesystem"
    ist, dann Eclipse neustarten.

Sollten die Module immer noch Fehler aufweisen, liegt dies an "Build Path"-Problemen.
Dazu gibt es zwei Lösungen:
1. entweder fehlt eine Abhängigkeit (Properties - Java Build Path - Projects)
2. oder die Sourcen eines anderen Moduls müssen in das Projekt gelinkt werden.

Manchmal reicht das setzen von Abhängigkeiten zwischen den Projekten nicht aus,
da bspw. bei einer Webapp alle Sourcen nach WEB-INF/classes compiliert werden müssen,
damit der Classloader vom Tomcat sie finden kann. Dazu:

1. Properties - Java Build Path - Source
1.1 Link Source...
2. Variables...
2.1 New...
2.2 Name: WORKSPACE, Location: Eclipse Workspace auswählen
3. Extend...
3.1 Sourcefolder des Moduls auswählen, dass eingebunden werden soll
3.2 "Folder name" anpassen; statt ganzem Verzeichnis ggfls. Projektname des anderen Moduls
4. Next - Exclusion patterns - Add
4.1 **/CVS*
5. Finish


2.) Maven

Maven2 ist für den Build und insb. das Auflösen von Abhängigkeiten verantwortlich.

2.1) Maven auf der Kommandozeile installieren

1. Maven2 runterladen: http://maven.apache.org/download.html
2. Entpacken und in PATH eintragen: PATH=$PATH:/path/to/maven2/bin

2.2) Maven in Eclipse installieren

Sollte es die erste Maven2-Installation sein, legt man vorher ein .m2-Verzeichnis
im Home an. Dort werden sämtliche JARs (Dependencies oder Maven Plugins) gespeichert.

1. In Eclipse Help - Software Updates - Find and Install...
1.1 Search for new features to install - New remote site...
2. Name: Maven2 - URL: http://m2eclipse.codehaus.org/
3. Site auswählen - Plugin auswählen - Installieren

2.3) Links

* http://maven.apache.org/guides/getting-started/index.html
* http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
* http://maven.apache.org/maven-conventions.html
* http://maven.apache.org/guides/mini/guide-naming-conventions.html
* http://maven.apache.org/guides/index.html
* http://maven.apache.org/ ;-))


3.) Dokumentation

Mit Maven kann eine HTML-Dokumentation erstellt werden.

3.1) Wie

Dazu startet man im bibsonomy-Rootverzeichnis folgendes:
mvn site:stage -DstagingDirectory=/irgend/ein/verzeichnis

3.2) Links

* http://maven.apache.org/guides/mini/guide-site.html
* http://maven.apache.org/guides/mini/guide-apt-format.html
* http://maven.apache.org/plugins/maven-site-plugin/


4.) Dependencies

You can find out the dependencies with
bibsonomy2 # find . -maxdepth 2 -iname .classpath | xargs grep -i combineaccessrules