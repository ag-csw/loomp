var checkboxPrefix = 'checkbox-annotation-';
var checkboxStore = [];
var editor = tinyMCEPopup.getWindowArg('editor', null);
var jsonData = tinyMCEPopup.getWindowArg('jsonData', {"text":'', "annotations":[]});
var lastResAnnotIndex = -1;
var openedResWin = null;
var originWindow = tinyMCEPopup.getWindowArg('originWindow', null);
var plugin_url = tinyMCEPopup.getWindowArg('plugin_url', '/');
var vocabIdsByTypes = {};


Element.observe(window, 'load', function() {
	var table = $('annotation-chooser-table');
	var tbody = $('annotation-chooser-tbody');
	var applyButton = $('applyButton');

	if (!jsonData || 0 == jsonData.annotations.length) {
		$('text').innerHTML = '<span class="message">No recommended annotations for the given text.</span>';
		table.remove();
		applyButton.remove();
		return;
	}

	$('text').innerHTML = jsonData.text;

	jsonData.annotations.each(function(obj, index) {
		annot = obj.annotation;
		conflicts = obj.conflicts;

		// create new row
		var tr = document.createElement('tr');

		// checkbox cell
		var checkbox = document.createElement('input');
		checkbox.type = 'checkbox';
		checkbox.id = window.checkboxPrefix + index;
		checkbox.value = annot.uri;
		checkbox.title = annot.label + ': ' + jsonData.text.substring(annot.begin, annot.end);

		var td = document.createElement('td');
		td.appendChild(checkbox);
		tr.appendChild(td);

		// store the checkbox
		window.checkboxStore.push(checkbox);

		// id cell
		td = document.createElement('td');
		td.innerHTML = index;
		tr.appendChild(td);

		// conflict cell
		td = document.createElement('td');
		td.innerHTML = conflicts.join(', ');
		tr.appendChild(td);

		// type cell
		td = document.createElement('td');
		td.innerHTML = annot.label;
		tr.appendChild(td);

		// text cell
		td = document.createElement('td');
		td.innerHTML = jsonData.text.substring(annot.begin, annot.end);
		tr.appendChild(td);

		// see also cell
		td = document.createElement('td');
		var seeAlsoLink = document.createElement('a');
		seeAlsoLink.href = annot.seeAlso;
		seeAlsoLink.target = '_blank';
		seeAlsoLink.innerHTML = annot.seeAlso.substring(0,30) + '...';
		td.appendChild(seeAlsoLink);
		tr.appendChild(td);

		// add all
		tbody.appendChild(tr);

		checkbox.observe('change', handleConflicts);
	});
	table.style.display = '';
});

/**
 * Adds a new property and value to the property cache.
 * 
 * @param {String} about
 * 		The resource.
 * @param {String} type
 * 		The property's type.
 * @param {String} value
 * 		The property's value.
 * @author Patrick Jungermann
 */
function addToCache(res, type, value) {
	// add new property and value to property cache
	if (!originWindow.propCache[res]) {
		originWindow.propCache[res] = new Object();
	}
	originWindow.propCache[res][type] = value;
}

/**
 * Applies the recommended annotations to the text.
 * 
 * @author Patrick Jungermann
 */
function applySelection() {
	getMissingResources();
}

/**
 * Finally, it applies the selected annotation to the text.
 * 
 * @param {Array} annots
 * 		Annotations that have to be added to the text.
 * @author Patrick Jungermann
 */
function applySelectionFinalize(annots) {
	// add each annotation
	var text = jsonData.text;
	var shift = 0;
	annots.each(function(annot) {
		var begin = annot.begin * 1 + shift;
		var end = annot.end * 1 + shift;

		var pre = text.substring(0, begin);
		var annotatedText = text.substring(begin, end);
		var post = text.substring(end);

		var moreAttrs = {};
		if (annot.seeAlso) {
			moreAttrs.seeAlso = annot.seeAlso;
		}
		var replacement = originWindow.editorBuildAnnotSpan(annot.about, annot.uri, annotatedText, moreAttrs);

		// add new property and value to property cache
		addToCache(annot.about, annot.uri, annotatedText);

		shift += replacement.length - annotatedText.length;
		text = pre + replacement + post
	});

	// set the new and annotated text to the editor
	editor.setContent(text);

	// finally, close this popup window
	tinyMCEPopup.close();
}

