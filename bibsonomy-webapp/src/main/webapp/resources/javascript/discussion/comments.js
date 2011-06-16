var COMMENTS_URL = "/ajax/comments";
var REPLY_FORM_ID = 'replyForm';
var REPLY_FORM_SELECTOR = '#' + REPLY_FORM_ID;
var EDIT_FORM_ID = 'editComment';
var EDIT_FORM_SELECTOR = '#' + EDIT_FORM_ID;

$(function() {
	// reply links
	$('a.reply').click(reply);
	
	// create comment form on bottom
	$('#createComment form').submit(createComment);
	
	$('a.toggleReplies').click(function() {
		$(this).parent().parent().siblings('ul.subdiscussionItems').toggle('slow');
		return false;
	});
	
	$('.commentMenu a.editLink').click(showEditCommentForm);
	
	$('.commentMenu a.deleteLink').click(deleteComment);
});

function reply() {
	var parent = $(this).parent().parent().parent();
	
	$(REPLY_FORM_SELECTOR).remove();
	
	// find parent hash
	var parentHash = parent.find('div.info').data('hash');
	
	var clone = $('#createComment').clone();
	clone.attr('id', REPLY_FORM_ID);
	var form = clone.find('form');
	form.submit(createComment);
	
	form.append($('<input />').attr('name', 'discussionItem.parentHash').attr('type', 'hidden').attr('value', parentHash));
	
	// bind group select
	form.find(ABSTRACT_GROUPING_RADIO_BOXES).click(onAbstractGroupingClick);
	form.find('textarea').focus(); // TODO: not working
	
	parent.append(clone);
	clone.show();
	
	scrollTo(REPLY_FORM_ID);
	return false;
}

function showEditCommentForm() {
	var comment = $(this).parent().parent().parent();
	
	$(EDIT_FORM_SELECTOR).remove();
	
	// create edit form
	var clone = $('#createComment').clone();
	clone.attr('id', EDIT_FORM_ID);
	var form = clone.find('form');
	
	// find values and set it in form		
	// … groups
	// TODO: maybe save groups and abstract grouping as json data attribute
	populateFormWithGroups(form, getAbstractGrouping(comment), getGroups(comment));
	
	// … text
	var commentText = comment.find('.text:first').text();
	form.find('textarea').attr('value', commentText);
	
	// … hash of comment
	var commentHash = getHash($(this));
	form.append($('<input />').attr('name', 'discussionItem.hash').attr('type', 'hidden').attr('value', commentHash));
	
	// method param for ajax
	form.append($('<input />').attr('name', '_method').attr('type', 'hidden').attr('value', 'PUT'));
	
	// update action text
	var updateText = getString('post.resource.comment.actions.update');
	clone.find('h4').text(updateText);
	form.find('input[type="submit"]').attr('value', updateText);
	var spinner = form.find('.spinner');
	var spinnImage = spinner.find('img');
	spinner.empty().append(spinnImage).append(getString('post.resource.comment.action.update'))
	
	// binding
	// … group
	form.find(ABSTRACT_GROUPING_RADIO_BOXES).click(onAbstractGroupingClick);
	// … submit
	form.submit(updateComment);
	
	// append and show
	comment.append(clone);
	clone.show('slow');
}

function createComment() {
	var commentForm = $(this);
	var parentDiv = commentForm.parent('#' + REPLY_FORM_ID);
	var spinner = commentForm.find('.spinner');
	// show spinner
	spinner.show();
	
	// get comment values
	var commentData = commentForm.serialize();
	var commentTextArea = commentForm.find('textarea');
	var commentText = commentTextArea.val();
	
	var commentAbstractGrouping = commentForm.find(ABSTRACT_GROUPING_RADIO_BOXES + ':checked').val();
	var commentGroups = commentForm.find(OTHER_GROUPING_CLASS).val();
	// TODO: allow multiple groups; remove line after this comment
	commentGroups = new Array(commentGroups);
	
	$.ajax({
		url:		COMMENTS_URL,
		type:		"POST",
		data:		commentData,
		success:	function(response) {			
						// clone template set text
						var commentTemplate = $('#commentTemplate').clone();
						commentTemplate.removeAttr('id');
			
						commentTemplate.find('.text').html(commentText);
						updateHash(commentTemplate, response.hash);
			
						var groupView = buildGroupView(commentAbstractGrouping, commentGroups);
						
						commentTemplate.find('.meta').append(groupView);
			
						var commentList = parentDiv.parent().children('.comments');
						
						if (commentList.length == 0) {
							commentList = $('#discussion');
							commentTextArea.val('');
							// TODO: reset groups and abstract grouping?
						}
			
						var li = $('<li></li>');
						li.append(commentTemplate);
			
						commentList.append(li);
						
						// bind click listener
						commentTemplate.find('a.reply').click(reply);
						// TODO: edit link
						
						// TODO: update reply counter
						
						commentTemplate.show();
						
						// remove reply form if present
						parentDiv.remove();
						spinner.hide();
					},
		error: 		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(commentForm, jQuery.parseJSON(jqXHR.responseText));
				    }
	});
	return false;
}

function updateComment() {
	var commentForm = $(this);
	
	var spinner = commentForm.find('.spinner')
	spinner.show();
	
	// get comment values
	var commentData = commentForm.serialize();
	var commentTextArea = commentForm.find('textarea');
	var commentText = commentTextArea.val();
	
	var commentAbstractGrouping = commentForm.find(ABSTRACT_GROUPING_RADIO_BOXES + ':checked').val();
	var commentGroups = commentForm.find(OTHER_GROUPING_CLASS).val();
	// TODO: allow multiple groups; remove line after this comment
	commentGroups = new Array(commentGroups);
	
	$.ajax({
		url:		COMMENTS_URL,
		type:		"POST",
		data:		commentData,
		success:	function(response) {
						alert("updated");
						commentForm.remove();
					},
		error:		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(commentForm, jQuery.parseJSON(jqXHR.responseText));
					},
	});
}

function deleteComment() {
	// TODO: confirm?
	
	var deleteLink = $(this);
	// display spinner
	deleteLink.siblings('.deleteInfo').show();
	
	var hash = getHash($(this));
	var interHash = getInterHash();
	var deleteUrl = COMMENTS_URL + "?ckey=" + ckey + "&hash=" + interHash + "&discussionItem.hash=" + hash;
	
	console.debug(deleteUrl);
	var comment = deleteLink.parent().parent().parent();
	deleteLink.remove();
	
	$.ajax({
		url: deleteUrl,
		type: "DELETE",
		success: function() {
			comment.fadeOut(FADE_DURATION, function() {
				$(this).remove();
			});
		},
		// TODO: handle error
	});
}