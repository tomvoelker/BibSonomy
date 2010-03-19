
$(document).ready(function(){
	deselectAll(document.recConfig.activeRecs);
	deselectAll(document.recConfig.disabledRecs);
});

function pushSelectedOptions(from, to){
  while(from.selectedIndex != -1){
    selectedOption = from.options[from.selectedIndex];
    newOption = new Option(selectedOption.text, selectedOption.value, false, false);

    if(document.all) to.add(newOption, 0);  //for msie
    else to.add(newOption, to.options[0]);

    from.remove(from.selectedIndex);
    from.focus();
  }
}

function selectAll(selectField){
    selectField.focus();
    for(var i=0; i<selectField.length; i++)
    selectField.options[i].selected = true;
}

function deselectAll(selectField){
    selectField.focus();
    for(var i=0; i<selectField.length; i++)
        selectField.options[i].selected = false;
}