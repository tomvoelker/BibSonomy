/** marksame.js
 * Funktionen für die admin-Seite
 * 
 * Bei Klick auf die IP-Adresse in der Liste der neuen Benutzer 
 * werden alle IP-Adresse in allen Zeilen hervorgehoben.
 * 
 *    
 * 2008 Sven Stefani, sst@cs.uni-kassel.de
*/


/* an die IP-Adressen ein onclick-Event anhängen
 * oder ein globales onclick nutzen
 * 
 * class=spammertable
 * in der 4. Zelle jeder Zeile stehen die IP-Adressen
 *
 * am einfachsten ist es wohl, bei jedem klick den feldinhalt mit allen anderen zu vergleichen und 
 * bei übereinstimmung zu markieren.
 *  
*/

// globale variablen
var ms_table_id = "";
var ms_table_col = "";
var ms_tab = "";
var ms_table_tbody = "";
var ms_oldstyles = "";

function ms_init(tid,col) {
	ms_table_id = tid;
	ms_table_col = col;
	
	// hole tabellenelement
	ms_tab = document.getElementById(ms_table_id);

	var i; // laufvariable
	var countTD = 0;
	var ipvalue = "";

	// add onclick to td-elements in table with tid

//	console.log("HallO");
/*
   	console.log(ms_tab.nodeType);
	console.log(ms_tab.nodeName);
	console.log(ms_tab.nodeValue);
	console.log(ms_tab.childNodes);
	console.log(ms_tab.childNodes.length);
*/
	
	for (i = 0; i < ms_tab.childNodes.length; i++)
	{
//		console.log(i+": "+ms_tab.childNodes[i].nodeName);
		if (ms_tab.childNodes[i].nodeName == "TBODY")
		{
			ms_table_tbody = ms_tab.childNodes[i];
			break;
		}
	};
	
	if (ms_table_tbody != "")
	{
		
//		console.log(ms_table_tbody.nodeName);
//		console.log(ms_table_tbody.childNodes.length);
		for (i = 0; i < ms_table_tbody.childNodes.length; i++)
		{
			// wenn childNode ein TR ist
			if (ms_table_tbody.childNodes[i].nodeName == "TR")
			{
				countTD = 0;
//				console.log(i+": "+ms_table_tbody.childNodes[i].nodeName);
				for (var j = 0; j < ms_table_tbody.childNodes[i].childNodes.length; j++)
				{
					if (ms_table_tbody.childNodes[i].childNodes[j].nodeName == "TD")
					{
						countTD++;
						
						if (countTD==ms_table_col) // 4
						{
							ipvalue = "x";

							if ( ms_table_tbody.childNodes[i].childNodes[j].childNodes.length > 0)
							{
								ipvalue = ms_table_tbody.childNodes[i].childNodes[j].childNodes[0].nodeValue;
							}
							else
							{
								ipvalue = "none";
							}
							
//							ms_oldstyles[i] = ms_table_tbody.childNodes[i].childNodes[j].style.backgroundColor;
//							console.log(i+": "+ms_oldstyles[i]);
							
							
							ms_table_tbody.childNodes[i].childNodes[j].setAttribute('onclick','ms_marksame("'+ipvalue+'")');
//							ms_table_tbody.childNodes[i].childNodes[j].setAttribute('onclick','ms_marksame()');
/*									
							console.log("...."+ms_table_tbody.childNodes[i].childNodes[j].nodeName +"   " +ms_table_tbody.childNodes[i].childNodes[j].nodeValue);
							for (var k = 0; k < ms_table_tbody.childNodes[i].childNodes[j].childNodes.length; k++)
							{
								console.log("......."+k+"."+ms_table_tbody.childNodes[i].childNodes[j].childNodes[k].nodeName + "   "+ms_table_tbody.childNodes[i].childNodes[j].childNodes[k].nodeValue );
							}
*/						

						}
					}



				}
			}
			
		};

	
	}
	

	
	
}


function ms_marksame(ipvalue) {

	// itereire über alle zeilen der tabelle
	// vergleiche spaltenwert mit eventwert 
	// markiere ggf. den wert bzw. nimm markeirung weg. 
	// markierung besteht aus zusätzlichem style-element! 


var duplicateCount = 0;

	
//	console.log("Welcome to function ms_marksame");
//	console.log(ipvalue);

	if (ms_table_tbody != "")
	{
		
//		console.log(ms_table_tbody.nodeName);
//		console.log(ms_table_tbody.childNodes.length);
		for (i = 0; i < ms_table_tbody.childNodes.length; i++)
		{
			// wenn childNode ein TR ist
			if (ms_table_tbody.childNodes[i].nodeName == "TR")
			{
				countTD = 0;
//				console.log(i+": "+ms_table_tbody.childNodes[i].nodeName);
				for (var j = 0; j < ms_table_tbody.childNodes[i].childNodes.length; j++)
				{
					if (ms_table_tbody.childNodes[i].childNodes[j].nodeName == "TD")
					{
						countTD++;

						if (countTD==ms_table_col) // 4
						{

							if ( ms_table_tbody.childNodes[i].childNodes[j].childNodes.length > 0)
							{
								// clicked value and current value are the same
								// change style
								if (ms_table_tbody.childNodes[i].childNodes[j].childNodes[0].nodeValue == ipvalue)
								{
									// set background-color to yellow
									ms_table_tbody.childNodes[i].childNodes[j].style.backgroundColor = "#FFFFAA";
									duplicateCount++;
								}
								else
								{ // reset style
//									ms_table_tbody.childNodes[i].childNodes[j].style.backgroundColor = ms_oldstyles[i];
									ms_table_tbody.childNodes[i].childNodes[j].style.backgroundColor = "";
								} 
							}
							else
							{
								// do nothing
							}

						} // end if (countTD==ms_table_col)
					} // end if (ms_table_tbody.childNodes[i].childNodes[j].nodeName == "TD")
				} // end for
			} // end if (ms_table_tbody.childNodes[i].nodeName == "TR")
		} // end for

	// write number of same ips in status-bar
	// in newer browsers you can only write in status bar if user allowed this before.
	// in firefox 3: 
	//    Click on Extras/Einstellungen/Inhalt then activate option "Javascript aktivieren" and click "Erweitert"
	//    activate option "Statusleistentext ändern"
	if (duplicateCount>1)
	{
		top.status = "IP address "+ipvalue+" occurs in entries: " + duplicateCount;
	}
	else
	{
		top.status = "IP address "+ipvalue+" does not occur in more than this entry.";
	}	

	} // end if (ms_table_tbody != "")
	
} 

