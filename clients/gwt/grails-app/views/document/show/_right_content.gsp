<ul class="right-buttons">
	<li><g:link controller="document" params="[uri: entity.uri]"><g:message code="default.button.edit.label"/></g:link></li>
</ul>

<g:if test="${entity}">
%{--<h3><g:message code="document.show.annotated.heading"/></h3>--}%

	<img id="annotated-terms-spinner" style="display: none;" src="${resource(dir: 'images', file: 'spinner.gif')}"
		 alt="${message(code: 'default.loading.label')}"/>

	<div id="annotated-terms"></div>

	<script type="text/javascript">
		$(document).ready(function() {
			$('#annotated-terms-spinner').fadeIn();
			$('#annotated-terms').load('${createLink(action: 'annotatedTermsAjax', params: [uri: entity.uri])}', function() {
				$('#annotated-terms-spinner').hide(); initAnnotationButtons();
			});
		});
	</script>
</g:if>
