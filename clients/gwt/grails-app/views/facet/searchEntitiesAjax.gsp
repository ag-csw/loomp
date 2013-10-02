<%@ page import="org.springframework.web.servlet.support.RequestContextUtils as RCU; loomp.oca.utils.CollectionUtils; org.apache.commons.lang.StringUtils" %>

<g:if test="${annotations}">
	<div class="tags-heading"><g:message code="face.index.selected_annotations"/></div>
	<ul id="selected-annotations" class="tags">
		<g:each in="${annotations}" var="annotation">
		<%-- NOTE: style attribute is important to the JavaScript providing the color picker --%>
			<li class="tag" style="" uri="${annotation.uri}" prop="${annotation.property}" domain="${annotation.domain}">
				<div class="color-picker" style="display: none;"></div>${annotation.labels.getAnyLiteral(RCU.getLocale(request).language)}<%-- no space here
				--%><g:remoteLink update="found-entities"
								  class="icon icon-delete-small"
								  url="[action: 'searchEntitiesAjax', params: [uris: uris, auris: CollectionUtils.without(auris, annotation.uri)]]"
								  onComplete="\$('#found-entities-spinner').hide(); initColorPickers(); updateSelected(); removeFromAF('${annotation.uri}')"><%--
						--%><span><g:message code="default.button.delete.label"/></span></g:remoteLink>
			</li>
		</g:each>
	</ul>
</g:if>

<g:if test="${views}">
	<div class="tags-heading"><g:message code="face.index.selected_resources"/></div>
	<ul id="selected-resources" class="tags">
		<g:each in="${views}" var="view">
		<%-- NOTE: style attribute is important to the JavaScript providing the color picker --%>
			<li class="tag" style="" uri="${view.resource.uri}" annotations="${StringUtils.join(view.annotations.uri, " ")}">
				<div class="color-picker" style="display: none;"></div>${view.label}<%-- no space here
				--%><g:remoteLink update="found-entities"
								  class="icon icon-delete-small"
								  url="[action: 'searchEntitiesAjax', params: [uris: CollectionUtils.without(uris, view.resource.uri), auris: auris]]"
								  onComplete="\$('#found-entities-spinner').hide(); initColorPickers(); updateSelected(); removeFromAF('${view.resource.uri}')"><%--
						--%><span><g:message code="default.button.delete.label"/></span></g:remoteLink>
			</li>
		</g:each>
	</ul>
</g:if>

<g:if test="${entities}">
	<g:render template="entityList" model="[entities, total]"/>
	<div class="pagination">
		<util:remotePaginate update="found-entities" total="${total}" action="searchEntitiesAjax" params="[uris: uris, auris: auris]"
							 onLoading="\$('#found-entities-spinner').fadeIn(200);"
							 onComplete="\$('#found-entities-spinner').hide(); updateSelected();"/>
	</div>
</g:if>
<g:else><p><g:message code="facet.searchEntitiesAjax.empty_list"/></p></g:else>
