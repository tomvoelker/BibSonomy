function markAll(input, update, resourceType) {
	if(input) {	 
		var markAllChecked = input.checked;
		var inp = document.getElementById("batchedit").getElementsByTagName("input");
		for (var i = 0; i<inp.length; i++) {
			if (inp[i].type.toLowerCase() == "checkbox" && inp[i].name.toLowerCase().indexOf("posts") == 0) {
				inp[i].checked = markAllChecked;
			}
		}

		if (markAllChecked) {
			$("#buttonDiv").empty();
			var deleteMessage = getString("batchedit.deleteAll");
			var ignoreMessage = getString("batchedit.saveAll");
			var normalizeMessage = getString("batchedit.normalizeAllBibtexKeys");
			if(update) {
				$("#buttonDiv").append("<th><div>" + deleteMessage + " <input type='checkbox' name='delete' style='vertical-align: middle;' value='true'/></div></th>");
			} else {
				$("#buttonDiv").append("<th><div>" + ignoreMessage + " <input type='checkbox' name='delete' style='vertical-align: middle;' value='true'/></div></th>");
			}
			if(resourceType.indexOf("bibtex") != -1) {
				$("#buttonDiv").append("<th><div>" + normalizeMessage + " <input type='checkbox' name='normalize' style='vertical-align: middle;' value='true'/></div></th>");
			}
		} else {
			$("#buttonDiv").empty();
		}

	}
}

function checkMarkAll(input, update, resourceType) {
	if(input) {
		if (input.type.toLowerCase() == "checkbox") {
			var oneNotChecked = false;
			var minOneChecked = false;
			var inp = document.getElementById("batchedit").getElementsByTagName("input");
			for (var i= 0; i<inp.length; i++) {
				if (inp[i].type.toLowerCase() == "checkbox" && inp[i].name.toLowerCase().indexOf("posts") == 0) {
					minOneChecked |= inp[i].checked;

					if (!inp[i].checked) {
						oneNotChecked = true;
					}
				}
			}

			if(!minOneChecked) {
				$("#buttonDiv").empty();
			} else {
				$("#buttonDiv").empty();
				var deleteMessage = getString("batchedit.deleteAll");
				var ignoreMessage = getString("batchedit.saveAll");
				var normalizeMessage = getString("batchedit.normalizeAllBibtexKeys");
				if(update) {
					$("#buttonDiv").append("<th><div>" + deleteMessage + " <input type='checkbox' name='delete' style='vertical-align: middle;' value='true'/></div></th>");
				} else {
					$("#buttonDiv").append("<th><div>" + ignoreMessage + " <input type='checkbox' name='delete' style='vertical-align: middle;' value='true'/></div></th>");
				}
				if(resourceType.indexOf("bibtex") != -1) {
					$("#buttonDiv").append("<th><div>" + normalizeMessage + " <input type='checkbox' name='normalize' style='vertical-align: middle;' value='true'/></div></th>");
				}
			}

			document.getElementById("all").checked = !oneNotChecked;
		}
	}
}
		           
maximizeById("general");