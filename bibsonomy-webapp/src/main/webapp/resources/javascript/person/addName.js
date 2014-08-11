$(document).ready(function() {
	var addNameDialog = $("#addNameDialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"Add Name to Person": function() {
				document.addPersonNameForm.submit();
				var x = $("<input/>", {type: "radio", name: "myName", value: $("#formGivenName").val() + "_" + $("#formSurName").val()})
				$("#nameList").append("<tr><td>" + $("#formGivenName").val() + " " + $("#formSurName").val() +"</td><td>" + $("<div/>").append(x).html() +"</td></tr>");
				addNameDialog.dialog( "close" );
			},
			Cancel: function() {
				addNameDialog.dialog( "close" );
			},
		},
		close: function() {
			document.AddPersonNameForm.reset();
		}
	});

	 $( "#addNameButton" ).on( "click", function(e) {
		 e.preventDefault();
		 addNameDialog.dialog("open");	
	});
});
