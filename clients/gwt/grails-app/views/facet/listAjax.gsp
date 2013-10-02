<g:if test="${entities}">
	<g:render template="entityList" model="[entities, total]"/>
	<div class="pagination">
		<util:remotePaginate update="found-entities" total="${total}" action="listAjax"
							 onLoading="\$('#found-entities-spinner').fadeIn(200);"
							 onComplete="\$('#found-entities-spinner').hide(); updateSelected();"/>
	</div>
</g:if>
<g:else><p><g:message code="facet.searchEntitiesAjax.empty_list"/></p></g:else>