/**
 * Creates a new resource and adds it to the annotation.
 * 
 * @param {String} type
 * 		The type of the resource.
 * @param {String} value
 * 		The value used for the resource.
 * @param {number} index
 * 		The index of the annotation.
 * @param {Window} openedResWindow
 * 		The "res"-window that is opened. Will be closed after finishing the process.
 * @author Patrick Jungermann
 */
function createNewRes(type, value, index, openedResWindow)  {
	var func = function(index, openedResWindow, transport) {
		var uri = transport.responseText;
		this.setAbout(index, uri, openedResWindow);
	};
	var onSuccessCallback = func.bind(window, index, openedResWindow);

	new Ajax.Request('../../../../mashup/createresource?type=' + type + '&value=' + escape(value), {
		method      :'get',
		asynchronous: false,
		onSuccess   : onSuccessCallback,
		onFailure   : function(transport){ 
			alert('ERROR: ' + transport.responseText); 
		}
	});
}

/**
 * Returns all selected annotations, sorted by their begin within the text.
 * 
 * @return {Array} The selected annotations.
 * @author Patrick Jungermann
 */
function getAllSelectedAnnotations() {
	// get all selected annotations
	var annots = [];
	checkboxStore.each(function(checkbox) {
		if (checkbox.checked) {
			var index = checkbox.id.replace(window.checkboxPrefix, '') * 1; 
			var annot = jsonData.annotations[index].annotation
			annot.index = index;
			annots.push(annot);
		}
	});

	// sort by begin
	annots.sort(function(a, b) {
		var ret;
		if (a.begin < b.begin) ret = -1;
		else if (a.begin > b.begin) ret = 1;
		else ret = 0;
		console.log(a.begin, b.begin, ret);

		return ret;
	});

	return annots;
}

/**
 * Adds missing resources to the selected annotations by let the
 * user choose one of the existing ones or create a new one.<br/>
 * If a user stops the process, the annotations will not be applied to the text.
 * 
 * @author Patrick Jungermann
 */
function getMissingResources() {
	// get all selected annotations
	var annots = getAllSelectedAnnotations();

	// check, if a window is currently opened
	if (openedResWin != null) {
		if (openedResWin && editor.windowManager.windows[openedResWin]) {
			setTimeout('getMissingResources()', 1000);
			return false;
		}
		else {
			openedResWin = null;
			// check, if a resource was selected (mandatory!)
			if (!annots[lastResAnnotIndex].about) {
				lastResAnnotIndex = -1;
				alert('To apply the text, for each choosen annotation you have to select a resource.\nPlease retry.');
				return false;
			}
		}
	}

	// choose the resource / about, if needed
	var opened = false;
	for (var i = lastResAnnotIndex + 1; i < annots.length; i++) {
		if (!annots[i].about) {
			// keep this index in mind
			lastResAnnotIndex = i;
			var type = getVocabIdByType(annots[i].uri);

			new Ajax.Request('../../../../mashup/getresources?type=' + type, {
				method      :'get',
				asynchronous: false,
				onSuccess   : function(transport) {
					var foundAnnots = transport.responseText.evalJSON(false);

					// open the res window
					editor.windowManager.open({
						file  : plugin_url + '/res.html',
				   		width : 400,
				   		height: 500,
				   		inline: true
					}, {
						annots      : foundAnnots,
						currentAnnot: annots[i],
						originWindow: window,
						propCache   : originWindow.propCache
					});

					// get the new window's ID
					var managedWindowsPost = [];
					for (var winId in editor.windowManager.windows) managedWindowsPost.push(winId);
					openedResWin = managedWindowsPost[managedWindowsPost.length - 1];
				},
				onFailure   : function(transport){ 
					alert('ERROR: ' + transport.responseText); 
				}
			});

			opened = true;
			break;
		}
	}

	// check, if all resources has been chosen
	if (opened) {
		setTimeout('getMissingResources()', 1000);
	}
	else {
		// finally, apply all selected annotations
		applySelectionFinalize(annots);
	}
}

