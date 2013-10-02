<%@ page import="loomp.model.db.Person" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="systemUser.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>

	<h1><g:message code="systemUser.index.heading"/></h1>
	<g:if test="${personList}">
		<ul>
			<g:each in="${personList}">
				<li>${it.userRealName} (${it.email})</li>
			</g:each>
		</ul>
	</g:if>
	<g:else>
		<p><g:message code="systemUser.index.heading"/></p>
	</g:else>
</div>
</body>
</html>