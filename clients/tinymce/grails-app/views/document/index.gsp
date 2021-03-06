<html>
<head>
	<meta name="layout" content="main"/>
	<g:javascript library="prototype"/>
	<g:javascript library="tiny_mce/tiny_mce"/>
	<g:javascript library="application"/>
</head>
<body>
<!-- fragment templates,TPL, TITLE, CONTENT gets replaced -->
<ul>
<li id="e_template_text" style="display: none;" class="chunk">
	<div class="chunk-full">
		<div class="chunk-item">
			<div class="handle"></div>
			<input type="hidden" id="i_TPL" name="mashup_data[TPL][uri]" value="URI" />
			<input type="hidden" name="mashup_data[TPL][type]" value="TYPE" />
			<input type="hidden" id="o_TPL" name="mashup_data[TPL][order]" value="" />
			<input type="text" id="n_TPL" name="mashup_data[TPL][title]" value="TITLE" onchange="editorMashupChanged();" /><br />
			<div id="t_TPL">CONTENT</div>
			<textarea id="tv_TPL" name="mashup_data[TPL][content]" style="display: none;"></textarea>
			<button onclick="editorChunkOpen('TPL')">edit</button>
			<button name="edit" onclick="editorChunkSave('TPL')" style="display: none;">close</button>
			<!--<button name="edit" onclick="editorMashupRemove('TPL')">new fragment and close</button> -->

			<button name="edit" onclick="editorChunkCancel('TPL')" style="display: none;">discard</button>
			<button onclick="editorMashupRemove('TPL')">delete</button>

		</div>
		<div class="chunk-new">
			<button onclick="editorMashupAdd('TPL')">+</button>
			<button onclick="editorMashupAdd('TPL','sparql')">+q</button>
		</div>
	</div>
	<div class="chunk-preview">
		<div class="chunk-preview-title">TITLE</div>
		<div class="chunk-preview-content">CONTENT</div>
	</div>
</li>

<li id="e_template_sparql" style="display: none;" class="chunk">
	<div class="chunk-full">
		<div class="chunk-item">
			<div class="handle"></div>
			<input type="hidden" id="i_TPL" name="mashup_data[TPL][uri]" value="URI" />
			<input type="hidden" name="mashup_data[TPL][type]" value="TYPE" />
			<input type="hidden" id="o_TPL" name="mashup_data[TPL][order]" value="" />
			<input type="text" id="n_TPL" name="mashup_data[TPL][title]" value="TITLE" onchange="editorMashupChanged();" /><br />
			<div class="sparql-edit">
				Query:<br />
				<textarea name="mashup_data[TPL][content][sparql]">SPARQL</textarea>
				<br />
				Template:<br />
				<textarea name="mashup_data[TPL][content][template]">TEMPLATE</textarea>
				<br />

				Endpoint (leave blank for local):<br />
				<input type="text" name="mashup_data[TPL][content][endpoint]" value="ENDPOINT" />
				<br />
				<br />
				</div>
			<div class="sparql-preview">
				This is a dynamic Fragment. Content preview is not available.
			</div>
			<button onclick="editorChunkOpen('TPL')">edit</button>
			<button name="edit" onclick="editorChunkSave('TPL')" style="display: none;">close</button>
			<!--<button name="edit" onclick="editorMashupRemove('TPL')">new fragment and close</button> -->

			<button name="edit" onclick="editorChunkCancel('TPL')" style="display: none;">discard</button>
			<button onclick="editorMashupRemove('TPL')">delete</button>

		</div>
		<div class="chunk-new">
			<button onclick="editorMashupAdd('TPL')">+</button>
			<button onclick="editorMashupAdd('TPL','sparql')">+q</button>
		</div>
	</div>
	<div class="chunk-preview">
		<div class="chunk-preview-title">TITLE</div>
		<div class="chunk-preview-content">Dynamic Fragment</div>
	</div>
</li>

</ul>
<!-- end fragment template -->

<div class="box200">
	<!-- fragment search form / result list -->
	<div id="chunks">
		<h3>Add Fragment</h3>

		<form id="searchform" onsubmit="return false;" style="margin-bottom: 5px;">
		<p class="small">.. search for fragments </p>
		<input id="query-search" type="text" name="search" onkeyup="editorSearchUpdate(this)" /><br />
		<button id="query-button" onclick="editorSearchClear()" style="display: none;" >clear</button>
		</form>
		<!-- list will be populated by fragment templates -->
		<ul id="chunks-list" type="none"></ul>
	</div>



</div>

<div class="content740">
	<!-- mashup -->
	<div id="mashup">
		<form id="editform" onsubmit="return false;">

			<div id="mashup-buttons" style="display: none;">
				Mashup changed.&nbsp;
				<button onclick="editorMashupSave()">save changes</button>
				<button onclick="editorMashupDiscard()">discard changes</button>
			</div>

			<div id="mashup-saved" style="display: none;">
				Mashup saved.&nbsp;
				<a id="mashup-view-link" target="_new" href="" name="<?=$this->url(array('controller'=>'index','action' => 'view'))?>">view</a>&nbsp;
				<a href="#" onclick="$('mashup-saved').style.display='none';">close</a>
			</div>

			<input type="text" id="mashup-title" name="mashup_meta[title]" value="" onchange="editorMashupChanged();" />
			<input type="hidden" id="mashup-uri" name="mashup_meta[uri]" value="" />
			<button id="mashup-add-first" onclick="editorMashupAdd('_first')">+</button>

			<!-- fragments, will get populated by fragment templates -->
			<ul id="mashup-list" type="none">
				<li>loading...please wait.</li>
			</ul>
		</form>
	</div>
</div>

<!-- js init & vocabulary js generation -->
<script type="text/javascript">
	/* load vocabularies */
	function getVocabularies() {
		return eval('(<?= $this->vocabs ?>)');
	}
	/* init editor code */
	editorMashupInit('<?php print $this->uri; ?>','<?php print $this->userUri; ?>');
</script>
</body>
</html>