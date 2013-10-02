<%--
	This page contains a form to send json to a document.save
--%>
<%@ page import="loomp.RenderFormat" contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title><g:message code="testContent.showElementText.heading"/></title>
	<meta name="layout" content="main"/>
</head>

<body>
<div class="body">
	<h1><g:message code="testContent.showElementText.heading"/></h1>

	<p>${element.uri}</p>

	<p>${element.content}</p>
	<ul>
		<g:each in="${resources}" var="resource">
			<li>${resource.uri}
				<table>
					<g:each in="${resource.props}" var="tpv">
						<tr><td>${tpv.property}</td><td>${tpv.value}</td></tr>
					</g:each>
				</table>
			</li>
		</g:each>
	</ul>
</div>
</body>
</html>
