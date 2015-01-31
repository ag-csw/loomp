(function(){
	// Load plugin specific language pack
	tinymce.PluginManager.requireLangPack('annotationrecommender');

	tinymce.create('tinymce.plugins.AnnotationRecommenderPlugin', {
		/**
		 * The path to the recommendation action.
		 * 
		 * @field
		 */
		controllerUri: 'ars/recommender',

		/**
		 * Initializes the plugin, this will be executed after the plugin has been created.
		 * This call is done before the editor instance has finished it's initialization so use the onInit event
		 * of the editor instance to intercept that event.
		 *
		 * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
		 * @param {string} url Absolute URL to where the plugin is located.
		 */
		init : function(editor, url) {
			// load the CSS rules
			tinymce.DOM.loadCSS(url + "/css/annotationrecommender.css");

			// Register the button
			editor.addButton('ars_recommend', {
				image  : url + '/img/button.gif',
				scope  : this, // bind this as scope to access the URI
				title  : 'annotationrecommender.button_title',
				onclick: function() {
/*
					// dummy
					editor.windowManager.open({
						file  : url + '/select.html',
						inline: 1,
						height: Math.floor(document.viewport.getHeight() * 0.8),
						width : Math.floor(document.viewport.getWidth()  * 0.8)
					}, {
						editor      : editor,
						jsonData    : {"text":"<p>Branded \"unfilmable\", Watchmen - the cult graphic novel about a group of retired, flawed superheroes - has finally made it to the big screen. From the second the opening credits roll, it is clear Watchmen is not your typical superhero movie.<br><br>An ageing vigilante, The Comedian, is attacked in his high-rise apartment before being hurled 10 storeys to his death... in graphic slow motion. What follows is a two-and-three-quarter hour epic that centres on an outlawed group of deeply flawed former heroes as a Cold War Doomsday clock inches ever closer to midnight and nuclear apocalypse.<br><br>First published in 12 parts by DC Comics in 1986, Watchmen was written by the British team of Alan Moore and illustrator Dave Gibbons.<br><br>Numerous attempts to film the book, included by Time magazine in its list of the Top 100 books of the 20th Century, failed to get off the ground. Respected directors like Terry Gilliam, Paul Greengrass and Darren Aronofsky were all involved at various stages. And legal wranglings between rival film studios over the adaptation rights threatened to wreck the project altogether. So it has fallen to Zack Snyder, the man who helmed 2007's surprise hit 300, to succeed where others have failed.<br mce_bogus=\"1\"><\/p>","annotations":[{"annotation":{"about":null,"begin":"724","end":"736","seeAlso":"http:\/\/d.opencalais.com\/pershash-1\/fdd4138c-ad05-3f2c-852e-8f9e6684c7ad","uri":"http:\/\/xmlns.com\/foaf\/0.1\/name","label":"name","description":"Name of a person.","type":"annotationProperty","annDomain":"http:\/\/xmlns.com\/foaf\/0.1\/Person","annRange":""},"conflicts":[4]},{"annotation":{"about":null,"begin":"931","end":"946","seeAlso":"http:\/\/d.opencalais.com\/pershash-1\/72f37d01-f67b-38fe-98da-7675969c5e30","uri":"http:\/\/xmlns.com\/foaf\/0.1\/name","label":"name","description":"Name of a person.","type":"annotationProperty","annDomain":"http:\/\/xmlns.com\/foaf\/0.1\/Person","annRange":""},"conflicts":[]},{"annotation":{"about":null,"begin":"697","end":"707","seeAlso":"http:\/\/d.opencalais.com\/pershash-1\/9b3fc024-274e-33ff-8012-8a36476db16f","uri":"http:\/\/xmlns.com\/foaf\/0.1\/name","label":"name","description":"Name of a person.","type":"annotationProperty","annDomain":"http:\/\/xmlns.com\/foaf\/0.1\/Person","annRange":""},"conflicts":[]},{"annotation":{"about":null,"begin":"916","end":"929","seeAlso":"http:\/\/d.opencalais.com\/pershash-1\/d83264e6-6901-3d6a-846f-98502d03af88","uri":"http:\/\/xmlns.com\/foaf\/0.1\/name","label":"name","description":"Name of a person.","type":"annotationProperty","annDomain":"http:\/\/xmlns.com\/foaf\/0.1\/Person","annRange":""},"conflicts":[]},{"annotation":{"about":null,"begin":"712","end":"736","seeAlso":"http:\/\/d.opencalais.com\/genericHasher-1\/7c174064-15e4-3188-a017-6b66114aedc3","uri":"http:\/\/xmlns.com\/foaf\/0.1\/name","label":"name","description":"Name of a person.","type":"annotationProperty","annDomain":"http:\/\/xmlns.com\/foaf\/0.1\/Person","annRange":""},"conflicts":[0]},{"annotation":{"about":null,"begin":"951","end":"967","seeAlso":"http:\/\/d.opencalais.com\/pershash-1\/76d32cbc-ddb1-3ad5-8208-b34bb8f0d3d5","uri":"http:\/\/xmlns.com\/foaf\/0.1\/name","label":"name","description":"Name of a person.","type":"annotationProperty","annDomain":"http:\/\/xmlns.com\/foaf\/0.1\/Person","annRange":""},"conflicts":[]}]},
						originWindow: window,
						plugin_url  : url
					});
*/					
					new Ajax.Request(this.controllerUri, {
						asynchronous: false,
						parameters: {text: editor.getContent()},
						onSuccess: function(transport) {
							jsonData = transport.responseJSON;

							// correct numbers
							for (var i = 0; i < jsonData.annotations.length; i++) {
								jsonData.annotations[i].annotation.begin *= 1;
								jsonData.annotations[i].annotation.end   *= 1;
							}

							editor.windowManager.open({
								file  : url + '/select.html',
								inline: 1,
								height: Math.floor(document.viewport.getHeight() * 0.8),
								width : Math.floor(document.viewport.getWidth()  * 0.8)
							}, {
								editor      : editor,
								jsonData    : jsonData,
								originWindow: window,
								plugin_url  : url
							});
						}
					});
//
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
				longname : 'Annotation Recommender Plugin',
				author   : 'Patrick Jungermann',
				authorurl: '',
				infourl  : '',
				version  : '0.1'
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('annotationrecommender', tinymce.plugins.AnnotationRecommenderPlugin);
	//tinyMCE.init({});
})();
