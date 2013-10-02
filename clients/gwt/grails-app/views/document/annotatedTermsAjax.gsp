<%@ page import="org.apache.commons.lang.StringUtils" %>

<g:if test="${views}">
	<ul id="selected-annotation-tags" class="tags">
		<g:each in="${views.annotations.flatten().unique()}" var="annotation">
			<li class="tag" uri="${annotation.uri}" style="display: none;" prop="${annotation.property}">
				<div class="color-picker" style="display: none;"></div>${annotation.labels.getAnyLiteral(ctxtLang)}<%-- no space here
				--%><g:link url="javascript:void(0)" class="icon icon-delete-small"
							onclick="toggleSelected(event, '${annotation.uri}')"><span>
					<g:message code="default.button.delete.label"/></span></g:link>
			</li>
		</g:each>
	</ul>

	<ul id="selected-annotations" class="resource-list"></ul>

	<h4 id="not-selected-heading" style="display: none;"><g:message code="document.show.annotated.not_selected.heading"/></h4>

	<ul id="not-selected-annotations" class="resource-list">
		<g:each in="${views}" var="view">
			<li class="resource-item" uri="${view.resource.uri}" annotations="${StringUtils.join(view.annotations.uri, " ")}">
				<g:if test="${view.annotations}">
					<div class="annotations">
						<g:each in="${view.annotations}" var="annotation">
							<a href="javascript:void(0)"
							   onclick="toggleSelected(event, '${annotation.uri}');">${annotation.labels.getAnyLiteral(ctxtLang)}</a>
						</g:each>
					</div>
				</g:if>

				<div class="label">${view.label}</div>

				<g:render template="/shared/propertyTable" model="[view: view]"/>
			</li>
		</g:each>
	</ul>
</g:if>
<g:else>
	<p><g:message code="document.show.resources.empty_list" args="[createLink(controller: 'document', params:[uri: entity.uri])]"/></p>
</g:else>
