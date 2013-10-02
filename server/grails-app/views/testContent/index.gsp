<%@ page import="loomp.vocabulary.Loomp; loomp.model.ElementText; loomp.RenderFormat; loomp.model.TypeMapper" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="testContent.index.heading"/></title>
	<meta name="layout" content="main"/>
</head>

<body>
<div class="body">
	<div><g:render template="/layouts/messages"/></div>

	<h1><g:message code="testContent.index.heading"/></h1>

	<h2><g:message code="testContent.index.generic.heading"/></h2>
	<ul>
		<li><g:link action="create"><g:message code="testContent.create.heading"/></g:link></li>
		<li><g:message code="testContent.index.generic.get_entity"/><br/>
			<g:form controller="content" action="get">
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
		<li><g:message code="testContent.index.generic.get_entities"/><br/>
			<g:form controller="content" action="getAll">
				<div class="fields">
					<label for="type"><g:message code="default.typeUri.label"/></label>
					<g:textField name="type"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton class="list" name="list" value="${message(code: 'default.button.load.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.generic.containing"/><br/>
			<g:form controller="content" action="containing">
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
		<li><g:message code="testContent.index.generic.search"/><br/>
			<g:form controller="content" action="search">
				<div class="fields">
					<label for="query"><g:message code="default.query.label"/></label>
					<g:textField name="query"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton name="search" value="${message(code: 'default.button.search.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.generic.types"/><br/>
			<g:form controller="content" action="type">
				<div class="fields">
					<label for="uri"><g:message code="default.uri.label"/></label>
					<g:textField name="uri"/>
					<label for="entity"><g:message code="default.onlyEntityType.label"/></label>
					<g:select name="entity" from="${['yes', 'no']}" valueMessagePrefix="option" value="no"/>
				</div>

				<div class="buttons">
					<g:submitButton class="list" name="list" value="${message(code: 'default.button.search.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.generic.countByType"/><br/>
			<g:form controller="content" action="count">
				<div class="fields">
					<label for="type"><g:message code="default.typeUri.label"/></label>
					<g:textField name="type"/>
				</div>

				<div class="buttons">
					<g:submitButton class="list" name="list" value="${message(code: 'default.button.count.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.generic.delete"/><br/>
			<g:form controller="content" action="delete">
				<div class="fields">
					<label for="uri"><g:message code="default.uri.label"/></label>
					<g:textField name="uri"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton class="delete" name="delete" value="${message(code: 'default.button.delete.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.specific.searchResource"/><br/>
			<g:form controller="content" action="search">
				<div class="fields">
					<label for="query"><g:message code="default.query.label"/></label>
					<g:textField name="query"/>
					<label for="prop"><g:message code="default.property.label"/></label>
					<g:textField name="prop"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton class="search" name="search" value="${message(code: 'default.button.search.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.specific.searchResource"/><br/>
			<g:form controller="content" action="searchResources">
				<div class="fields">
					<label for="query"><g:message code="default.query.label"/></label>
					<g:textField name="query"/>
					<label for="auris"><g:message code="default.annotation.label"/></label>
					<g:textField name="auris"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton class="search" name="search" value="${message(code: 'default.button.search.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.specific.searchElementText"/><br/>
			<g:form controller="content" action="search">
				<g:hiddenField name="type" value="${Loomp.ElementText}"/>
				<div class="fields">
					<label for="query"><g:message code="default.query.label"/></label>
					<g:textField name="query"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton class="search" name="search" value="${message(code: 'default.button.search.label')}"/>
				</div>
			</g:form>
		</li>
		<li><g:message code="testContent.index.specific.containedResources"/><br/>
			<g:form controller="content" action="containedResources">
				<div class="fields">
					<label for="uri"><g:message code="default.uri.label"/></label>
					<g:textField name="uri"/>
					<label for="fmt"><g:message code="default.format.label"/></label>
					<g:select name="fmt" from="${RenderFormat.values()}"/>
				</div>

				<div class="buttons">
					<g:submitButton class="search" name="search" value="${message(code: 'default.button.search.label')}"/>
				</div>
			</g:form>
		</li>
	</ul>


	<h2><g:message code="testContent.index.specific.heading"/></h2>

	<g:each in="${TypeMapper.instance.getDomainClasses().sort{ it.getSimpleName() }}" var="clazz">
		<h3>${clazz.getSimpleName()}</h3>
		<ul>
			<li><g:link controller="content" action="getAll" params="[type: clazz.type]">
				<g:message code="testContent.index.specific.get_all"/></g:link></li>
			<li><g:link controller="content" action="latest" params="[type: clazz.type]">
				<g:message code="testContent.index.specific.latest"/></g:link></li>
			<li><g:link controller="content" action="count" params="[type: clazz.type]">
				<g:message code="testContent.index.specific.count"/></g:link></li>
		</ul>
	</g:each>
</div>
</body>
</html>
