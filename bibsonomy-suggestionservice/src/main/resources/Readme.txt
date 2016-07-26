====
    BibSonomy - A blue social bookmark and publication sharing system.

    Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
                                  University of Kassel, Germany
                                  http://www.kde.cs.uni-kassel.de/
                              Data Mining and Information Retrieval Group,
                                  University of Würzburg, Germany
                                  http://www.is.informatik.uni-wuerzburg.de/en/dmir/
                              L3S Research Center,
                                  Leibniz University Hannover, Germany
                                  http://www.l3s.de/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
====

####################################  Suggest Tree Einrichtung und Funktionalität  #############################################################

######################################## Einrichtung ########################################

Bevor der Suggest tree genutzt werden kann muss in der webapp (bibsonomy-webapp) die project.properties >gegebenenfalls< angepasst werden, 
wenn man einen anderen Pfad verwenden möchte.

Standardmäßig ist die Variable folgend gesetzt:
titleSuggestion.sourceFilePath = ${project.files.home}/titleSuggestion/

An dieser Stelle muss ein Ordner erstellt werden mit dem Namen "titleSuggestion".
${project.files.home} ist normalerweise auf das User Verzeichnis des Rechners eingestellt (project.files.home = ${user.home})

Ist der Ordner (mit exakt diesem Namen !) erstellt, so müssen 2 Dateien (mit exakt diesen Namen !) in den Ordner kopiert werden:

bookmark_title.txt
publication_title.txt

Beispieldateien findet ihr im bibsonomy-recommender-tree Projekt unter:
src.test.ressources 

Ihr könnt euch später auch selbst welche erstellen (Erklärung weiter unten in Funktionalität).

Habt ihr dies eingerichtet, werdet ihr beim Tomcat Start nicht mehr diese warnings im log haben:

[ WARN] [03 Okt 2013 15:46:49,528] [main] [] [org.bibsonomy.logic.SuggestionLogic] - Source File 'publication_title.txt' NOT found in path : /Users/nilsraabe/titleSuggestion//  - Cannot build tree !
[ WARN] [03 Okt 2013 15:46:49,529] [main] [] [org.bibsonomy.logic.SuggestionLogic] - Source File 'bookmark_title.txt' NOT found in path : /Users/nilsraabe/titleSuggestion//  - Cannot build tree !

Sondern diese:

[ INFO] [03 Okt 2013 17:14:08,794] [main] [] [org.bibsonomy.logic.SuggestionLogic] - building publication tree
[ INFO] [03 Okt 2013 17:14:08,804] [main] [] [org.bibsonomy.logic.SuggestionLogic] - finished building publication tree done.
[ INFO] [03 Okt 2013 17:14:08,804] [main] [] [org.bibsonomy.logic.SuggestionLogic] - building bookmark tree
[ INFO] [03 Okt 2013 17:14:08,815] [main] [] [org.bibsonomy.logic.SuggestionLogic] - finished building bookmark tree done.

Ist alles fertig eingerichtet und die Tomcat warnings erscheinen nicht mehr, ist alles fertig konfiguriert.
Nun könnt ihr im Browser auf http://localhost:8080/ oben im Suchfeld etwas eingeben, und es sollten passende Recommendations folgen.

Fertig !

