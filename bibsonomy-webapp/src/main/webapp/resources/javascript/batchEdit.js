$(document).ready(function() {
	$('#selectAll').change(function() {
		var markAllChecked = $(this).is(':checked');
		/*
		 * mega haxxor jquery selector to select input checkboxes with name beginning
		 * with posts.
		 */
		$('input[name^=posts]:checkbox').each(function() {
			$(this).attr('checked', markAllChecked);
			$(this).change();
		});
	});
});

$(document).ready(function() {
	$('input[name^=posts]:checkbox').change(function() {
		if ($(this).is(':checked')) {
			if ($('#selector').val() == 1) {
				changeTagInputs(this, false);
		    } else {
		    	changeTagInputs(this, true);
		    }
		} else {
			changeTagInputs(this, true);
		}
		var oneNotChecked = false;
		/*
		 * mega haxxor jquery selector to select input checkboxes with name beginning
		 * with posts that are not checked.
		 */
		$('input[name^=posts]:checkbox:not(:checked)').each(function() {
			oneNotChecked = true;
			return;
		});
		$('#selectAll').attr('checked', !oneNotChecked);
	});
});

$(document).ready(function() {
	$('#selector').change(function() {
		if($(this).val() == 1) {
			$('input[name=tags]').attr('disabled', false);
			changeTagInputs('input[name^=posts]:checkbox:checked', false);
		} else {
			var value = true;
			
			if($(this).val() == 3) {
				value = confirm(getString("batchedit.deleteSelected.confirm"));
			} else if($(this).val() == 4) {
				value = confirm(getString("batchedit.ignoreSelected.confirm"));
			}
			
			if(!value) {
				$(this).find('option:eq(1)').prop("selected", true);
			}
			$('input[name=tags]').attr('disabled', value);
			changeTagInputs('input[name^=posts]:checkbox:checked', value);
		}
	});
});

function changeTagInputs(selector, disabled) {
	$(selector).each(function() {
		/*
		 * remove possible special characters from selector string
		 */
		var attr = $(this).attr('name').replace('posts','newTags').replace(/([;&,\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '\\$1');
		$('input[name=' + attr + ']:text').attr('disabled', disabled);
	});
}
		           
maximizeById("general");