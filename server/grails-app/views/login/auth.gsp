<%@ page contentType="text/html;charset=UTF-8" %>
<head>
	<meta name='layout' content='main'/>
	<title><g:message code="login.auth.heading"/></title>
</head>

<body>
<div><g:render template="/layouts/messages"/></div>
<h1><g:message code="login.auth.heading"/></h1>
<form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
	<div>
		<label for='j_username'><g:message code="login.auth.email"/></label>
		<input type='text' class='text_' name='j_username' id='j_username' value='${request.remoteUser}'/>
	</div>
	<div>
		<label for='j_password'><g:message code="login.auth.password"/></label>
		<input type='password' class='text_' name='j_password' id='j_password'/>
	</div>
	<div class="inline">
		<label for='remember_me'><g:message code="login.auth.remember"/></label>
		<input type='checkbox' class='chk' name='_spring_security_remember_me' id='remember_me'
			<g:if test='${hasCookie}'>checked='checked'</g:if>/>
	</div>
	<div class="bottons">
		<input type='submit' value='${message(code:'default.button.login.label')}'/>
	</div>
</form>
<script type='text/javascript'>
	<!--
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
	// -->
</script>
</body>
