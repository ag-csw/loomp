<head>
	<meta name="layout" content="main"/>
	<title>User Registration</title>
</head>

<body>

<h1><g:message code="register.index.heading-1" /></h1>

<div class="form">
	<g:form action="save">
		<label for='userRealName' class="${hasErrors(bean: person, field: 'userRealName', 'errors')}">
			<g:message code="person.userRealName.label"/>
		</label>
		<g:textField name="userRealName" value="${person?.userRealName?.encodeAsHTML()}"/>

		<label for='passwd' class="${hasErrors(bean: person, field: 'passwd', 'errors')}">
			<g:message code="person.passwd.label"/>
		</label>
		<g:passwordField name="passwd" value="${person?.passwd?.encodeAsHTML()}"/>

		<label for='repasswd' class="${hasErrors(bean: person, field: 'passwd', 'errors')}">
			<g:message code="person.confirmPasswd.label"/>
		</label>
		<g:passwordField name="repasswd" value="${person?.passwd?.encodeAsHTML()}"/>

		<label for='email' class="${hasErrors(bean: person, field: 'email', 'errors')}">
			<g:message code="person.email.label"/>
		</label>
		<g:textField name="email" value="${person?.email?.encodeAsHTML()}"/>

		<label for='code' class="${hasErrors(bean: person, field: 'code', 'errors')}">
			<g:message code="register.code.label"/>
		</label>
		<g:textField name="code" size="8"/>
		<img class="captcha" src="${createLink(controller: 'captcha', action: 'index')}"/>

		<g:submitButton name="login" class="button" value="${message(code:'default.button.create.label')}"/>
	</g:form>
</div>
</body>
