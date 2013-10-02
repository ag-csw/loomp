<% import grails.persistence.Event %>
<%=packageName%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="layout" content="main"/>
	<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label')}"/>
	<title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>
<body>
<h1><g:message code="default.edit.label" args="[entityName]"/></h1>

<div class="form">
<g:form method="post" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
	<g:hiddenField name="id" value="\${${propertyName}?.id}"/>
	<g:hiddenField name="version" value="\${${propertyName}?.version}"/>
	<% excludedProps = Event.allEvents.toList() << 'version' << 'id'
	props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
	Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
	props.each {p ->
		cp = domainClass.constrainedProperties[p.name]
		display = (cp ? cp.display : true)
		if (display) { %>
	<label for="${p.name}" class="\${hasErrors(bean: ${propertyName}, field: '${p.name}', 'errors')}">
		<g:message code="${domainClass.propertyName}.${p.name}.label"/>
	</label>
	${renderEditor(p)}
	<% }
	} %>
	<g:actionSubmit class="save" action="update" value="\${message(code: 'default.button.update.label', default: 'Update')}"/>
	<g:actionSubmit class="delete" action="delete" value="\${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
	</div>
</g:form>
</div>
</body>
</html>
