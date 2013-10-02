/**
 * Add event listener for elements that serve as a button for highlight annotation.
 */
function initAnnotationButtons() {
	$('.resource-item')
			.mouseenter(function() {
				$('[about="' + $(this).attr('uri') + '"]').addClass('highlighted');
			})
			.mouseleave(function() {
				$('[about="' + $(this).attr('uri') + '"]').removeClass('highlighted');
			})
			.click(function() {
				$(this).toggleClass('selected');
				$('[about="' + $(this).attr('uri') + '"]').toggleClass('permanent-highlighted');

				// if a tag was selected then highlight the resource in the color of the tag
				if ($(this).hasClass('selected')) {
					var resourceTags = $(this).attr('annotations').split(" ");
					$('#selected-annotation-tags li:visible').each(function() {
						if ($.inArray(this, resourceTags))
							highlight($('[uri="' + $(this).attr('uri') + '"]'));
					});
				} else {
					$('[about="' + $(this).attr('uri') + '"]')
							.css('backgroundColor', '')
							.css('color', '');
				}
			});
	disableAnnotationButtons('.resource-item');
	initColorPickers();
}

/**
 * Highlight all resources that are annotated with a given tag using the tags font color and background color.
 *
 * @param tag
 *		 a tag element
 */
function highlight(tag) {
	tag = $(tag);
	var uri = $(tag).attr('uri');
	if (tagColors[uri] == undefined)
		return;

	var isDark = isdark(tagColors[uri]);
	tag.css('backgroundColor', tagColors[uri])
			.css('color', isDark ? '#FFFFFF' : '')
			.children('a').toggleClass('icon-white', isDark).toggleClass('icon', !isDark);
	$('#selected-annotations [annotations~="' + tag.attr('uri') + '"]').each(function() {
		$('[about="' + $(this).attr('uri') + '"]')
				.css('backgroundColor', tagColors[uri])
				.css('color', isDark ? '#FFFFFF' : '');
	});
}

/**
 * Remove the highlighting all resources that are annotated with a given tag.
 *
 * @param tag
 *		 a tag element
 */
function removeHighlight(tag) {
	$('#selected-annotations [annotations~="' + $(tag).attr('uri') + '"]').each(function() {
		$('[about="' + $(this).attr('uri') + '"]')
				.css('backgroundColor', '')
				.css('color', '');
	});
}

/**
 * Click the elements that serve as a button for highlight annotation.
 * @param sel
 *		 selector selecting the buttons
 */
function enableAnnotationButtons(sel) {
	$(sel).each(function() {
		$('[about="' + $(this).attr('uri') + '"]').addClass('permanent-highlighted');
		$(this).addClass('selected');
	});
}

/**
 * Click the elements that serve as a button for highlight annotation.
 * @param sel
 *		 selector selecting the buttons
 */
function disableAnnotationButtons(sel) {
	$(sel).each(function() {
		$('[about="' + $(this).attr('uri') + '"]')
				.removeClass('permanent-highlighted highlighted')
				.css('color', '')
				.css('backgroundColor', '');
		$(this).removeClass('selected');
	});
}

/**
 * Toggle selection of all elements annotated with the given URI.
 *
 * @param event
 *		click event
 * @param uri
 *		URI of selected annotation
 */
function toggleSelected(event, uri) {
	var tag = $('[uri="' + uri + '"]');
	$('#selected-annotation-tags').append(tag);
	tag.toggle();

	$('#selected-annotation-tags li:hidden').each(function() {
		$('#not-selected-annotations').append($('ul.resource-list [annotations~="' + $(this).attr('uri') + '"]'));
		removeHighlight(this);
	});

	$('#selected-annotation-tags li:visible').each(function() {
		$('#selected-annotations').append($('ul.resource-list [annotations~="' + $(this).attr('uri') + '"]'));
		highlight(this);
	});

	var selectedRes = $('#selected-annotations li');
	var notSelectedRes = $('#not-selected-annotations li');
	$('#not-selected-heading').toggle(selectedRes.length != 0 && notSelectedRes.length != 0);
	enableAnnotationButtons(selectedRes);
	disableAnnotationButtons(notSelectedRes);

	event.stopPropagation();
}
