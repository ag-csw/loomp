<html>
<head>
	<title><g:layoutTitle default="Grails"/></title>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
	<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
	<g:javascript library="jquery" plugin="jquery"/>
	<g:javascript library="application"/>
	<g:layoutHead/>
</head>
<body>
<div id="nav">
	<g:render template="/layouts/nav"/>
</div>
<div id="content">
	<div id="spinner" class="spinner" style="display:none;">
		<img src="${resource(dir: 'images', file: 'spinner.gif')}" alt="Spinner"/>
	</div>
	<g:layoutBody/>
</div>
</body>
</html>
