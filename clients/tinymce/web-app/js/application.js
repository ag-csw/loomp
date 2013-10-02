var mashupList;
var mashupListName;
var mashupButtons;
var mashupButtonsName;
var mashupSaved;
var mashupSavedName;
var mashupAddButton;
var mashupAddButtonName;
var mashupChunksList;
var mashupChunksListName;
var curEditId = null;
var mashupUri;
var userUri;

var queryRunning = false;
var oldQuery;
var oldTitle;
var oldContent;
var curVocabId = null;
var propCache;




/* SACH */
	var geoSubjectToPredicateMap = {

		cityName : [  "capitalOf" , "locatedNear", "belongsTo", "locatedIn"  ],
		riverName : [ "flowsThrough" , "locatedIn" , "belongsTo"],
		countryName : [ "locatedNear" ]

	}
	var geoPredicateToObjectMap = {

		capitalOf : [ "countryName" ],
		belongsTo : [ "cityName" , "countryName" , "villageName" ],
		locatedIn : [ "countryName" ],
		flowsThrough : [ "cityName", "countryName", "villageName"],
		locatedNear: [ "countryName", "riverName", "oceanName", "cityName"]

	}

	var geoResourcePredicatesPriority = {

		cityName : [  "capitalOf" , "locatedNear", "locatedIn"  ],
		riverName : [ "flowsThrough" , "locatedIn" ],
		countryName : [ "locatedNear" ]

	}





