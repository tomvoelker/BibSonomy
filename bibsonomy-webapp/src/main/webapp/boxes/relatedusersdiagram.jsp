<h3 style="display: none;" id="tag"><c:out value="${param.requTag}" /></h3>
<div id="paintarea" style="height: 500px; text-align: left"></div>
<!-- common, all times required, imports -->
<!--REGEXP_START_REMOVE-->
<SCRIPT src="/resources/javascript/draw2d/wz_jsgraphics.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/events.js"></SCRIPT>

<SCRIPT src="/resources/javascript/draw2d/debug.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/dragdrop.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/Color.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/Point.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/Border.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/LineBorder.js"></SCRIPT>

<SCRIPT src="/resources/javascript/draw2d/Figure.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/Label.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/Rectangle.js"></SCRIPT>

<SCRIPT src="/resources/javascript/draw2d/Line.js"></SCRIPT>
<SCRIPT src="/resources/javascript/draw2d/Canvas.js"></SCRIPT>
<!--REGEXP_END_REMOVE--> 
<script>

var workflow = new Canvas("paintarea");		
	
/* Selecting all links where href contains "/user/" */	
var all_links = document.getElementsByTagName("a");
var users = new Array();

for(var l = 0; l < all_links.length; l++) {
	if(all_links[l].href.match("/user/") != null && all_links[l].title.match("weight") != null) {
		users.push(all_links[l]);
	}
}

var num_users = 10/*users.length*/;
		
/* some variables for arranging the users on an ellipse */
var centerx = window.innerWidth - (window.innerWidth * 0.25 / 2) - window.outerWidth/window.innerWidth * 20;
var centery = (630 + (70 * (screen.width / window.innerWidth))) * (screen.width / window.innerWidth);
var radiusx = window.innerWidth * 0.25 / 3;
var radiusy = 150;
var speed = 360 / num_users;
var angle = 0;

/* The box to display the tag */
var tagRect = new Rectangle();
tagRect.setDimension(70,30);
tagRect.setBackgroundColor(new Color(255, 255, 255));

workflow.addFigure(tagRect,centerx-35,centery-15);

var tagLabel = new Label();
tagLabel.setDimension(70,30);
tagLabel.setText(document.getElementById("tag").innerHTML);
tagLabel.setFontSize(10);

workflow.addFigure(tagLabel,centerx-38,centery-8);

for(var k = 0; k < num_users; k++) {
	
	/* stetting the angle */
	angle < (360-speed) ? angle+=speed : angle=0;
	
	/* assign a user for better handling */
	var user = users[k];
	
	/* we need the float value of the title */
	var regex = /\bw.+\s(\w.+)/; regex.exec(user.title);
	var weight = parseFloat(RegExp.$1);

	/* User box */
	var userRect = new Rectangle();
	userRect.setDimension(70,30);
	userRect.setBackgroundColor(new Color(255, 255, 255));
	
	workflow.addFigure(userRect, centerx+Math.sin(angle*Math.PI/180)*(radiusx - radiusx * weight) - 35, centery-Math.cos(angle*Math.PI/180)*(radiusy - radiusy * weight) - 15);
	
	/* user label - should be link */				
	var userLabel = new Label();
	userLabel.setDimension(70,30);
	userLabel.setFontSize(8);
	userLabel.setLinkText(user.innerHTML, user.href);
	
	workflow.addFigure(userLabel, centerx+Math.sin(angle*Math.PI/180)*(radiusx - radiusx * weight) - 38, centery-Math.cos(angle*Math.PI/180)*(radiusy - radiusy * weight) - 5);

	/* The line between the tag box and the users - always starts in the middle of the window */
	var line = new Line();	
	line.setStartPoint(centerx, centery);	
	line.setEndPoint(centerx+Math.sin(angle*Math.PI/180)*(radiusx - radiusx * weight), centery-Math.cos(angle*Math.PI/180)*(radiusy - radiusy * weight));
	workflow.addFigure(line);
			
}
</script>