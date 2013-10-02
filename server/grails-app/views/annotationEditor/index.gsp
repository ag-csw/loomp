<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="admin.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>

<div><g:render template="/layouts/messages"/></div>
<h1><g:message code="annotationEditor.heading"/></h1>

<ul>
	<li><g:remoteLink update="annotationSetEditArea" action="newAnnotationSetAjax">
		<g:message code="default.create.label" args="[message(code: 'annotationSet.label')]"/></g:remoteLink></li>
	<g:each in="${annotationSets}" var="annotationSet">
		<li>${annotationSet.labels.getAnyLiteral()}
		(<g:link action="deleteAnnotationSet" params="[uri: annotationSet.uri]"><g:message code="default.button.delete.label"/></g:link>)
			<ul>
				<li><g:remoteLink update="annotationSetEditArea" action="editAnnotationSetAjax" params="[uri: annotationSet.uri]">
					<g:message code="default.create.label" args="[message(code: 'default.locale.label')]"/></g:remoteLink></li>
				<g:each in="${annotationSet.labels.keySet().sort()}" var="locale">
					<li><g:remoteLink update="annotationSetEditArea" action="editAnnotationSetAjax" params="[uri: annotationSet.uri, locale: locale]">
						${locale}: ${annotationSet.labels.getAnyLiteral(locale)}</g:remoteLink></li>
				</g:each>
			</ul>
		</li>
	</g:each>
</ul>

<div id="annotationSetEditArea"></div>
</body>
</html>
