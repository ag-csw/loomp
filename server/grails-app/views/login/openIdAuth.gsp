<html>
<head>
	<meta name='layout' content='main'/>
	<title>Login</title>
</head>

<body>
<div><g:render template="/layouts/messages"/></div>
<form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
	<p>
		<label for='j_username'>OpenID Identity</label>
		<input type='text' class='text_' name='j_username'/>
	</p>
	<p>
		<input type='submit' value='Login'/>
	</p>
</form>
<script type='text/javascript'>
	(function() {
		document.forms['loginForm'].elements['j_username'].focus();
	})();
</script>
</body>
</html>
