<% import grails.persistence.Event %>
<%=packageName%>
<html>
<head>
	<meta name="layout" content="main"/>
	<g:set var="entityName" value="\${message(code: '${domainClass.propertyName}.label')}"/>
	<title><g:message code="default.create.label" args="[entityName]"/></title>
</head>
<body>
<h1><g:message code="default.create.label" args="[entityName]"/></h1>

<div class="form">
	<g:form action="save" method="post" <%= multiPart ? ' enctype="multipart/form-data"' : '' %>>
		<% excludedProps = Event.allEvents.toList() << 'version' << 'id'
		props = domainClass.properties.findAll { !excludedProps.contains(it.name) }
		Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
		props.each {p ->
			if (!Collection.class.isAssignableFrom(p.type)) {
				cp = domainClass.constrainedProperties[p.name]
				display = (cp ? cp.display : true)
				if (display) { %>
		<label for="${p.name}" class="\${hasErrors(bean: ${propertyName}, field: '${p.name}', 'errors')}">
			<g:message code="${domainClass.propertyName}.${p.name}.label" default="${p.naturalName}"/>
		</label>
		${renderEditor(p)}
		<% }
		}
		} %>
		<g:submitButton name="create" class="save" value="\${message(code: 'default.button.create.label')}"/>
	</g:form>
</div>
</body>
</html>
