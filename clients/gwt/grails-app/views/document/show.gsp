<html>
<head>
	<meta name="layout" content="main"/>
	<g:javascript src="jquery-plugins/syronex-colorpicker.js"/>
	<g:javascript src="document.js"/>
	<script type="text/javascript">
		$(document).ready(function() {
			initDropDowns();
		})
	</script>
	<link rel="stylesheet" href="${resource(dir: 'js', file: 'jquery-plugins/syronex-colorpicker.css')}"/>
</head>

<body>

<h1>${entity.title ?: message(code: 'elementText.title.empty')}</h1>

<div class="content">
	${entity.content}
</div>

</body>
</html>
