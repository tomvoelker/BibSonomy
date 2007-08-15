/* set tag_box sytle */
function changeTagBox(mode){
	if(mode == "list"){
		setTagBoxList();
	}else if(mode == "cloud"){
		setTagBoxCloud();		
	}else if(mode == "alph"){
		setTagBoxAlph();
	}else if(mode == "freq"){
		setTagBoxFreq();
	}
}

/* change tag_box to list */
function setTagBoxList(){
	var ulmode = document.getElementById("ultag");
	var linklist = document.getElementById("mklist");
	var linkcloud = document.getElementById("mkcloud");
	if(ulmode.className != "taglist"){
		ulmode.className = "taglist";
		// change list|cloud links
		linklist.href = "";
		linkcloud.href = "?tagShow=cloud";
		// set cookie
		var date = new Date();
		var age = date.getTime() + (1000*3600*24*365);
		date.setTime(age);
		document.cookie = 'tagShow=list; path=/; expires=' + date.toGMTString()
	}
}

/* change tag_box to cloud */
function setTagBoxCloud(){
	var ulmode = document.getElementById("ultag");
	var linklist = document.getElementById("mklist");
	var linkcloud = document.getElementById("mkcloud");
	if(ulmode.className != "tagcloud"){
		ulmode.className = "tagcloud";
		// change list|cloud links
		linklist.href = "?tagShow=list";
		linkcloud.href = "";
		// set cookie
		var date = new Date();
		var age = date.getTime() + (1000*3600*24*365);
		date.setTime(age);
		document.cookie = 'tagShow=cloud; path=/; expires=' + date.toGMTString()
	}
}

 	var request;
    function ajaxInit(){
    	var req;
      	try{
        	if(window.XMLHttpRequest){
          		req = new XMLHttpRequest();
        	}else if(window.ActiveXObject){
          		req = new ActiveXObject("Microsoft.XMLHTTP");
        	}else{
          		alert("no ajax");
        	}
        	if( req.overrideMimeType ) {
            	req.overrideMimeType('text/xml');
        }
        	
     	} catch(e){
     	   	alert("error: " + e);
     	}
     	request = req;
    }
    
    function setTagBoxAlph(){
    	ajaxInit();
		var url = "http://kde-pool6:8080/ajax/ajaxtest.jsp?sortOder=alph";
		request.open('GET', url, true);
		request.onreadystatechange = hinzufuegen;
		request.send(null);
    }
    
    function setTagBoxFreq(){
    	ajaxInit();
		var url = "http://kde-pool6:8080/ajax/ajaxtest.jsp?sortOder=freq";
		request.open('GET', url, true);
		request.onreadystatechange = hinzufuegen;
		request.send(null);
    }
    
    function hinzufuegen(){
    	if( 4 == request.readyState ) {
        	if( 200 == request.status ) {
          		var result = request.responseXML;
          		var rightbox = document.getElementById("rightbox");
          		var tagbox = document.getElementById("ultag");
         	    var tags = tagbox.getElementsByTagName("li");
			    for (x=0; x<tags.length; x++) {
			    	tagbox.removeChild(tags[x]);
			    }
			}	
        }
    }
