<html>
<head>
	<meta name="layout" content="main"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#latest').load('${createLink(controller: 'document', action: 'latestAjax')}');
			initDropDowns();
		})
	</script>
</head>

<body>

<h1><g:message code="home.index.heading"/></h1>

%{--<p><g:message code="home.index.par.introduction"/></p>--}%

%{--<h2><g:message code="home.latest.heading"/></h2>--}%

<div id="latest">
	<img src="${resource(dir: 'images', file: 'spinner.gif')}" alt="${message(code: 'default.loading.label')}"/>
</div>

</body>
</html>
