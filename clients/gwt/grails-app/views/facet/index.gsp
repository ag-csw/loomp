<html>
<head>
	<meta name="layout" content="main"/>
	<g:javascript src="jquery-plugins/syronex-colorpicker.js"/>
	<g:javascript src="facet.js"/>
	<script type="text/javascript">
		$(document).ready(function() {
			initDropDowns();
		})
	</script>
	<link rel="stylesheet" href="${resource(dir: 'js', file: 'jquery-plugins/syronex-colorpicker.css')}"/>
</head>

<body>

<img id="found-entities-spinner" class="right" style="display: none;" src="${resource(dir: 'images', file: 'spinner.gif')}"
	 alt="${message(code: 'default.loading.label')}"/>

<div id="found-entities"></div>
</body>
</html>
