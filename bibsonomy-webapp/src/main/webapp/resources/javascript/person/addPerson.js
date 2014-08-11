$(document).ready(function() {
	var names = [{role: "1", givenName: "Christian", surName: "Pfeiffer", label: "Christian Pfeiffer"},
	             {role: "2", givenName: "Christ", surName: "Da", label: "Christ Da"},
	             {role: "3", givenName: "Chr", surName: "A", label: "blablab bal"}];
	$("#addPersonAuto").autocomplete({
		source: names,
		select: function(event, ui) {
			$("#addPersonAuto").val(ui.item.label);
			$("#givenNameAdd").val(ui.item.givenName);
			$("#surNameAdd").val(ui.item.surName);
			$("#roleAdd").val(ui.item.role);
		}
	});
	$("#addPersonAuto").autocomplete("widget").css('z-index',1000);
	 
	var addPersonDialog = $("#addPersonDialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		modal: true,
		buttons: {
			"Add Person": function() {
				var subID = $(this).parent().parent().parent().parent().attr("data-latestid") + 1;
				$(this).parent().parent().parent().parent().attr("data-latestid", subID);
				var id = $("#addPersonId").val();
				var x = $("<a/>", {href: "#", text: "E", "style": "color:red", "class":'editPersonRole'});
				var y = $("<a/>", {href: "#", text: "D", "style": "color:red", "class":'deletePersonRole'});
				$("#"+id).find("tbody").append('<tr class="personLine"><td class="personColoumn">' + $("#givenNameAdd").val() + " " + $("#surNameAdd").val() +'</td><td class="roleColoumn">'+$("#roleAdd").val()+"</td><td>" + $("<div/>").append(x).html() + $("<div/>").append(y).html() +"</td></tr>");
				addPersonDialog.dialog("close");
				refresh();
			},
			Cancel: function() {
				addPersonDialog.dialog( "close" );
			},
		},
		close: function() {
			document.addPersonForm.reset();
		}
	});
	
	$(".addPerson").on("click", function(e) {
		var id = $(this).parent().parent().parent().parent().attr("id");
		$("#addPersonId").val(id);
		addPersonDialog.dialog("open");
	});
});
