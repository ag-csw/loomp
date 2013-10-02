<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.system.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>
	<h1><g:message code="admin.system.heading"/></h1>

	<ul>
		<li><g:link action="upload"><g:message code="admin.upload.heading"/></g:link></li>
		<li><g:link action="clearEndpoint"  onclick="return confirm('${message(code: 'admin.system.clearEndpoint.confirm')}')">
			<g:message code="admin.system.clearEndpoint.label"/></g:link></li>
	</ul>
</div>
</body>
</html>