/**
 * Add event listener for elements that serve as a button for selecting resources.
 */
function initResourceButtons(url) {
	$('.resource-item')
			.mouseenter(function() {
				$('[about="' + $(this).attr('uri') + '"]').addClass('highlighted');
			})
			.mouseleave(function() {
				$('[about="' + $(this).attr('uri') + '"]').removeClass('highlighted');
			})
			.click(function(event) {
				$('#found-entities-spinner').fadeIn(200);
				if ($(this).hasClass('selected')) {
					// update form for filtering annotations
					$('#selected-resources [uri="' + $(this).attr('uri') + '"] a').get(0).click();
				} else {
					// update form for filtering annotations
					addToAF('uris', $(this).attr('uri'));
					$("#annotation-filter").submit();
				}
			});

	// on click a restriction on the property is added to the search form
	$('#selected-resource-annotations li').click(function(event) {
		$(this).children('a').get(0).click();
	});

	updateSelected();
}

function updateSelected() {
	$('#found-resources li').removeClass('selected');

	// highlight resources that have been selected
	$('#selected-resources li').each(function() {
		$('[about="' + $(this).attr('uri') + '"]').addClass('highlighted');
		$('#found-resources [uri="' + $(this).attr('uri') + '"]').addClass('selected');
		highlight(this);
	});

	// highlight annotations that have been selected
	$('#selected-annotations li').each(function(idx, elem) {
		doWithAnnotatedResources({'property': $(elem).attr('prop'), 'typeof': $(elem).attr('domain')}, function() {
			$(this).addClass('highlighted');
		});
	});

	// highlight annotations that have been selected
	$('#search [name=prop]').each(function() {
		$('#selected-resource-annotations [prop~="' + $(this).val() + '"]').addClass('selected');
	});
}

/**
 * Highlight all resources that are annotated with a given tag using the tags font color and background color.
 *
 * @param tag
 *		 a tag element
 */
function highlight(tag) {
	tag = $(tag);
	var uri = tag.attr('uri');
	if (tagColors[uri] == undefined)
		return;

	var isDark = isdark(tagColors[uri]);
	tag.css('backgroundColor', tagColors[uri])
			.css('color', isDark ? '#FFFFFF' : '')
			.children('a').toggleClass('icon-white', isDark).toggleClass('icon', !isDark);

	if (tag.attr('prop') != undefined) {
		var propUri = tag.attr('prop');
		var domainUri = tag.attr('domain');
		doWithAnnotatedResources({'property': propUri, 'typeof': domainUri}, function() {
			console.log(this);
			$(this).css('backgroundColor', tagColors[uri])
					.css('color', isDark ? '#FFFFFF' : '');

		});
	} else {
		$('[about="' + uri + '"]')
				.css('backgroundColor', tagColors[uri])
				.css('color', isDark ? '#FFFFFF' : '');
	}
}

/**
 * Add a hidden field to the annotation filter.
 *
 * @param name
 *		 name of the field
 * @param value
 *		 value of the field
 */
function addToAF(name, value) {
	if ($('#annotation-filter [value="' + value + '"]').length == 0) {
		$('<input/>', {type: 'hidden', name: name, value: value}).appendTo($('#annotation-filter'));
		return true;
	} else {
		return false;
	}
}

/**
 * Remove the fields with the given value from the annotation filter.
 *
 * @param value
 *		 value of the field
 */
function removeFromAF(value) {
	$('#annotation-filter [value="' + value + '"]').remove();
}
