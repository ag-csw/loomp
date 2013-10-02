<script type="text/javascript">
	$(document).ready(function() {
		$('#found-entities-spinner').fadeIn(200);
		$('#found-entities').load('${createLink(action: 'listAjax')}', function() {
			$('#found-entities-spinner').hide();
		});

		$('#annotation-filter #label').autocomplete({
					source: '${createLink(action: 'completeAnnotation')}',
					minLength: 1,
					autoFocus: true,
					select: function(event, ui) {
						if (addToAF('auris', ui.item.uri)) {
							$(this).parent().submit();
						}
					}
				})
				.data("autocomplete")._renderItem = function(ul, item) {
					$("<li></li>").data("item.autocomplete", item)
							.append("<a>" + item.value + "</a>").appendTo(ul);
					return ul;
				}
	});
</script>

<h4><g:message code="facet.searchResourcesAjax.add_anntotation.heading"/></h4>
<g:formRemote name="annotation-filter" update="found-entities" url="[action: 'searchEntitiesAjax']"
			  onLoading="\$('#found-entities-spinner').fadeIn(200);"
			  onComplete="\$('#found-entities-spinner').hide(); updateSelected(); initColorPickers(); \$('#label').val('');">
	<g:textField name="label"/>
	<g:each in="${auris}" var="auri" status="i"><g:hiddenField id="auris_${i}" name="auris" value="${auri}"/></g:each>
	<g:each in="${uris}" var="uri" status="i"><g:hiddenField id="uris_${i}" name="uris" value="${uri}"/></g:each>
</g:formRemote>


<h4><g:message code="facet.searchResourcesAjax.add_filter.heading"/></h4>
<g:formRemote class="search" name="search"
			  update="found-resources-container" url="[action: 'searchResourcesAjax']"
			  onLoading="\$('#found-resources-spinner').fadeIn(200)" onComplete="\$('#found-resources-spinner').hide()"
			  onSuccess="initResourceButtons('${createLink(action: 'searchEntitiesAjax')}'); updateSelected();">
	<g:textField name="query" value="${query}"/>
	<g:submitButton class="button" name="search" value="${message(code: 'default.button.search.label')}"/>
</g:formRemote>


<img id="found-resources-spinner" class="right" style="display: none;" src="${resource(dir: 'images', file: 'spinner.gif')}"
	 alt="${message(code: 'default.loading.label')}"/>

<div id="found-resources-container"></div>

<g:if test="${query}">
	<script type="text/javascript">
		$(document).ready(function() {
			$('#found-resources-spinner').fadeIn();
			$('#found-resources-container').load('${createLink(action: 'searchResourcesAjax', params: [query: query, prop: propUris])}', function() {
				$('#found-resources-spinner').hide();
				initResourceButtons('${createLink(action: 'searchEntitiesAjax')}');
				updateSelected();
			});
		});
	</script>
</g:if>
