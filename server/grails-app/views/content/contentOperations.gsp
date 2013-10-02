<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.index.heading"/></title>
	<meta name="layout" content="main"/>
	<script
		src="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dojo/dojo.xd.js"
		type="text/javascript"></script>


</head>
<body>
       	<g:form name="editDictionaryForm" url="[controller:'AnnotationEditor',action:'addResource']">
			<g:textField name="data" value="no"/>
			<div class="buttons">
				<g:submitButton class="save" name="create" value="${message(code: 'default.button.create.label')}"/>
			</div>
	</g:form>
</body>
</html>