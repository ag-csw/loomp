<%@ page import="loomp.oca.utils.CollectionUtils; org.apache.commons.lang.StringUtils" %>

<g:if test="${views}">
	<g:if test="${annotations}">
		<ul id="selected-resource-annotations" class="tags">
			<g:each in="${annotations}" var="annotation">
				<g:set var="selected" value="${auris && auris.contains(annotation.uri)}"/>
				<li class="tag ${selected ? 'selected' : ''}" uri="${annotation.uri}" prop="${annotation.property}" domain="${annotation.domain}"><%-- no linebreak here
				--%>${annotation.labels.getAnyLiteral(ctxtLang)}<%-- no linebreak here
				--%><g:remoteLink style="display: none;" update="found-resources-container" method="post"
								  url="[action: 'searchResourcesAjax', params: [query: query, auris: CollectionUtils.toggle(auris, annotation.uri)]]"
								  onLoading="\$('#found-resources-spinner').fadeIn(200);" onComplete="\$('#found-resources-spinner').hide()"
								  onSuccess="initResourceButtons('${createLink(action: 'searchEntitiesAjax')}');">${annotation.labels.getAnyLiteral(ctxtLang)}</g:remoteLink></li>
			</g:each>
		</ul>
	</g:if>

	<ul id="found-resources" class="resource-list">
		<g:each in="${views}" var="view">
			<li class="resource-item" uri="${view.resource.uri}" annotations="${StringUtils.join(view.annotations.uri, " ")}">
				<g:if test="${view.annotations}">
					<div class="annotations">
						<g:each in="${view.annotations}" var="annotation">
							<span>${annotation.labels.getAnyLiteral(ctxtLang)}</span>
						</g:each>
					</div>
				</g:if>

				<div class="label">${view.label}</div>

				<g:render template="/shared/propertyTable" model="[view: view]"/>
			</li>
		</g:each>
	</ul>
	<util:remotePaginate update="found-resources-container" total="${total}" action="searchResourcesAjax"
						 params="[query: query, auris: auris]"
						 onLoading="\$('#found-resources-spinner').fadeIn(200);" onComplete="\$('#found-resources-spinner').hide()"
						 onSuccess="initResourceButtons('${createLink(action: 'searchEntitiesAjax')}');"/>
</g:if>
<g:else>
	<p><g:message code="facet.searchResourcesAjax.empty_list"/></p>
</g:else>
