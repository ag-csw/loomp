<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title><g:layoutTitle default="${message(code:'project.title')}"/></title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<g:layoutHead/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'loomp_skeleton.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'loomp_nav.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'loomp_form.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'loomp_font.css')}"/>
	<link rel="stylesheet" href="${resource(dir: 'css', file: 'loomp_content.css')}"/>
	<%-- <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/> --%>
</head>
<body>

<!-- HEADER ON -->
<div id=head>
	<div id="headPage">
		<g:render template="/layouts/header"/>
	</div>
</div>
<!-- HEADER OFF -->

<!-- LOGIN AND MENU ON -->
<div id=headAll>
	<div id="headPrivate">
		<g:render template="/layouts/nav"/>
	</div>
</div>
<!-- LOGIN AND MENU OFF -->


<div id="page">
	<div id="left">
		<gen:renderIfExists template="${actionName}/left_content"/>
	</div>
	<div class="wrapper">
		<g:render template="/layouts/messages"/>
		<g:layoutBody/>
	</div>
</div>
</body>
</html>