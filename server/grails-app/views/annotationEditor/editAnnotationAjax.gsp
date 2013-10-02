<h2><g:message code="annotationEditor.annotation.heading"/></h2>

<g:formRemote update="annotationSetEditArea" name="editAnnotationForm" url="[action: isNew ? 'saveAnnotationAjax' : 'updateAnnotationAjax']">
	<div class="fields">
		<g:hiddenField name="asUri" value="${asUri}"/>
		<g:hiddenField name="locale" value="${locale}"/>

		<g:if test="${isNew}">
			<label for="uri"><g:message code="default.uri.label"/></label>
			<g:textField name="uri" value="${cmd.uri}"/>
		</g:if>
		<g:else>
			<label for="uri"><g:message code="default.uri.label"/></label>
			<div id="uri" style="white-space: nowrap;">${cmd.uri}</div>
			<g:hiddenField name="uri" value="${cmd.uri}"/>
		</g:else>

		<label for="label"><g:message code="default.title.label"/></label>
		<g:textField name="label" value="${cmd.label}"/>

		<label for="comment"><g:message code="default.comment.label"/></label>
		<g:textArea name="comment" value="${cmd.comment}" cols="" rows=""/>

		<label for="propertyUri"><g:message code="annotation.propertyUri"/></label>
		<g:textField name="propertyUri" value="${cmd.propertyUri}"/>

		<label for="domainUri"><g:message code="annotation.domainUri"/></label>
		<g:textField name="domainUri" value="${cmd.domainUri}"/>

		<label for="rangeUri"><g:message code="annotation.rangeUri"/></label>
		<g:textField name="rangeUri" value="${cmd.rangeUri}"/>
	</div>
	<div class="buttons">
		<g:if test="${isNew}"><g:submitButton name="create" value="${message(code: 'default.button.create.label')}"/></g:if>
		<g:else><g:submitButton name="create" value="${message(code: 'default.button.update.label')}"/></g:else>
	</div>
</g:formRemote>
