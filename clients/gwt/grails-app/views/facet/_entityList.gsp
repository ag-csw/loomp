<ul class="element-list">
	<g:each in="${entities}" var="entity" status="i">
		<g:set var="title" value="${entity.title ?: message(code: 'elementText.title.empty')}"/>
		<li class="element-item">
			<div class="element-menu-drop icon icon-menu">
				<ul id="element-menut-${i}" class="element-menu" style="display: none;">
					<li>
						<g:link controller="document" params="[uri: entity.uri]"
								title="${message(code: 'default.edit.entity.label', args:[title])}">
							<g:message code="default.button.edit.label"/>
						</g:link>
					</li>
					<li>
						<g:link controller="document" action="show" params="[uri: entity.uri]"
								title="${message(code: 'default.view.entity.label', args:[title])}">
							<g:message code="default.button.view.label"/>
						</g:link>
					</li>
					<li>
						<g:remoteLink update="latest"
									  url="[controller: 'document', action: 'latestDeleteAjax', params: [uri: entity.uri]]"
									  title="${message(code: 'default.delete.entity.label', args:[title])}">
							<g:message code="default.button.delete.label"/>
						</g:remoteLink>
					</li>
				</ul>
			</div>

			<g:if test="${entity.lastModified}">
				<div class="metadata">
					<g:message code="default.last_updated.label" args="[lo.formatDuration(date: entity.lastModified)]"/>
				</div>
			</g:if>
			<h3 id="element-${i}">
				<g:link controller="document" action="show" params="[uri: entity.uri]"
						title="${message(code: 'default.edit.entity.label', args:[title])}">
					${title}
				</g:link>
			</h3>

			<div class="content">${entity.content}</div>
		</li>
	</g:each>
</ul>
