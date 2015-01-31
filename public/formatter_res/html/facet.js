var annotatedSpans;
var presentProperties;
var facetCount = 0;
var colors = new Array(
	'Aquamarine',
	'Bisque',
	'Chartreuse',
	'Coral',
	'DarkKhaki',
	'DarkSeaGreen',
	'GreenYellow',
	'HotPink',
	'LightBlue',
	'Plum'
);

function ge(ele) {
	var hlEle = document.getElementById(ele);
	//if (hlEle == null) console.log('missing element with id ' + ele);
	return hlEle;
}

function getSelectedProperty(ele) {
	var selectEle = ge(ele);
	if (selectEle.selectedIndex < 0) alert('Select one already!');
	else return selectEle.options[selectEle.selectedIndex].value;
	return false;
}

function highlightProperty() {
	var selectedProp = getSelectedProperty('facet-properties');
	var selectedColor = getSelectedProperty('facet-colors');
	//var inverseColor = invertMe(selectedColor);
	if (selectedProp == "" || selectedColor == "") {
		alert("Please select a Property and a Color!");
		return;
	}
	clearHighlights(selectedProp);
	for(i = 0; i < annotatedSpans.length; i++) {
		var curSpan = annotatedSpans[i];
		if (curSpan.getAttribute('property') == selectedProp) {
			curSpan.style.backgroundColor = selectedColor;
		}
	}
	//add prop to facet list
	var hlEle = ge('facet-highlights');
	var newLi = document.createElement('li');
	newLi.innerHTML = selectedProp.replace(/^.*[\/\\]/g, '') + " <a href=\"#\" onclick=\"clearHighlights('" + selectedProp + "')\">x</a>";
	newLi.setAttribute('name',selectedProp);
	newLi.style.backgroundColor = selectedColor;
	hlEle.appendChild(newLi);
	facetCount++;
	ge('facet-clear').style.display = 'inline';
}

function clearHighlights(prop) {
	for(i = 0; i < annotatedSpans.length; i++) {
		var curSpan = annotatedSpans[i];
		if (prop == null || curSpan.getAttribute('property') == prop) {
			curSpan.style.backgroundColor = '';
		}
	}
	// remove prop(s) from facet list
	var hlEle = ge('facet-highlights');
	for (var i = 0; i < hlEle.childNodes.length; i++) {
		var curLi = hlEle.childNodes[i];
		if (curLi.getAttribute('name') == prop) {
			hlEle.removeChild(curLi);
			facetCount--;
		}
	}
	if (prop == null) {
		hlEle.innerHTML = '';
		facetCount = 0;
	}
	if (facetCount == 0) ge('facet-clear').style.display = 'none';
	
}

function selectNextColor() {
	ge('facet-colors').selectedIndex = Math.floor(Math.random() * colors.length)+1;
}

function in_array(arr,ele) {
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] == ele) return true;
	}
	return false;
}

function initFacet(facetName) {
	// add field to body
	
	var codeEle = document.createElement('div');
	codeEle.id = facetName;
	var code = '<h3>Faceted Viewing</h3>' + 
		'<form>' + 
			'<select id="facet-properties" onchange="selectNextColor()"><option /></select>' + 
			'<select id="facet-colors"><option /></select>' + 
			'<button onclick="highlightProperty(); return false;">highlight</button>&nbsp;' + 
			'<ul id="facet-highlights"></ul>' + 
			'<button id="facet-clear" onclick="clearHighlights(null); return false;" style="display:none">clear all</button>' + 
		'</form>';
		
	//codeEle.innerHTML = code;
	//window.document.body.insertBefore(codeEle,window.document.body.firstChild);

	var facet_body = document.getElementById("facet_container");
	facet_body.innerHTML = code;
	
	var allSpans = document.getElementsByTagName('span');
	presentProperties = new Array();
	annotatedSpans = new Array();
	for (var i=0; i < allSpans.length; i++) {
		var curSpan = allSpans[i];
		var curProp = curSpan.getAttribute('property');
		if (curProp != null ) {
			if (!in_array(presentProperties,curProp)) presentProperties.push(curProp);
			annotatedSpans.push(curSpan);
		}
	}
	var selectEle = ge('facet-properties');
	for (var i=0; i < presentProperties.length; i++) {
		var curProp = presentProperties[i]
		var newOpt = document.createElement('option');
		newOpt.innerHTML = curProp.replace(/^.*[\/\\]/g, '');
		newOpt.setAttribute('value',curProp);
		selectEle.appendChild(newOpt);
	}
	
	var selectEle = ge('facet-colors');
	for (var i=0; i < colors.length; i++) {	
		var curCol = colors[i]
		var newOpt = document.createElement('option');
		newOpt.innerHTML = curCol;
		newOpt.setAttribute('value',curCol);
		newOpt.style.backgroundColor = curCol;
		selectEle.appendChild(newOpt);
	}
	var ff = ge(facetName);
	if (annotatedSpans.length == 0 && ff) ff.style.display = 'none';
}