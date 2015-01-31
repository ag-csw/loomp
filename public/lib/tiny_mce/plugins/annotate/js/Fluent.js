var ITEMS_PER_ROW = 6;
var lastAddedRow;
var singleLineHeight;
var firefox = navigator.userAgent.indexOf("Firefox") != -1;

var currentRow;
var selectedButton;

var expanded = false;


function add(item) {
	var td = document.createElement("td");
	td.style.padding="5px";
	td.style.margin="0px";
	td.onmouseover = function() {td.style.padding='0px'; td.style.border='5px solid yellow';};
	td.onmouseout = function() {td.style.padding='5px'; td.style.border='none';};
	
	var button = document.createElement("button");
	button.type="button";
	button.style.fontSize="10px";
	button.style.border="none";
	button.style.width="60px";
	button.style.height="40px";
	button.style.wordWrap="break-word";
	button.style.background="white";
	button.onclick = function() {select(button)};
	
	var text = document.createTextNode(item);
	button.appendChild(text);
	
	td.appendChild(button);
	
	if(!lastAddedRow) {
		lastAddedRow = addRow(true);
		currentRow = lastAddedRow;
	}
	var childCount = lastAddedRow.childNodes.length;
	if (childCount == ITEMS_PER_ROW) {
		lastAddedRow = addRow(false);
	}
	
	lastAddedRow.appendChild(td);
	
	if (!singleLineHeight)
		singleLineHeight = document.getElementById("theTable").offsetHeight;
}

function complete() {
	if(!lastAddedRow) {
		lastAddedRow = addRow(true);
		currentRow = lastAddedRow;
	}
	var numberOfMissingPlaceholders = ITEMS_PER_ROW - lastAddedRow.childNodes.length;
	for (i=0; i < numberOfMissingPlaceholders; i++) {
		var td = document.createElement("td");
		td.style.padding="5px";
		td.style.margin="0px";
		var button = document.createElement("button");
		button.type="button";
		button.style.border="none";
		button.style.width="60px";
		button.style.height="40px";
		button.style.visibility="hidden";
		
		td.appendChild(button);
		lastAddedRow.appendChild(td);

	}
}

function addRow(visible) {
	var table = document.getElementById("theTable");
	var rowCount = table.getElementsByTagName("tr").length;
	var tr = document.createElement("tr");
	tr.id = "row" + rowCount;
	setVisible(tr, visible);
	table.appendChild(tr);
	return tr;
}
		
function scrollUp() {
	scroll(currentRow.previousSibling);
}

function scrollDown() {
	scroll(currentRow.nextSibling);
}

function scroll(targetRow) {
	collapseTable();
	if (targetRow && targetRow.nodeType == 1) {
		setVisible(targetRow, true);
		setVisible(currentRow, false);
		currentRow = targetRow;
	}
}

function showOrHideTable() {
	if (expanded)
		collapseTable();
	else
		showEntireTable();
}

function showEntireTable() {
	var theTable = document.getElementById("theTable");
	for(i=0;;i++) {
		var currentRow = document.getElementById("row" + i);
		if (!currentRow)
			break;
		setVisible(currentRow, true);
	}
	var margin = singleLineHeight - theTable.offsetHeight;
	theTable.style.marginBottom = margin;
	expanded = true;
}

function collapseTable() {
	for(i=0;;i++) {
		var tr = document.getElementById("row" + i);
		if (!tr)
			break;
		if (tr == currentRow)
			setVisible(tr, true);
		else
			setVisible(tr, false);
	}
	document.getElementById("theTable").style.marginBottom = "0px";
	expanded = false;
}

function select(button) {
	if (selectedButton) {
		var oldSelectedTd = selectedButton.parentNode;
		oldSelectedTd.style.padding='5px'; oldSelectedTd.style.border='none';
		oldSelectedTd.onmouseover = function() {oldSelectedTd.style.padding='0px'; oldSelectedTd.style.border='5px solid yellow';};
		oldSelectedTd.onmouseout = function() {oldSelectedTd.style.padding='5px'; oldSelectedTd.style.border='none';};
	}
	
	selectedButton = button;
	var newSelectedTd = button.parentNode;
	newSelectedTd.style.padding='0px'; newSelectedTd.style.border='5px solid orange';
	newSelectedTd.onmouseover = function() {newSelectedTd.style.padding='0px'; newSelectedTd.style.border='5px solid yellow';};
	newSelectedTd.onmouseout = function() {newSelectedTd.style.padding='0px'; newSelectedTd.style.border='5px solid orange';};

	currentRow = newSelectedTd.parentNode;
	collapseTable();
}

function setVisible(element, visible) {
	if (firefox) {
		if (visible) 
			element.style.visibility = "visible";
		else
			element.style.visibility = "collapse";
	} else {
		if (visible) 
			element.style.display = "";
		else
			element.style.display = "none";
	}
}
