var getKeyStack = null;
var getSavedKeysStack = null;
var getCurrentInput = null;
var getCurrentCallback = null;
{
	var stack = new Array();
	var stackSavedKeys = new Array();
	var input = null;
	var callback = null;
	getKeyStack = function() {
		return stack;
	};
	getCurrentInput = function(element) {
		if (element != undefined)
			input = element;
		return input;
	};
	getCurrentCallback = function(newCallback) {
		if (newCallback != undefined)
			callback = newCallback;
		return callback;
	};
	getSavedKeyStack = function(value) {
		if (value != undefined)
			stackSavedKeys = value;
		return stackSavedKeys;
	};
}

function getKeyString(keyCode) {
	return keyCode == 17 ? chrome.i18n.getMessage("CTRL")
			: keyCode == 18 ? chrome.i18n.getMessage("ALT")
					: keyCode == 16 ? chrome.i18n.getMessage("SHIFT")
							: keyCode == 8 ? chrome.i18n
									.getMessage("BACKSPACE")
									: keyCode == 9 ? chrome.i18n
											.getMessage("TAB")
											: keyCode == 46 ? chrome.i18n
													.getMessage("DEL")
													: keyCode == 13 ? chrome.i18n
															.getMessage("ENTER")
															: keyCode == 32 ? chrome.i18n
																	.getMessage("SPACE")
																	: String
																			.fromCharCode(keyCode);
}

function keyupGrabber(e) {
	e.preventDefault();
	var index = -1;

	if ((index = getKeyStack().indexOf(e.keyCode)) != -1) {
		getKeyStack().splice(index, 1);
		if (getKeyStack().length == 0 && getSavedKeyStack().length > 1) {
			e.target.value = "";
			for ( var i = 0; i < getSavedKeyStack().length; i++)
				e.target.value += getKeyString(getSavedKeyStack()[i])
						+ (i == getSavedKeyStack().length - 1 ? " " : " + ");
			// document.getElementById("result").innerHTML="saved!";
			chrome.extension.sendMessage({
				message : e.target.id,
				shortcut : e.target.value
			}, function(response) {
			});
		}
	}
	return !1;
}
function keydownGrabber(e) {
	e.preventDefault();

	if (getKeyStack().indexOf(e.keyCode) > -1)
		return !1;

	getSavedKeyStack().length = 0;

	if ((!getKeyStack().length && !(15 < e.keyCode && e.keyCode < 19))
			|| (getKeyStack().length > 0 && getKeyStack().indexOf(16)
					+ getKeyStack().indexOf(17) + getKeyStack().indexOf(18) == -3)) {
		getKeyStack().length = 0;
		e.target.value = "";
		return !1;
	}
	e.target.value = "";
	getKeyStack().push(parseInt(e.keyCode));

	for ( var i = 0; i < getKeyStack().length; i++) {
		e.target.value += getKeyString(getKeyStack()[i])
				+ (i == getKeyStack().length - 1 ? " " : " + ");
		getSavedKeyStack()[i] = getKeyStack()[i];
	}
	return !1;
}

function keypressGrabber(e) {
	e.preventDefault();
}