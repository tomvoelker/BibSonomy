var COMMENTS_URL = "/ajax/comments";

var REPLY_SELECTOR = 'a.reply';
var EDIT_COMMENT_LINKS_SELECTOR = 'a.commentEditLink';
var DELETE_COMMENT_LINKS_SELECTOR = 'a.commentDeleteLink';
var TOGGLE_REPLY_SELECTOR = 'a.toggleReplies';

$(function() {
	// reply links
	$(REPLY_SELECTOR).click(reply);
	
	$(TOGGLE_REPLY_SELECTOR).click(function() {
		var view = $(this).parent().parent().siblings('ul.subdiscussionItems');
		var visible = view.is(':visible');
		view.toggle('slow');
		
		var text = getString('post.resource.discussion.replies.show');
		var title = getString('post.resource.discussion.replies.show.title')
		if (!visible) {
			text = getString('post.resource.discussion.replies.hide');
			title = getString('post.resource.discussion.replies.hide.title')
		}
		
		$(this).text(text).attr('title', title);
		return false;
	});
	
	$(EDIT_COMMENT_LINKS_SELECTOR).click(showEditCommentForm);
	$(DELETE_COMMENT_LINKS_SELECTOR).click(deleteComment);
});

function reply() {
	showDiscussion();
	
	var parent = $(this).parent().parent().parent();
	// remove all other forms
	removeAllOtherDiscussionForms();
	
	// find parent hash
	var parentHash = getHash($(this));
	
	var clone = $('#createComment').clone();
	clone.attr('id', REPLY_FORM_ID);
	var form = clone.find('form');
	
	form.append($('<input />').attr('name', 'discussionItem.parentHash').attr('type', 'hidden').attr('value', parentHash));
	
	// bind some actions (submit, group switch)
	form.submit(createComment);
	form.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	
	if (parentHash != undefined) {
		parent.append(clone);
	} else {
		$(DISCUSSION_SELECTOR).prepend(clone);
	}
	
	clone.show();
	form.find('textarea').TextAreaResizer();
	
	scrollTo(REPLY_FORM_ID);
	return false;
}

function showEditCommentForm() {
	var comment = $(this).parent().parent().parent();
	removeAllOtherDiscussionForms();
	
	// create edit form
	var clone = $('#createComment').clone();
	clone.attr('id', EDIT_COMMENT_FORM_ID);
	var form = clone.find('form');
	
	// find values and set it in form		
	// … groups
	// TODO: maybe save groups and abstract grouping as json data attribute
	populateFormWithGroups(form, getAbstractGrouping(comment), getGroups(comment));
	
	// … text
//	var commentText = comment.find('.text:first').text();
	var ct = comment.find('.originalText').text();
	var commentText = ct != "" ? ct : comment.find('.text:first').text();
	
	
	form.find('textarea').attr('value', commentText);
	if (comment.hasClass(ANONYMOUS_CLASS)) {
		form.find(ANONYMOUS_SELECTOR).attr('checked', 'checked');
	}
	
	// … hash of comment
	var commentHash = getHash($(this));
	form.append($('<input />').attr('name', 'discussionItem.hash').attr('type', 'hidden').attr('value', commentHash));
	
	// method param for ajax
	form.append($('<input />').attr('name', '_method').attr('type', 'hidden').attr('value', 'PUT'));
	
	// update action text
	var updateText = getString('post.resource.comment.actions.edit');
	clone.find('h4').text(updateText);
	form.find('input[type="submit"]').attr('value', updateText);
	var spinner = form.find('.spinner');
	var spinnImage = spinner.find('img');
	spinner.empty().append(spinnImage).append(getString('post.resource.comment.action.update'))
	
	// binding textarea resizer, group, and submit
	form.find('textarea').TextAreaResizer();
	form.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR).click(onAbstractGroupingClick);
	form.submit(updateComment);
	
	// append and show
	comment.append(clone);
	clone.show('slow');
	scrollTo(EDIT_COMMENT_FORM_ID);
	
	return false;
}

