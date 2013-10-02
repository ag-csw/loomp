<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>
	<h1><g:message code="admin.index.heading"/></h1>

	<ul>
		<li><g:link controller="systemUser"><g:message code="systemUser.index.heading"/></g:link></li>
		<li><g:link controller="systemParam"><g:message code="systemParam.index.heading"/></g:link></li>
		<li><g:link action="system"><g:message code="admin.system.heading"/></g:link></li>
		<li><g:link action="generate"><g:message code="admin.generate.heading"/></g:link></li>
	</ul>
</div>
</body>
</html>