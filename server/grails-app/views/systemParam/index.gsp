<%@ page import="loomp.model.db.SystemParam; loomp.model.db.Person" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="systemParam.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>

	<h1><g:message code="systemParam.index.heading"/></h1>
	<g:if test="${systemParamList}">
		<table>
			<thead>
			<tr>
				<th><g:message code="default.name.label"/></th>
				<th><g:message code="default.value.label"/></th>
				<th><g:message code="default.action.label"/></th>
			</tr>
			</thead>
			<tbody>
			<g:each in="${systemParamList}">
				<tr><td>${it.name}</td><td>${it.value}</td>
					<td><g:link controller="systemParam" action="edit" id="${it.id}">
						<img src="${resource(dir: 'images/skin', file: 'database_edit.png')}"
								title="${message(code: 'default.button.edit.label')}"
								alt="${message(code: 'default.button.edit.label')}"/></g:link>
						<g:link controller="systemParam" action="delete" id="${it.id}">
							<img src="${resource(dir: 'images/skin', file: 'database_delete.png')}"
									title="${message(code: 'default.button.delete.label')}"
									alt="${message(code: 'default.button.delete.label')}"/></g:link></td>
				</tr>
			</g:each>
			</tbody>
		</table>
	</g:if>
	<g:else>
		<p><g:message code="systemParam.index.no_set"/></p>
	</g:else>
</div>

<div id="right">
	<g:if test="${systemParam?.id}">
		<h2><g:message code="systemParam.index.modify.heading"/></h2>
		<g:form action="update">
			<g:render template="form"/>
			<div class="buttons">
				<g:submitButton class="save" name="Update" value="${message(code: 'default.button.update.label')}"/>
				<g:submitButton type="reset" class="cancel" name="cancel"
						onclick="document.location.href='${createLink(action: index)}'"
						value="${message(code: 'default.button.cancel.label')}"/>
			</div>
		</g:form>
	</g:if>
	<g:else>
		<h2><g:message code="systemParam.index.add.heading"/></h2>
		<g:form action="save">
			<g:render template="form"/>
			<div class="buttons">
				<g:submitButton class="save" name="Save" value="${message(code: 'default.button.save.label')}"/>
			</div>
		</g:form>
	</g:else>
</div>
</body>
</html>
