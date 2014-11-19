$(function() {

	$('.input-group.date').datepicker({
		autoclose : true
	});

	$('.collapse').collapse();

	$('.toggleAdvanced').click(function(event) {
		event.preventDefault();
		$(this).parents('li.media').find('.advanced').toggle();
	});

	$('.post-popover, .help-popover').popover({
		html : true,
		trigger : "focus",
		container : 'body',
		placement : function() {
			return ($(window).width() >= 990) ? 'right' : 'auto';
		},
		title : function() {
			$(this).next().html();
		},
		content : function() {
			return $(this).next().next().html();
		},
		delay : 0
	});

	$('.system-tags-link').popover({
		animation : false,
		html : true,
		trigger : 'manual',
		placement : 'right',
		delay : 0,
		content : function() {
			return $(this).next().find('.popover-content').html();
		}

	});

	$('.system-tags-link').click(function(event) {
		event.preventDefault();
		$(this).popover('show');
	});

	/**
	 * publication details abstract and description more link
	 */
	maxChar = 350;
	dots = "&hellip;";
	moretext = "";
	lesstext = "";

	$('.show-more')
			.each(
					function() {

						var moreLink = $(document.createElement("a"));
						var contentContainer = $(this).children(
								".contentContainer")[0];

						if (contentContainer) {

							moreLink
									.data("text", contentContainer.innerHTML)
									.html("(" + getString("more") + ")")
									.addClass("moreLink")
									.click(
											function(event) {
												event.preventDefault();
												var contentContainer = $(
														this.parentNode)
														.children(
																".contentContainer")[0];

												if ($(this).hasClass(
														'show-less')) {
													$(this)
															.html(
																	"("
																			+ getString("more")
																			+ ")")
															.removeClass(
																	"show-less")
															.addClass(
																	"show-more");
												} else {
													$(this)
															.html(
																	"("
																			+ getString("less")
																			+ ")")
															.removeClass(
																	"show-more")
															.addClass(
																	"show-less");
												}
												shortenContent(
														contentContainer,
														moreLink.data("text"));
												return false;
											});

							this.appendChild(moreLink[0]);
							if (!shortenContent(contentContainer, moreLink
									.data("text"))) {
								moreLink.hide();
							}
						}

					});

	/**
	 * SYSTEM TAGS HANDLING
	 */
	var isSystemTag = function(item) {
		//TODO: identify system tags by system tag config
		pattern = new RegExp('.+:.+');
		return pattern.test(item);
	};

	$('input[data-role=tagsinput]').tagsinput(
			{
				confirmKeys : [ 32, 13 ], // space and return
				trimValue : true,
				freeInput : true,
				tagClass : function(item) {
					return isSystemTag(item) ? 'label label-warning'
							: 'label label-primary';
				},
				delimiter : ' '
			});

	$('.edit-tags-form').submit(function(e) {
		e.preventDefault();
		var url = $(this).attr('action');
		var data = $(this).serialize();
		var resourceHash = $(this).attr('data-resource-hash');
		var tagField = $(this).find('input[data-role=tagsinput]');
		var tags = $(tagField).tagsinput('items');
		
		$.ajax({
			url : url,
			type : 'POST',
			data : data,
			success : function() { //on success
				//remove tags
				$('#list-item-' + resourceHash + ' .ptags span.label').remove();
				//append current tags
				$(tags).each(function(i, v) {
					if(!isSystemTag(v)) {
						var item = '<span class="label label-grey"><a href="/user/' + encodeURIComponent(currUser) + '/' + encodeURIComponent(tags[i]) + '">' + tags[i] + '</a></span> ';
						$('#list-item-' + resourceHash + ' .ptags').append(item);
					} else {
						//TODO: add system tags
					}
				});
			}
		});
		return false;
	});

	$('.rename-tags-btn').click(function() {
		$(this).parent().prev().focus().next().show().hide();
	});

	var sidebarAdjustments = function sidebarAdjusts() {
		if ($('#sidebar').prev().hasClass('content')) {

			var contentContainer = $('#sidebar').prev();
			var contentHeight = contentContainer.height();
			var sidebarHeight = $('#sidebar').height();

			if (contentHeight > sidebarHeight && $('#sidebar').is(':visible')) {
				$('#sidebar').css('height', contentHeight + 20);
			}
		}
	}

	sidebarAdjustments();

	$(window).resize(sidebarAdjustments);

	function shortenContent(el, text) {
		var shortened = false;
		if (el.innerHTML.length > maxChar + dots.length) {
			text = text.substr(0, maxChar) + dots;
			shortened = true;
		}
		$(el).html(text);
		return shortened;
	}

	$('.community-page-user-list li a.show-less').click(function(event) {
		event.preventDefault();
		$(this).parent().parent().find('.show').each(function() {
			$(this).removeClass('show').addClass('hidden');
		});
	});

	$('.community-page-user-list li a.show-more').click(function(event) {
		event.preventDefault();
		$(this).parent().parent().find('.hidden').each(function() {
			$(this).removeClass('hidden').addClass('show');
		});

	});

	/** MOBILE FUNCTIONS * */

	$('#hide-bookmarks').click(
			function(event) {
				event.preventDefault();
				$('.bookmarksContainer').is(':visible') ? $(this).text(
						getString("list.hide")) : $(this).text(
						getString("list.show"));
				$('.bookmarksContainer').slideToggle();

			});

	$('#hide-publications').click(
			function(event) {
				event.preventDefault();
				$('.publicationsContainer').is(':visible') ? $(this).text(
						getString("list.hide")) : $(this).text(
						getString("list.show"));
				$('.publicationsContainer').slideToggle();
			});

});

function dummyDownHandler(e) {
	var event = e || window.event;

	var character = String
			.fromCharCode((96 <= e.keyCode && e.keyCode <= 105) ? e.keyCode - 48
					: e.keyCode);
	if (!(character.toLowerCase() == 'c' && event.ctrlKey)) {
		e.preventDefault();
		e.stopPropagation();
		return false;
	}
}

function dummyHandler(e) {

	e.preventDefault();

	e.stopPropagation();
	return false;
}

function dummyUpHandler(e) {

	e.preventDefault();

	e.stopPropagation();
	return false;
}

function activateAffixEntry(el) {
	$(el).addClass("active").siblings().each(function(h, g) {
		$(g).removeClass("active");
	});
}

function findBootstrapEnvironment() {
	var envs = [ 'xs', 'sm', 'md', 'lg' ];

	$el = $('<div>');
	$el.appendTo($('body'));

	for (var i = envs.length - 1; i >= 0; i--) {
		var env = envs[i];

		$el.addClass('hidden-' + env);
		if ($el.is(':hidden')) {
			$el.remove();
			return env
		}
	}
	;
}