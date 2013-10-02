<%@ page import="de.fuberlin.loomp.domain.News" %>

<g:if test="${News.list()}">
	<div class="box280">
		<g:each in="${News.list()}" var="news">
			<h3>${news.title}</h3>

			<p>${news.message}</p>
			<p class="small"><g:message code="by" args="[news.author]" /></p>
		</g:each>
	</div>
</g:if>

