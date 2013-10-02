<g:if test="${latest}">
	<ul class="element-list latest">
		<g:each in="${latest}" var="entity" status="i">
			<g:set var="title" value="${entity.title ?: message(code: 'elementText.title.empty')}"/>
			<li class="element-item">
				<div class="element-menu-drop icon-gray icon-menu">
					<ul id="element-menut-${i}" class="element-menu" style="display: none;">
						<li>
							<g:link controller="document" params="[uri: entity.uri]"
									title="${message(code: 'default.edit.entity.label', args:[title])}">
								<g:message code="default.button.edit.label"/>
							</g:link>
						</li>
						<li>
							<g:link controller="document" action="show" params="[uri: entity.uri]"
									title="${message(code: 'default.show.label', args:[title])}">
								<g:message code="default.button.view.label"/>
							</g:link>
						</li>
						<li>
							<g:remoteLink update="latest"
										  url="[controller: 'document', action: 'latestDeleteAjax', params: [uri: entity.uri]]"
										  title="${message(code: 'default.delete.label', args:[title])}">
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
							title="${message(code: 'default.show.label', args:[title])}">
						${title}
					</g:link>
				</h3>

				<div class="content"><lo:shortText length="240">${entity.content}</lo:shortText></div>
			</li>
		</g:each>
	</ul>
</g:if>
<g:else><p><g:message code="document.latest.empty_list" args="[createLink(controller: 'document')]"/></p></g:else>
