/**
 * @author ralph
 */

var regexp = /(-*\d+).*/;
var scrollInProgress = false;

var selectedButton;
var expanded = false;

var oldListPos;

var buttonId = 0;


function add(name, tagName) {
	var li = document.createElement("li");
	li.style.display = "inline";

	var button = document.createElement("button");
	button.id = "button_" + (buttonId++);
	button.style.margin = "2px 2px 0px 0px";
	button.style.border="none";
	button.style.fontSize="10px";
	button.style.width="65px";
	button.style.height="40px";
	button.style.wordWrap="none";
	button.style.background="white";
	button.style.overflow="hidden";
	button.onclick = function() {select(button);};
	button.onmouseover = function() {highlight(button);};
	button.onmouseout = function() {unhighlightAll();};
	button.onkeydown = function(evt) {evt = (evt) ? evt : window.event; handleKey(button, evt.keyCode);};
	
	name = name.replace(/\s/g, "&nbsp;");
	tagName = tagName.replace(/\s/g, "&nbsp;");

	button.innerHTML = "<span style='font-weight:bold'>" + name + "</span><br/><span>" + tagName + "<span>";
	
	li.appendChild(button);
	
	var theList = document.getElementById("theList");
	theList.appendChild(li);
}

		
function scrollDown() {
	if (scrollInProgress) {
		window.setTimeout("scrollDown()", 1);
		return;
	}
	var theList = document.getElementById("theList");
	var pos = _stripUnit(theList.style.top);
	var actualHeight = _stripUnit(theList.offsetHeight);
	if (pos + actualHeight == 44)
		return;
	_scroll(-42);
}

function scrollUp() {
	if (scrollInProgress) {
		window.setTimeout("scrollUp()", 1);
		return;
	}
	var pos = _stripUnit(document.getElementById("theList").style.top);
	if (pos == 0)
		return;
	_scroll(42);
}

function checkRowIsVisible() {
	var theList = document.getElementById("theList");
	if (_stripUnit(theList.style.top) + _stripUnit(theList.offsetHeight) < 42)
		scrollUp();
}

function _scroll(amount) {
	scrollInProgress = true;
	if (amount == 0) {
		scrollInProgress = false;
		return;
	}
	var theList = document.getElementById("theList");
	var sign = amount / Math.abs(amount);
	var oldPosStr = theList.style.top;
	var oldPos = _stripUnit(oldPosStr);
	var newPos = (oldPos + sign);
	theList.style.top = newPos + "px";
	
	window.setTimeout("_scroll(" + (amount-sign) + ")", 1);
}

function _stripUnit(string) {
	regexp.exec(string);
	return parseInt(RegExp.$1);
}

function showOrHideTable() {
	if (expanded)
		collapseTable();
	else
		expandTable();
}

function expandTable() {
	var theList = document.getElementById("theList");
	var theDiv = document.getElementById("theDiv");
	oldListPos = theList.style.top;
	theList.style.top = "0px";
	theDiv.style.overflow = "visible";
	expanded = true;
}

function collapseTable() {
	var theList = document.getElementById("theList");
	var theDiv = document.getElementById("theDiv");
	theDiv.style.overflow = "hidden";
	theList.style.top = oldListPos;
	expanded = false;
}

function highlight(button) {
	button.style.backgroundImage="url(img/border_hover.png)";
}

function unhighlight(button) {
	if (button == selectedButton) {
		button.style.backgroundImage="url(img/border_selected.png)";
	} else {
		button.style.backgroundImage="";
	}
}

function select(button) {
	if (selectedButton) {
		selectedButton.style.backgroundImage="";
	}
	button.style.backgroundImage="url(img/border_selected.png)";
	selectedButton = button;
	
	oldListPos = 2 - button.offsetTop;
	collapseTable();
}

function handleKey(button, keyCode) {
	if (scrollInProgress)
		return;
	switch(keyCode) {
		case 37:
			var newParent = button.parentNode.previousSibling;
			if(newParent && newParent.nodeName.toLowerCase() == "li") {
				var newButton = newParent.firstChild;
				if (newButton.offsetTop < button.offsetTop)
					scrollUp();
				switchHiglightWhenScrollingFinished(newButton);
			}
			break;
		case 38:
			var oldPos = button.parentNode.offsetLeft;
			var nextLI=button.parentNode;
			var nextButton;
			while (nextLI.previousSibling != undefined) {
				nextLI=nextLI.previousSibling;
				var newPos = nextLI.offsetLeft;
				if (newPos == oldPos) {
					nextButton = nextLI.firstChild;
					break;
				}
			}
			if (!nextButton && nextLI.offsetTop != button.parentNode.offsetTop) {
				nextButton = nextLI.firstChild;
			}
			if (nextButton) {
				scrollUp();
				switchHiglightWhenScrollingFinished(nextButton);
			}
			break;
		case 39:
			var newParent = button.parentNode.nextSibling;
			if (newParent && newParent.nodeName.toLowerCase() == "li") {
				var newButton = newParent.firstChild;
				if (newButton.offsetTop > button.offsetTop)
					scrollDown();
				switchHiglightWhenScrollingFinished(newButton);
			}
			break;
		case 40:
			var oldPos = button.parentNode.offsetLeft;
			var nextLI=button.parentNode;
			var nextButton;
			while (nextLI.nextSibling != undefined) {
				nextLI=nextLI.nextSibling;
				var newPos = nextLI.offsetLeft;
				if (newPos == oldPos) {
					nextButton = nextLI.firstChild;
					break;
				}
			}
			if (!nextButton && nextLI.offsetTop != button.parentNode.offsetTop) {
				nextButton = nextLI.firstChild;
			}
			if (nextButton) {
				scrollDown();
				switchHiglightWhenScrollingFinished(nextButton);
			}
			break;
		case 13:
			select(button);
			break;
	}
}

function switchHiglightWhenScrollingFinished(newButton) {
	unhighlightAll();
	if (scrollInProgress) {
		window.setTimeout(function() {switchHiglightWhenScrollingFinished(newButton)}, 1);
		return;
	}
	highlight(newButton);
	newButton.focus();	
}

function unhighlightAll() {
	var lis = document.getElementById("theList").childNodes;
	for(i=0; i<lis.length; i++) {
		var li = lis[i];
		if (li.nodeName.toLowerCase() == "li") {
			var button = li.firstChild;
			unhighlight(button);
		}
	}
	
}
