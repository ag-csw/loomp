<h2><g:message code="annotationEditor.annotationSet.heading"/></h2>

<g:form name="editAnnotationSetForm" url="[action: isNew ? 'saveAnnotationSet' : 'updateAnnotationSet']">
	<div class="fields">
		<g:if test="${isNew}">
			<label for="uri"><g:message code="default.uri.label"/></label>
			<g:textField name="uri" value="${cmd.uri}"/>

			<label for="locale"><g:message code="default.locale.label"/></label>
			<g:textField name="locale" value="${cmd.locale}"/>
		</g:if>
		<g:else>
			<label for="uri"><g:message code="default.uri.label"/></label>
			<div id="uri" style="white-space: nowrap;">${cmd.uri}</div>
			<g:hiddenField name="uri" value="${cmd.uri}"/>
			<g:if test="${cmd.locale}">
				<g:hiddenField name="locale" value="${cmd.locale}"/>
			</g:if>
			<g:else>
				<label for="locale"><g:message code="default.locale.label"/></label>
				<g:textField name="locale" value="${cmd.locale}"/>
			</g:else>
		</g:else>

		<label for="label"><g:message code="default.title.label"/></label>
		<g:textField name="label" value="${cmd.label}"/>

		<label for="comment"><g:message code="default.comment.label"/></label>
		<g:textArea name="comment" value="${cmd.comment}" cols="" rows=""/>
	</div>
	<div class="buttons">
		<g:if test="${isNew}"><g:submitButton name="create" value="${message(code: 'default.button.create.label')}"/></g:if>
		<g:else><g:submitButton name="create" value="${message(code: 'default.button.update.label')}"/></g:else>
	</div>
</g:form>

<h2><g:message code="annotationEditor.annotations.heading"/></h2>

<ul>
	<li><g:remoteLink update="annotationEditArea" action="newAnnotationAjax" params="[asUri: cmd.uri, locale: cmd.locale]">New</g:remoteLink></li>
	<g:each in="${annotations}" var="annotation">
		<li><g:remoteLink update="annotationEditArea" action="editAnnotationAjax" params="[asUri: cmd.uri, uri: annotation.uri, locale: cmd.locale]">
			${annotation.labels.getAnyLiteral(cmd.locale)}</g:remoteLink>
		(<g:remoteLink update="annotationSetEditArea" action="deleteAnnotationAjax"
				params="[asUri: cmd.uri, uri: annotation.uri, locale: cmd.locale]"><g:message code="default.button.delete.label"/></g:remoteLink>)
		</li>
	</g:each>
</ul>

<div id="annotationEditArea"></div>
