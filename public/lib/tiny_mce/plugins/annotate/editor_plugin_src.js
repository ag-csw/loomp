// Widths for the two choice elements (number of buttons per row).
var annotationChoiceWidth = 6;
var vocabularyChoiceWidth = 1;
var firstVocabButton = false;

/* SACH */
var selectedSpanId;
var selectedSpanProperty;
var selectedSpanAbout;
var selectedSpanText;
var currentVocabSelection;
var currentSelectedSpanItem;



/**
 * $Id: editor_plugin_src.js 885 2008-06-22 19:23:20Z schaef $
 *
 * @author loomp.org
 * @copyright Copyright © 2008, loomp.org, All rights reserved.
 */

(function() {
	tinymce.create('tinymce.plugins.AnnotationPlugin', {
		
		createControl: function(n, cm) {
	        switch (n) {
	            case 'annotate':
	                // Return the new Fluent choice instance
	                return this.getAnnotationChoices(cm);
					
				case 'switchVocabulary':
					return this.getVocabularyChoice(cm);
	        }
	        return null;
	    },
		
		getAnnotationChoices : function(cm) {
			if (!this.toggler) {
				this.initControls(cm);
			}
			return this.toggler;
		},
	
		getVocabularyChoice : function(cm) {
			if (!this.vocabularyChoice) {
				this.initControls(cm);
			}
			return this.vocabularyChoice;
		},
		
		initControls : function(cm) {
			var t = this, DOM = tinymce.DOM, ed = cm.editor;
			
			t.buttons = new Array();
			
			// this is the container that holds the different choice controls for each vocabulary. One at a time is visible.
			t.toggler = new tinymce.ui.FluentChoiceToggler({});

			// this is the choice control with which the user toggles between the vocabularies contained in the above defined
			// toggler.
            t.vocabularyChoice = cm.createListBox(DOM.uniqueId(), {}, tinymce.ui.FluentChoice);
            // Add values to the choice controls
			if (t.vocabularyChoice) {
				var availableVocabularies = getVocabularies();				
				
				tinymce.each(availableVocabularies, function(vocabulary, vocabularyIndex) {				
		            var annotationChoice = cm.createListBox(DOM.uniqueId(), {updown:true}, tinymce.ui.FluentChoice);
					tinymce.each(vocabulary.annotations, function(annotation, annotIndex) {
						var button = annotationChoice.add(annotation.label, annotation.uri, {annotation:annotation}, cm);
						//debug;
						button.addEventHandler("click", function() {
							if (!button.isDisabled()) {
								var se = ed.selection;
								var ele = se.getStart();
								var curAnnot = ele.id;
								// check children nodes for annots
								
								
								
								if (se.getContent().search(/id=("|')([^"']+)("|')/) != -1) {
									//alert("found!");
									each(ele.childNodes,function(e) {
										if (e.nodeName != undefined && e.nodeName.toLowerCase() == "span" && e.id != "") {
											curAnnot = e.id;
											ele = e;
										}
									});
								}
								
								// check parent nodes for annots
								while (ele && curAnnot != "") {									
									if (ele.nodeName.toLowerCase() == "span" && ele.id != "") {
										curAnnot = ele.id;
										
										break;
									}
									ele = ele.parentNode; 
								}
								if (curAnnot != "") { // cursor inside some annot? -> remove after confirm
									if (!confirm("Really remove this annotation?")) return;
									ele.parentNode.replaceChild(document.createTextNode(ele.innerHTML),ele);
									button.unselect();
									annotationChoice.collapseTable();
									if (curAnnot == button.settings.value) return; // otherwise use new annot
								}
								
								if (se.isCollapsed()) { // nothing selected -> do nothing
									alert("Please select some Text to be annotated");
									button.unselect();
									annotationChoice.collapseTable();
									return;
								}
								// callback to loomp code (loomp.js)
								editorAnnotCreate(button.settings.annotation.annDomain,'',se,button);

								annotationChoice.collapseTable();
							}
						});
						button.addEventHandler("mouseover", function() {
							if (!button.isDisabled()) {
								button.container.unhighlightAll();
								button.highlight();
								button.element.focus();
							}
						});
						button.addEventHandler("mouseout", function() {
							if (!button.isDisabled())
								button.unhighlight();
						});
						button.addEventHandler("keydown", function(e) {
							e = (e) ? e : window.event;
							if (!button.isDisabled())
								button.handleKey(e);
						});
						t.buttons.push(button);//[] = button;
					});
					
					t.toggler.add(annotationChoice);					
					
					var vocabularyButton = t.vocabularyChoice.add(vocabulary.label, vocabulary.id, {}, cm);				
					vocabularyButton.addEventHandler("click", function() {						
						if (!vocabularyButton.isDisabled()) {
							vocabularyButton.select();
							t.toggler.select(annotationChoice);
							currentVocabSelection = vocabulary.label;	// SACH			
						}
					});
					vocabularyButton.addEventHandler("mouseover", function() {
						if (!vocabularyButton.isDisabled()) {
							vocabularyButton.container.unhighlightAll();
							vocabularyButton.highlight();
							vocabularyButton.element.focus();
						}
					});
					vocabularyButton.addEventHandler("mouseout", function() {
						if (!vocabularyButton.isDisabled())
							vocabularyButton.unhighlight();
					});
					vocabularyButton.addEventHandler("keydown", function(e) {
						e = (e) ? e : window.event;
						if (!vocabularyButton.isDisabled()) {
							vocabularyButton.handleKey(e);
							var keyCode = e.keyCode;
							if (keyCode == 13) {
								t.toggler.select(annotationChoice);
								currentVocabSelection = vocabulary.label;	// SACH
							}
						}
						
					});
					if (!firstVocabButton) { 
						firstVocabButton = vocabularyButton; 
						currentVocabSelection = availableVocabularies[0].label; 	// SACH
					}
				});
			}	
		},
	
		init : function(ed, url) {
			tinymce.DOM.loadCSS(url + "/css/annotate.css");
			var t = this;
			ed.onInit.add(function() {				
				var overallButtonWidth;
				for (a in document.styleSheets) {
					var css = document.styleSheets[a];
					var href = css.href;
					if (href != undefined && href.indexOf("annotate.css") != -1) {
						var cssRules = css.cssRules ? css.cssRules : css.rules;
						for (b in cssRules) {
							if(cssRules[b].selectorText != undefined && cssRules[b].selectorText.toLowerCase().indexOf("button.annotate_item_button")!=-1) {
								var buttonWidth = _stripUnit(cssRules[b].style.width);
								var buttonMarginL = _stripUnit(cssRules[b].style.marginLeft);
								var buttonMarginR = _stripUnit(cssRules[b].style.marginRight);
								var buttonPaddingL = _stripUnit(cssRules[b].style.paddingLeft);
								var buttonPaddingR = _stripUnit(cssRules[b].style.paddingRight);
								overallButtonWidth = buttonWidth + buttonMarginL + buttonMarginR + buttonPaddingL + buttonPaddingR;
								break;
							}
						}
					}
					if (overallButtonWidth)
						break;
				}
				
				var possibleSpace = $(tinyMCE.activeEditor.editorContainer).offsetWidth;
				annotationChoiceWidth = Math.abs(Math.round((possibleSpace / overallButtonWidth))-3);
				var annotationChoiceWidthPx = (annotationChoiceWidth * overallButtonWidth) + "px";
				tinymce.each(t.toggler.choices, function(choice){
					choice.theDiv.style.width = annotationChoiceWidthPx;
				});
				var vocabularyChoiceWidthPx = (vocabularyChoiceWidth * overallButtonWidth) + "px";
					t.vocabularyChoice.theDiv.style.width = vocabularyChoiceWidthPx;
			});
			
			/* Triggered, when the cursor is somewhere in the textfield */
			ed.onMouseUp.add(function(ed, e) {	
				
				/* SACH */
				selectedSpanId = null;		//
				selectedSpanProperty = null;
				selectedSpanAbout = null;
				selectedSpanText = null;
				currentSelectedSpanItem = null;

				//alert(ed.selection.getNode().getAttribute('about'));
				var spanItem = ed.selection.getNode();
				
				if(spanItem.nodeName.toLowerCase() == "span") {
					selectedSpanId = spanItem.getAttribute('id');
					selectedSpanProperty = spanItem.getAttribute('property');
					selectedSpanAbout = spanItem.getAttribute('about');
					selectedSpanText = spanItem.innerHTML;
					currentSelectedSpanItem = spanItem;
				}	
				/* SACH Ende */
				
				var se = ed.selection;
				var parent = se.getNode();
				t.toggler.unhighlightAll();
				t.buttons.each(function(b) {
					b.unselect();
				});
				
				// check if we have a valid uri within the selection
				var insideIdRaw = se.getContent().search(/id=("|')([^"']+)("|')/);
				if (insideIdRaw != -1) {
					// we do!
					var insideId = RegExp.$2;
					t.checkHighlight(insideId);
				}
				
				// else check parent nodes
				while (parent) {
					if (parent.nodeName.toLowerCase() == "span") {
						t.checkHighlight(parent.id);
					}
					parent = parent.parentNode;
				}
				
			});
			
			ed.onBeforeSetContent.add(function(ed, o) {
				
			});

			ed.onPostProcess.add(function(ed, o) {
				
			});
			
			ed.addCommand('loompSetParent', function(ui, v) {
				alert("loompSetParent ui="+ui+" v="+v);
//				if (e = ed.selection.getNode()) {
//					ed.dom.setAttrib(e, 'style', v);
//					ed.execCommand('mceRepaint');
//				}
			});
			
		},
		
		checkHighlight : function(id) {
			var t = this;
			t.toggler.choices.each(function(c,i) {
				c.items.each(function(b) {
					if (b.settings.value == id) {
						t.toggler.select(c);
						t.vocabularyChoice.items[i].select();
						b.select();						
						return;
					}
				});
			});
		},
		
		getInfo : function() {
			return {
				longname : 'Annotation',
				author : 'loomp.org',
				authorurl : 'http://loomp.org',
				infourl : 'http://loomp.org',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},
		
		/**
		 * Loads the identifiers for available vocabularies. The identifiers are later used to
		 * switch between vocabularies.
		 * @return a one-dimensional array containing identifiers for available vocabularies
		 not needed since switch to new vocab structure - hm
		getAvailableVocabularyIDs : function() {
			return getAvailableVocabularyIDs();
		},
		
		getVocabulary : function(id) {
			return getAvail(id);
		}*/
	});

	// Register plugin
	tinymce.PluginManager.add('annotate', tinymce.plugins.AnnotationPlugin);
	
	// Initialize TinyMCE with the new plugin and button
	tinyMCE.init({
//		plugins : 'annotate', // - means TinyMCE will not try to load it
//		theme_advanced_buttons1 : 'annotationListBox' // Add the new annotationListBox control to the toolbar
	});

})();


/* The FluentChoiceToggler class */
(function() {
	var DOM = tinymce.DOM, Event = tinymce.dom.Event, each = tinymce.each, Dispatcher = tinymce.util.Dispatcher;

	/**#@+
	 * @class This class is a container for multiple FluentChoice instances. Only one FluentChoice in this
	 * container is visible at a time. This class provides methods for switching between the individual
	 * FluentChoices, setting the currently visible FluentChoice invisible and the new selected one visible.
	 * @member tinymce.ui.FluentContainer
	 * @base tinymce.ui.Container
	 */
	tinymce.create('tinymce.ui.FluentChoiceToggler:tinymce.ui.Container', {
		
		/**
		 * Constructs a new FluentConteiner instance.
		 */
		FluentChoiceToggler : function(s) {
			var t = this;
			t.parent(tinymce.DOM.uniqueId(), s);
			t.choices = new Array();
			t.selectedChoice = undefined;
		},
		
		/**
		 * 
		 * @param {Object} fluentChoice
		 */
		add : function(fluentChoice) {
			this.choices.push(fluentChoice);
		},
		
		select : function(newSelectedChoice) {
			var t = this;
			if (newSelectedChoice) {
				if (t.selectedChoice) {
					t.selectedChoice.setVisible(false);
				}
				newSelectedChoice.setVisible(true);
				t.selectedChoice = newSelectedChoice;
			}
		},
		
		/**
		 * Renders this FluentContainer as an HTML string. This method is much faster than using the DOM and when
		 * creating a whole toolbar with buttons it does make a lot of difference.
		 *
		 * @return {String} HTML for the Fluent choice control element.
		 */
		renderHTML : function() {
			var h = '', t = this, s = t.settings, cp = t.classPrefix;
			h += '<div class="annotateContainer">';
			var first = true;
			each(t.choices, function(choice, index) {
				if (index == 0) {
					choice.setVisible(true);
					t.selectedChoice = choice;
				} else {
					choice.setVisible(false);
				}
				h += choice.renderHTML();
			});
			h += '</div>';
			return h;
		},
		
		postRender : function() {
			var t = this;
			var firstChoice = t.choices[0];
			if (firstVocabButton) {
				//firstChoice.setVisible(true);
				firstVocabButton.select();	
			}
		},
		
		unhighlightAll : function() {
			each(this.choices, function(choice) {
				choice.unhighlightAll();
			});
		}
	});
		
})();

/* The FluentChoice control class */
(function() {
	var DOM = tinymce.DOM, Event = tinymce.dom.Event, each = tinymce.each, Dispatcher = tinymce.util.Dispatcher;

	/**#@+
	 * @class This class mimics a Fluent choice control.
	 * @member tinymce.ui.FluentChoice
	 * @base tinymce.ui.Container
	 */
	tinymce.create('tinymce.ui.FluentChoice:tinymce.ui.Container', {
		/**
		 * Constructs a new FluentChoice control instance.
		 *
		 * @param {String} id Control id for the Fluent choice control.
		 * @param {Object} s Optional name/value settings object.
		 */
		FluentChoice : function(id, s) {
			var t = this;

			t.parent(id, s);
			t.onChange = new Dispatcher(t);
			t.onPostRender = new Dispatcher(t);
			t.onAdd = new Dispatcher(t);
//			t.onRenderMenu = new tinymce.util.Dispatcher(this);
			t.classPrefix = 'mceFluentChoice';
			
			t.pluginURL = tinymce.PluginManager.urls["annotate"];
			
			t.selectedMenu = undefined;
			
			t.buttonHeight = 42;
			t.scrollSpeed = 6;
			t.scrollInProgress = false;
			t.selectedButton = undefined;
			t.expanded = false;
			t.oldListPos = "0px";
			
			var cm = s.control_manager;
			t.buttonUp = cm.createButton(DOM.uniqueId(), {onclick : function() {t.scrollUp();}});
			t.buttonDown = cm.createButton(DOM.uniqueId(), {onclick : function() {t.scrollDown();}});
			t.buttonExpand = cm.createButton(DOM.uniqueId(), {onclick : function() {t.showOrHideTable();}});

			t.items = new Array();
			t.visible = true;
		},

		/**#@+
		 * @method
		 */


		/**
		 * Adds a button to the choice.
		 *
		 * @param {String} n Title for the new option.
		 * @param {String} v Value for the new option.
		 * @param {Object} o Optional object with settings like for example class.
		 * @param {Object} cm The global ControlManager instance
		 * @return the button that has been created and added to this choice control.
		 */
		add : function(n, v, o, cm) {
			var t = this, DOM = tinymce.DOM;
			
			o = o || {};
			o = tinymce.extend(o, {
				title : n,
				value : v
			});
			
			var buttonId = DOM.uniqueId();
			o = tinymce.extend({
				'id' : buttonId
			}, o);
			
			var button = new tinymce.ui.FluentButton(buttonId, this, o);
			cm.add(button);
			
			cm.editor.addCommand();
			
			if (t.items.length != 0) {
				var previous = t.items[t.items.length-1];
				button.previous = previous;
				previous.next = button;
			}
			
			t.items.push(button);
			t.onAdd.dispatch(t, o);
			
			return button;
		},

		/**
		 * Returns the number of items inside the list choice.
		 *
		 * @param {Number} Number of items inside the choice.
		 */
		getLength : function() {
			return this.items.length;
		},

		/**
		 * Renders the Fluent choice as an HTML string. This method is much faster than using the DOM and when
		 * creating a whole toolbar with buttons it does make a lot of difference.
		 *
		 * @return {String} HTML for the Fluent choice control element.
		 */
		renderHTML : function() {
			var h = '', t = this, s = t.settings, cp = t.classPrefix;			

			t.tableId = DOM.uniqueId();
			t.divId = DOM.uniqueId();
			t.listId = DOM.uniqueId();
			
			h += '<table id="' + t.tableId + '" class="annotate" style="display:';
			h += (t.visible ? 'inline' : 'none') + ';">';
			h += '<tr>';
			h += '<td>';
			h += '<div id="' + t.divId + '" class="annotate" style="height:' + t.buttonHeight + 'px;">';
			h += '<ul id="' + t.listId + '" class="annotate">';
			
			each(t.items, function(button) {
				h += '<li class="annotate">';
				
				h += button.renderHTML();
				
				h += '</li>\n';
			});
			
			h += '</ul>';
			h += '</div>';
			h += '</td>';
			h += '<td>';
			
			if (s.updown) { 
				h += '<img class="annotate_navbutton_up" id="' + t.buttonUp.id + '" onclick="return false;" src="' + t.pluginURL + '/img/arrow_up.png" />';
				h += '<img class="annotate_navbutton_down" id="' + t.buttonDown.id + '" onclick="return false;" src="' + t.pluginURL + '/img/arrow_down.png" />';
			}
			h += '<img class="annotate_navbutton_expand" id="' + t.buttonExpand.id + '" onclick="return false;" src="' + t.pluginURL + '/img/expand.png" />';
			h += '</td>';
			h += '</tr>';
			h += '</table>';
			
			return h;
		},
		
		postRender : function() {
			var t = this;
			t.theDiv = DOM.get(t.divId);
			t.theList = DOM.get(t.listId);
			t.element = DOM.get(t.tableId);
			each(t.items, function(button) {
				button.element = DOM.get(button.settings.id);
			});
		},

		destroy : function() {
			this.parent();

			Event.clear(this.id + '_text');
		},
		
		unhighlightAll : function() {
			each(this.items, function(button) {
				button.unhighlight();
			});
		},

		scrollDown : function() {
			var t = this;
			if (t.scrollInProgress) {
				window.setTimeout(function() {
					t.scrollDown();
				}, 1);
				return;
			}
			t.collapseTable();
			var pos = _stripUnit(t.theList.style.top);
			var actualHeight = _stripUnit(t.theList.offsetHeight);
			if (pos + actualHeight -2 == t.buttonHeight)
				return;
			t.scroll(-(t.buttonHeight));
		},
		
		scrollUp : function() {
			var t = this;
			if (t.scrollInProgress) {
				window.setTimeout(function(){
					t.scrollUp();
				}, 1);
				return;
			}
			t.collapseTable();
			var pos = _stripUnit(t.theList.style.top);
			if (pos == 0)
				return;
			t.scroll(t.buttonHeight);
		},
		
		checkRowIsVisible: function() {
			if (_stripUnit(this.theList.style.top) + _stripUnit(this.theList.offsetHeight) < this.buttonHeight)
				this.scrollUp();
		},
		
		scroll : function(amount) {
			var t = this;
			t.scrollInProgress = true;
			if (amount == 0) {
				t.scrollInProgress = false;
				return;
			}
			var sign = amount / Math.abs(amount) * t.scrollSpeed;
			var oldPosStr = t.theList.style.top;
			var oldPos = _stripUnit(oldPosStr);
			var newPos = (oldPos + sign);
			t.theList.style.top = newPos + "px";
			t.oldListPos = newPos + "px";
			
			window.setTimeout(function(){
				t.scroll(amount - sign);
			}, 1);
		},
		
		showOrHideTable : function() {
			if (this.expanded)
				this.collapseTable();
			else
				this.expandTable();
		},
		
		expandTable : function() {
			this.oldListPos = this.theList.style.top;
			this.theList.style.top = "0px";
			this.theDiv.style.overflow = "visible";
			this.expanded = true;
		},
		
		collapseTable : function() {
			this.theDiv.style.overflow = "hidden";
			this.theList.style.top = _stripUnit(this.oldListPos) + "px";
			this.expanded = false;
		},
		
		switchHiglightWhenScrollingFinished : function(newButton) {
			var t = this;
			newButton.highlight();
			if (t.scrollInProgress) {
				window.setTimeout(function() {
					t.switchHiglightWhenScrollingFinished(newButton);
				}, 20);
				return;
			}
			newButton.element.focus();
		},
		
		setVisible : function(visible) {
			this.visible = visible;
			if (this.element) {
				if (visible) {
					this.element.style.display = "inline";
				} else {
					this.element.style.display = "none";
				}
			}
		}
		
		/**#@-*/
	});
})();




/**
 * The Fluent Control Button prototype.
 */
(function() {
	var DOM = tinymce.DOM;

	/**#@+
	 * @class This class is used to create a UI button. A button is basically a link
	 * that is styled to look like a button or icon.
	 * @member tinymce.ui.Button
	 * @base tinymce.ui.Control
	 */
	tinymce.create('tinymce.ui.FluentButton:tinymce.ui.Control', {
		/**
		 * Constructs a new button control instance.
		 *
		 * @param {String} id Control id for the button.
		 * @param {Object} container The Fluent Choice control this button is child of.
		 * @param {Object} s Optional name/value settings object.
		 */
		FluentButton : function(id, container, s) {
			this.parent(id, s);
			this.container = container;
			this.classPrefix = 'fluentButton';
			this.previous = undefined;
			this.next = undefined;
			this.eventHandlers = new Array();
		},
		
		/**
		 * Sets event handlers for this button. The reason why this is not done in the constructor is
		 * that the event handlers passed to this function should be allowed to contain references to
		 * this button. 
		 * @param {Object} eventHandlers An associative array containing as keys event names (like 'click', 'mouseOver', 'keyPressed', etc.) and as values functions that will be executed at the appropriate event.
		 */
		addEventHandler : function(eventName, eventHandler) {
			this.eventHandlers[eventName] = eventHandler;
		},

		/**#@+
		 * @method
		 */

		/**
		 * Renders the button as a HTML string. This method is much faster than using the DOM and when
		 * creating a whole toolbar with buttons it does make a lot of difference.
		 *
		 * @return {String} HTML for the button control element.
		 */
		renderHTML : function() {
			var h;
			h = '<button id="' + this.settings.id + '" type="button" class="annotate_item_button" onclick="return false;" onmouseover="return false;" onmouseout="return false;" onkeydown="return false;">';
			h += this.settings.title;
			h += '</button>';

			return h;
		},

		/**
		 * Post render handler. This function will be called after the UI has been
		 * rendered so that events can be added.
		 */
		postRender : function() {
			var t = this;
			
			for (eventName in t.eventHandlers) {
				tinymce.dom. Event.add(t.id, eventName, t.eventHandlers[eventName]);
			}
		},
		
		highlight : function() {
			this.container.unhighlightAll();
			this.element.style.backgroundImage = "url(" + this.getFullURL("img/border_hover.png") + ")";
		},
		
		unhighlight : function () {
			var button = this.element;
			if (button == this.container.selectedButton) {
				button.style.backgroundImage = "url(" + this.getFullURL("img/border_selected.png") + ")";
			} else {
				button.style.backgroundImage = "";
			}
		},
		
		select : function() {
			var button = this.element;
			if (this.container.selectedButton) {
				this.container.selectedButton.style.backgroundImage = "";
			}
			button.style.backgroundImage = "url(" + this.getFullURL("img/border_selected.png") + ")";
			this.container.selectedButton = button;
			this.container.selectedItem = this;
			
			this.container.oldListPos = (2 - _stripUnit(button.offsetTop)) + "px";
			this.container.collapseTable();
		},
		
		isSelected : function() {
			return (this.container.selectedButton == this.element);
		},
		
		unselect : function() {
			this.element.style.backgroundImage = "";
			this.container.selectedButton = undefined;
		},
		
		handleKey : function(evt) {
			var keyCode = evt.keyCode;
			
			if (this.container.scrollInProgress)
				return;
			switch(keyCode) {
				case 37: // left
					var previousButton = this.previous;
					if (previousButton) {
						var buttonElement = previousButton.element;
						if (buttonElement.offsetTop < this.element.offsetTop)
							this.container.scrollUp();
						this.container.switchHiglightWhenScrollingFinished(previousButton);
					}
					break;
				case 38: // up
					var oldPos = this.element.parentNode.offsetLeft;
					var previousButton = this.previous;
					while (previousButton) {
						var newPos = previousButton.element.parentNode.offsetLeft;
						if (newPos == oldPos)
							break;
						previousButton = previousButton.previous;
					}
					if (previousButton) {
						this.container.scrollUp();
						this.container.switchHiglightWhenScrollingFinished(previousButton);
					}
					break;
				case 39: // right
					var nextButton = this.next;
					if (nextButton) {
						var buttonElement = nextButton.element;
						if (buttonElement.offsetTop > this.element.offsetTop)
							this.container.scrollDown();
						this.container.switchHiglightWhenScrollingFinished(nextButton);
					}
					break;
				case 40: // down
					var oldPos = this.element.parentNode.offsetLeft;
					var lastButton = this;
					var nextButton = this.next;
					while (nextButton) {
						var newPos = nextButton.element.parentNode.offsetLeft;
						if (newPos == oldPos) {
							break;
						}
						lastButton = nextButton;
						nextButton = nextButton.next;
					}
					if (!nextButton && lastButton.element.parentNode.offsetTop != this.element.parentNode.offsetTop) {
						nextButton = lastButton;
					}
					if (nextButton) {
						this.container.scrollDown();
						this.container.switchHiglightWhenScrollingFinished(nextButton);
					}
					break;
				case 13: // enter
					this.select();
					break;
			}
		},
				
		getFullURL : function(resource) {
			return this.container.pluginURL + "/" + resource;
		}


		/**#@-*/
	});
})();

var regexp = /(-*\d+).*/;
function _stripUnit (string) {
	this.regexp.exec(string);
	return parseInt(RegExp.$1);
}
