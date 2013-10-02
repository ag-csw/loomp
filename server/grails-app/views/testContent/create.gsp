<%--
	This page contains a form to send json to a document.save
--%>
<%@ page import="loomp.RenderFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="testContent.create.heading"/></title>
	<meta name="layout" content="main"/>
</head>

<body>
<div class="body">
	<h1><g:message code="testContent.create.heading"/></h1>

	<h2><g:message code="testContent.create.element_text"/></h2>

	<g:hasErrors bean="${element}">
		<div class="errors">
			<g:renderErrors bean="${element}" as="list"/>
		</div>
	</g:hasErrors>
	<g:form class="withText" action="createElementText">
		<g:hiddenField name="uri" value="${element.uri}"/>
		<div class="fields">
			<label for="title" class="${hasErrors(bean: element, field: 'firstName', 'error')}"><g:message
					code="default.title.label"/></label>
			<g:textField name="title" value="${element?.title}"/>
			<label for="content" class="${hasErrors(bean: element, field: 'firstName', 'error')}"><g:message
					code="default.content.label"/></label>
			<g:textArea name="content" value="${element?.content}" cols="" rows=""/>
		</div>

		<div class="buttons">
			<g:submitButton class="save" name="save" value="${message(code: 'default.button.save.label')}"/>
		</div>
	</g:form>

	<div style="margin-top: 30px">
		<ul>
			<g:each in="${elements}" var="e" status="i">
				<li>
					<g:link url="javascript:void(0)" onclick="\$('#element-${i}').toggle()">${e.title ?: e.uri}</g:link>
					(<g:link action="create" params="[euri: e.uri]"><g:message code="default.button.edit.label"/></g:link>
					<g:link action="showElementText" params="[uri: e.uri]"><g:message code="default.button.show.label"/></g:link>
					<g:link action="deleteElementText" params="[euri: e.uri]"><g:message code="default.button.delete.label"/></g:link>)
					<div id="element-${i}" style="display: none;">
						<p>${e.content}</p>
					</div>
				</li>
			</g:each>
		</ul>
	</div>

	<h2><g:message code="testContent.create.json"/></h2>

	<g:form class="withText" controller="content" action="save">
		<div class="fields">
			<label for="data"><g:message code="default.data.label"/></label>
			<g:textArea name="data" rows="15" cols=""/>
			<label for="fmt"><g:message code="default.format.label"/></label>
			<g:select name="fmt" from="${RenderFormat.values()}"/>
		</div>

		<div class="buttons">
			<g:submitButton class="save" name="save" value="${message(code: 'default.button.save.label')}"/>
		</div>
	</g:form>


	<h2><g:message code="testContent.create.person"/></h2>

	<g:hasErrors bean="${person}">
		<div class="errors">
			<g:renderErrors bean="${person}" as="list"/>
		</div>
	</g:hasErrors>
	<g:form action="createUser">
		<div class="fields">
			<label for="firstName" class="${hasErrors(bean: person, field: 'firstName', 'error')}"><g:message
					code="default.firstName.label"/></label>
			<g:textField name="firstName" value="${person?.firstName}"/>
			<label for="lastName" class="${hasErrors(bean: person, field: 'firstName', 'error')}"><g:message
					code="default.lastName.label"/></label>
			<g:textField name="lastName" value="${person?.lastName}"/>
			<label for="email" class="${hasErrors(bean: person, field: 'firstName', 'error')}"><g:message
					code="default.email.label"/></label>
			<g:textField name="email" value="${person?.email}"/>
		</div>

		<div class="buttons">
			<g:submitButton class="save" name="save" value="${message(code: 'default.button.save.label')}"/>
		</div>
	</g:form>
</div>
</body>
</html>
