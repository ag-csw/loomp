<%@ page import="grails.util.GrailsUtil" %>
<div class="logo">
	<img src="${resource(dir: 'images', file: 'loomp-logo.png')}" alt="loomp"/>
	<p><g:message code="nav.version" args="[grailsApplication.metadata['app.version']]"/></p>
</div>

<g:isLoggedIn>
	<div class="loginstatus">
		<g:loggedInUsername/> (<g:link controller='logout'><g:message code="nav.logout"/></g:link>)
	</div>
</g:isLoggedIn>

<div>
	<ul>
		<li><g:link controller="admin"><g:message code="nav.menu.admin"/></g:link></li>
		<li><g:link controller="testContent"><g:message code="nav.menu.testContent"/></g:link></li>
		<li><g:link controller="testAnnotation"><g:message code="nav.menu.testAnnotation"/></g:link></li>
		<li><g:link controller="testMeta"><g:message code="nav.menu.testMeta"/></g:link></li>
		<li><g:link url="http://loomp.org/api.html"><g:message code="nav.menu.docs"/></g:link></li>
	</ul>
</div>

