<!-- login info ON -->
<%--
<div id="navL">
	<ul>
		<g:isLoggedIn>
			<? if ($this->loginStatus) { ?>
				<!-- logged in -->
			<li>
				<p>
					<g:message code="nav.loggedInAs.label" args="${[loggedInUserInfo(field:'userRealName')]}"/>
					<g:link controller="logout"><g:message code="nav.logout.label"/></g:link>
				</p>
			</li>
		</g:isLoggedIn>
		<g:isNotLoggedIn>
			<li>
				<p>
					<g:link controller="login"><g:message code="nav.login.label"/></g:link>
					<g:link controller="register"><g:message code="nav.register.label"/></g:link>
				</p>
			</li>
		</g:isNotLoggedIn>
	</ul>
</div>
<!-- login info OFF -->
--%>

<!-- menu info ON -->
<div id="navH">
	<ul>
		<li class="${gen.active(name: 'home')}">
			<g:link url="${createLinkTo(dir:'')}"><g:message code="nav.home.label"/></g:link>
		</li>
		<li class="${gen.active(name: 'document')}">
			<g:link controller="document"><g:message code="nav.document.label"/></g:link>
		</li>
		<li class="${gen.active(name: 'facet')}">
			<g:link controller="facet"><g:message code="nav.facet.label"/></g:link>
		</li>

		<%-- use this later for a user to be logged in to edit
		<g:isLoggedIn>
			<li class="${gen.active(name: 'document')}">
				<g:link controller="document"><g:message code="nav.document.label"/></g:link>
			</li>
			<li class="${gen.active(name: 'account')}">
				<g:link controller="account"><g:message code="nav.account.label"/></g:link>
			</li>
		</g:isLoggedIn>
		--%>
		<g:ifAnyGranted role="ROLE_ADMIN">
			<li class="${gen.active(name: 'admin')}">
				<g:link controller="admin"><g:message code="nav.admin.label"/></g:link>
			</li>
		</g:ifAnyGranted>
	</ul>
	<br style="clear:both;"/>

</div>
<!-- menu info OFF -->

