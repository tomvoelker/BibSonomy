var WS = {
	BaseHref: null,
	Key: null,
	ParentID: null,
	ClassName: null,
	Cursor: {
		x: 0,
		y: 0
	},
	Dimensions: {
		w: 0,
		h: 0,
		position: null,
		offset_x: 0,
		offset_y: 0,
		im: 0,
		iw: 0,
		ih: 0
	},
	bolInitDone: null,
	Url: null,
	Bubble: document.getElementById("WSBubble") || {},
	opacTimer: null,
	Method: null,
	init: function() {
		if (WS.bolInitDone != null) {
			return
		}
		WS.BaseHref = "http://bubble.websnapr.com/";
		WS.ClassName = "websnapr";
		WS.addEvent(window, ["mousemove"], WS.getCurPos);
		WS.addEvent(window, ["resize"], WS.getDimensions);
		WS.addEvent(window, ["scroll"], WS.getDimensions);
		var B = WS.fetchElements();
		var A = B.length;
		for (i = 0; i < A; i++) {
			WS.Url = B[i].href;
			if (WS.BubbleStyle == "i") {
				var C = document.createElement("img");
				C.setAttribute("src", WS.BaseHref + "images/websnapr7.gif");
				
				C.setAttribute("border", 0);
				C.className = "wsbubbleicon";
				WS.addEvent(C, ["mouseover"],
				function() {
					WS.showBubble(this.parentNode)
				});
				WS.addEvent(C, ["mouseout"],
				function() {
					WS.hideBubble()
				});
				B[i].appendChild(C)
			} else {
				WS.addEvent(B[i], ["mouseover"],
				function() {
					WS.showBubble(this)
				});
				WS.addEvent(B[i], ["mouseout"],
				function() {
					WS.hideBubble()
				})
			}
		}
		WS.createBubble();
		WS.createCSS();
		WS.bolInitDone = true
	},
	createCSS: function() {
		if (document.createStyleSheet) {
			with(document.createStyleSheet()) {
				addRule("#WSBubble", "display:none; overflow:hidden; position: absolute;");
				addRule("#WSBubble img", "margin:9px;");
				addRule("#WSBubble.small", "width:220px; height:170px; background-image:url(" + WS.BaseHref + "images/wsbg_0.gif);");
				addRule("#WSBubble.large", "width:418px; height:318px; background-image:url(" + WS.BaseHref + "images/wsbg_1.gif);");
				addRule(".wsbubbleicon", "margin-left:5px")
			}
		} else {
			var head = document.getElementsByTagName("head")[0];
			var style = document.createElement("style");
			style.setAttribute("type", "text/css");
			var csstext = "#WSBubble		{ display:none; overflow:hidden; position: absolute; }\n";
			csstext += "#WSBubble img 	{ margin:9px; }\n";
			csstext += "#WSBubble.small { width:220px; height:170px; background:url(" + WS.BaseHref + "images/wsbg_0.gif); }\n";
			csstext += "#WSBubble.large { width:418px; height:318px; background:url(" + WS.BaseHref + "images/wsbg_1.gif); }\n";
			csstext += ".wsbubbleicon {margin-left:5px; }\n";
			var css = document.createTextNode("\n<!--\n" + csstext + "\n-->\n");
			style.appendChild(css);
			head.appendChild(style)
		}
	},
	showBubble: function(D) {
		var B = D.href;
		WS.getCurPos();
		WS.getDimensions(D);
		var C = WS.Dimensions;
		WS.Url = B;
		var A = WS.createWSUrl(D.className);
		WS.Bubble = document.getElementById("WSBubble");
		WS.Bubble.style.left = (WS.Cursor.x + WS.Dimensions.offset_x) + "px";
		WS.Bubble.style.top = (WS.Cursor.y + WS.Dimensions.offset_y) + "px";
		WS.Bubble.style.display = "block";
		WS.Bubble.innerHTML = '<img src="' + A + '">';
		WS.Bubble.className = WS.getImgSize(D.className, "cn")
	},
	hideBubble: function() {
		clearTimeout(WS.opacityTimer);
		WS.Bubble = document.getElementById("WSBubble");
		WS.Bubble.innerHTML = "";
		WS.Bubble.style.display = "none"
	},
	createBubble: function() {
		var A = document.createElement("div");
		A.id = "WSBubble";
		A.className = "small";
		document.getElementsByTagName("body")[0].appendChild(A)
	},
	createWSUrl: function(B) {
		B = (B == "" ? WS.ClassName: B);
		var A = Math.random(100);
		return ("http://" + WS.Domain + ".websnapr.com/?url=" + encodeURIComponent(WS.Url) + "&key=" + encodeURIComponent(WS.Key) + "&nocache=" + A + "&size=s")
	},
	getDimensions: function(B) {
		var G = B.className;
		var L = WS.Cursor.x;
		var I = WS.Cursor.y;
		var J = WS.getImgSize(G, "m");
		var A = WS.getImgSize(G, "w");
		var N = WS.getImgSize(G, "h");
		var D = WS.Dimensions;
		if (document.documentElement) {
			var F = document.documentElement
		}
		var E = window;
		if (typeof(E.innerWidth) == "number") {
			var M = E.innerWidth;
			var C = E.innerHeight;
			var K = E.pageXOffset;
			var H = E.pageYOffset
		} else {
			if (F && (F.clientWidth || F.clientHeight)) {
				var M = F.clientWidth;
				var C = F.clientHeight;
				var K = F.scrollLeft;
				var H = F.scrollTop
			}
		}
		D.w = M;
		D.h = C;
		if ((L - K) < A && (I - H) < N) {
			D.position = "TL";
			D.offset_x = J;
			D.offset_y = J
		}
		if ((L - K) > (M / 2) && (I - H) < N) {
			D.position = "TR";
			D.offset_x = -A - J;
			D.offset_y = J
		}
		if ((L - K) < A && (I - H) > (C / 2)) {
			D.position = "BL";
			D.offset_x = J;
			D.offset_y = -N - J
		}
		if ((L - K) > (M / 2) && (I - H) > (C / 2)) {
			D.position = "BR";
			D.offset_x = -A - J;
			D.offset_y = -N - J
		}
		if (D.position == null) {
			D.position = "TL";
			D.offset_x = J;
			D.offset_y = J
		}
	},
	getImgSize: function(B, A) {
		var C = "";
		if (A == "w") {
			C = 220
		}
		if (A == "h") {
			C = 170
		}
		if (A == "m") {
			C = 20
		}
		if (A == "cn") {
			C = "small"
		}
		return C
	},
	addEvent: function(E, B, D) {
		var C;
		for (var A = 0; A < B.length; A++) {
			C = B[A];
			if (E.attachEvent) {
				E["e" + C + D] = D;
				E[C + D] = function() {
					E["e" + C + D](window.event)
				};
				E.attachEvent("on" + C, E[C + D])
			} else {
				E.addEventListener(C, D, false)
			}
		}
	},
	fetchElements: function() {
		var targetUrlObj = new Array();
		var tagObj = new Array();
		var parentObj = new Array();
		if (WS.ParentID != null) {
			if (WS.Method === "c") {
				var parentObj = WS.getElementsByClassName(WS.ParentID)
			} else {
				parentObj[0] = document.getElementById(WS.ParentID)
			}
			var bolForceClass = false
		} else {
			parentObj[0] = document;
			var bolForceClass = (WS.Method == "w")
		}
		for (var k = 0; k < parentObj.length; k++) {
			tagObj = parentObj[k].getElementsByTagName("a");
			for (i = 0; i < tagObj.length; i++) {
				var ob = tagObj[i];
				if (ob.href.search(eval("/^(http:|https:)/g")) == -1) {
					continue
				}
				if (bolForceClass) {
					var arrClasses = ob.className.split(" ");
					var bol_found = false;
					for (var j = 0; j < arrClasses.length; j++) {
						bol_found = bol_found || arrClasses[j] == WS.ClassName
					}
					if (!bol_found) {
						continue
					}
				}
				targetUrlObj = WS.mergeArray(targetUrlObj, ob)
			}
		}
		return targetUrlObj
	},
	getCurPos: function(C) {
		var B, A;
		var D = {
			x: 0,
			y: 0
		};
		if (!C) {
			C = window.event
		}
		if (!C) {
			return
		}
		if (typeof(C.pageX) == "number") {
			B = C.pageX;
			A = C.pageY
		} else {
			if (typeof(C.clientX) == "number") {
				B = C.clientX;
				A = C.clientY;
				if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
					B += document.body.scrollLeft;
					A += document.body.scrollTop
				} else {
					if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
						B += document.documentElement.scrollLeft;
						A += document.documentElement.scrollTop
					}
				}
			} else {
				return
			}
		}
		WS.Cursor = {
			x: B,
			y: A
		}
	},
	mergeArray: function(B) {
		var A = B;
		for (var C = 1; C < arguments.length; C++) {
			A = A.concat(arguments[C])
		}
		return A
	},
	getElementsByClassName: function(D) {
		var A, C = new Array(),
		B = 0,
		E;
		if (document.all) {
			A = document.all
		} else {
			if (document.getElementsByTagName && !document.all) {
				A = document.getElementsByTagName("*")
			}
		}
		for (i = 0; i < A.length; i++) {
			if (A[i].className.indexOf(D) != -1) {
				E = "," + A[i].className.split(" ").join(",") + ",";
				if (E.indexOf("," + D + ",") != -1) {
					C[B] = A[i];
					B++
				}
			}
		}
		return C
	}
};
WS.Key = "4Jp4m4r6A0W4";
WS.Method = "c";
WS.BubbleStyle = "i";
WS.Domain = "images";
WS.addEvent(window, ["load"], WS.init);
