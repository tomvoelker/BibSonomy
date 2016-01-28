/**
 * Created by kater on 11.01.16.
 */

function RegLogger() {

	this.loggedInteractions = {};
	this.lastLoggedInteraction = {};
	this.startTime = Date.now();
	this.version = 1;
	this.referers = [];
	this.userAgents = [];
	this.errors = [];
	this.assignedNumbers = {};
	this.lastNumber = -1;

	this.log = function(event) {
		var target, type, typeNumber, cur, last, diff;
		target = this._stringToNumber(this._getTarget(event));
		type = event.type;
		typeNumber = this._stringToNumber(type);

		// check first occurence of type and target
		if (this.loggedInteractions[typeNumber] == undefined) {
			this.loggedInteractions[typeNumber] = {};
			this.lastLoggedInteraction[typeNumber] = {};
		}
		if (this.loggedInteractions[typeNumber][target] == undefined) {
			this.loggedInteractions[typeNumber][target] = [];
			this.lastLoggedInteraction[typeNumber][target] = [];
			// fill with zeros for first diff calculation. Max 3 positions
			// needed.
			for (var i = 0; i < 3; i++) {
				this.lastLoggedInteraction[typeNumber][target].push(0)
			}
		}

		cur = [ event.timeStamp - this.startTime ];
		last = this.lastLoggedInteraction[typeNumber][target];

		if (type == "select") {
			cur.push(event.target.selectionStart, event.target.selectionEnd);
			diff = [ cur[0] - last[0], cur[1], cur[2] ];
		} else if (type == "resize") {
			cur.push($(window).width(), $(window).height());
			diff = [ cur[0] - last[0], cur[1] - last[1], cur[2] - last[2] ];
		} else if (type == "scroll") {
			cur.push(event.target.scrollingElement.scrollTop);
			diff = [ cur[0] - last[0], cur[1] - last[1] ];
		} else if (type.startsWith("click") || type.startsWith("mouse")) {
			cur.push(event.clientX, event.clientY);
			diff = [ cur[0] - last[0], cur[1] - last[1], cur[2] - last[2] ];
			if (diff[0] == 0 && diff[1] == 0 && diff[2] == 0) {
				return;
			}
		} else if (type.startsWith("key") && event.target.type != "password"
				|| event.keyCode == 8) {
			cur.push(event.keyCode, event.altKey ? 1 : 0,
					event.ctrlKey ? 1 : 0, event.shiftKey ? 1 : 0);
			diff = [ cur[0] - last[0], cur[1], cur[2], cur[3], cur[4] ];
		} else {
			diff = [ cur[0] - last[0] ];
		}

		this.loggedInteractions[typeNumber][target].push(diff);
		this.lastLoggedInteraction[typeNumber][target] = cur;
	}

	this.setListener = function(obversable) {
		var currentObj = this;
		$(obversable)
				.on(
						"blur focus focusin focusout load resize scroll unload click "
								+ "dblclick mousedown mouseup mousemove mouseover mouseout mouseenter "
								+ "mouseleave change select submit keydown keypress keyup error",
						function(event) {
							currentObj.log(event);
						});
	}

	this.checkErrors = function() {
		var currentObj = this;
		var curErrors = [];
		$("span").each(function(index) {
			var id = $(this).attr("id")
			//error spans always ends with errors
			if(!(id === undefined) && id.slice(-7) == ".errors"){
				curErrors.push(currentObj._stringToNumber(id));
			}
		});
		this.errors.push(curErrors);
	}

	this._stringToNumber = function(string) {
		if (this.assignedNumbers[string] === undefined) {
			this.lastNumber = this.lastNumber + 1;
			this.assignedNumbers[string] = this.lastNumber;
			return this.lastNumber;
		} else {
			return this.assignedNumbers[string];
		}
	}

	this.addReferer = function() {
		this.referers.push(this._stringToNumber(document.referrer));
	}

	this.addUserAgent = function() {
		this.userAgents.push(this._stringToNumber(navigator.userAgent));
	}

	this._getTarget = function(event) {
		var id, name;
		id = event.target.id;
		name = event.target.name;
		if (id === undefined || id === null || id == "") {
			if (name === undefined || name === null || name == "undefined") {
				return "";
			} else {
				return name;
			}
		} else {
			return id;
		}
	}

}