function createComment() {
	var commentForm = $(this);
	commentForm.unbind('submit');

	if (!hasGoldstandardCreationPermission()) {
		// what does that do?
		commentForm.submit(createComment);
		return false;
	}
	
	var parentDiv = commentForm.parent('#' + REPLY_FORM_ID);
	var spinner = commentForm.find('.spinner');
	// show spinner
	spinner.show();
	
	// get comment values
	var commentData = commentForm.serialize();
	var commentTextArea = commentForm.find('textarea');
	var commentText = commentTextArea.val();
	var anonymous = commentForm.find(ANONYMOUS_SELECTOR).is(':checked');
	
	var commentAbstractGrouping = commentForm.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR + ':checked').val();
	var commentGroups = commentForm.find(OTHER_GROUPING_CLASS_SELECTOR).val();
	// TODO: allow multiple groups; remove the next five lines after this comment
	if (commentGroups == null) {
		commentGroups = new Array();
	} else {
		commentGroups = new Array(commentGroups);
	}
	
	$.ajax({
		url:		COMMENTS_URL,
		type:		"POST",
		data:		commentData,
		dataType:   "json",
		success:	function(response) {
						var reload = (response.reload);
						if(reload == "true") {
			 				window.location.reload();
			 			}
						// clone template set text
						var commentTemplate = $('#commentTemplate').clone();
						commentTemplate.removeAttr('id');
						updateCommentView(commentTemplate, response.hash, commentText, anonymous, commentAbstractGrouping, commentGroups);
						
						var li = $('<li class="comment"></li>');
						li.append(commentTemplate);
						
						// bind click listener
						commentTemplate.find('a.reply').click(reply);
						commentTemplate.find('a.editLink').click(showEditCommentForm);
						commentTemplate.find('a.deleteLink').click(deleteComment);
						
						// TODO: show reply counter
						
						if ($(DISCUSSION_SELECTOR).children(REPLY_FORM_SELECTOR).length > 0) {
							$(DISCUSSION_SELECTOR + ' ul.subdiscussionItems:first').prepend(li);
						} else {
							// comment append to list
							parentDiv.parent().children('.subdiscussionItems').append(li);	
						}
						commentTemplate.show();
						
						// remove reply form if present
						parentDiv.remove();
						spinner.hide();
						commentForm.submit(createComment);
						showReviewForm();
						highlight(li);
					},
		error: 		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(commentForm, jQuery.parseJSON(jqXHR.responseText));
						commentForm.submit(createComment);
				    }
	});
	
	return false;
}

function updateCommentView(commentView, commentHash, commentText, anonymous, commentAbstractGrouping, commentGroups) {
	
	commentView.find(".originalText").remove();
	/*
	 * update comment text
	 */
	commentView.find('.text:first').replaceWith(commentText);
	
	/*
	 * update the hash of the discussion item
	 */
	updateHash(commentView, commentHash);

	/*
	 * update group info
	 */
	commentView.find('.' + GROUPS_CLASS).remove();
	
	var groupView = buildGroupView(commentAbstractGrouping, commentGroups);
	commentView.find('.info:first').append(groupView);
	
	/*
	 * update anonymous info
	 */
	if (anonymous) {
		commentView.addClass(ANONYMOUS_CLASS);
	} else {
		commentView.removeClass(ANONYMOUS_CLASS);
	}
}

function updateComment() {
	var commentForm = $(this);
	var commentView = commentForm.parent().parent();
	commentForm.unbind('submit');
	
	var spinner = commentForm.find('.spinner')
	spinner.show();
	
	// get comment values
	var commentData = commentForm.serialize();
	var commentTextArea = commentForm.find('textarea');
	var commentText = commentTextArea.val();
	var anonymous = commentForm.find(ANONYMOUS_SELECTOR).is(':checked');
	
	var commentAbstractGrouping = commentForm.find(ABSTRACT_GROUPING_RADIO_BOXES_SELECTOR + ':checked').val();
	var commentGroups = commentForm.find(OTHER_GROUPING_CLASS_SELECTOR).val();
	// TODO: allow multiple groups; remove the next five lines after this comment
	if (commentGroups == null) {
		commentGroups = new Array();
	} else {
		commentGroups = new Array(commentGroups);
	}
	// end
	
	$.ajax({
		url:		COMMENTS_URL,
		type:		"POST",
		dataType:   "json",
		data:		commentData,
		success:	function(response) {
						updateCommentView(commentView, response.hash, parseLinks(commentText), anonymous, commentAbstractGrouping, commentGroups);
						commentForm.parent().remove();
						highlight(commentView);
						showReviewForm();
						commentView.children(".details").find(".citeBox").hide();
						commentView.children(".details").find(".bookCiteBox").children("div").remove();
						commentView.children(".details").find(".bookCiteBox").hide();
		     			handleLinks(commentView);
					},
		error:		function(jqXHR, data, errorThrown) {
						handleAjaxErrors(commentForm, jQuery.parseJSON(jqXHR.responseText));
						commentForm.submit(updateComment);
					},
	});
	
	return false;
}

function deleteComment() {
	// TODO: confirm?
	
	var deleteLink = $(this);
	// display spinner
	deleteLink.siblings('.deleteInfo').show();
	
	var hash = getHash($(this));
	var interHash = getInterHash();
	var deleteUrl = COMMENTS_URL + "?ckey=" + ckey + "&hash=" + interHash + "&discussionItem.hash=" + hash;
	var comment = deleteLink.parent().parent().parent();
	
	deleteLink.remove();
	
	$.ajax({
		url: deleteUrl,
		type: "DELETE",
		success: function() {
			deleteDiscussionItemView(comment);
		},
		// TODO: handle error
	});
}