var ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR = 'input[name="abstractGrouping"]';
var OTHER_GROUPING_CLASS_SELECTOR = '.otherGroupsBox';

var GROUPS_CLASS = 'groups';
var PUBLIC_GROUPING = 'public';
var PRIVATE_GROUPING = 'private';
var OTHER_GROUPING = 'other';
var FRIENDS_GROUP_NAME = 'friends';


$(function() {	
	
	// TODO: move and use in post edit views
	$(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	 
	$.each($('.abstractGroupingGroup'), function(index, box) {
		toggleGroupBox(box);
	});
});


// TODO: rename

// TODO: move and use in post edit views
function onAbstractGroupingClick() {
	toggleGroupBox($(this).parent());
}

// TODO: move and use in post edit views
function toggleGroupBox(radioButtonGroup) {
	// find the checked abstract grouping
	var selectedAbstractGrouping = $(radioButtonGroup).children('input:checked');
	
	// find otherGroupsBox of the abstract grouping
	var otherBox = $(radioButtonGroup).siblings(OTHER_GROUPING_CLASS_SELECTOR);
	
	// disable groups select if private or public is checked or enable
	// if other is checked
	if (!selectedAbstractGrouping.hasClass('otherGroups')) {
		
		/*var Options = otherBox.options;
		  //  var elements = document.getElementById("ddBusinessCategory").options;

		for(var i = 0; i < Options.length; i++){
		     Options[i].selected = false;
		 }*/
		//otherBox.attr('selected', false);
		otherBox.attr('disabled', 'disabled');
	} else {
		otherBox.removeAttr('disabled');
	}
}
function clearSelected(element){
	var Options = element.options;
 
    for(var i = 0; i < Options.length; i++){
      Options[i].selected = false;
    }
  };