function nonRauteDesc(path) {
	return path.replace(/^.*[\#]/g, '');
}






var editorSettings = {
		// General options
		mode : "none",
		theme : "advanced",
		skin : "o2k7",
		skin_variant : "silver",
		plugins : "preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,inlinepopups,annotate,annotationrecommender,ressourceconnector",

		// Theme options
		theme_advanced_buttons1 : "annotate,switchVocabulary,|,code",
		theme_advanced_buttons2 : "res_connect_button, ars_recommend",
		theme_advanced_buttons3 : "",
//		theme_advanced_buttons4 : "",

		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left",
		theme_advanced_statusbar_location : "none",
		theme_advanced_resizing : false,

		// Example content CSS (should be your site CSS)
		content_css : "", // is being overwritten by template
		cleanup : false, // needed for rdfa
		auto_resize: true
	}

var searchQuery;
function editorSearchUpdate(ele) {
	searchQuery = ele.value.replace (/^\s+/, '').replace (/\s+$/, '');
	editorSearchUpdateCall();
}

function editorSearchUpdateCall() {
	var query = searchQuery;
	// try not to fire a callback *every* time...
	if (query == '' || query.length < 3) {
		mashupChunksList.innerHTML = '';
		$('query-button').style.display = 'none';
		return;
	}
	if (query == oldQuery) {
		return;
	}

	if (queryRunning) {
		window.setTimeout("editorSearchUpdateCall()",500);
		return false;
	}
	queryRunning = true;
	mashupChunksList.innerHTML = '<li>searching...</li>'
	oldQuery = query;
	$('query-button').style.display = 'inline';
	new Ajax.Request('mashup/search?query=' + escape(query) + '&user=' + userUri,
	  {
	    method:'get',
	    onSuccess: function(transport){
	    	queryRunning = false;
	    	if (transport.responseText == "no_result") {
	    		mashupChunksList.innerHTML = '';
	    		return;
	    	}
	    	var fragments = transport.responseText.evalJSON(false);
			// clear first
			mashupChunksList.innerHTML = '';
	    	for (var i = 0; i < fragments.length; i++) {
				// create the fragments
				var fragment = fragments[i];
				var ele = editorChunkCreate(fragment.uri,fragment.title,fragment.content);
				ele.style.display = 'block';
				// add to list
				mashupChunksList.appendChild(ele);
			}
			Sortable.create(mashupChunksListName, {
	 			containment: [mashupListName, mashupChunksListName],
	  			constraint:''
			});
	    },
	    onFailure: function(transport){
	    	alert('ERROR: ' + transport.responseText);
	    	queryRunning = false;
	    }
	  });
}

function editorSearchClear() {
	$('chunks-list').innerHTML = '';
	$('query-search').value = '';
	$('query-button').style.display = 'none';
}

function editorAnnotCreate(type,query,selection,button) {
	var annots = null;
	//alert("type "+type+" selection "+selection+" button "+button+" query "+query);
	new Ajax.Request('mashup/getresources?type=' + escape(type) + '&query=' + escape(query), {
		method:'get',
		onSuccess: function(transport) {
			annots = transport.responseText.evalJSON(false);
			//alert(transport.responseText);
			tinyMCE.activeEditor.windowManager.open({
		   		url : 'lib/tiny_mce/plugins/annotate/res2.html',
		   		width : 400,
		   		height : 500,
		   		inline : true
				}, {
		   		annots: annots,
		   		selection: selection,
		   		button: button,
		   		type: type,
		   		propCache: propCache
			});
		},
		onFailure: function(transport){
			alert('ERROR: ' + transport.responseText);
		}
	});

	return true;
}



/** SACH */
function searchMatchingRes() {

	var type = $('res_type-search').value;
	//var label = $('res_label-search').value;
	var query = "";

	var annots = null;
	//alert("type "+type+" label "+label);

	new Ajax.Request('mashup/getmatchingres?type=' + escape(type) + '&query=' + escape(query), {
		method:'get',
		onSuccess: function(transport) {
			annots = transport.responseText.evalJSON(false);
			//alert(transport.responseText);

			tinyMCE.activeEditor.windowManager.open({
		   		url : 'lib/tiny_mce/plugins/annotate/myres.html',
		   		width : 600,
		   		height : 500,
		   		inline : true
				}, {
		   		annots: annots,
		   		//selection: selection,
		   		//button: button,
		   		type: type,
		   		propCache: propCache
			});
		},
		onFailure: function(transport){
			alert('ERROR: ' + transport.responseText);
		}
	});

	return true;

}



/** SACH */
function connectResources(subject_uri, pradicate, object_uri) {
	var antwort;
	//alert(subject_uri+" "+ pradicate+" "+ object_uri);
	//alert("Creating Ressource with type: "+type+" and value: "+value);

	new Ajax.Request('mashup/connectres?subjuri=' + escape(subject_uri) + '&type=' + escape(pradicate) + '&objuri=' + escape(object_uri), {
		method:'get',
		onSuccess: function(transport) {
			antwort = transport.responseText;
			alert(antwort);

		},
		onFailure: function(transport){
			alert('ERROR: ' + transport.responseText);
		}
	});
}











/**
 * Builds the SPAN-tag that will be the replacement for a normal
 * text passage and marks an annotated area of a text.
 *
 * @param {string} res
 * 		The URI of the resource of the annotated text passage.
 * @param {string} type
 * 		The type of the annotation.
 * @param {string} value
 * 		The annotated text passage.
 * @param {Object} [moreAttrs]
 * 		Optional, defaults to <code>{}</code>.<br/>
 * 		More attributes for the SPAN-tag, e.g. like "seeAlso" etc.
 * @author Patrick Jungermann
 * @version 1.0
 */
function editorBuildAnnotSpan(res, type, value, moreAttrs) {
	moreAttrs = moreAttrs || {}

	var span = '<span about="' + res + '" property="' + type + '" id="' + type + '"';
	tinymce.each(moreAttrs, function(item, key) {
		if (item != null && typeof item != 'undefined') {
			span += ' ' + key + '="' + item + '"';
		}
	});
	span += '>' + value + '</span>';

	return span;
}

function editorAnnotFinalize(res) {
	// check global vars
	var se = tinyMCEPopup.getWindowArg('selection',null);
	var button = tinyMCEPopup.getWindowArg('button',null);
	if (se == null || button == null) alert("Uh-Oh. Try again.");
	// do the annotation
	var newProp = button.settings.value;
	var newPropValue = se.getContent();

	// add new property and value to property cache
	if (propCache[res] == undefined) propCache[res] = new Object();
	propCache[res][newProp] = newPropValue;

	// set the annotation within the editor
	se.setContent(editorBuildAnnotSpan(res, newProp, newPropValue));
	button.select();
	// close window
	tinyMCEPopup.close()
}

function editorAnnotFinalizeNew() {
	var se = tinyMCEPopup.getWindowArg('selection',null);
	var type = tinyMCEPopup.getWindowArg('type',null);
	if (se == null || type == null) alert("No text to annotate or no type selected.");
	// ask server for new uri
	editorAnnotNew(type,se.getContent());
}

function editorAnnotRemote() {
	var se = tinyMCEPopup.getWindowArg('selection',null);
	var label = se.getContent();
	$('remotereslist').innerHTML = "loading...";
	new Ajax.Request('../../../../mashup/getforeignres?query=' + escape(label), {
		method:'get',
		onSuccess: function(transport) {
			var data = transport.responseText.evalJSON(false);
			var liststr = "";
			$('remotereslist').innerHTML = "nothing found...";
			tinymce.each(data,function(ele,key) {
				var layer = "info_" + key;
				liststr += "<li><a onmouseover=\"$('" + layer + "').style.display='block'\" onmouseout=\"$('" + layer + "').style.display='none'\" href=\"#\" onclick=\"editorAnnotFinalize('" + key + "')\">" + basename(key) + "</a>";
				liststr += "<div onmouseout=\"$('" + layer + "').style.display='none'\" id=\"" + layer + "\" style=\"display:none; position: absolute; background-color: white; padding: 5px;\">" + ele + "</div>";
			});
			$('remotereslist').innerHTML = liststr;
		},
		onFailure: function(transport){
			alert('ERROR: ' + transport.responseText);
		}
	});
}

function editorAnnotNew(type,value) {
	var uri;
	//alert("Creating Ressource with type: "+type+" and value: "+value);
	new Ajax.Request('../../../../mashup/createresource?type=' + escape(type) + '&value=' + escape(value), {
		method:'get',
		onSuccess: function(transport) {
			uri = transport.responseText;
			// add annotation
			editorAnnotFinalize(uri);
		},
		onFailure: function(transport){
			alert('ERROR: ' + transport.responseText);
		}
	});
}

function editorChunkOpen(eleId) {
	editorMashupDisableOthers(eleId);
	editorChunkEnable(eleId);
	oldTitle = $(getTitleName(eleId)).value;
	var cf = $(getDataName(eleId));
	if (cf) oldContent = cf.innerHTML;
	return false;
}

function editorChunkSave(eleId) {
	editorChunkDisable(eleId);
	editorMashupEnableDragdrop();
	editorMashupChanged();
	return false;
}


function editorChunkCancel(eleId) {
	editorChunkDisable(eleId);
	editorMashupEnableDragdrop();
	$(getTitleName(eleId)).value = oldTitle;
	$(getDataName(eleId)).innerHTML = oldContent;
	oldTitle = '';
	oldContent = '';
	return false;
}

function editorChunkEnable(eleId) {
	var buttons = $$('#' + getEleName(eleId) + ' button');
	for (var b = 0; b < buttons.length; b++) {
		// hide edit button & add button
		if (buttons[b].name == 'edit') {
			buttons[b].style.display = 'inline';
		}
		// show other buttons
		else {
			buttons[b].style.display = 'none';
		}
	}
	// enable title
	$(getTitleName(eleId)).removeAttribute('disabled');
	// enable mce
	//$(getEleName(eleId)).className = 'chunk-item-enabled';
	$$("#" + getEleName(eleId) + " .chunk-item").each(function(ele){ele.className='chunk-item-enabled'});
	tinyMCE.settings = editorSettings;
	curEditId = eleId;
	if ($(getDataName(eleId))) tinyMCE.execCommand('mceAddControl', true, getDataName(eleId));
	//$('mashup-title').setAttribute('disabled','disabled');
}

function editorChunkDisable(eleId) {
	var buttons = $$('#' + getEleName(eleId) + ' button');
	for (var b = 0; b < buttons.length; b++) {
		// show edit button & add button
		if (buttons[b].name == 'edit') {
			buttons[b].style.display = 'none';
		}
		// hide other buttons
		else {
			buttons[b].style.display = 'inline';
		}
	}
	// disable title
	//$(getTitleName(eleId)).setAttribute('disabled','disabled');
	// disable mce
	$$("#" + getEleName(eleId) + " .chunk-item-enabled").each(function(ele){ele.className='chunk-item'});
	curEditId = null;
	if ($(getDataName(eleId))) tinyMCE.execCommand('mceRemoveControl', false, getDataName(eleId));
	//$('mashup-title').removeAttribute('disabled');
}


function editorMashupDisableOthers(eleId) {
	// hide global add button
	mashupAddButton.style.display = 'none';
	// hide handles
	var list = $$('#' + mashupListName + ' .handle');
	for (var i = 0; i < list.length; i++) {
		list[i].className = 'handle-hidden';
	}
	// shadow other objects
	var list = $$('#' + mashupListName + ' li');
	for (var i = 0; i < list.length; i++) {
		if (eleId != getEleId(list[i].id)) {
			list[i].className = 'shadow';
			// hide their buttons
			var buttons = $$('#' + list[i].id + ' button');
			for (var b = 0; b < buttons.length; b++) {
				buttons[b].style.display = 'none';
			}
		}
	}
	// kill drag&drop
	Sortable.destroy(mashupListName);
	Sortable.destroy(mashupChunksListName);
}

function editorMashupEnableDragdrop() {

	// show handles
	var list = $$('#' + mashupListName + ' .handle-hidden');
	for (var i = 0; i < list.length; i++) {
		list[i].className = 'handle';
	}
	// remove shadow
	var list = $$('#' + mashupListName + ' li');
	for (var i = 0; i < list.length; i++) {
		list[i].className = '';
		// show some of their buttons

		var buttons = $$('#' + mashupListName + ' button');
		for (var b = 0; b < buttons.length; b++) {
			if (buttons[b].name != 'edit') {
				buttons[b].style.display = 'inline';
			}
		}

	}
	// show global add button
	mashupAddButton.style.display = 'block';
	Sortable.create(mashupListName, {
	  onUpdate: function() {
	  	editorMashupChanged();
	  	oldQuery = ''; // change search behaviour
	  	/*new Effect.Highlight(list);*/
	  	return false;
	  },
	  handle: "handle",
	  containment: [mashupListName, mashupChunksListName]
	});

	Sortable.create(mashupChunksListName, {
	  containment: [mashupListName, mashupChunksListName],
	  constraint:''
	});
}


function editorMashupAdd(eleId,fragmentType) {
	if (!fragmentType) fragmentType = "text";
	editorMashupChanged();
	var ele;
	if (eleId != "_first") {
		ele = getElement(getEleName(eleId));
	}
	var newEle = editorChunkCreate('', 'New Fragment', '',fragmentType);
	// insert new element
	if (ele == null) 	mashupList.insertBefore(newEle, mashupList.firstChild); // first element
	else				mashupList.insertBefore(newEle,ele.nextSibling);
	// for drag / drop to work again!
	editorMashupEnableDragdrop();
	// effects, yaaay!
	Effect.Appear(newEle.id, { duration: 0.5, afterFinish: function() {
		editorChunkOpen(getEleId(newEle.id));
	}});
	//newEle.style.display = 'block';
	return false;
}

function editorChunkCreate(uri, title, content, type) {
	// clone template node
	var newEle = $('e_template_' + type).cloneNode(true);
	// replace TPL vars
	var newId = randomString("012345670abcdef",5);
	newEle.innerHTML = newEle.innerHTML.replace(/TYPE/g, type);
	newEle.innerHTML = newEle.innerHTML.replace(/TPL/g, newId);
	newEle.innerHTML = newEle.innerHTML.replace(/TITLE/g, title);
	newEle.innerHTML = newEle.innerHTML.replace(/URI/g, uri);
	if (type == "text") {
		newEle.innerHTML = newEle.innerHTML.replace(/CONTENT/g, content);
	}
	if (type == "sparql") {
		if (content == "") {
			content = {sparql:"",template:"",endpoint:""};
		}
		newEle.innerHTML = newEle.innerHTML.replace(/SPARQL/g, content.sparql);
		newEle.innerHTML = newEle.innerHTML.replace(/TEMPLATE/g, content.template);
		newEle.innerHTML = newEle.innerHTML.replace(/ENDPOINT/g, content.endpoint);

	}
	newEle.id = 'e_' + newId;
	return newEle;
}


function editorMashupRemove(eleId) {
	if (!confirm("Wollen Sie dieses Element wirklich loeschen?")) {
		return false;
	}
	//editorChunkCancel(eleId);
	editorMashupChanged();
	mashupList.removeChild(getElement(getEleName(eleId)));
	return false;
}

function editorMashupInsert(mashup) {
	$('mashup-title').value = mashup.title;
	$('mashup-uri').value = mashup.uri;
	for (var i = 0; i < mashup.fragments.length; i++) {
		var fragment = mashup.fragments[i];
		var ele = editorChunkCreate(fragment.uri,fragment.title,fragment.content,fragment.type);
		ele.style.display = 'block';
		mashupList.appendChild(ele);
	}
	editorMashupEnableDragdrop();
}

function editorMashupLoadContent() {
	if (mashupUri == "new") {
		editorMashupInit(mashupUri,userUri);
		return false;
	}
	new Ajax.Request('mashup/load?mashup=' + mashupUri,
	  {
	    method:'get',
	    onSuccess: function(transport){
	    mashupList.innerHTML = '';
	     var mashup = transport.responseText.evalJSON(false);
	     editorMashupInsert(mashup);
	    },
	    onFailure: function(transport){ alert('ERROR: ' + transport.responseText) }
	  });

}

function editorMashupInit(curMashupUri, curUserUri) {
	// init prop cache
	propCache = new Object();
	// get elements
	mashupUri = curMashupUri;
	userUri = curUserUri;
	mashupListName = 'mashup-list';
	mashupList = getElement(mashupListName);
	mashupButtonsName = 'mashup-buttons';
	mashupButtons = getElement(mashupButtonsName);
	mashupSavedName = 'mashup-saved';
	mashupSaved = getElement(mashupSavedName);
	mashupAddButtonName = 'mashup-add-first';
	mashupAddButton = getElement(mashupAddButtonName);
	mashupChunksListName = 'chunks-list';
	mashupChunksList = getElement(mashupChunksListName);

	if (curMashupUri == "new") {
		mashupList.innerHTML = '';
		editorMashupAdd('_first');
	}
	else editorMashupLoadContent();
	editorMashupEnableDragdrop(mashupListName);
}


function editorMashupDiscardAll() {
	editorMashupLoadContent();
	var entries = mashupList.getElementsByTagName('li');
	for (var i = 0; i < entries.length; i++) {
		//editorChunkCancel(getEleId(entries[i].id));
	}
	return false;
}


function editorMashupChanged() {
	mashupButtons.style.display = 'block';
	mashupSaved.style.display = 'none';
	//editorMashupEnableDragdrop(mashupListName);
}

function editorMashupSave() {
	if (curEditId != null) {
		editorChunkDisable(curEditId);
		editorMashupEnableDragdrop(); // for Sortable to work again...
	}

	// TODO: check mashup title
	if ($('mashup-title').value == '') {
		alert('Please enter a title for your mashup!');
		$('mashup-title').focus();
		return;
	}

	var sequence = Sortable.sequence(mashupListName);
	for (var i=0; i < sequence.length; i++) {
		var curId = sequence[i];
		$('o_' + curId).value = i;
		// save content into textarea for form submit
		if ($('tv_' + curId)) $('tv_' + curId).value = $('t_' + curId).innerHTML;
		// check data (titles, empty contents)

		/*if ($('n_' + curId).value == '') {
			alert('Please enter a title for all your Fragments!');
			$('n_' + curId).focus();
			return;
		}*/
		if ($('t_' + curId) && $('t_' + curId).value == '') {
			alert('Please enter some content for all your Fragments!');
			$('t_' + curId).focus();
			return;
		}
	}
	// puh

	new Ajax.Request('mashup/save',
	  {
	    method:'post',
	    postBody:Form.serialize($('editform')),
	    onSuccess: function(transport){
	    	mashupButtons.style.display = 'none';
	    	if (mashupUri == "new") {
				//alert(mashupUri);
	    		editorMashupInit(transport.responseText,userUri);
	    	}
	    	$('mashup-view-link').href = $('mashup-view-link').name + "?mashup=" + transport.responseText;
	   		mashupSaved.style.display = 'block';
	   		propCache = new Object();
	    },
	    onFailure: function(transport){ alert('ERROR: ' + transport.responseText) }
	  });

}

function editorMashupDiscard() {
	editorMashupDiscardAll();
	mashupButtons.style.display = 'none';
	// TODO: if empty, create default mashup
	propCache = new Object();
}

function randomString(sChrs,iLen) {
	var sRnd = '';
	for (var i=0; i < iLen; i++) {
		var randomPoz = Math.floor(Math.random() * sChrs.length);
		sRnd += sChrs.substring(randomPoz,randomPoz+1);
	}
	return sRnd;
}

function getElement(name) {
	var ele = $(name);
	if (ele == null) {
		alert('element ' + name + ' not found!');
		return false;
	}
	return ele;
}

function getEleName(eleId) {
	return 'e_' + eleId;

}

function getEleId(eleName) {
	var curEle = getElement(eleName);
	return curEle.id.substring(curEle.id.indexOf('_')+1);

}

function getDataName(eleId) {
	return 't_' + eleId;
}

function getTitleName(eleId) {
	return 'n_' + eleId;
}

function getPreviewTitleName(eleId) {
	return 'pt_' + eleId;
}

function getPreviewContentName(eleId) {
	return 'pc_' + eleId;
}

function confirmRedirect(uri,text) {
	if(confirm(text)) window.location = uri;
}

function basename(path) {
    return path.replace(/^.*[\/\\]/g, '');
}