/**
 * Returns the vocabulary ID for the given type.<br/>
 * Uses a cache for the vocabulary IDs that was already resolved.
 * 
 * @param {String} type
 * 		The property type.
 * @return {String} The vocabulary ID for the given type.
 * @author Patrick Jungermann
 */
function getVocabIdByType(type) {
	var vocabId = vocabIdsByTypes[type] || null;
	if (!vocabId) {
		var vocabIds = originWindow.getAvailableVocabularyIDs();
		for (var i = 0; i < vocabIds.length; i++) {
			var vocabEntries = originWindow.getVocabulary(vocabIds[i]);
			var found = false;
			for (var k = 0; k < vocabEntries.length; k++) {
				if (type == vocabEntries[k][0]) {
					vocabId = vocabIds[i];
					break;
				}
			}

			if (found) {
				vocabIdsByTypes[type] = vocabId;
				break;
			}
		}
	}

	return vocabId;
}

/**
 * Handles the conflicts for an annotation
 * and disables not usable checkboxes.
 * 
 * @author Patrick Jungermann
 */
function handleConflicts() {
	var obj = this;
	var index = obj.id.replace(window.checkboxPrefix, '');

	var conflicts = jsonData.annotations[index].conflicts;
	conflicts.each(function(conflictId) {
		var conflict = $(window.checkboxPrefix + conflictId);
		if (obj.checked) {
			conflict.checked = false;
			conflict.disabled = true;
		}
		else {
			conflict.disabled = false;
		}
	});

	updateText();	
}

/**
 * Sets a resource to the annotation with the given index within the recommended annotations.
 * 
 * @param {number} index
 * 		The index of the annotation.
 * @param {String} about
 * 		The URI of the resource.
 * @param {Window} openedResWindow
 * 		The "res"-window that is opened. Will be closed after finishing the process.
 * @author Patrick Jungermann
 */
function setAbout(index, about, openedResWindow) {
	jsonData.annotations[index].annotation.about = about;

	// finally, close the current window
	editor.windowManager.close(openedResWindow);
}

/**
 * Sets a new resource to the annotation with the given index of the recommended annotations.
 * 
 * @param {number} index
 * 		The index of the related annotation.
 * @param {Window} openedResWindow
 * 		The "res"-window, opened. Will be closed after finishing the process.
 * @author Patrick Jungermann
 */
function setNewAbout(index, openedResWindow) {
	var annot = jsonData.annotations[index].annotation;
	var value = jsonData.text.substring(annot.begin, annot.end);

	var vocabId = getVocabIdByType(annot.uri);
	createNewRes(vocabId, value, index, openedResWindow);
}

/**
 * Updates the text and marks all areas of the text
 * that will be annotated, if the current selection
 * would be applied.
 * 
 * @author Patrick Jungermann
 */
function updateText() {
	// get all selected annotations
	var annots = getAllSelectedAnnotations();

	// mark all areas
	var text = jsonData.text;
	var shift = 0;
	annots.each(function(annot) {
		var begin = annot.begin * 1 + shift;
		var end = annot.end * 1 + shift;
		console.log(begin, end);

		var pre = text.substring(0, begin);
		var annotatedText = text.substring(begin, end);
		var post = text.substring(end);
		var replacement = '<span class="annotated">' + annotatedText + '</span>';
		console.log(annotatedText, replacement);

		shift += replacement.length - annotatedText.length;
		text = pre + replacement + post;		
	});

	$('text').innerHTML = text;
}
