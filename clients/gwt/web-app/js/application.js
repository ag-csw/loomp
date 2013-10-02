// TODO remove global variable
var tagColors = [];

/**
 * Initialize the tags and color pickers.
 */
function initColorPickers() {
	$('.color-picker').colorPicker({
		click: function(event, color) {
			var tag = $(event.target).parents('li');
			// the function is also called on initialization of the color picker. But we like only events issued by a user
			if ('pageX' in event) {
				var uri = tag.attr('uri');
				// check if it is the default color
				tagColors[uri] = (color == this.color[0]) ? '' : color;
			}
			highlight(tag);
		}
	});

	$('.color-picker').parents('li').hover(
			function() {
				$(this).children('.color-picker').fadeIn(300);
			},
			function() {
				$(this).children('.color-picker').hide();
			});
}

function initDropDowns() {
	$('.element-menu-drop')
			.live("mouseenter", function() {
				$(this).children('.element-menu').fadeIn(200);
			})
			.live("mouseleave", function() {
				$(this).children('.element-menu').fadeOut(200);
			});

	$('.property-table-drop')
			.live("mouseenter", function() {
				$(this).siblings('.property-table').show();
			})
			.live("mouseleave", function() {
				var table = $(this).siblings('.property-table');
				if (!table.hasClass('stay'))
					table.fadeOut(200);
			})
			.live("click", function(event) {
				$(this).toggleClass('icon-menu-large icon-menu-close-large');
				$(this).siblings('.property-table').toggleClass('stay');
				// live events cannot be stopped; see http://api.jquery.com/event.stopPropagation/
			});
}

/**
 * Apply the callback function to all spans having given properties and URIs.
 *
 * @param attrUriMap
 *		 a map <property, URI> defining which attributes should have which URI as value
 * @param callback
 *		 callback function
 */
function doWithAnnotatedResources(attrUriMap, callback) {
	$('.content > span').each(function(j, content) {

		// extract the namespaces from the span surrounding the content
		var ns = [];
		$.each(this.attributes, function(i, attr) {
			var name = attr.name;
			var value = attr.value;
			if (name.indexOf('xmlns:') == 0) {
				ns[attr.value] = name.substring(6);
			}
		});

		// create a selector which selects all tags satisfying the attrUriMap
		var selector = '';
		for (var attr in attrUriMap) {
			var propUri = attrUriMap[attr];
			if (propUri != undefined) {
				for (var url in ns) {
					if (propUri.indexOf(url) == 0) {
						var nsUri = ns[url] + ':' + propUri.substring(url.length);
						selector += '[' + attr + '="' + nsUri + '"]';
						break;
					}
				}
			}
		}

		callback.call($(content).children(selector));
	});
}

