PADDING = 50;
SCROLL_SPEED = 1000;
SCROLL_TIMEOUT = 250;
SECTION_OFFSET = 50;

SECTIONS = [];

//declaring event handler for scrollStopped event
$.fn.scrollStopped = function(callback) {
	$(this).scroll(
			function() {
				var self = this, $this = $(self);
				if ($this.data('scrollTimeout')) {
					clearTimeout($this.data('scrollTimeout'));
				}
				$this.data('scrollTimeout', setTimeout(
						callback, SCROLL_TIMEOUT, self));
			});
};

function detectSection(offset) {

	for (var i = 0; i < SECTIONS.length; ++i) {
		if (i < SECTIONS.length - 1) {
			if ($(window).scrollTop() >= $('#' + SECTIONS[i])
					.position().top
					&& $(window).scrollTop() < $(
							'#' + SECTIONS[i + 1]).position().top
							- offset) {
				return SECTIONS[i];
			}
			continue;
		} else if ($(window).scrollTop() >= $(
				'#' + SECTIONS[SECTIONS.length - 1]).position().top) {
			return SECTIONS[SECTIONS.length - 1];
		}
	}
}


//own affix
$.fn.affixMenu = function(options) {
	var offset = this.offset().top;
	var affix = $(this);
	
	$(window).scroll(function() {
		//console.log($(document).scrollTop());
		if($(document).scrollTop() > offset) {
			affix.css(
				{"position": "fixed", "margin-top": "50px", "top": 0 });
		} else {
			affix.css(
				{"position":"relative","margin-top": 0 });
		}
	});
};

$(document).ready(function() {
	
	//initialize sections
	$('.section-scroll > .list-group-item').each(
		function(i) {
			SECTIONS[i] = $(this).attr('href').substr(1);
		}
	);
	
	$('.affix').affixMenu();
	
	//set event handler for scroll stopped
	$(window).scrollStopped(function() {
		//remove active link classes
		$('.section-scroll > *.active').removeClass('active');
		//detect section
		var sectionName = detectSection(SECTION_OFFSET);
		//get link
		var sectionLink = $('a[href="#'
				+ sectionName
				+ '"]');
		//set link active
		sectionLink.addClass('active');
	});

	$('.section-scroll a.list-group-item').click(function(event) {
		var href = $(this).attr('href');

		if (href.substr(0, 1) === '#') {
			event.preventDefault();

			//get target section
			var target = href.substr(1);
			var opts = {
				easing : 'easeInQuint',
				scrollSpeed : SCROLL_SPEED,
				padding : PADDING,
				element : 'html,body'
			};
			
			//animate scroll to target section
			$('#' + target).animatescroll(opts);
			
			return false;
		} else {
			//window.location: href;
		}
	});
});