<!-- link:home -->
<g:link url="${createLinkTo(dir:'')}"><div class="logo"></div></g:link>

<div class="search" id="searchForm">
	<g:form controller="home" action="search">
		<div style="padding:1px; margin:0px;  float:left; border: 1px solid #fff">
			<g:textField name="query" value="${query}"/>
		</div>
		<g:submitButton name="submit" class="button" value="${message(code: 'default.button.search.label')}"/>
	</g:form>
</div>
