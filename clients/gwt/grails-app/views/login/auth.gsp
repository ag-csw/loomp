<head>
	<meta name='layout' content='main'/>
	<title><g:message code="login.auth.heading"/></title>
</head>

<body>

<h1><g:message code="login.auth.heading"/></h1>

<div class="form">
	<form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
		<label for='j_username'><g:message code="person.email.label"/></label>
		<g:textField name='j_username' value='${request.remoteUser}'/>

		<label for='j_password'><g:message code="person.passwd.label"/></label>
		<g:passwordField name='j_password'/>
		<%--
		 <label for='remember_me'>Remember me</label>
		 <input type='checkbox' class='chk' name='_spring_security_remember_me' id='remember_me'
			 <g:if test='${hasCookie}'>checked='checked'</g:if>/>
		 --%>
		<g:submitButton name="login" class="button" value="${message(code:'default.button.login.label')}"/>
	</form>
</div>
<script type='text/javascript'>
	<!--
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
	// -->
</script>
</body>