**** Tomcat logs live in der console anschauen mit [tail -f {TomcatHome}/logs/*] 

######################################## Funktionalität des Suggest Tree ########################################

Das Subprojekt bibsonomy-recommender-tree stellt eine SuggestionLogic für die webapp bereit, 
sodass zu gegebenen queries, die im Suchfeld von BibSonomy im Header eingegeben werden, 
eine passende recommendation geliefert werden kann (Volltextsuche über die Post Titel derzeit).

######### 1 - Erstellen der 2 Dateien "bookmark_title.txt" und "publication_title.txt" ##########################

In dem Subprojekt bibsonomy-batch liegt eine Perl Datei mit dem Namen batch_suggestion_title.pl (unter src.build.batch_scripts)
Dieses Script greift beim ausführen auf eine Datenbank zu.
Welche Datenbank er nimmt, müsst ihr auf eurem Rechner in der .bash_profile definieren:

export SLAVE_DB="bibsonomy"
export SLAVE_USER="root"
export SLAVE_PASS="****"
export SLAVE_HOST="localhost"
export SLAVE_SOCK="/tmp/mysql.sock"
export SLAVE_PORT="3306"

Ich habe bei mir auf dem Rechner eine lokale Bibsonomy Datenbank aufgesetzt. Mit: 
Name = bibsonomy
Benutzer = root
Passwort = root

Müsst ihr gegebenenfalls anpassen wenn ihr einen anderen Datenbanknamen etc. habt.

Führt ihr das Script nun aus müsste es euch 2 Dateien ausspucken, welche genau die Namen
bookmark_title.txt
publication_title.txt
tragen.

Zusammengefasst holt sich das Script die Namen und die Bewertung jeder eingetragenen Publikation 
und jedes eingetragenen Lesezeichens und speichert sie in der jeweiligen Datei.

Mit diesen Dateien können wir nun unseren Baum beim Tomcat Start befüllen, indem wir sie manuell hierhin verschieben:

titleSuggestion.sourceFilePath = ${project.files.home}/titleSuggestion/


############################################## Server Startup ################################################

Startet ihr den Tomcat wird nach der bibsonomy2-servlet.xml (bibsonomy-webapp: src.main.webapp.WEB-INF) die Java Beans erstellt.
Dort ist ein Eintrag:

	<!-- provides access to the post suggestion --> 
 	<bean id="suggestionLogic" class="org.bibsonomy.logic.SuggestionLogic">
		<property name="sourceFilePath" value="${titleSuggestion.sourceFilePath}"/>
	</bean>

Es wird also eine java Bean mit der id suggestionLogic von der Java Klasse org.bibsonomy.logic.SuggestionLogic erstellt.
(Die Klasse SuggestionLogic, die sich in bibsonomy-recommender-tree befindet)
Als Parameter wird bei der Initialisierung der Pfad zu dem Ordner mit übergeben, der der in der project.properties hinterlegt ist.

Der Baum wird "gebaut" und gibt die Logs auf der Konsole über den Status aus.

Der Server ist nun gestartet.

############################################## Zur Laufzeit - JSON ################################################

Ruft ihr nun folgende Seite auf:

http://localhost:8080/suggestion?postType=bookmark&postPrefix=Com

Werden euch alle Titel zu dem query "Com" im JSON Format zurück gegeben.
[postType Parameter derzeit noch ohne Funktion, siehe unten]

In der bibsonomy2-servlet.xml ist die oben aufgerufene Seite definiert:

	<entry key="/suggestion">
		<bean parent="controllerBase">
			<property name="controllerBeanName" value="suggestionController"/>
			<property name="allowedFields">
				<list>
					<value>postType</value>
					<value>postPrefix</value>
				</list>
			</property>
		</bean>
	</entry>

 Wird diese Seite aufgerufen wird folgender Controller aufgerufen [initialisiert und abgearbeitet] (auch in bibsonomy2-servlet.xml):
 
 	<bean id="suggestionController" class="org.bibsonomy.webapp.controller.ajax.SuggestionController" scope="request">
			<property name="suggestionLogic" ref="suggestionLogic"/>
	</bean>
 
 In diesem Controller werden derzeit ungeachtet des postType Parameters (in der URL) alle passenden Suggestions an die JSON View zurück gegeben.
 Man könnte sich aus dem command den postType heraus ziehen und z.B. nur Bookmark oder Publication Title zurück geben.
 Derzeit werden beide ungeachtet des Parameters zurück gegeben.
 
 Die Logik ist in der SuggestionLogic.java schon implementiert:
 
 	public List<Pair<String, Integer>> getPublicationSuggestion(String prefix) 
	public List<Pair<String, Integer>> getBookmarkSuggestion(String prefix)
	public List<Pair<String, Integer>> getPostSuggestion(final String prefix)  <- Liefert Publication + Bookmark
 
 Sebastian hatte einen Design Vorschlag gemacht, nach dem man zwischen Bookmark und Publication mit einem Bildchen neben dem Vorschlag unterscheiden kann (auf der Website direkt).
 Deswegen habe ich das schon mal soweit vorbereitet.
 
 ############################################## Zur Laufzeit - Javascript ################################################
 
 Gibt man auf BibSonomy im Suchfeld etwas ein, wird eine Javascript Funktion aufgerufen.
 
 Der Listener wurde folgend initialisiert:
 
 Klickt der User auf der Webseite auf search (oben links) und wählt etwas aus, wird eine Funktion aufgerufen.
 Dies ist definiert in der search.tagx (webapp). 
 Die aufgerufene Funktion switchNavi(X,X) liegt in der style.js
 
 Am Ende der switchNavi Funktion:
 
 	if(scope == "search") {
		startPostAutocompletion($("#inpf"));
	}
 
 Wählt der User "search" aus, wird die Funktion startPostAutocompletion($("#inpf")) aufgerufen.
 Diese Funktion liegt in der functions.js (webapp). 
 
Die Funktion startPostAutocompletion registriert eine jQuery Autocompletion auf dem Eingabefeld.
Gibt der User etwas ein wird diese ausgeführt und holt sich vereinfacht den query aus dem Eingabefeld,
holt sich die Titel über die oben beschriebene JSON Abfrage und gibt diese aus.

Das wars soweit :)

############################################### MÖGLICHE TODO'S #######################################################

Die TODO's auf jeden Fall mit Stephan (oder anderem Mitarbeiter) absprechen !

1. SuggestionController.java auf verschiedene postType Parameter der URL anpassen 
	postType=bookmark		->	gebe nur bookmark Titel im JSON zurück
	postType=publication	->	gebe nur publication Titel im JSON zurück
	postType=post			->	gebe nur post Titel im JSON zurück
	
2.	Automatisiert das Perl Script ausführen und die Text Dateien 
	bookmark_title.txt
	publication_title.txt
	in das Live System BibSonomy einpflegen bzw. den Baum neu builden.
	
	Dies sollte ähnlich wie bei dem Lucene Index geschehen.
	Ansatzpunkt: Ähnlich wie in bibsonomy2-servlet-cronjobs.xml
	
3.	Zukunftsmusik:
	Tags könnten auch in einem Suggest Tree gespeichert werden und nach Sebastians Vorschlag recommended werden.
	Dann könnten die zwei Funktionen 
	startTagAutocompletion
	startPostAutocompletion
	aus der functions.js vielleicht zusammengeführt werden. Nach derzeitigem Stand wäre dies aber unsinnig gewesen,
	weil ich noch nicht wusste wo die Reise genau hingeht.
	Dies dürfte ein größerer Aufwand sein und einem viel Denk-Arbeit abverlangen.	


############################################### ZUSATZ ####################################################### 

################################# Perl Script auf BibLicious ausführen #######################################

Wollt ihr das Perl Script mit den Daten von BibLicious ausführen (ca. 130 mb große .txt Dateien !),
müsst ihr dies über einen Admin wegen der Rechte tuen. Nik hat mir dabei sehr geholfen.
Er musste damals manuell NUR die exports für den SLAVE einpflegen und dann das Script ausführen:

export MASTER_DB="bibsonomy"
export MASTER_USER="bibsonomy"
export MASTER_PASS="****"
export MASTER_HOST="biblicious.org"
export MASTER_SOCK="/var/run/mysqld/mysqld.sock"
export MASTER_PORT="3308"

export SLAVE_DB="bibsonomy"
export SLAVE_USER="bibsonomy"
export SLAVE_PASS="****"
export SLAVE_HOST="biblicious.org"
export SLAVE_SOCK="/var/run/mysqld/mysqld.sock"
export SLAVE_PORT="3308"

