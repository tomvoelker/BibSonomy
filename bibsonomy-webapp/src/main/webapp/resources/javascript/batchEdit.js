function markAll(input) {
	if(input) {	 
		var markAllChecked = input.checked;
		var inp = document.getElementById("batchedit").getElementsByTagName("input");
		for (var i = 0; i<inp.length; i++) {
			if (inp[i].type.toLowerCase() == "checkbox" && inp[i].name.toLowerCase().indexOf("posts") == 0) {
				inp[i].checked = markAllChecked;
			}
		}
	}
}

function checkMarkAll(input) {
	if(input) {
		if (input.type.toLowerCase() == "checkbox") {
			var oneNotChecked = false;
			var inp = document.getElementById("batchedit").getElementsByTagName("input");
			for (var i= 0; i<inp.length; i++) {
				if (inp[i].type.toLowerCase() == "checkbox" && inp[i].name.toLowerCase().indexOf("posts") == 0) {
					if (!inp[i].checked) {
						oneNotChecked = true;
					}
				}
			}
			document.getElementById("all").checked = !oneNotChecked;
		}
	}
}
		           
maximizeById("general");