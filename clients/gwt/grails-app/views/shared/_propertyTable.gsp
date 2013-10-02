<%@ page import="org.apache.commons.lang.StringUtils" %>

<%--
@param view
	a resource view
--%>

<div class="property-table-drop icon icon-menu-large"></div>

<div class="property-table" style="display: none">
	<table>
		<g:if test="${view.annotated}">
			<g:if test="${view.otherLiterals}">
				<tr><td colspan="2" class="heading"><g:message code="annotations.label"/></td></tr>
			</g:if>
			<tr><th><g:message code="annotation.property.label"/></th><th><g:message code="annotation.phrases.label"/></th></tr>
			<g:each in="${view.annotated}" var="entry">
				<tr><td class="property">${entry.key}</td><td class="values">${StringUtils.join(entry.value, "; ")}</td></tr>
			</g:each>
		</g:if>

		<g:if test="${view.otherLiterals}">
			<g:if test="${view.annotated}">
				<tr><td colspan="2" class="separator"></td></tr>
				<tr><td colspan="2" class="heading"><g:message code="external_data.label"/></td></tr>
			</g:if>
			<tr><th><g:message code="property.label"/></th><th><g:message code="value.label"/></th></tr>
			<g:each in="${view.otherLiterals}" var="entry">
				<tr><td class="property">${entry.key}</td><td class="values">${StringUtils.join(entry.value, "; ")}</td></tr>
			</g:each>
		</g:if>
	</table>
</div>
