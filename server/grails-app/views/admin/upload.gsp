<%@ page import="loomp.RenderFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.upload.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<h1><g:message code="admin.upload.heading"/></h1>
	
	<g:uploadForm controller="admin" action="uploadFile">
		<div class="fields">
			<label for="graphUri"><g:message code="admin.upload.graphUri.label"/></label>
			<g:textField name="graphUri" value="${graphUri}"/>
			<label for="file"><g:message code="admin.upload.rdf_model.label"/></label>
			<input type="file" name="file" id="file" />
			<label for="clear"><g:message code="admin.upload.clear_store"/></label>
			<g:select name="clear" from="${['no', 'yes']}" valueMessagePrefix="option"/>
		</div>
		<div class="buttons">
			<g:submitButton class="save" name="upload" value="${message(code: 'default.button.upload.label')}"/>
		</div>
	</g:uploadForm>
</div>
</body>
</html>
