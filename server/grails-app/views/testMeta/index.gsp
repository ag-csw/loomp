<%@ page import="loomp.model.TypeMapper" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="testMeta.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>
	<h1><g:message code="testMeta.index.heading"/></h1>

	<ul>
		<li><g:link controller="meta" action="version"><g:message code="testMeta.index.version"/></g:link></li>
	</ul>
</div>
</body>
</html>