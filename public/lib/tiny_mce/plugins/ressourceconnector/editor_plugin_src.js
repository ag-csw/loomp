(function(){
	// Load plugin specific language pack
	tinymce.PluginManager.requireLangPack('ressourceconnector');

	tinymce.create('tinymce.plugins.RessourceConnectorPlugin', {
		/**
		 * The path to the recommendation action.
		 * 
		 * @field
		 */
		//controllerUri: 'ars/connector',

		/**
		 * Initializes the plugin, this will be executed after the plugin has been created.
		 * This call is done before the editor instance has finished it's initialization so use the onInit event
		 * of the editor instance to intercept that event.
		 *
		 * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
		 * @param {string} url Absolute URL to where the plugin is located.
		 */
		init : function(editor, url) {
			
			tinymce.DOM.loadCSS(url + "/css/resconnector.css");
			
			/* SACH */
			editor.addButton('res_connect_button', {
				image  : url + '/img/con_res.gif',
				//scope  : this, // bind this as scope to access the URI
				title  : 'ressourceconnector.button_title',
				onclick: function() {
				
					//alert(currentSelectedSpanItem);
					//var enabledChunk = $$('div.chunk-item-enabled');
					
					//var fragmentURI = enabledChunk[0].getElementsByTagName('input')[0].value;
					
					//alert(fragmentURI);
									
					if(selectedSpanId==null && selectedSpanProperty == null && selectedSpanAbout == null && selectedSpanText == null) {
						alert("No annotation selected.");
					} else {	
						var annotURI = selectedSpanAbout;
						var annotType = nonRauteDesc(selectedSpanId);
						var currentVocab = currentVocabSelection;
						var query = 'none';
						//alert("id: "+selectedSpanId+ "   property: "+ selectedSpanProperty + "    about: "+ selectedSpanAbout +"     text:"+ selectedSpanText);
						
						
						
						new Ajax.Request('mashup/resourcebyuri?subjuri='+annotURI, {
							method:'get',
							onSuccess: function(transport) {
								annots = transport.responseText.evalJSON(false);
								//alert(transport.responseText);
								
								tinyMCE.activeEditor.windowManager.open({
									url : 'lib/tiny_mce/plugins/annotate/connectres.html',
									width : 600,
									height : 500,
									inline : true
									}, {
									annots: annots,
									annotURI: annotURI,
									//fragmentURI: fragmentURI,
									currentVocab: currentVocab,
									annotType: annotType	
								}); 
							},
							onFailure: function(transport){ 
								alert('ERROR: ' + transport.responseText); 
							}
						}); 
						
						
						
						
						
						
						
						/*
						tinyMCE.activeEditor.windowManager.open({
							url : 'lib/tiny_mce/plugins/annotate/connectres.html',
							width : 600,
							height : 500,
							inline : true
							}, {
							annotURI: annotURI,
							//selection: selection,
							currentVocab: currentVocab,
							annotType: annotType							
						}); */
					}
				}
			});
			
			
			
		},

		/**
		 * Creates control instances based in the incomming name. This method is normally not
		 * needed since the addButton method of the tinymce.Editor class is a more easy way of adding buttons
		 * but you sometimes need to create more complex controls like listboxes, split buttons etc then this
		 * method can be used to create those.
		 *
		 * @param {String} name Name of the control to create.
		 * @param {tinymce.ControlManager} cm Control manager to use inorder to create new control.
		 * @return {tinymce.ui.Control} New control instance or null if no control was created.
		 */
		createControl : function(name, cm) {
			return null;
		},

		/**
		 * Returns information about the plugin as a name/value array.
		 * The current keys are longname, author, authorurl, infourl and version.
		 *
		 * @return {Object} Name/value array containing information about the plugin.
		 */
		getInfo : function() {
			return {
				longname : 'Ressource Connector Plugin',
				author   : 'Selim Achmerzaev',
				authorurl: '',
				infourl  : '',
				version  : '0.1'
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('ressourceconnector', tinymce.plugins.RessourceConnectorPlugin);
	//tinyMCE.init({});
})();
