<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.generate.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>
	<h1><g:message code="admin.generate.heading"/></h1>

	<h2><g:message code="admin.generate.documents.heading"/></h2>

	<g:form controller="admin" action="genDocuments">
		<div class="fields">
			<label for="num"><g:message code="default.number.label"/></label>
			<g:textField name="num" value="${num ? num : ''}"/>
			<label for="clear"><g:message code="admin.upload.clear_store"/></label>
			<g:select name="clear" from="${['yes', 'no']}" valueMessagePrefix="option"/>
		</div>
		<div class="buttons">
			<g:submitButton class="save" name="create" value="${message(code: 'default.button.create.label')}"/>
		</div>
	</g:form>

	<h2><g:message code="admin.generate.resources.heading"/></h2>

	<g:form controller="admin" action="genResources">
		<div class="fields">
			<label for="num"><g:message code="default.number.label"/></label>
			<g:textField name="num" value="${num ? num : ''}"/>
			<label for="clear"><g:message code="admin.upload.clear_store"/></label>
			<g:select name="clear" from="${['yes', 'no']}" valueMessagePrefix="option"/>
		</div>
		<div class="buttons">
			<g:submitButton class="save" name="create" value="${message(code: 'default.button.create.label')}"/>
		</div>
	</g:form>

	<h2><g:message code="admin.generate.annotations.heading"/></h2>

	<g:form controller="admin" action="genAnnotations">
		<div class="fields">
			<label for="num"><g:message code="default.number.label"/></label>
			<g:textField name="num" value="${num ? num : ''}"/>
			<label for="clear"><g:message code="admin.upload.clear_store"/></label>
			<g:select name="clear" from="${['yes', 'no']}" valueMessagePrefix="option"/>
		</div>
		<div class="buttons">
			<g:submitButton class="save" name="create" value="${message(code: 'default.button.create.label')}"/>
		</div>
	</g:form>
</div>
</body>
</html>