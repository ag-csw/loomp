<%@ page import="loomp.vocabulary.Loomp; loomp.model.ElementText; loomp.RenderFormat; loomp.model.TypeMapper" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="testAnnotation.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>
<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>

	<h1><g:message code="testAnnotation.index.heading"/></h1>

	<ul>
		<li>
			<h3><g:message code="annotationSet.label" /></h3>
			<ul>
				<li><g:link controller="content" action="getAll" params="[type: Loomp.AnnotationSet]"><g:message code="testContent.index.specific.get_all"/></g:link></li>
				<li><g:link controller="content" action="count" params="[type: Loomp.AnnotationSet]"><g:message code="testContent.index.specific.count"/></g:link></li>
			</ul>
		</li>
		<li>
			<h3><g:message code="annotation.label" /></h3>
			<ul>
				<li><g:link controller="content" action="getAll" params="[type: Loomp.Annotation]"><g:message code="testContent.index.specific.get_all"/></g:link></li>
				<li><g:link controller="content" action="count" params="[type: Loomp.Annotation]"><g:message code="testContent.index.specific.count"/></g:link></li>
			</ul>
		</li>
		<li><g:message code="testAnnotation.index.get"/><br/>
			<g:form controller="annotation" action="get">
				<div class="fields">
					<label for="uri"><g:message code="default.uri.label"/></label>
					<g:textField name="uri"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>
				<div class="buttons">
					<g:submitButton class="list" name="list" value="${message(code: 'default.button.load.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testAnnotation.index.get_all"/><br/>
			<g:form controller="annotation" action="getAll">
				<div class="fields">
					<label for="type"><g:message code="default.typeUri.label"/></label>
					<g:select name="type" from="${[Loomp.Annotation, Loomp.AnnotationSet]}"/>
					<label for="set"><g:message code="default.annotationSet.label"/></label>
					<g:textField name="set"/>
					<label for="property"><g:message code="default.property.label"/></label>
					<g:textField name="property"/>
					<label for="domain"><g:message code="default.domain.label"/></label>
					<g:textField name="domain"/>
					<label for="range"><g:message code="default.range.label"/></label>
					<g:textField name="range"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>
				<div class="buttons">
					<g:submitButton class="list" name="list" value="${message(code: 'default.button.load.label')}"/>
				</div>
			</g:form>
		</li>
      	<li>
			<h3><g:message code="view.ars.info.headline" /></h3>
			<ul>
				<li><g:link controller="ars" action="info"><g:message code="view.ars.info.title"/></g:link></li>
                <li><g:link controller="ars" action="recommendInput"><g:message code="view.ars.info.manualInput"/></g:link></li>
			</ul>
		</li>
		<li>
			<h3><g:message code="annotationEditor.heading" /></h3>
			<ul>
				<li><g:link controller="annotationEditor" action="index"><g:message code="annotationEditor.heading"/></g:link></li>
			</ul>
		</li>
	</ul>
</div>
</body>
</html>
